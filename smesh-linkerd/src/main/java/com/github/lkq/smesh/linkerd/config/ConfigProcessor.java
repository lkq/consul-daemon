package com.github.lkq.smesh.linkerd.config;

import com.github.lkq.smesh.Constants;
import com.github.lkq.smesh.exception.SmeshException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.HashMap;

/**
 * write linkerd config to a folder, which later will be bind to a volume of the container
 */
public class ConfigProcessor {

    private static Logger logger = LoggerFactory.getLogger(ConfigProcessor.class);

    /**
     * replace place holders
     *
     * @return
     * @param templateRoot
     * @param configFineName
     * @param variables
     * @param resourceLoaderClass
     */
    public String process(String templateRoot, String configFineName, HashMap<String, String> variables, Class<?> resourceLoaderClass) {
        try {
            Configuration config = new Configuration(Configuration.VERSION_2_3_28);
            config.setTemplateLoader(new ClassTemplateLoader(resourceLoaderClass, templateRoot));
            Template template = config.getTemplate(configFineName,
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
