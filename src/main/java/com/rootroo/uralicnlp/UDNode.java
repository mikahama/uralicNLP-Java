/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mikahama
 */
public class UDNode {

    String id;
    String form;
    String lemma;
    String upostag;
    String pos;
    String xpostag;
    String feats;
    String misc;
    UDRelation head = null;
    List<UDRelation> children;
    List<UDRelation> heads;
    List<UDRelation> secondaryChildren;
    UDRelation relation;

    public UDNode(String id, String form, String lemma, String upostag, String xpostag, String feats, String misc) {
        this.id = id;
        this.form = form;
        this.lemma = lemma;
        this.upostag = upostag;
        this.xpostag = xpostag;
        this.pos = upostag;
        this.feats = feats;
        this.misc = misc;
        children = new ArrayList<UDRelation>();
        heads = new ArrayList<UDRelation>();
        secondaryChildren = new ArrayList<UDRelation>();

    }

    /**
     * Get a list of UD features, split by the default delimiter |
     *
     * @return features
     */
    public String[] getFeats() {
        return getFeats("|");
    }

    /**
     * Get a list of UD features, split by a delimiter
     *
     * @param delimiter
     * @return features
     */
    public String[] getFeats(String delimiter) {
        return feats.split(Pattern.quote(delimiter));
    }


    public String getAttribute(String attribute) {

        if (attribute.equals("id")) {
            return this.id;
        } else if (attribute.equals("form")) {
            return this.form;
        } else if (attribute.equals("lemma")) {
            return this.lemma;
        } else if (attribute.equals("upostag") || attribute.equals("pos")) {
            return this.upostag;
        } else if (attribute.equals("xpostag")) {
            return this.xpostag;
        } else if (attribute.equals("feats")) {
            return this.feats;
        } else if (attribute.equals("misc")) {
            return this.misc;
        } else if (attribute.equals("deprel")) {
            return this.head.relation;
        } else {
            return null;
        }
    }
    
    public List<UDNode> find(HashMap<String, String> query){
        return find(query, new HashMap<String, String>(), false, false, false, false);
    }
    
    public List<UDNode> find(HashMap<String, String> query, HashMap<String, String> headQuery, boolean matchRangeTokens, boolean matchEmptyNodes, boolean enhancedDependencies, boolean useRegex){
List<UDNode> results = new ArrayList<UDNode>();
		boolean passed = true;
		if ((!matchRangeTokens && id.contains("-")) || (!matchEmptyNodes && id.contains("."))){
			passed = false;
                                }
                else{
			for (String key : query.keySet()){
                            String attr;
				if (key.equals("deprel")){
					if (head == null){
						attr = "root";}
                                        else{
						attr = head.relation;}
                                                        }
                                else{
					attr = getAttribute(key);}
				String v = query.get(key);
				if (useRegex){
                                    Pattern p = Pattern.compile(v);
                                    Matcher m = p.matcher(attr);
                                    passed = m.find();
                                    break;
                                }
					
                                else{
					if (!v.equals(attr)){
						passed = false;
						break;}
                                                        }
                                                        }
			
			if (head != null && passed){
                            List<UDRelation> heads = new ArrayList<UDRelation>();
                            
				if (enhancedDependencies){
					heads.addAll(this.heads);
                                heads.add(head);}
                                else{
					heads.add(head);}
				boolean head_pass = true;
				for (UDRelation head : heads){
					head_pass = true;
					for (String key : headQuery.keySet()){
                                            String attr;
						if (key.equals("deprel")){
							if (head == null){
								attr = "root";
                                                                        }
                                                        else{
								attr = head.relation;}
                                                }
                                                else{
							attr = getAttribute(key);
                                                                }
						String v = headQuery.get(key);
						if (useRegex){
                                                    Pattern p = Pattern.compile(v);
                                    Matcher m = p.matcher(attr);
                                    head_pass = m.find();
						
								break;}
                                                else{
							if (!v.equals(attr)){
								head_pass = false;
								break;}}
                                                                        }
					if(head_pass){
						break;}
                                                        }
				passed = head_pass;
                                        }		

                        else if( head == null && !headQuery.isEmpty()){
				passed = false;}
                                        }
		if (passed){
			results.add(this);
                                }
		for (UDRelation child : children){
			List<UDNode> r = child.node.find(query, headQuery, matchRangeTokens, matchEmptyNodes, enhancedDependencies, useRegex);
			results.addAll(r);
                                }
		return results;
    }
    
    @Override
    public String toString(){
    	
		String head_repr = "0\troot";
                String deps;
		if (head != null){
			head_repr = head.head.id + "\t" + head.relation;
                                }
		if (heads.size() == 0){
			deps = "_";
                }
                else{
                        List<UDRelation> rels = new ArrayList<UDRelation>();
                        for(UDRelation r : heads){
                            if(!r.head.equals(this)){
                            rels.add(r);
                            }
                        }
			
			if (head != null){
				rels.add(head);
                        }
			Collections.sort(rels, new UDRelationComparator());
                        ArrayList<String> relsStrings = new ArrayList<String>();
                        for(UDRelation rel : rels){
                        relsStrings.add(rel.toString());
                        }
                        deps = String.join("|", relsStrings);
			
			if (deps.equals("")){
				deps = "_";}
                                        }
		return id + "\t" + form + "\t" + lemma + "\t" + upostag + "\t" + xpostag + "\t" + feats + "\t" + head_repr + "\t" + deps + "\t" + misc;
	
    }
    
    @Override
    public boolean equals(Object node){
        return this.toString().equals(node.toString());
    }
    
    public boolean lt(UDNode other){
                boolean s_dash = false;
                float s_id;
		if (id.contains("-")){
			s_dash = true;
			s_id = Integer.parseInt(id.split("-")[0]);
                }
                else{
			s_id = Float.parseFloat(id);
                }
		boolean o_dash = false;
                float o_id;
		if (other.id.contains("-")){
			o_dash = true;
			o_id = Integer.parseInt(other.id.split("-")[0]);
                }
                else{
			o_id = Float.parseFloat(other.id);
                }
		if (o_id == s_id){
			if (s_dash){
				return true;
                        }
			return false;
                                }
                else{
			return s_id < o_id;
                                }
    }

}
