/*
 * (C) Mika Hämäläinen 2022 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author mikahama
 */
public class Tokenizer {

    private String sentenceEnd = "!?。……‥！？。⋯…؟჻!…";
    private String wordEndPuct = ",;:”’'\"»」)]}،؛》』〕｠〉》】〗〙〛–—";
    private String wordStartPunct = "'\"¡¿「«“”‘({[《『〔｟〈《【〖〘〚–—”";
    private String numbers = "0123456789١٢٣٤٥٦٧٨٩٠";
    private String customPunctuation = "!\"#$%&'()*+,-.:;<=>?@[]^_`{|}~";
    Pattern abrvRegex;

    /**
     * Initializes a tokenizer
     */
    public Tokenizer() {
        List<String> abreviations = new ArrayList<String>();
        JSONParser jsonParser = new JSONParser();

        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream("abrvs.json");) {
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

            JSONArray abreviationsJSON = (JSONArray) obj;
            for (Object o : abreviationsJSON) {
                abreviations.add(Pattern.quote((String) o));
            }

        } catch (Exception e) {
            //Should not happen because the JSON is always embedded in the JAR
            e.printStackTrace();
        }
        String regex = "(^|\\s)(" + String.join("|", abreviations) + ")$";
        abrvRegex = Pattern.compile(regex);
    }

    /**
     * Does a sentence tokenization and removes extra whitespaces and line breaks
     * E.g. "A cat jumps. A dog barks." -> ["A cat jumps.", "A dog barks."]
     * @param text Text to be tokenized
     * @return A list where each element is a sentence
     */
    public List<String> sentences(String text) {
        List<String> parts = new ArrayList<String>();
        String current_s = "";
        boolean previous_break = false;
        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
            if (sentenceEnd.contains(c)) {
                //End of a sentence, not a dot
                if (current_s.length() > 0) {
                    //There is a current sentence, apped it and clear it
                    parts.add(current_s + c);
                    current_s = "";
                } else if (parts.size() > 0) {
                    //No current sentence, add to a previous sentence
                    parts.set(parts.size() - 1, parts.get(parts.size() - 1) + c);

                } else {
                    //Make it a current sentence
                    current_s = c;
                }
            } else if (c.equals(".")) {
                //A dot
                if (current_s.length() == 0) {
                    //no current sentence
                    if (parts.size() > 0) {
                        //append
                        parts.set(parts.size() - 1, parts.get(parts.size() - 1) + c);
                    } else {
                        current_s = c;
                    }
                } else if (current_s.length() > 0 && numbers.contains(String.valueOf(current_s.charAt(current_s.length() - 1)))) {
                    //previous is a number
                    current_s += c;
                } else if (endsInAbrv(current_s)) {
                    //abreviation
                    current_s += c;
                } else if (text.length() > i + 1 && String.valueOf(text.charAt(i + 1)).strip().length() != 0) {
                    //dot is not followed by a space
                    current_s += c;
                } else {
                    //dot ending a sentence
                    parts.add(current_s + c);
                    current_s = "";
                }
            } else if (c.equals("\n")) {
                //line break
                if (previous_break && current_s.length() > 0) {
                    parts.add(current_s);
                    current_s = "";
                }
                if (!previous_break && current_s.length() > 0) {
                    current_s += c;
                }
                previous_break = true;
                continue;
            } else if (c.equals("\r")) {
                //Windows line break
                continue;
            } else {
                //Any other character
                current_s += c;
            }
            previous_break = false;
        }
        if (current_s.length() > 0) {
            parts.add(current_s);
        }
        List<String> returnParts = new ArrayList<String>();
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i).trim().replaceAll("\\s+", " ");
            if (part.length() > 0) {
                returnParts.add(part);
            }
        }

        return returnParts;
    }

    /**
     * Does a word tokenization and removes extra whitespaces and all line breaks
     * E.g "A cat jumps. A dog barks." -> ["A", "cat", "jumps", ".", "A", "dog", "barks", "."]
     * @param text A text to be tokenized
     * @return A list of word tokens
     */
    public List<String> words(String text) {
        Pattern multidot = Pattern.compile("(\\.{2,})$");
        for (int i = 0; i < sentenceEnd.length(); i++) {
            String sentence_end_p = String.valueOf(sentenceEnd.charAt(i));
            text = text.replaceAll(Pattern.quote(sentence_end_p), " " + sentence_end_p);
        }
        text = text.trim().replaceAll("\\s+", " ");
        String[] whitespace_tokens = text.split(" ");
        List<String> tokens = new ArrayList<String>();
        for (int i = 0; i < whitespace_tokens.length; i++) {
            String t = whitespace_tokens[i];
            ArrayList<String> first_tok = new ArrayList<String>();
            ArrayList<String> last_tok = new ArrayList<String>();
            boolean cont_first = true;
            while (cont_first) {
                cont_first = false;
                if (t.length() > 0 && wordStartPunct.contains(String.valueOf(t.charAt(0)))) {
                    cont_first = true;
                    first_tok.add(String.valueOf(t.charAt(0)));
                    t = t.substring(1);
                }
            }
            boolean cont_last = true;
            while (cont_last) {
                cont_last = false;
                if (t.length() > 0 && wordEndPuct.contains(String.valueOf(t.charAt(t.length() - 1)))) {
                    cont_last = true;

                    last_tok.add(0, String.valueOf(t.charAt(t.length() - 1)));
                    t = t.substring(0, t.length() - 1);
                } else if (t.length() > 1 && String.valueOf(t.charAt(t.length() - 1)).equals(".") && wordEndPuct.contains(String.valueOf(t.charAt(t.length() - 2)))) {
                    cont_last = true;
                    last_tok.add(0, String.valueOf(t.charAt(t.length() - 1)));
                    last_tok.add(0, String.valueOf(t.charAt(t.length() - 2)));
                    t = t.substring(0, t.length() - 2);
                }
            }
            Matcher m = multidot.matcher(t);

            if (m.find()) {
                //Make .. or ... or whaterver its own token
                String dots = m.group();

                last_tok.add(0, dots);
                t = t.substring(0, t.length() - dots.length());
            } else if (t.length() > 0 && String.valueOf(t.charAt(t.length() - 1)).equals(".")) {
                if (!endsInAbrv(t.substring(0, t.length() - 1))) {
                    t = t.substring(0, t.length() - 1);
                    last_tok.add(0, ".");
                }
            }
            boolean hasCustomPunct = false;
            for (int o = 0; o < customPunctuation.length(); o++) {
                if (t.contains(String.valueOf(customPunctuation.charAt(o)))) {
                    hasCustomPunct = true;
                    break;
                }
            }
            String[] t_tok;
            if ((t.contains("/") || t.contains("\\")) && !hasCustomPunct) {
                //not a link
                t = t.replaceAll(Pattern.quote("/"), " /").replaceAll(Pattern.quote("\\"), " \\");
                t_tok = t.split(" ");
            } else {
                t_tok = new String[]{t};
            }
            List<String> tt = new ArrayList<String>();
            for (int u = 0; u < t_tok.length; u++) {
                String ts = t_tok[u];
                if (ts.length() > 0) {
                    tt.add(ts);
                }
            }

            first_tok.addAll(tt);
            first_tok.addAll(last_tok);
            tokens.addAll(first_tok);
        }
        ArrayList<String> returnTokens = new ArrayList<String>();
        for (String tok : tokens) {
            if (tok.length() > 0) {
                returnTokens.add(tok);
            }
        }
        return returnTokens;
    }

    /**
     * Tokenizes a text on a sentence and word level, and removes extra whitespaces and line breaks
     * E.g. "A cat jumps. A dog barks." -> [["A", "cat", "jumps", "."],[ "A", "dog", "barks", "."]]
     * @param text Text to be tokenized
     * @return A list of sentences that are lists of word level tokens
     */
    public List<List<String>> tokenize(String text) {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> sents = sentences(text);
        for (String sent : sents) {
            result.add(words(sent));
        }
        return result;
    }

    private boolean endsInAbrv(String text) {
        Matcher matcher = abrvRegex.matcher(text);
        return matcher.find();
    }
}
