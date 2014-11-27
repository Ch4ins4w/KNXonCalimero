package com.calimero.knx.knxoncalimero.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 27.11.14.
 */
public class KnxFloatObject extends KnxComparableObject {
    private float value;

    public KnxFloatObject(GroupAddress groupAddress) {
        super(groupAddress);
    }

    public KnxFloatObject(GroupAddress groupAddress, float value) {
        super(groupAddress);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
