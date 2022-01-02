/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.Comparator;

/**
 *
 * @author mikahama
 */
public class UDNodeComparator implements Comparator<UDNode>{
    
    @Override
    public int compare(UDNode self, UDNode other) {
        if(self.equals(other)){
        return 0;
        }
        if(self.lt(other)){
        return -1;
        }else{
        return 1;
        }
    }
    
}
