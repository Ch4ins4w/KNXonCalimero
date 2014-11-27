package com.calimero.knx.knxoncalimero.knxobject;


import java.util.Date;

import tuwien.auto.calimero.GroupAddress;

public class KnxComparableObject implements Comparable<KnxComparableObject> {
    private final Date createDate;
    private final GroupAddress groupAddress;

    public KnxComparableObject(GroupAddress groupAddress) {
        this.createDate = new Date();
        this.groupAddress = groupAddress;
    }

    @Override
    public int compareTo(KnxComparableObject another) {
        int out = 0;
        if (this.createDate.before(another.createDate)) {
            out = -1;
        } else if (this.createDate.after(another.createDate)) {
            out = 1;
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof KnxComparableObject && ((KnxComparableObject) o).groupAddress.equals(this.groupAddress);
    }

    public Date getCreateDate() {
        return createDate;
    }

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }
}
