package com.passer.simpletransfer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * <p>配置</p>
 * <p>创建时间：2023/2/3</p>
 *
 * @author hj
 */
@Configuration
public class STWebMvcConfigurer implements WebMvcConfigurer {
    private final SimpleTransferProperties simpleTransferProperties;

    @Autowired
    public STWebMvcConfigurer(SimpleTransferProperties simpleTransferProperties) {
        this.simpleTransferProperties = simpleTransferProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String locationPath = "file:" + System.getProperty("user.dir") + File.separator + simpleTransferProperties.getSaveFolderPath() + File.separator;
        registry.addResourceHandler(simpleTransferProperties.getStaticPathPattern()).addResourceLocations(locationPath);
    }
}
