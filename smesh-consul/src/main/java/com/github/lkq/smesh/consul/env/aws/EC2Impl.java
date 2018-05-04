package com.github.lkq.smesh.consul.env.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import com.github.lkq.smesh.exception.SmeshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EC2Impl implements EC2 {

    private static Logger logger = LoggerFactory.getLogger(EC2Impl.class);

    private final AmazonEC2 amazonEC2;

    protected EC2Impl() {
        amazonEC2 = AmazonEC2ClientBuilder.defaultClient();
    }

    public boolean isEc2() {
        return true;
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
        throw new SmeshException("tag not found");
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




