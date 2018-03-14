package com.lkq.services.docker.daemon.env.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AWSClient {

    private static Logger logger = LoggerFactory.getLogger(AWSClient.class);

    private static AWSClient instance = new AWSClient();

    private Boolean isAws;
    private final AmazonEC2 amazonEC2;

    private AWSClient(){
        amazonEC2 = AmazonEC2ClientBuilder.defaultClient();
    }

    public static AWSClient instance() {
        return instance;
    }

    public boolean isAws() {
        if (isAws == null) {
            try {
                isAws = StringUtils.isNotEmpty(EC2MetadataUtils.getInstanceId());
            } catch (Exception ignored) {
                isAws = false;
            }
        }
        return isAws;
    }

    public String getPrivateIP() {
        return EC2MetadataUtils.getPrivateIpAddress();
    }

    public String getTagValue(String key, String defaultValue) {
        try {
            Filter tagKeyFilter = new Filter();
            tagKeyFilter.setName(key);
            DescribeTagsRequest request = new DescribeTagsRequest(Arrays.asList(tagKeyFilter));
            DescribeTagsResult result = amazonEC2.describeTags(request);
            List<TagDescription> tags = result.getTags();
            logger.info("tag key={}, tag values= {}", key, tags);
            if (tags.size() > 0) {
                return tags.get(0).getValue();
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
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
