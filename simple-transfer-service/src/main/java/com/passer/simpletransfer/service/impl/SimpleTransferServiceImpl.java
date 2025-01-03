package com.passer.simpletransfer.service.impl;

import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.passer.simpletransfer.config.SimpleTransferProperties;
import com.passer.simpletransfer.service.SimpleTransferService;
import com.passer.simpletransfer.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;

/**
 * <p>SimpleTransferService 实现类</p>
 * <p>创建时间：2023/2/10</p>
 *
 * @author hj
 */
@Slf4j
@Service
public class SimpleTransferServiceImpl implements SimpleTransferService {
    private final SimpleTransferProperties simpleTransferProperties;
    private final ConfigurableApplicationContext applicationContext;

    @Autowired
    public SimpleTransferServiceImpl(SimpleTransferProperties simpleTransferProperties, ConfigurableApplicationContext applicationContext) {
        this.simpleTransferProperties = simpleTransferProperties;
        this.applicationContext = applicationContext;
    }

    @Override
    public void clearFolder() {
        File folder = new File(simpleTransferProperties.getSaveFolderPath());
        if (folder.exists()) {
            FileUtils.deleteFolder(folder, false);
        }
    }

    @Async
    @Override
    public void closeService() {
        log.info("延时4秒后自动关闭应用");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            log.error("休眠失败：{}", e.getMessage());
        }
        applicationContext.close();
    }

    @Override
    public String getQrCode() {
        QrConfig qrConfig = QrConfig.create()
                // .setImg("static/favicon.ico")
                .setForeColor(new Color(7237845))
                .setBackColor(new Color(255, 255, 255, 0))
                .setRatio(8)
                .setMargin(0);
        String qrCode = QrCodeUtil.generateAsSvg(simpleTransferProperties.getSimpleTransferUrl(), qrConfig);
        return qrCode.replace("style=\"background-color:", "style=\"height: 100%;width: 100%;background-color: ");
    }
}
