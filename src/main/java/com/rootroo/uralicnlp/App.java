package com.rootroo.uralicnlp;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        UralicApi api = new UralicApi();
        //api.download("fra");
        //api.download("spa");
        //api.download("swe");
        //api.download("eng");
        //api.download("fin_hist");
        try {
            /*String conl = "# text = Toinen palkinto\n1\tToinen\ttoinen\tADJ\tNum\tCase=Nom\t2\tnummod\t_\t_\n2\tpalkinto\tpalkinto\tNOUN\tN\tCase=Nom\t0\troot\t_\t_";
            UDSentence sentence = UDTools.parseSentence(conl);
            for(UDNode word : sentence){
                System.out.println(word.lemma + " " + word.pos + " " + word.deprelName());
            }
            
            FileInputStream fis = new FileInputStream("sms_giellagas-ud-test.conllu");
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            UDCollection udCollection = new UDCollection(reader);
            for(UDSentence sentence : udCollection){
                for(UDNode word : sentence){
                    System.out.println(word.lemma + " " + word.pos + " " + word.deprelName());
                }
                System.out.println("---");
            }
             */
 /*
            Cg3 cg = new Cg3("fin");
            Tokenizer tokenizer = new Tokenizer();
            String sentence = "Kissa voi nauraa";
            List<String> tokens = tokenizer.words(sentence);
            ArrayList<ArrayList<Cg3Word>> disambiguatedSentence = cg.disambiguate(tokens);
            for(ArrayList<Cg3Word> wordReadings : disambiguatedSentence){
                for(Cg3Word wordReading :wordReadings){
                    System.out.println("Form: " + wordReading.form + " lemma " + wordReading.lemma + " morphology: " + String.join(", ", wordReading.morphology));
                }
                System.out.println("---");
            }
            
            
            
            sentence = "Mr. Burns talks with Dr. Hibbert. But why?";
            System.out.println(tokenizer.sentences(sentence));
            System.out.println(tokenizer.words(sentence));
            System.out.println(tokenizer.tokenize(sentence));
            
            HashMap<String, Float> results = api.generate("voida+V+Act+Ind+Prt+Sg1", "fin");
            for(String s : results.keySet()){
                System.out.println(s);
            }
             */
            System.out.println(api.lemmatize("voin", "fin"));
            System.out.println(api.lemmatize("luutapiiri", "fin"));
            //System.out.println(api.lemmatize("chiens", "fra"));
            System.out.println(api.lemmatize("luutapiiri", "fin_hist", true));
            //System.out.println(api.lemmatize("como", "spa",true));
            System.out.println(api.lemmatize("hundvalp", "swe", true));
            //System.out.println(api.lemmatize("cats", "eng"));

            StringProcessing st = new StringProcessing();
            System.out.println(st.isoToName("fin"));

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
