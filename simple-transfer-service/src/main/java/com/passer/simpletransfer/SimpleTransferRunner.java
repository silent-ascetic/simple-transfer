package com.passer.simpletransfer;

import com.passer.simpletransfer.config.SimpleTransferProperties;
import com.passer.simpletransfer.utils.NetworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.SocketException;
import java.util.Optional;

/**
 * <p>服务启动时的初始化操作</p>
 * <p>创建时间：2022/5/1</p>
 *
 * @author passer
 * @version 0.0.1
 */
@Slf4j
@Component
public class SimpleTransferRunner implements ApplicationRunner {
    private final SimpleTransferProperties simpleTransferProperties;
    private final ConfigurableApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    public SimpleTransferRunner(SimpleTransferProperties simpleTransferProperties, ConfigurableApplicationContext applicationContext, Environment environment) {
        this.simpleTransferProperties = simpleTransferProperties;
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        File fileFolder = new File(simpleTransferProperties.getSaveFolderPath());
        if (!fileFolder.exists() && !fileFolder.mkdirs()) {
            log.error("文件上传目录不存在且无法创建，path：{}", simpleTransferProperties.getSaveFolderPath());
            applicationContext.close();
            return;
        }
        String localIp4 = "127.0.0.1";
        Optional<Inet4Address> localIp4Address = Optional.empty();
        try {
            localIp4Address = NetworkUtils.getLocalIp4Address();
        } catch (SocketException e) {
            log.error("服务器IP获取失败：{} {}", e.getClass().getName(), e.getMessage());
        }
        if (localIp4Address.isPresent())  {
            localIp4 = localIp4Address.get().getHostAddress();
        }
        String simpleTransferUrl = "http://"+ localIp4 +":"+environment.getProperty("server.port");
        if (simpleTransferProperties.isAutoOpenBrowser()) {
            try {
                Runtime.getRuntime().exec("cmd /c start " + simpleTransferUrl);
            } catch (IOException e) {
                log.error("浏览器启动失败：{}", e.getMessage());
            }
        }
        simpleTransferProperties.setSimpleTransferUrl(simpleTransferUrl);
        log.info("简单传输启动成功：{}", simpleTransferUrl);
    }
}
