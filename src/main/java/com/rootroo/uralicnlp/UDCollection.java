/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A class that holds an entire UD file
 * @author mikahama
 */
public class UDCollection implements Iterable<UDSentence> {

    ArrayList<UDSentence> sentences = new ArrayList<UDSentence>();

    /**
     * Initializes the class with a BufferedReader that reads a CoNLL-U formatted file
     * @param in a CoNLL-U formatted file
     * @throws IOException May fail reading the file
     */
    public UDCollection(BufferedReader in) throws IOException {
        int intValueOfChar;
        String targetString = "";
      String str;
      while ((str = in.readLine()) != null) {
          targetString += str +"\n";
      }
        in.close();
        init(targetString);
    }

    /**
     * Initializes the class with a CoNLL-U formatted string
     * @param UDText a CoNLL-U formatted string
     */
    public UDCollection(String UDText) {
        init(UDText);
    }

    private void init(String UDText) {
        UDText = UDText.replaceAll("\r", "");
        String[] lines = UDText.split("\n");
        ArrayList<String> sentence = new ArrayList<String>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.replaceAll("\n", "").length() == 0) {
                if (sentence.size() > 0) {
                    sentences.add(UDTools.parseSentence(sentence));
                }
                sentence = new ArrayList<String>();
            } else {
                sentence.add(line);
            }
        }

        if (sentence.size() > 0) {
            //add the last sentence
            sentences.add(UDTools.parseSentence(sentence));
        }
    }

    /**
     * Finds sentences matching a query.
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @return Matching sentences
     */
    public List<UDSentence> findSentences(HashMap<String, String> query) {
        return findSentences(query, new HashMap<String, String>(), false, false, false, false);
    }

    /**
     * Finds sentences matching a query.
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @param headQuery A query that matches the head word with UD features, for instance {"lemma":"cat"}
     * @param matchRangeTokens Matches range tokens
     * @param matchEmptyNodes Matches empty nodes
     * @param enhancedDependencies Use enhanced dependencies
     * @param useRegex Use regex in the query values e.g. {"lemma":"cat.*"}
     * @return Matching sentences
     */
    public List<UDSentence> findSentences(HashMap<String, String> query, HashMap<String, String> headQuery, boolean matchRangeTokens, boolean matchEmptyNodes, boolean enhancedDependencies, boolean useRegex) {
        List<UDSentence> results = new ArrayList<UDSentence>();
        for (UDSentence sentence : sentences) {
            List<UDNode> r = sentence.find(query, headQuery, matchRangeTokens, matchEmptyNodes, enhancedDependencies, useRegex);
            if (r.size() > 0) {
                results.add(sentence);
            }

        }
        return results;
    }

    /**
     * Get features used in the UD tree bank
     * @return Features
     */
    public List<String> getUniqueFeats() {
        return getUniqueFeats("|");
    }

     /**
     * Get features used in the UD tree bank
     * @param delimiter usually "|"
     * @return Features
     */
    public List<String> getUniqueFeats(String delimiter) {
        List<String> feats = new ArrayList<String>();
        for (UDSentence sentence : sentences) {
            List<String> fs = sentence.getUniqueFeats(delimiter);
            for (String f : fs) {
                if (!feats.contains(f)) {
                    feats.add(f);
                }
            }
        }
        return feats;
    }

    /**
     * Gets all attribute values in the UD tree bank
     * @param attribute a UD feature e.g. lemma, form, misc...
     * @return Attribute values
     */
    public List<String> getUniqueAttributes(String attribute) {
        List<String> feats = new ArrayList<String>();
        for (UDSentence sentence : sentences) {
            List<String> fs = sentence.getUniqueAttributes(attribute);
            for (String f : fs) {
                if (!feats.contains(f)) {
                    feats.add(f);
                }
            }
        }
        return feats;
    }

    /**
     * Finds words in the UD that match the query
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @return Matching words
     */
    public List<UDNode> findWords(HashMap<String, String> query) {
        return findWords(query, new HashMap<String, String>(), false, false, false, false);
    }
    
    /**
     * Finds words in the UD that match the query
     * @param query A query with UD features, for instance {"lemma":"cat"}
     * @param headQuery A query that matches the head word with UD features, for instance {"lemma":"cat"}
     * @param matchRangeTokens Matches range tokens
     * @param matchEmptyNodes Matches empty nodes
     * @param enhancedDependencies Use enhanced dependencies
     * @param useRegex Use regex in the query values e.g. {"lemma":"cat.*"}
     * @return Matching words
     */
    public List<UDNode> findWords(HashMap<String, String> query, HashMap<String, String> headQuery, boolean matchRangeTokens, boolean matchEmptyNodes, boolean enhancedDependencies, boolean useRegex) {
        List<UDNode> results = new ArrayList<UDNode>();
        for (UDSentence sentence : sentences) {
            List<UDNode> r = sentence.find(query, headQuery, matchRangeTokens, matchEmptyNodes, enhancedDependencies, useRegex);
            if (r.size() > 0) {
                results.addAll(r);
            }

        }
        return results;
    }

    @Override
    public Iterator<UDSentence> iterator() {
        return new Iterator<UDSentence>() {
            private final Iterator<UDSentence> iter = sentences.iterator();

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public UDSentence next() {
                return iter.next();
            }

            @Override
            public void remove() {
                //iter.remove();
            }
        };
    }
}
