package com.rootroo.uralicnlp;

import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        UralicApi api = new UralicApi();
        //api.download("fra");
        //api.download("spa");
        //api.download("swe");
        //api.download("eng");
        //api.download("fin_hist");
        try{
            HashMap<String, Float> results = api.generate("voida+V+Act+Ind+Prt+Sg1", "fin");
            for(String s : results.keySet()){
                System.out.println(s);
            }
            System.out.println(api.lemmatize("voin", "fin"));
            System.out.println(api.lemmatize("luutapiiri", "fin"));
            //System.out.println(api.lemmatize("chiens", "fra"));
            System.out.println(api.lemmatize("luutapiiri", "fin_hist",true));
            //System.out.println(api.lemmatize("como", "spa",true));
            System.out.println(api.lemmatize("hundvalp", "swe",true));
            //System.out.println(api.lemmatize("cats", "eng"));
        }catch (Exception e){
            System.out.println(e);
        }
        
    }
}
