package com.calimero.knx.knxoncalimero;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

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

public class KnxBusConnection extends Thread {

    private final String gatewayIp;
    private final String hostIp;
    private KNXNetworkLinkIP netLinkIp = null;
    private ProcessCommunicator processCommunicator;

    public KnxBusConnection(String hostIp, String gatewayIp) {
        this.hostIp = hostIp;
        this.gatewayIp = gatewayIp;
    }

    @Override
    public void run() {
        boolean b;
        b = initBus(hostIp, gatewayIp);
        System.out.println(b);
    }

    private synchronized boolean initBus(String hostIp, String gatewayIp) {
        boolean result = false;
        try {
            netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, new InetSocketAddress(InetAddress.getByName(hostIp), 0), new InetSocketAddress(InetAddress.getByName(gatewayIp), KNXnetIPConnection.IP_PORT), false, new TPSettings(false));
            processCommunicator = new ProcessCommunicatorImpl(netLinkIp);
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

    public synchronized boolean writeToBus(GroupAddress groupAddress, boolean value) {
        boolean result = false;
        try {
            processCommunicator.write(groupAddress, value);
            result = true;
        } catch (KNXTimeoutException e) {
            System.out.println("KNXTimeoutException, writeToBus(" + value + ", " + groupAddress + ")");
            e.printStackTrace();
        } catch (KNXLinkClosedException e) {
            System.out.println("KNXLinkClosedException, writeToBus(" + value + ", " + groupAddress + ")");
            e.printStackTrace();
        }
        return result;
    }

    public synchronized boolean readBooleanFromBus(GroupAddress groupAddress) throws KNXException {
        boolean readBoolean = false;
        try {
            readBoolean = processCommunicator.readBool(groupAddress);
        } catch (KNXException e) {
            System.out.println("KNXException, readBooleanFromBus(" + groupAddress + ")");
            e.printStackTrace();
            throw e;
        }
        return readBoolean;
    }

    public synchronized float readFloatFromBus(GroupAddress groupAddress) throws KNXException {
        float readFloat = -1;
        try {
            readFloat = processCommunicator.readFloat(groupAddress);
        } catch (KNXException e) {
            System.out.println("KNXException, readFloatFromBus(" + groupAddress + ")");
            e.printStackTrace();
            throw e;
        }
        return readFloat;
    }

    public synchronized void closeBus() {
        netLinkIp.close();
    }
}
