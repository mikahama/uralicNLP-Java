/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mikahama
 */
public class UDTools {

    public static UDSentence parseSentence(String sentence) {
        sentence = sentence.replaceAll("\r", "");
        String[] list = sentence.split("\n");
        return parseSentence(Arrays.asList(list));
    }

    public static UDSentence parseSentence(List<String> conlluSentence) {
        HashMap<String, UDNode> nodes = new HashMap<String, UDNode>();
        HashMap<String, String[]> relations = new HashMap<String, String[]>();
        UDSentence ud_sentence = new UDSentence();
        String comments = "";
        for (String annotation : conlluSentence) {
            if (annotation.isEmpty()) {
                continue;
            }
            if (annotation.startsWith("#")) {
                comments = comments + annotation + "\n";
                continue;
            }
            String[] parts = annotation.split("\t");
            if (parts[0].contains("-")) {
                //multi-part annotation --> skip for now
                continue;
            }
            UDNode node = new UDNode(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[9]);
            nodes.put(parts[0], node);

            relations.put(parts[0], new String[]{parts[6], parts[7], parts[8]});
        }
        ud_sentence.comments = comments;
        UDNode root = null;
        nodes.put("0", ud_sentence.udRootNode);
        for (String id : relations.keySet()) {
            String[] relation = relations.get(id);
            String head_id = relation[0];
            if (head_id.equals("0")) {
                root = nodes.get(id);
            }
            if (id.contains(".") && head_id.equals("_")) {
                head_id = id.split(".")[0];
            }
            UDRelation headRelation = new UDRelation(nodes.get(id), relation[1], nodes.get(head_id));
            String o_rel = relation[2];
            String[] other_relations;
            if (o_rel.equals("_")) {
                other_relations = new String[]{};
            } else {
                other_relations = o_rel.split("|");
            }
            String head_rel = head_id + ":" + relation[1];
            for (int u = 0; u < other_relations.length; u++) {
                String other_relation = other_relations[u];
                if (other_relation.equals(head_rel)) {
                    continue;
                }
                String[] other_parts = other_relation.split(":");
                UDRelation r = new UDRelation(nodes.get(id), other_parts[1], nodes.get(other_parts[0]), false);
            }
        }
        ud_sentence.setRoot(root);
        return ud_sentence;
    }
}
