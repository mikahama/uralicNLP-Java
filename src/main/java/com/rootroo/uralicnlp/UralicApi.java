/*
* (C) Mika Hämäläinen 2021-2022 CC BY-NC-ND 4.0
* Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import fi.seco.hfst.Transducer;
import fi.seco.hfst.Transducer.Result;
import fi.seco.hfst.TransducerAlphabet;
import fi.seco.hfst.TransducerHeader;
import fi.seco.hfst.UnweightedTransducer;
import fi.seco.hfst.WeightedTransducer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mikahama
 */
public class UralicApi {

    private String modelPath;
    private String downloadServerUrl = "https://models.uralicnlp.com/nightly/";
    private HashMap<String, Transducer> transducerCache = new HashMap<>();
 
    /**
     * Initializes UralicApi with the default path for models (~/.uralicnlp/)
     */
    public UralicApi() {
        modelPath = Paths.get(System.getProperty("user.home"), ".uralicnlp").toString();
    }

    /**
     * Initializes UralicApi with a custom path for models.
     * @param modelPath Path for downloading and loading models.
     */
    public UralicApi(String modelPath) {
        this.modelPath = modelPath;
    }

    /**
     * Downloads all the models for a given language and saves them in the user home directory or a custom path
     * This method does not throw exceptions if it fails. It's better to distribute the models with your application.
     * Note: not all languages have all the models, so some errors during the download are to be expected.
     * @param language ISO code of the language
     */
    public void download(String language) {
        download(language, true);
    }

