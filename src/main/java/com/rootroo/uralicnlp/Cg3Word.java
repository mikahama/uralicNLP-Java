/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

/**
 *
 * @author mikahama
 */
public class Cg3Word {
    
    public String form;
    public String lemma;
    public String[] morphology;
    
    public Cg3Word(String form, String lemma, String[] morphology){
        this.form = form;
        this.lemma = lemma;
        this.morphology = morphology;
    }
    
    public String toString(){
        return "<" + lemma + " - " + String.join(", ", morphology) + ">";
    }
    
}
