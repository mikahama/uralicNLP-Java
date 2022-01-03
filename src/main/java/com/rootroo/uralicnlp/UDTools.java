/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.Arrays;
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

    public static UDSentence parseSentence(List<String> sentence) {
        
    }
}
