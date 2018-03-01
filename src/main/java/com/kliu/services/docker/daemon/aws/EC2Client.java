package com.kliu.services.docker.daemon.aws;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class EC2Client {

    private static Logger logger = LoggerFactory.getLogger(EC2Client.class);

    public static void main(String[] args) {
        List<TagDescription> consul = new EC2Client().getTag("consul");
    }
    public List<TagDescription> getTag(String key) {
        AmazonEC2 amazonEC2 = AmazonEC2ClientBuilder.defaultClient();
        DescribeInstancesResult instancesResult = amazonEC2.describeInstances();
        logger.info("instance: {}", instancesResult);
        Filter tagKeyFilter = new Filter();
        tagKeyFilter.setName(key);
        DescribeTagsRequest request = new DescribeTagsRequest(Arrays.asList(tagKeyFilter));
        DescribeTagsResult result = amazonEC2.describeTags(request);
        List<TagDescription> tags = result.getTags();
        logger.info("tag key={}, tag values= {}", key, tags);
        return tags;
    }
}
