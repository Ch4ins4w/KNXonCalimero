package com.calimero.knx.knxoncalimero.knxobject;


import java.util.Date;

import tuwien.auto.calimero.GroupAddress;

public class KnxComparableObject implements Comparable<KnxComparableObject> {
    private final Date createDate;
    private final GroupAddress groupAddress;
    private final boolean read;

    public KnxComparableObject(GroupAddress groupAddress, boolean read) {
        this.createDate = new Date();
        this.groupAddress = groupAddress;
        this.read = read;
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
        return o instanceof KnxComparableObject && ((KnxComparableObject) o).groupAddress.equals(this.groupAddress) && ((KnxComparableObject) o).isRead() == this.isRead();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "Date: " + createDate + " GroupAddress: " + groupAddress + " isRead: " + read;
    }
}

