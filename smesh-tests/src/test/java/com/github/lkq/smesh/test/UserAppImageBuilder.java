package com.github.lkq.smesh.test;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.lkq.smesh.docker.DockerClientFactory;
import com.github.lkq.smesh.exception.SmeshException;
import com.google.common.collect.ImmutableSet;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UserAppImageBuilder {

    private static Logger logger = LoggerFactory.getLogger(UserAppImageBuilder.class);

    public void build(String artifactPath, String artifactName) {
        try {
            DockerClient dockerClient = DockerClientFactory.get();

            File dockerFile = new File(prepareDockerFile(artifactPath, artifactName));
            File baseDir = dockerFile.getParentFile();

            FileUtils.copyFile(new File(artifactPath, artifactName), new File(baseDir, artifactName));

            String imageId = dockerClient.buildImageCmd()
                    .withDockerfile(dockerFile)
                    .withNoCache(true)
                    .withTags(ImmutableSet.of("user-app"))
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();

            logger.info("created image: {}, {}", imageId);
        } catch (Exception e) {
            throw new SmeshException("failed to build docker image", e);
        }
    }

    /**
     * load docker file from template and export to a temp folder
     *
     * @param artifactPath
     * @param artifactName
     * @return the exported docker file
     */
    private String prepareDockerFile(String artifactPath, String artifactName) {
        URL resourceRootURL = UserAppImageBuilder.class.getClassLoader().getResource("");
        try {
            File resourceRoot = new File(resourceRootURL.getPath());
            Configuration config = new Configuration(Configuration.VERSION_2_3_28);
            config.setTemplateLoader(new FileTemplateLoader(resourceRoot));
            Template template = config.getTemplate("user-app.dockerfile", "UTF-8");

            Map<String, String> variables = new HashMap<>();
            variables.put("artifactPath", artifactPath);
            variables.put("artifactName", artifactName);

            File targetFolder = new File(resourceRoot, "docker-file-" + System.currentTimeMillis());
            targetFolder.mkdir();
            File targetFile = new File(targetFolder, "user-app.dockerfile");

            template.process(variables, new FileWriterWithEncoding(targetFile, "UTF-8"));
            logger.info("created docker file: {}", targetFile.getAbsolutePath());
            logger.info(FileUtils.readFileToString(targetFile, "UTF-8"));
            return targetFile.getAbsolutePath();
        } catch (Exception e) {
            throw new SmeshException("failed to prepare docker file", e);
        }
    }

    public static void main(String[] args) {
        new UserAppImageBuilder().build("/Users/kingson/Sandbox/smesh/smesh-tests/target", "smesh-tests-0.1.0-SNAPSHOT.jar");
    }
}
