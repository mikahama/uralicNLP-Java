/*
 * (C) Mika Hämäläinen 2022 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

/**
 * An instance of a parsed word
 * @author mikahama
 */
public class Cg3Word {
    
    public String form;
    public String lemma;
    public String[] morphology;
    
    /**
     * Initializes Cg3Word
     * @param form form in the text
     * @param lemma lemma
     * @param morphology morphological reading
     */
    public Cg3Word(String form, String lemma, String[] morphology){
        this.form = form;
        this.lemma = lemma;
        this.morphology = morphology;
    }
    
    @Override
    public String toString(){
        return "<" + lemma + " - " + String.join(", ", morphology) + ">";
    }
    
}
