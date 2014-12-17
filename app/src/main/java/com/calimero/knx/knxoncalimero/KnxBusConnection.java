package com.calimero.knx.knxoncalimero;


import com.calimero.knx.knxoncalimero.knxobject.KnxBooleanObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxComparableObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxControlObject;
import com.calimero.knx.knxoncalimero.knxobject.KnxFloatObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Observable;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class KnxBusConnection extends Observable implements Runnable {

    private final String gatewayIp;
    private final String hostIp;
    private final int port;
    private final Container busActionContainer, resultContainer;
    private KNXNetworkLinkIP netLinkIp = null;
    private ProcessCommunicator processCommunicator;
    private boolean connected;

    public KnxBusConnection(String hostIp, String gatewayIp, int port, Container busActionContainer, Container resultContainer) {
        this.hostIp = hostIp;
        this.gatewayIp = gatewayIp;
        this.port = port;
        this.busActionContainer = busActionContainer;
        this.resultContainer = resultContainer;
    }

    @Override
    public void run() {
        initBus();
        if (isConnected()) {
            System.out.println("Verbindung erfolgreich aufgebaut");
        } else {
            System.out.println("Verbindung konnte nicht aufgebaut werden");
        }
        KnxComparableObject object;
        while (isConnected()) {
            object = busActionContainer.pop();
            if (object.isRead()) {
                if (object instanceof KnxFloatObject) {
                    System.out.println("Reading Float from Bus: " + object);
                    float read = readFloatFromBus(object.getGroupAddress());
                    ((KnxFloatObject) object).setValue(read);
                } else if (object instanceof KnxBooleanObject) {
                    System.out.println("Reading Boolean from Bus: " + object);
                    boolean read = readBooleanFromBus(object.getGroupAddress());
                    ((KnxBooleanObject) object).setValue(read);
                } else if (object instanceof KnxControlObject) {
                    System.out.println("Reading Control from Bus: " + object);
                    byte read = readControlFromBus(object.getGroupAddress());
                    ((KnxControlObject) object).setValue(read);
                }

                resultContainer.push(object);
            } else {
                if (object instanceof KnxBooleanObject) {
                    System.out.println("Writing Boolean to Bus: " + object);
                    writeBooleanToBus(object.getGroupAddress(), ((KnxBooleanObject) object).getValue());
                }
            }
        }
        closeBus();
    }

    private synchronized boolean initBus() {
        boolean result = false;
        try {
            netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, new InetSocketAddress(InetAddress.getByName(hostIp), 0), new InetSocketAddress(InetAddress.getByName(gatewayIp), port), false, new TPSettings(false));
            processCommunicator = new ProcessCommunicatorImpl(netLinkIp);
            processCommunicator.setResponseTimeout(1);
            result = true;
        } catch (KNXLinkClosedException e) {
            System.out.println("KNXLinkClosedException, initBus(" + hostIp + ", " + gatewayIp + ")");
            e.printStackTrace();
        } catch (KNXFormatException e) {
            System.out.println("KNXFormatException, initBus(" + hostIp + ", " + gatewayIp + ")");
            e.printStackTrace();
        } catch (KNXException e) {
            System.out.println("KNXException, initBus(" + hostIp + ", " + gatewayIp + ")");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException, initBus(" + hostIp + ", " + gatewayIp + ")");
            e.printStackTrace();
        }
        return result;
    }

    private synchronized boolean writeBooleanToBus(GroupAddress groupAddress, boolean value) {
        boolean result = false;
        try {
            processCommunicator.write(groupAddress, value);
            result = true;
        } catch (KNXTimeoutException e) {
            System.out.println("KNXTimeoutException, writeBooleanToBus(" + value + ", " + groupAddress + ")");
            e.printStackTrace();
        } catch (KNXLinkClosedException e) {
            System.out.println("KNXLinkClosedException, writeBooleanToBus(" + value + ", " + groupAddress + ")");
            e.printStackTrace();
        }
        return result;
    }

    private synchronized boolean readBooleanFromBus(GroupAddress groupAddress) {
        boolean readBoolean = false;
        try {
            readBoolean = processCommunicator.readBool(groupAddress);
        } catch (KNXException e) {
            System.out.println("KNXException, readBooleanFromBus(" + groupAddress + ")");
            e.printStackTrace();
        }
        return readBoolean;
    }

    private synchronized float readFloatFromBus(GroupAddress groupAddress) {
        float readFloat = -1;
        try {
            readFloat = processCommunicator.readFloat(groupAddress);
        } catch (KNXException e) {
            System.out.println("KNXException, readFloatFromBus(" + groupAddress + ")");
            e.printStackTrace();
        }
        return readFloat;
    }

    private synchronized byte readControlFromBus(GroupAddress groupAddress) {
        byte readControl = -1;
        try {
            readControl = processCommunicator.readControl(groupAddress);
        } catch (KNXException e) {
            System.out.println("KNXException, readControlFromBus(" + groupAddress + ")");
            e.printStackTrace();
        }
        return readControl;
    }

    private synchronized void closeBus() {
        if (netLinkIp != null) {
            netLinkIp.close();
            netLinkIp = null;
        }
        setConnected(false);
    }

    public synchronized boolean isConnected() {
        boolean returnVal = false;
        if (netLinkIp != null) {
            returnVal = netLinkIp.isOpen();
        }
        setConnected(returnVal);
        return returnVal;
    }

    private void setConnected(boolean connected) {
        if (connected != this.connected) {
            this.setChanged();
        }
        System.out.println("setConnected: " + connected);
        this.connected = connected;
        this.notifyObservers();
    }
}