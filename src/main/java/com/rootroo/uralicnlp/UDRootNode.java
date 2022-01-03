/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

/**
 * A root node for attaching a regular root UDNode to a UDSentence
 * @author mikahama
 */
public class UDRootNode extends UDNode{
    
    /**
     * UD features
     * @param id ID
     * @param form form
     * @param lemma lemma
     * @param upostag universal pos tag
     * @param xpostag language specific pos tag
     * @param feats features
     * @param misc miscellaneous information
     */
    public UDRootNode(String id, String form, String lemma, String upostag, String xpostag, String feats, String misc){
        super(id, form, lemma, upostag, xpostag, feats, misc);
    }
}
