/*
 * (C) Mika Hämäläinen 2022 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mikahama
 */
public class UDSentence implements Iterable<UDNode>{

    String comments = "";
    String id = "0";
    List<UDNode> children;
    List<UDNode> secondaryChildren;
    UDNode root;

    public UDSentence() {
        children = new ArrayList<UDNode>();
        secondaryChildren = new ArrayList<UDNode>();
    }

    public void setRoot(UDNode root) {
        this.root = root;
    }

    public List<UDNode> find() {
        return find(new HashMap<String, String>(), new HashMap<String, String>(), false, false, false, false);
    }

    public List<UDNode> find(HashMap<String, String> query) {
        return find(query, new HashMap<String, String>(), false, false, false, false);
    }

    public List<UDNode> find(HashMap<String, String> query, HashMap<String, String> headQuery, boolean matchRangeTokens, boolean matchEmptyNodes, boolean enhancedDependencies, boolean useRegex) {
        return root.find(query, headQuery, matchRangeTokens, matchEmptyNodes, enhancedDependencies, useRegex);
    }

    public List<String> getUniqueFeats() {
        return getUniqueFeats("|");
    }

    public List<String> getUniqueFeats(String delimiter) {
        List<UDNode> children = find();
        List<String> feats = new ArrayList<String>();
        for (UDNode child : children) {
            String[] fs = child.getFeats(delimiter);
            for (int i = 0; i < fs.length; i++) {
                String f = fs[i];
                if (!feats.contains(f)) {
                    feats.add(f);
                }
            }
        }
        return feats;
    }

    public List<String> getUniqueAttributes(String attribute) {
        List<UDNode> children = find();
        List<String> feats = new ArrayList<String>();
        for (UDNode child : children) {
            String f = child.getAttribute(attribute);
            if (!feats.contains(f)) {
                feats.add(f);
            }

        }
        return feats;
    }
    
    public UDNode get(int index){
        List<UDNode> children = find();
        Collections.sort(children, new UDNodeComparator());
        return children.get(index);
    }

    @Override
    public String toString() {
        List<UDNode> children = find();
        Collections.sort(children, new UDNodeComparator());
        String representation = comments;
        for (UDNode child : children) {
            representation = representation + child.toString() + "\n";
        }
        return representation;
    }
    
    public List<UDNode> getSortedChildren(){
        List<UDNode> children = find();
        Collections.sort(children, new UDNodeComparator());
        return children;
    }
    
        @Override
    public Iterator<UDNode> iterator() {
        return new Iterator<UDNode> () {
            private final Iterator<UDNode> iter = getSortedChildren().iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public UDNode next() {
                return iter.next();
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

}
