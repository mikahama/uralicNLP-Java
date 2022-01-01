/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mikahama
 */
public class StringProcessing {
    
    private JSONObject isoMap;
    
    /**
     * Initialization. This loads ISO codes to the memory.
     */
    public StringProcessing(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("res/lang_codes.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            isoMap = (JSONObject) obj;
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gives an English name for a 3 letter language ISO code
     * @param isoCode 3 letter ISO code
     * @return English name
     */
    public String isoToName(String isoCode){
        return (String) isoMap.get(isoCode);
    }

    /**
     * Removes all non-Arabic characters
     * @param word a word with mixed Arabic and non-Arabic characters
     * @param keepVowels true -> keeps diacritics, false -> removes diacritics
     * @param combineBy combines the found instances of Arabic words with this separator token
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
