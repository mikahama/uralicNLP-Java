/*
* (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
* Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import com.sun.tools.javac.util.StringUtils;
import fi.seco.hfst.HfstOptimizedLookup;
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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mikahama
 */
public class UralicApi {

    private String modelPath;
    private String downloadServerUrl = "https://models.uralicnlp.com/nightly/";
    private HashMap<String, Transducer> transducerCache = new HashMap<>();

    public UralicApi() {
        modelPath = Paths.get(System.getProperty("user.home"), ".uralicnlp").toString();
    }

    public UralicApi(String modelPath) {
        this.modelPath = modelPath;
    }

    public void download(String language) {
        download(language, true);
    }

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

    public HashMap<String, Float> analyze(String word, String language) throws IOException {
        return analyze(word, language, true, false);
    }

    public HashMap<String, Float> analyze(String word, String language, boolean descriptive, boolean dictionaryForms) throws IOException {
        String modelName = getModelName(true, descriptive, dictionaryForms);
        Transducer t = loadTransducer(language, modelName);
        Collection<Result> analyses = t.analyze(word);
        return parseHfstResult(analyses);
    }

    public HashMap<String, Float> generate(String word, String language) throws IOException {
        return generate(word, language, false, false);
    }

    public HashMap<String, Float> generate(String word, String language, boolean descriptive, boolean dictionaryForms) throws IOException {
        String modelName = getModelName(false, descriptive, dictionaryForms);
        Transducer t = loadTransducer(language, modelName);
        Collection<Result> analyses = t.analyze(word);
        return parseHfstResult(analyses);
    }

    public ArrayList<String> lemmatize(String word, String language) throws IOException {
        return lemmatize(word, language, true, false);
    }

    public ArrayList<String> lemmatize(String word, String language, boolean descriptive, boolean dictionaryForms) throws IOException {
        ArrayList<String> results = new ArrayList<String>();
        HashMap<String, Float> res = analyze(word, language, descriptive, dictionaryForms);
        for (String r : res.keySet()) {
            results.add(r);
        }
        return results;
    }

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
