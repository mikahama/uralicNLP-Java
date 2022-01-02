/*
 * (C) Mika Hämäläinen 2021 CC BY-NC-ND 4.0
 * Full license https://creativecommons.org/licenses/by-nc-nd/4.0/legalcode
 */
package com.rootroo.uralicnlp;

/**
 *
 * @author mikahama
 */
class UDRelation {

    String relation;
    UDNode node;
    UDNode head;
    boolean primary;

    public UDRelation(UDNode node, String relation, UDNode head) {
        init(node, relation, head, true);
    }

    public UDRelation(UDNode node, String relation, UDNode head, boolean primary) {
        init(node, relation, head, primary);
    }

    private void init(UDNode node, String relation, UDNode head, boolean primary) {
        this.node = node;
        this.head = head;
        this.relation = relation;
        if (primary) {
            if (head != null) {
                head.children.add(this);
            }
            node.head = this;
        } else {
            head.secondaryChildren.add(this);
            node.heads.add(this);
        }
        this.primary = primary;
    }

    @Override
    public String toString() {
        return head.id + ":" + relation;
    }

    @Override
    public boolean equals(Object u) {
        return this.toString().equals(u.toString());
    }
}
