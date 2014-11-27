package com.calimero.knx.knxoncalimero;

import com.calimero.knx.knxoncalimero.knxobject.KnxComparableObject;

import java.util.LinkedList;
import java.util.List;

import tuwien.auto.calimero.GroupAddress;

public class Container {
    private List<KnxComparableObject> objects = new LinkedList<KnxComparableObject>();

    public synchronized void push(KnxComparableObject object) {
        int index = objects.indexOf(object);
        if (index == -1) {
            objects.add(object);
            System.out.println("Added: " + object);
        } else {
            if (object.compareTo(objects.get(index)) == 1) {
                System.out.println("Removed: " + objects.remove(index));
                objects.add(object);
                System.out.println("Added: " + object);
            }
        }
        notifyAll();
    }

    public synchronized KnxComparableObject pop() {
        while (objects.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        KnxComparableObject object = objects.remove(0);
        notifyAll();
        return object;
    }

    public synchronized KnxComparableObject getByGroupAdress(GroupAddress groupAddress) {
        int objectInd = objects.indexOf(new KnxComparableObject(groupAddress));
        KnxComparableObject object = objects.get(objectInd);
        notifyAll();
        return object;
    }

    public synchronized boolean isEmpty() {
        return objects.isEmpty();
    }
}