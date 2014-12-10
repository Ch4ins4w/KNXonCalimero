package com.calimero.knx.knxoncalimero;

import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxComparableObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxFloatObject;

import java.util.Observable;
import java.util.Observer;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 08.12.14.
 */
public class KnxCommunicationObject extends Observable implements Observer {
    private final KnxBusConnection knxBusConnection;
    private final Container taskContainer;
    private final Container resultContainer;

    public KnxCommunicationObject(String hostIp, String gatewayIp) throws Exception {
        taskContainer = new Container();
        resultContainer = new Container();
        knxBusConnection = new KnxBusConnection(hostIp, gatewayIp, taskContainer, resultContainer);
        resultContainer.addObserver(this);
        knxBusConnection.addObserver(this);
        Thread knxBusConnectionThread = new Thread(knxBusConnection);
        knxBusConnectionThread.start();
        if (knxBusConnection.isConnected()) {
            throw new Exception("Can not connect to GatewayIp: " + gatewayIp);
        }
    }

    public void writeBooleanToBus(GroupAddress groupAddress, boolean value) {
        taskContainer.push(new KnxBooleanObject(groupAddress, value, false));
        taskContainer.push(new KnxBooleanObject(groupAddress, true));
    }

    public boolean readBoolean(GroupAddress groupAddress) {
        taskContainer.push(new KnxBooleanObject(groupAddress, true));
        KnxComparableObject knxBoolean = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxBoolean != null && knxBoolean instanceof KnxBooleanObject) {
            return ((KnxBooleanObject)knxBoolean).getValue();
        } else {
            return false;
        }
    }

    public float readFloat(GroupAddress groupAddress) {
        taskContainer.push(new KnxFloatObject(groupAddress, true));
        KnxComparableObject knxFloat = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxFloat != null && knxFloat instanceof KnxBooleanObject ) {
            return ((KnxFloatObject)knxFloat).getValue();
        } else {
            return -1;
        }
    }

    public boolean isConnected() {
        return knxBusConnection.isConnected();
    }

    @Override
    public void update(Observable observable, Object data) {
        this.setChanged();
        this.notifyObservers(data);
    }
}
