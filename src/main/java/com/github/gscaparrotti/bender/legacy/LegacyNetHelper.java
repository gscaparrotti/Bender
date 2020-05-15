package com.github.gscaparrotti.bender.legacy;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LegacyNetHelper {

    public static String getCurrentIP() {
        try {
            String addressesString = "<html>";
            final Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                final NetworkInterface n = networkInterfaces.nextElement();
                final Enumeration<InetAddress> inetAddresses = n.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    final InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.getHostAddress().startsWith("192.168") || inetAddress.getHostAddress().startsWith("10.0")) {
                        addressesString = addressesString.concat(inetAddress.getHostAddress() + "<br/>");
                    }
                }
            }
            addressesString += "</html>";
            return addressesString;
        } catch (SocketException e) {
            return "(IP Non Disponibile)";
        }
    }
}
