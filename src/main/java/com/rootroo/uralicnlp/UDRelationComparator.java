/*
 * (C) Mika Hämäläinen 2022 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

import java.util.Comparator;

/**
 * A comparator between two UDRelation objects
 * @author mikahama
 */
public class UDRelationComparator implements Comparator<UDRelation> {

    @Override
    public int compare(UDRelation self, UDRelation other) {
        if (self.equals(other)) {
            return 0;
        }
        if (self.node.id.contains(".")) {
            String[] splits = self.node.id.split(".");
            String main_index = splits[0];
            String second_index = splits[1];
            if (other.node.id.contains(".")) {
                String[] otherSplits = other.node.id.split(".");
                String main_index_o = otherSplits[0];
                String second_index_o = otherSplits[1];
                if (main_index_o.equals(main_index)) {
                    if (Integer.parseInt(second_index) < Integer.parseInt(second_index_o)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            } else if (main_index.equals(other.node.id)) {
                return 1;
            }
        }
        boolean s_dash = false;
        float s_id;
        if (self.node.id.contains("-")) {
            s_dash = true;
            s_id = Integer.parseInt(self.node.id.split("-")[0]);
        } else {
            s_id = Float.parseFloat(self.node.id);
        }
        boolean o_dash = false;
        float o_id;
        if (other.node.id.contains("-")) {
            o_dash = true;
            o_id = Integer.parseInt(other.node.id.split("-")[0]);
        } else {
            o_id = Float.parseFloat(other.node.id);
        }
        if (o_id == s_id) {
            if (s_dash) {
                return -1;
            }
            return 1;
        } else {
            if (s_id < o_id) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
