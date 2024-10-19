package com.passer.simpletransfer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>项目自定义 properties</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Data
@Component
@ConfigurationProperties(
        prefix = "simple-transfer"
)
public class SimpleTransferProperties {
    private boolean autoOpenBrowser = true;
    private String saveFolderPath = "allFiles";
    private String staticPathPattern = "/files/**";
    private String simpleTransferUrl;
}
