/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

/**
 *
 * @author mikahama
 */
public class UDRootNode extends UDNode{
    
    public UDRootNode(String id, String form, String lemma, String upostag, String xpostag, String feats, String misc){
        super(id, form, lemma, upostag, xpostag, feats, misc);
    }
}
