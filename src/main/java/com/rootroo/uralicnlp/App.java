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
        //api.download("fin");
        try{
            HashMap<String, Float> results = api.analyze("voin", "fin");
            for(String s : results.keySet()){
                System.out.println(s);
            }
        }catch (Exception e){
        
        }
    }
}
