package com.kliu.services.docker.daemon.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.util.EC2MetadataUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AWSClientImpl implements AWSClient {

    private static Logger logger = LoggerFactory.getLogger(AWSClientImpl.class);

    AWSClientImpl() {
    }

    public boolean isAws() {
        return true;
    }

    public String getPrivateIP() {
        return EC2MetadataUtils.getPrivateIpAddress();
    }

    @Override
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
