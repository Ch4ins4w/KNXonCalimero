package com.calimero.knx.knxoncalimero;


import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXFormatException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

public class KnxBusConnection {

    private KNXNetworkLinkIP netLinkIp = null;
    private ProcessCommunicator pc;

    /**
     * Stellt eine Verbindung zum Bus her
     *
     * @param host    Die lokale IP
     * @param gateway Die IP des Gatewaysto
     * @return "Verbunden" oder eine Fehlermeldung
     */
    public String busInit(String host, String gateway) {
        String output = null;
        Writer fw = null;
        try {
            System.out.println();
            netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, new InetSocketAddress(InetAddress.getByName(host), 0), new InetSocketAddress(InetAddress.getByName(gateway), KNXnetIPConnection.IP_PORT), false, new TPSettings(false));
            pc = new ProcessCommunicatorImpl(netLinkIp);
            netLinkIp.addLinkListener(new NetworkLinkListener() {
                @Override
                public void confirmation(FrameEvent frameEvent) {
                    System.out.println(frameEvent);
                }

                @Override
                public void indication(FrameEvent frameEvent) {
                    System.out.println(frameEvent);
                }

                @Override
                public void linkClosed(CloseEvent closeEvent) {
                    System.out.println(closeEvent);
                }
            });
        } catch (KNXLinkClosedException e) {
            output = "Fehler beim Verbinden (Link closed) " + e.getMessage();
        } catch (KNXFormatException e) {
            output = "Fehler beim Verbinden (KNX Format) " + e.getMessage();
        } catch (KNXException e) {
            output = "Fehler beim Verbinden (KNX Exception) " + e.getMessage();
            for (StackTraceElement x : e.getStackTrace()) {
                System.out.println("" + x.toString() + x.getLineNumber());
            }
            System.out.println("Host = " + host + " Gateway ist = " + gateway);
            System.out.println("Fehlermeldung: " + e.getMessage());
        } catch (UnknownHostException e) {
            output = "Fehler beim Verbinden (Host unknown) " + e.getMessage();
        }
        if (output == null) {
            output = "Verbunden";
        }
        if (fw != null) {
            try {
                fw.close();
            } catch (Exception e) {
            }
        }
        return output;
    }

    public String writeSoTH() {
        try {
            pc.write(new GroupAddress(0, 0, 1), true);
        } catch (KNXTimeoutException e) {
            e.printStackTrace();
        } catch (KNXLinkClosedException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Schließt die Busverbindung
     *
     * @return Die Meldung, dass die Verbindung getrennt wurde
     */
    public String busClose() {
        netLinkIp.close();
        return "Verbindung getrennt.";
    }
}