    /**
     * Downloads all the models for a given language and saves them in the user home directory or a custom path
     * This method does not throw exceptions if it fails. It's better to distribute the models with your application.
     * Note: not all languages have all the models, so some errors during the download are to be expected.
     * @param language ISO code of the language
     * @param showProgress set to false to hide progress
     */
    public void download(String language, boolean showProgress) {
        HashMap<String, String> urlMap = new HashMap<String, String>();
        urlMap.put("analyser", "analyser-gt-desc.hfstol");
        urlMap.put("analyzer.pt", "../neural/" + language + "_analyzer_nmt-model_step_100000.pt");
        urlMap.put("generator.pt", "../neural/" + language + "_generator_nmt-model_step_100000.pt");
        urlMap.put("lemmatizer.pt", "../neural/" + language + "_lemmatizer_nmt-model_step_100000.pt");
        urlMap.put("analyser-norm", "analyser-gt-norm.hfstol");
        urlMap.put("analyser-dict", "analyser-dict-gt-norm.hfstol");
        urlMap.put("generator-desc", "generator-gt-desc.hfstol");
        urlMap.put("generator-norm", "generator-gt-norm.hfstol");
        urlMap.put("generator", "generator-dict-gt-norm.hfstol");
        urlMap.put("cg", "disambiguator.bin");
        urlMap.put("metadata.json", "metadata.json");
        urlMap.put("dictionary.json", "dictionary.json");

        Path languageFolder = Paths.get(modelPath, language);
        try {
            Files.createDirectories(languageFolder);
        } catch (IOException ex) {

        }

        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            String fileName = entry.getKey();
            String urlName = language + "/" + entry.getValue();
            System.out.println("Downloading model " + fileName + " for language " + language);
            try {
                CommonTools.downloadToFile(downloadServerUrl + urlName, Paths.get(languageFolder.toString(), fileName).toString(), showProgress);
            } catch (Exception ex) {
                System.out.println("Model wasn't downloaded, it may not exist for this language");
                if ("metadata.json".equals(fileName)) {
                    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(languageFolder.toString(), fileName), StandardCharsets.UTF_8)) {
                        writer.write("{\"info\":\"no metadata provided\"}");
                    } catch (IOException ex1) {

                    }
                }
            }
        }
    }

    /**
     * Checks if models of a language are installed in the system
     * @param language ISO code of the language
     * @return language installed
     */
    public boolean isLanguageInstalled(String language) {
        Path languageFolder = Paths.get(modelPath, language);
        return Files.exists(languageFolder);
    }

    private Transducer loadTransducer(String language, String filename) throws FileNotFoundException, IOException {
        String languageFolder = Paths.get(modelPath, language, filename).toString();
        if (!transducerCache.containsKey(languageFolder)) {
            FileInputStream transducerfile = new FileInputStream(languageFolder);
            DataInputStream charstream = new DataInputStream(transducerfile);
            TransducerHeader h = new TransducerHeader(charstream);
            TransducerAlphabet a = new TransducerAlphabet(charstream, h.getSymbolCount());
            Transducer transducer;
            
            if (h.isWeighted()) {
                transducer = new WeightedTransducer(charstream, h, a);
            } else {
                
                transducer = new UnweightedTransducer(charstream, h, a);
            }
            transducerCache.put(languageFolder, transducer);
        }
        return transducerCache.get(languageFolder);
    }

    /**
     * Prints out information of the model of a language
     * @param language ISO code of the language
     * @throws Exception metadata.json cannot be accessed
     */
    public void modelInfo(String language) throws Exception {
        Path languageFolder = Paths.get(modelPath, language, "metadata.json");
        FileInputStream fis = new FileInputStream(languageFolder.toString());
        InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);

        String str;
        while ((str = reader.readLine()) != null) {
            System.out.println(str);
        }
        fis.close();

    }

    /**
     * Uninstalls the models of a language
     * @param language ISO code of the language
     */
    public void uninstall(String language) {
        CommonTools.deleteDir(new File(Paths.get(modelPath, language).toString()));
    }
    

    

    private String getModelName(boolean analyzer, boolean descriptive, boolean dictionaryForms) {
        if (analyzer) {
            if (dictionaryForms) {
                return "analyser-dict";
            } else if (descriptive) {
                return "analyser";
            } else {
                return "analyser-norm";
            }
        } else {
            if (!descriptive && dictionaryForms) {
                return "generator";
            } else if (descriptive) {
                return "generator-desc";
            } else {
                return "generator-norm";
            }
        }
    }

    private HashMap<String, Float> parseHfstResult(Collection<Result> results) {
        HashMap<String, Float> res = new HashMap<String, Float>();
        for (Result result : results) {
            String text = "";
            for (String s : result.getSymbols()) {
                text += s;
            }
            res.put(text, result.getWeight());
        }
        return res;
    }

    /**
     * Analyzes a word morphologically
     * @param word a single word
     * @param language ISO code of the language
     * @return A HashMap where the keys are possible morphological readings and values are weights given by the model
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public HashMap<String, Float> analyze(String word, String language) throws IOException {
        return analyze(word, language, true, false);
    }

    /**
     * Analyzes a word morphologically
     * @param word a single word
     * @param language ISO code of the language
     * @param descriptive true -> descriptive model, false -> normative model
     * @param dictionaryForms true -> dictionary model
     * @return A HashMap where the keys are possible morphological readings and values are weights given by the model
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public HashMap<String, Float> analyze(String word, String language, boolean descriptive, boolean dictionaryForms) throws IOException {
        String modelName = getModelName(true, descriptive, dictionaryForms);
        Transducer t = loadTransducer(language, modelName);
        Collection<Result> analyses = t.analyze(word);
        return parseHfstResult(analyses);
    }

    /**
     * Inflects a word into a morphological form
     * @param word a lemma and its morphological tags (following the format of analyze)
     * @param language ISO code of the language
     * @return A HashMap where the keys are possible inflections and values are weights given by the model
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public HashMap<String, Float> generate(String word, String language) throws IOException {
        return generate(word, language, false, false);
    }

    /**
     * Inflects a word into a morphological form
     * @param word a lemma and its morphological tags (following the format of analyze)
     * @param language ISO code of the language
     * @param descriptive true -> descriptive model, false -> normative model
     * @param dictionaryForms true -> dictionary model
     * @return A HashMap where the keys are possible inflections and values are weights given by the model
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public HashMap<String, Float> generate(String word, String language, boolean descriptive, boolean dictionaryForms) throws IOException {
        String modelName = getModelName(false, descriptive, dictionaryForms);
        Transducer t = loadTransducer(language, modelName);
        Collection<Result> analyses = t.analyze(word);
        return parseHfstResult(analyses);
    }

    /**
     * Lemmatizes a word 
     * @param word a single word
     * @param language ISO code of the language
     * @return A list of lemmas
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public ArrayList<String> lemmatize(String word, String language) throws IOException {
        return lemmatize(word, language, true, false, false);
    }

    /**
     * Lemmatizes a word 
     * @param word a single word
     * @param language ISO code of the language
     * @param wordBoundaries set true to mark word boundaries in compound words with a pipe (|)
     * @return A list of lemmas
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public ArrayList<String> lemmatize(String word, String language, boolean wordBoundaries) throws IOException {
        return lemmatize(word, language, true, false, wordBoundaries);
    }

    /**
     * Lemmatizes a word 
     * @param word a single word
     * @param language ISO code of the language
     * @param descriptive true -> descriptive model, false -> normative model
     * @param dictionaryForms true -> dictionary model
     * @param wordBoundaries set true to mark word boundaries in compound words with a pipe (|)
     * @return A list of lemmas
     * @throws IOException Fails if the models are not downloaded or the transducers are not in a supported format
     */
    public ArrayList<String> lemmatize(String word, String language, boolean descriptive, boolean dictionaryForms, boolean wordBoundaries) throws IOException {
        ArrayList<String> results = new ArrayList<String>();
        HashMap<String, Float> res = analyze(word, language, descriptive, dictionaryForms);

        String bound = "";
        if (wordBoundaries) {
            bound = "|";
        }

        for (String an : res.keySet()) {
            String lemma;
            if (language.equals("swe")) {
                lemma = an.replaceAll("[<].*?[>]", bound);
                while (lemma.length() > 0 && bound.length() >0 && bound.charAt(0) == lemma.charAt(lemma.length() - 1)) {
                    lemma = lemma.substring(0, lemma.length() - 1);
                }
            } else if (language.equals("ara")) {
                lemma = StringProcessing.filterArabic(an, true, bound);
            } else if (language.equals("fin_hist")) {
                String rege = "(?<=WORD_ID=)[^\\]]*";
                List<String> allMatches = new ArrayList<String>();
                Matcher matches;
                matches = Pattern.compile(rege)
                        .matcher(an);
                while (matches.find()) {
                    allMatches.add(matches.group());
                }
                lemma = String.join(bound, allMatches);
            }else if(an.contains("<") && an.contains(">")){
                //apertium
                String[] parts = an.split("\\+");
                //lemma = bound.join([x.split("<")[0] for x in parts])
                List<String> lemmaParts = new ArrayList<String>();
                for(int i =0; i< parts.length; i++){
                    lemmaParts.add(parts[i].split("<")[0]);
                }
                lemma = String.join(bound, lemmaParts);
            }else{
                if(an.contains("#") && !an.contains("+Cmp#")){
                    an = an.replaceAll("#", "+Cmp#");
                }
                String[] parts = an.split("\\+Cmp#");
                List<String> lemmaParts = new ArrayList<String>();
                for(int i =0; i< parts.length; i++){
                    String p = parts[i].split("\\+")[0];
                    if(language.equals("eng")){
                        p = p.replaceAll("[\\[].*?[\\]]", "");
                    }
                    lemmaParts.add(p);
                }
                lemma = String.join(bound, lemmaParts);
                
            }
            results.add(lemma);
        }

        return new ArrayList<String>(
      new LinkedHashSet<String>(results));
    }

    /**
     * Downloads a list of supported languages and prints it, while all of them work on Python, there might be some compatibility issues with Java
     * @throws IOException Fails if it cannot download the list
     */
    public void supportedLanguages() throws IOException {
        String s = CommonTools.readToString(downloadServerUrl + "supported_languages.json");
        System.out.println(s);
    }
    /*
    public String[] dictionarySearch(String word, String language) {
    }

    public String[] dictionaryLemmas(String language) {
    }
     */
}
