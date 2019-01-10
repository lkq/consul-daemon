package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.smesh.exception.SmeshException;
import com.github.lkq.smesh.linkerd.Constants;
import com.github.lkq.smesh.linkerd.Main;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * a freemarker template processor
 */
@Singleton
public class TemplateProcessor {

    private static Logger logger = LoggerFactory.getLogger(TemplateProcessor.class);

    @Inject
    public TemplateProcessor() {
    }

    /**
     *
     * @param sourceFolder the template folder
     * @param sourceFileName the template file name
     * @param targetFilePathName the target file path to be written
     * @param variables the place holder values
     * @return
     */
    public String process(String sourceFolder, String sourceFileName, String targetFilePathName, Map<String, String> variables) {

        String configContent = load(sourceFolder, sourceFileName, variables, Main.class);

        try {
            File targetFile = new File(targetFilePathName);
            FileUtils.writeStringToFile(targetFile,
                    configContent,
                    Constants.ENCODING_UTF8);
            String writtenFilePath = targetFile.getAbsolutePath();
            logger.info("writing linkerd config to: {}", writtenFilePath);
            return writtenFilePath;
        } catch (IOException e) {
            throw new SmeshException("failed to write config to file: " + targetFilePathName, e);
        }
    }

    /**
     * load template content and replace the place holders with its value
     *
     * @return the processed template content
     * @param sourceFolder the template file path
     * @param sourceFileName the template file name
     * @param variables place holder values
     * @param resourceLoaderClass a class which class loader will be used to load the template
     */
    public String load(String sourceFolder, String sourceFileName, Map<String, String> variables, Class<?> resourceLoaderClass) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_28);
            config.setTemplateLoader(new ClassTemplateLoader(resourceLoaderClass, sourceFolder));
            Template template = config.getTemplate(sourceFileName,
                    Constants.ENCODING_UTF8);

            StringWriter configWriter = new StringWriter();
            template.process(variables, configWriter);
            String content = configWriter.toString();
            logger.info("loaded linkerd config: \n{}", content);
            return content;
        } catch (Exception e) {
            throw new SmeshException("failed to prepare config file", e);
        }
    }
}
