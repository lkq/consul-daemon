package com.lkq.services.docker.daemon.container;

import com.lkq.services.docker.daemon.aws.AWSClient;
import com.lkq.services.docker.daemon.config.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class HostIPResolver {

    public static final String IPV4_PATTERN = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
    private static Logger logger = LoggerFactory.getLogger(HostIPResolver.class);

    private AWSClient awsClient;

    public HostIPResolver(AWSClient awsClient) {
        this.awsClient = awsClient;
    }

    /**
     * select a ip address for consul bind
     * for aws, uses private ip
     * otherwise, get local ip address
     */
    public String get() {
        String address;
        if (awsClient.isAws()) {
            address = awsClient.getPrivateIP();
            logger.info("using aws private ip: {}", address);
            return address;
        }

        address = getByNetworkInterface();
        if (StringUtils.isNotEmpty(address)) {
            logger.info("using network interface address: {}", address);
            return address;
        }

        address = getLocalHost();
        logger.info("using local host: {}", address);
        return address;

    }

    private String getByNetworkInterface() {
        String networkInterfaceName = Environment.getEnv("consul.network.interface", "");
        try {
            if (StringUtils.isNotEmpty(networkInterfaceName)) {
                logger.info("getting address from network interface: {}", networkInterfaceName);
                Pattern pattern = Pattern.compile(IPV4_PATTERN);
                NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceName);
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    String hostAddress = inetAddresses.nextElement().getHostAddress();
                    if (pattern.matcher(hostAddress).matches()) {
                        return hostAddress;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("failed to get bind address from network interface " + networkInterfaceName, e);
        }
        return null;
    }

    private String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ignored) {
        }
        return null;
    }

    public static void main(String[] args) throws SocketException {
        Enumeration e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements())
        {
            NetworkInterface n = (NetworkInterface) e.nextElement();
            Enumeration ee = n.getInetAddresses();
            while (ee.hasMoreElements())
            {
                InetAddress i = (InetAddress) ee.nextElement();
                System.out.println(i.getHostAddress());
            }
        }
    }

}
