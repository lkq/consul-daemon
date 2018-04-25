package com.lkq.services.docker.daemon.env.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import com.github.lkq.smesh.exception.ConsulDaemonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EC2Client {

    private static Logger logger = LoggerFactory.getLogger(EC2Client.class);

    private static EC2Client instance = new EC2Client();

    private Boolean isEc2;
    private final AmazonEC2 amazonEC2;

    private EC2Client() {
        amazonEC2 = AmazonEC2ClientBuilder.defaultClient();
    }

    public static EC2Client instance() {
        return instance;
    }

    public boolean isEc2() {
        if (isEc2 == null) {
            try {
                isEc2 = StringUtils.isNotEmpty(EC2MetadataUtils.getInstanceId());
            } catch (Exception ignored) {
                isEc2 = false;
            }
        }
        return isEc2;
    }

    public String getPrivateIP() {
        return EC2MetadataUtils.getPrivateIpAddress();
    }

    public String getTagValue(String key, String defaultValue) {
        try {
            String value = getTagValue(key);
            if (value != null) return value;
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public String getTagValue(String key) {
        Filter tagKeyFilter = new Filter();
        tagKeyFilter.setName(key);
        DescribeTagsRequest request = new DescribeTagsRequest(Arrays.asList(tagKeyFilter));
        DescribeTagsResult result = amazonEC2.describeTags(request);
        List<TagDescription> tags = result.getTags();
        logger.info("tag key={}, tag values= {}", key, tags);
        if (tags.size() > 0) {
            return tags.get(0).getValue();
        }
        throw new ConsulDaemonException("tag not found");
    }

    public List<String> getInstanceIPByTag(String key) {
        Filter filter = new Filter().withName("tag-key").withValues(key);
        return getInstanceIPByFilter(filter);
    }

    public List<String> getInstanceIPByTagValue(String key, String value) {
        Filter filter = new Filter().withName("tag:" + key).withValues(value);
        return getInstanceIPByFilter(filter);
    }

    private List<String> getInstanceIPByFilter(Filter filter) {
        List<String> instanceIPs = new ArrayList<>();
        try {
            DescribeInstancesRequest request = new DescribeInstancesRequest()
                    .withFilters(filter);
            DescribeInstancesResult instancesResult = amazonEC2.describeInstances(request);
            List<Reservation> reservations = instancesResult.getReservations();
            for (Reservation reservation : reservations) {
                for (Instance instance : reservation.getInstances()) {
                    instanceIPs.add(instance.getPrivateIpAddress());
                }
            }
        } catch (Exception e) {
            logger.info("failed to get instance ip by " + filter, e);
        }
        return instanceIPs;
    }
}
