/*
 * (C) Mika Hämäläinen 2021-2022 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Tools for string manipulation
 * @author mikahama
 */
public class StringProcessing {

    private JSONObject isoMap;

    /**
     * Initialization. This loads ISO codes to the memory.
     */
    public StringProcessing() {
        JSONParser jsonParser = new JSONParser();

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("lang_codes.json");) {
            //Read JSON file
            String UTF8 = "utf8";
            int BUFFER_SIZE = 8192;
            String file = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(stream,
                    UTF8), BUFFER_SIZE);
            String str;
            while ((str = br.readLine()) != null) {
                file += str;
            }
            Object obj = jsonParser.parse(file);

            isoMap = (JSONObject) obj;

        } catch (Exception e) {
            //Should not happen because the JSON is always embedded in the JAR
            e.printStackTrace();
        }
    }

    /**
     * Gives an English name for a 3 letter language ISO code
     *
     * @param isoCode 3 letter ISO code
     * @return English name
     */
    public String isoToName(String isoCode) {
        return (String) isoMap.get(isoCode);
    }

    /**
     * Removes all non-Arabic characters
     *
     * @param word a word with mixed Arabic and non-Arabic characters
     * @param keepVowels true -> keeps diacritics, false -> removes diacritics
     * @param combineBy combines the found instances of Arabic words with this
     * separator token
     * @return Arabic text separated by combineBy
     */
    public static String filterArabic(String word, boolean keepVowels, String combineBy) {
        String rege = "[ء-ي'ًٌٍَُِّْـ']+";
        if (!keepVowels) {
            rege = "[ء-ي]+";
        }
        List<String> allMatches = new ArrayList<String>();
        Matcher matches;
        matches = Pattern.compile(rege)
                .matcher(word);
        while (matches.find()) {
            allMatches.add(matches.group());
        }
        return String.join(combineBy, allMatches);
    }
}
