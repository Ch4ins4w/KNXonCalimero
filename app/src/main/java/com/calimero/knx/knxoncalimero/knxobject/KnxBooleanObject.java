package com.calimero.knx.knxoncalimero.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 27.11.14.
 */
public class KnxBooleanObject extends KnxComparableObject {
    private boolean value;

    public KnxBooleanObject(GroupAddress groupAddress) {
        super(groupAddress);
    }

    public KnxBooleanObject(GroupAddress groupAddress, boolean value) {
        super(groupAddress);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
