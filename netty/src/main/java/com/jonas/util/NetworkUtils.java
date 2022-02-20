package com.jonas.util;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author zhongxiaofeng
 * @createTime 2021/4/1 15:14
 */
public class NetworkUtils {

    private final static String LOCAL_IP = "127.0.0.1";

    // 测试代码
    @Deprecated
    public static InetAddress getLocalInet() throws SocketException {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("bond1");
            Enumeration<?> cardipaddress = networkInterface.getInetAddresses();
            while(cardipaddress.hasMoreElements()){
                InetAddress ip = (InetAddress) cardipaddress.nextElement();
                if(!ip.isLoopbackAddress()){
                    if(ip.getHostAddress().equalsIgnoreCase("127.0.0.1")){
                        continue;
                    }
                    if(ip instanceof Inet6Address)  {
                        continue;
                    }
                    if(ip instanceof Inet4Address)  {
                        return ip;
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
        return null;
    }

    // 测试代码
    @Deprecated
    public static InetAddress getLocalAddress(){
        Enumeration<?> netInterfaces;
        List<NetworkInterface> netlist = new ArrayList<NetworkInterface>();
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni=(NetworkInterface)netInterfaces.nextElement();
                if(ni.isLoopback()) {
                    continue;
                }
                netlist.add(0,ni);
            }

            for(NetworkInterface list:netlist) {
                Enumeration<?> cardipaddress = list.getInetAddresses();
                while(cardipaddress.hasMoreElements()){
                    InetAddress ip = (InetAddress) cardipaddress.nextElement();
                    if(!ip.isLoopbackAddress()){
                        if(ip.getHostAddress().equalsIgnoreCase("127.0.0.1")){
                            continue;
                        }
                        if(ip instanceof Inet6Address)  {
                            continue;
                        }
                        if(ip instanceof Inet4Address)  {
                            if (ip.getHostAddress().startsWith("10.")) {
                                return ip;
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地所有的网卡设备
     */
    public static List<NetworkInterface> getNetworkInterfaces() throws Exception {
        List<NetworkInterface> localIPlist = new ArrayList<NetworkInterface>();
        Enumeration<NetworkInterface> interfs =
                NetworkInterface.getNetworkInterfaces();
        if (interfs == null) {
            return new ArrayList<>();
        }
        while (interfs.hasMoreElements()) {
            NetworkInterface interf = interfs.nextElement();
            Enumeration<InetAddress> addres = interf.getInetAddresses();
            while (addres.hasMoreElements()) {
                InetAddress in = addres.nextElement();
                if (in instanceof Inet4Address) {
                    if (!LOCAL_IP.equals(in.getHostAddress())){
                        localIPlist.add(interf);
                    }
                }
            }
        }
        return localIPlist;
    }


    /*验证IP是否属于某个IP段
     *
     * ipSection    IP段（以'-'分隔）
     * ip           所验证的IP号码
     *
     */
    public static boolean ipExistsInRange(String ip,String ipSection) {
        ipSection = ipSection.trim();
        ip = ip.trim();
        int idx = ipSection.indexOf('-');
        String beginIP = ipSection.substring(0, idx);
        String endIP = ipSection.substring(idx + 1);
        return getIp2long(beginIP)<=getIp2long(ip) &&getIp2long(ip)<=getIp2long(endIP);
    }

    public static long getIp2long(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip2long = 0L;
        for (int i = 0; i < 4; ++i) {
            ip2long = ip2long << 8 | Integer.parseInt(ips[i]);
        }
        return ip2long;
    }

    public static long getIp2long2(String ip) {
        ip = ip.trim();
        String[] ips = ip.split("\\.");
        long ip1 = Integer.parseInt(ips[0]);
        long ip2 = Integer.parseInt(ips[1]);
        long ip3 = Integer.parseInt(ips[2]);
        long ip4 = Integer.parseInt(ips[3]);
        long ip2long =1L* ip1 * 256 * 256 * 256 + ip2 * 256 * 256 + ip3 * 256 + ip4;
        return ip2long;
    }

    /*
     *比较IP大小
     */
    public static int compareIpV4s(String ip1,String ip2) {
        int result = 0;
        int ipValue1 = getIpV4Value(ip1);
        int ipValue2 = getIpV4Value(ip2);
        if(ipValue1 > ipValue2) {
            result =  1;
        } else if(ipValue1 < ipValue2) {
            result = -1;
        }
        return result;
    }

    public static int getIpV4Value(String ipOrMask) {
        byte[] addr = getIpV4Bytes(ipOrMask);
        int address1  = addr[3] & 0xFF;
        address1 |= ((addr[2] << 8) & 0xFF00);
        address1 |= ((addr[1] << 16) & 0xFF0000);
        address1 |= ((addr[0] << 24) & 0xFF000000);
        return address1;
    }

    public static byte[] getIpV4Bytes(String ipOrMask) {
        try{
            String[] addrs = ipOrMask.split("\\.");
            int length = addrs.length;
            byte[] addr = new byte[length];
            for (int index = 0; index < length; index++) {
                addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);
            }
            return addr;
        }catch (Exception e){
        }
        return new byte[4];
    }
}
