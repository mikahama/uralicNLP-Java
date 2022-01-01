/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mikahama
 */
public class Cg3 {

    String morphologyLanguages;
    String cgPath;
    String language;
    UralicApi api;

    public Cg3(String language, String morphologyLanguages) {
        init(language, morphologyLanguages);
    }

    private void init(String language, String morphologyLanguages) {
        this.api = new UralicApi();
        this.morphologyLanguages = morphologyLanguages;

        cgPath = Paths.get(System.getProperty("user.home"), ".uralicnlp", "cg").toString();

        this.language = language;
    }

    public Cg3(String language) {

        init(language, language);
    }
    
    public ArrayList<ArrayList<Cg3Word>> disambiguate(String[] words) throws IOException{
        return disambiguate(words,null,true,true,null,false,null);
    }

    public ArrayList<ArrayList<Cg3Word>> disambiguate(String[] words, String morphology_ignore_after, boolean descriptive, boolean remove_symbols, String temp_file, boolean language_flags, List<HashMap<String, Float>> morphologies) throws IOException {
        List<String> wordsA = new ArrayList<String>(Arrays.asList(words));
        wordsA.add("");
        String hfst_output = parseSentence(wordsA.toArray(new String[0]), morphologyLanguages, morphology_ignore_after, descriptive, remove_symbols, language_flags, morphologies);
        ProcessBuilder pb;
        if (temp_file == null) {
            List<String> commands = new ArrayList<String>();
            commands.add("echo");
            commands.add(hfst_output);
            pb = new ProcessBuilder(commands);

        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(temp_file), StandardCharsets.UTF_8)) {
                writer.write(hfst_output);
            }
            List<String> commands = new ArrayList<String>();
            commands.add("cat");
            commands.add(temp_file);
            pb = new ProcessBuilder(commands);

        }
        List<String> commands = new ArrayList<String>();
        commands.add("cg-conv");
        commands.add("-f");
        ProcessBuilder cg_conv = new ProcessBuilder(commands);

        List<String> vcommands = new ArrayList<String>();
        vcommands.add("vislcg3");
        vcommands.add("--grammar");
        vcommands.add(cgPath);
        ProcessBuilder vislcg3 = new ProcessBuilder(vcommands);
        List builders = Arrays.asList(pb, cg_conv, vislcg3);
        List<Process> processes = ProcessBuilder.startPipeline(builders);

        Process last = processes.get(processes.size() - 1);

        int BUFFER_SIZE = 1024;
        String cg_results = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(last.getInputStream()), BUFFER_SIZE);
        String str;
        while ((str = br.readLine()) != null) {
            cg_results += str;
        }

        return parseCgResults(cg_results);
    }

    private String parseSentence(String[] words, String language, String morphology_ignore_after, boolean descriptive, boolean remove_symbols, boolean language_flags, List<HashMap<String, Float>> words_analysis) throws IOException {
        List<String> sentence = new ArrayList<String>();
        if (words_analysis != null && words_analysis.size() < words.length) {
            HashMap<String, Float> e = new HashMap<String, Float>();
            words_analysis.add(e);
        }

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            HashMap<String, Float> existingAnalysis = null;
            if (words_analysis != null) {
                existingAnalysis = words_analysis.get(i);
            }
            List<String> analysis = HfstFormat(word, language, morphology_ignore_after, descriptive, remove_symbols, language_flags, existingAnalysis);

            sentence.addAll(analysis);
        }
        String hfst_result_string = String.join("\n", sentence);
        return hfst_result_string;
    }

    private List<String> HfstFormat(String word, String language, String morphology_ignore_after, boolean descriptive, boolean remove_symbols, boolean language_flags, HashMap<String, Float> analysis) throws IOException {
        if (analysis == null) {
            analysis = api.analyze(word, language, descriptive, false);
        }
        List<String> hfsts = new ArrayList<String>();
        if (analysis.size() == 0) {
            hfsts.add(word + "\t" + word + "+?\tinf");
        }
        for (Map.Entry<String, Float> entry : analysis.entrySet()) {
            String a;
            if (morphology_ignore_after == null) {
                a = entry.getKey();
            } else {
                a = entry.getKey().split(morphology_ignore_after)[0];
            }
            hfsts.add(word + "\t" + a + "\t" + entry.getValue().toString());
        }
        hfsts.add("");
        return hfsts;
    }   

    private ArrayList<ArrayList<Cg3Word>> parseCgResults(String cg_results) {
        String[] lines = cg_results.split("\n");
        ArrayList<ArrayList<Cg3Word>> results = new ArrayList<ArrayList<Cg3Word>>();
        String current_word = null;
        ArrayList<Cg3Word> current_list = new ArrayList<Cg3Word>();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.startsWith("\"<")) {
                if (current_word != null) {
                    results.add(current_list);
                }
                current_word = line.substring(2, line.length() - 2);
                current_list = new ArrayList<Cg3Word>();
            } else if (line.startsWith("\t")) {
                line = line.substring(2);

                String[] parts = line.split("\" ", 2);
                if (parts.length < 2) {
                    continue;
                }
                Cg3Word w = new Cg3Word(current_word, parts[0], parts[1].split(" "));
                current_list.add(w);
            }

        }
        return results;
    }
}
