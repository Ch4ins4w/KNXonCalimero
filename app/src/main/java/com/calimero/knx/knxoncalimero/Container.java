package com.calimero.knx.knxoncalimero;

import com.calimero.knx.knxoncalimero.knxobject.KnxComparableObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import tuwien.auto.calimero.GroupAddress;

public class Container extends Observable {
    private List<KnxComparableObject> objects = new LinkedList<KnxComparableObject>();

    public synchronized void push(KnxComparableObject object) {
        int index = objects.indexOf(object);
        if (index == -1) {
            objects.add(object);
            System.out.println("Push: Added: " + object);
            setChanged();
            notifyObservers();
        } else {
            if (object.compareTo(objects.get(index)) == 1) {
                KnxComparableObject removedObject = objects.remove(index);
                System.out.println("Push: Removed: " + removedObject);
                objects.add(object);
                System.out.println("Push: Added: " + object);
                setChanged();
                notifyObservers();
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
        System.out.println("Pop: Removed: " + object);
        notifyAll();
        return object;
    }

    public synchronized KnxComparableObject getByGroupAddress(GroupAddress groupAddress, boolean read) {
        int objectInd = objects.indexOf(new KnxComparableObject(groupAddress, read));
        KnxComparableObject object = objects.get(objectInd);
        System.out.println("GetByGroupAddress: Return: " + object);
        notifyAll();
        return object;
    }

    public synchronized boolean isEmpty() {
        return objects.isEmpty();
    }
}
