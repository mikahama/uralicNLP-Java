/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mikahama
 */
public class UDCollection implements Iterable<UDSentence> {

    ArrayList<UDSentence> sentences = new ArrayList<UDSentence>();

    public UDCollection(Reader in) throws IOException {
        int intValueOfChar;
        String targetString = "";
        while ((intValueOfChar = in.read()) != -1) {
            targetString += String.valueOf(intValueOfChar);
        }
        in.close();
        init(targetString);
    }

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

    public List<UDSentence> findSentences(HashMap<String, String> query) {
        return findSentences(query, new HashMap<String, String>(), false, false, false, false);
    }

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

    public List<String> getUniqueFeats() {
        return getUniqueFeats("|");
    }

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

    public List<UDNode> findWords(HashMap<String, String> query) {
        return findWords(query, new HashMap<String, String>(), false, false, false, false);
    }

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
