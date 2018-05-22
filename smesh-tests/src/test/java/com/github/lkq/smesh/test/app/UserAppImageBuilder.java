package com.github.lkq.smesh.test.app;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.lkq.smesh.Constants;
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

/**
 * build a docker image from the UserApp package
 */
public class UserAppImageBuilder {

    private static Logger logger = LoggerFactory.getLogger(UserAppImageBuilder.class);

    public static final String IMAGE_TAG = "userapp";

    public static final String DOCKERFILE_NAME = "userapp.dockerfile";
    public static final String VAR_ARTIFACT_PATH = "artifactPath";
    public static final String VAR_ARTIFACT_NAME = "artifactName";
    public static final String VAR_REGISTER_URL = "registerURL";
    private final DockerClient dockerClient;

    public UserAppImageBuilder(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String build(String artifactPath, String artifactName, String registerURL) {
        try {
            File dockerFile = new File(prepareDockerFile(artifactPath, artifactName, registerURL));
            File baseDir = dockerFile.getParentFile();

            FileUtils.copyFile(new File(artifactPath, artifactName), new File(baseDir, artifactName));

            String imageId = dockerClient.buildImageCmd()
                    .withDockerfile(dockerFile)
                    .withNoCache(true)
                    .withTags(ImmutableSet.of(IMAGE_TAG))
                    .exec(new BuildImageResultCallback())
                    .awaitImageId();

            logger.info("created image: {}", imageId);
            return imageId;
        } catch (Exception e) {
            throw new SmeshException("failed to build docker image", e);
        }
    }

    /**
     * load docker file from template and export to a temp folder
     *
     * @param artifactPath
     * @param artifactName
     * @param registerURL
     * @return the exported docker file
     */
    private String prepareDockerFile(String artifactPath, String artifactName, String registerURL) {
        URL resourceRootURL = UserAppImageBuilder.class.getClassLoader().getResource("");
        try {
            File resourceRoot = new File(resourceRootURL.getPath() + "/template");
            Configuration config = new Configuration(Configuration.VERSION_2_3_28);
            config.setTemplateLoader(new FileTemplateLoader(resourceRoot));
            Template template = config.getTemplate(DOCKERFILE_NAME, Constants.ENCODING_UTF8);

            Map<String, String> variables = new HashMap<>();
            variables.put(VAR_ARTIFACT_PATH, artifactPath);
            variables.put(VAR_ARTIFACT_NAME, artifactName);
            variables.put(VAR_REGISTER_URL, registerURL);

            File targetFolder = new File(resourceRoot, "docker-file-" + System.currentTimeMillis());
            targetFolder.mkdir();
            File targetFile = new File(targetFolder, DOCKERFILE_NAME);

            template.process(variables, new FileWriterWithEncoding(targetFile, Constants.ENCODING_UTF8));
            logger.info("created docker file: {}", targetFile.getAbsolutePath());
            logger.info(FileUtils.readFileToString(targetFile, Constants.ENCODING_UTF8));
            return targetFile.getAbsolutePath();
        } catch (Exception e) {
            throw new SmeshException("failed to prepare docker file", e);
        }
    }

    public static void main(String[] args) {
        String[] artifact = new UserAppPackager().buildPackage();
        UserAppImageBuilder imageBuilder = new UserAppImageBuilder(DockerClientFactory.get());
        imageBuilder.build(artifact[0], artifact[1], "http://172.17.0.2:1025");
    }
}