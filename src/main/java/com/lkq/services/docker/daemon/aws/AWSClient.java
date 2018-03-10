package com.lkq.services.docker.daemon.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

public class AWSClient {

    private static Logger logger = LoggerFactory.getLogger(AWSClient.class);

    private static AWSClient instance = new AWSClient();

    private Boolean isAws;

    private AWSClient(){}

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

    public String getTag(String key, String defaultValue) {
        AmazonEC2 amazonEC2 = AmazonEC2ClientBuilder.defaultClient();
        Filter tagKeyFilter = new Filter();
        tagKeyFilter.setName(key);
        DescribeTagsRequest request = new DescribeTagsRequest(Arrays.asList(tagKeyFilter));
        DescribeTagsResult result = amazonEC2.describeTags(request);
        List<TagDescription> tags = result.getTags();
        logger.info("tag key={}, tag values= {}", key, tags);
        if (tags.size() > 0) {
            return tags.get(0).getValue();
        }
        return defaultValue;
    }
}
