package com.passer.simpletransfer.utils;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

/**
 * <p>网络相关工具类</p>
 * <p>创建时间：2023/2/20</p>
 *
 * @author hj
 */
public class NetworkUtils {

    /**
     * 获取本机实际使用的 IPV4 地址
     * @return Optional<Inet4Address>
     * @throws SocketException socket 异常
     */
    public static Optional<Inet4Address> getLocalIp4Address() throws SocketException {
        final List<Inet4Address> ipByNi = getLocalIp4AddressFromNetworkInterface();
        if (ipByNi.size() != 1) {
            final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
            if (ipBySocketOpt.isPresent()) {
                return ipBySocketOpt;
            } else {
                return ipByNi.isEmpty() ? Optional.empty() : Optional.of(ipByNi.get(0));
            }
        }
        return Optional.of(ipByNi.get(0));
    }

    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface n = e.nextElement();
            if (!isValidInterface(n)) {
                continue;
            }
            Enumeration<InetAddress> ee = n.getInetAddresses();
            while (ee.hasMoreElements()) {
                InetAddress i = ee.nextElement();
                if (isValidAddress(i)) {
                    addresses.add((Inet4Address) i);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth、ens或wlan开头
     *
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens") || ni.getName().startsWith("wlan"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址.
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }


    public static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

}
