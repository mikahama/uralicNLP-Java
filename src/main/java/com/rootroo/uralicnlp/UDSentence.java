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
 * UDSentence holds a list of UDNodes representing a sentence
 *
 * @author mikahama
 */
public class UDSentence implements Iterable<UDNode> {

    String comments = "";
    String id = "0";
    List<UDNode> children;
    List<UDNode> secondaryChildren;
    UDNode root;

    UDRootNode udRootNode;

    /**
     * Initialize a UDSentence, you must also call setRoot after populating the
     * object
     */
    public UDSentence() {
        children = new ArrayList<UDNode>();
        secondaryChildren = new ArrayList<UDNode>();
        udRootNode = new UDRootNode("", "", "", "", "", "", "");
    }

    /**
     * Sets the root node
     *
     * @param root root node
     */
    public void setRoot(UDNode root) {
        this.root = root;
    }

    /**
     * Gets all words
     *
     * @return all words
     */
    public List<UDNode> find() {
        return find(new HashMap<String, String>(), new HashMap<String, String>(), false, false, false, false);
    }

    /**
     * Finds words based on the query
     *
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @return Matching words
     */
    public List<UDNode> find(HashMap<String, String> query) {
        return find(query, new HashMap<String, String>(), false, false, false, false);
    }

    /**
     * Finds words based on the query
     *
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @param headQuery A query that matches the head word with UD features, for
     * instance {"lemma":"cat"}
     * @param matchRangeTokens Matches range tokens
     * @param matchEmptyNodes Matches empty nodes
     * @param enhancedDependencies Use enhanced dependencies
     * @param useRegex Use regex in the query values e.g. {"lemma":"cat.*"}
     * @return Matching words
     */
    public List<UDNode> find(HashMap<String, String> query, HashMap<String, String> headQuery, boolean matchRangeTokens, boolean matchEmptyNodes, boolean enhancedDependencies, boolean useRegex) {
        return root.find(query, headQuery, matchRangeTokens, matchEmptyNodes, enhancedDependencies, useRegex);
    }

    /**
     * Get features used in the sentence
     *
     * @return Features
     */
    public List<String> getUniqueFeats() {
        return getUniqueFeats("|");
    }

    /**
     * Get features used in the sentence
     *
     * @param delimiter usually "|"
     * @return Features
     */
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

    /**
     * Gets all unique attribute values in the sentence
     *
     * @param attribute a UD feature e.g. lemma, form, misc...
     * @return Attribute values
     */
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

    /**
     * Gets a word based on index
     *
     * @param index index of the word in the sentence
     * @return word by index
     */
    public UDNode get(int index) {
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

    /**
     * Gets all words in order
     *
     * @return words in order
     */
    public List<UDNode> getSortedChildren() {

        List<UDNode> children = find();
        Collections.sort(children, new UDNodeComparator());
        return children;
    }

    @Override
    public Iterator<UDNode> iterator() {
        return new Iterator<UDNode>() {
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
                //iter.remove();
            }
        };
    }

}
