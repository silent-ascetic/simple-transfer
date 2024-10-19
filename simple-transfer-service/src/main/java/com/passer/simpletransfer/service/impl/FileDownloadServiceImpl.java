package com.passer.simpletransfer.service.impl;

import com.passer.simpletransfer.config.SimpleTransferProperties;
import com.passer.simpletransfer.model.TransferFile;
import com.passer.simpletransfer.service.FileDownloadService;
import com.passer.simpletransfer.utils.FileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>FileDownloadService 实现类</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Slf4j
@Service
public class FileDownloadServiceImpl implements FileDownloadService {
    private final SimpleTransferProperties simpleTransferProperties;

    @Autowired
    public FileDownloadServiceImpl(SimpleTransferProperties simpleTransferProperties) {
        this.simpleTransferProperties = simpleTransferProperties;
    }

    @Override
    public List<TransferFile> getList() {
        File folder = new File(simpleTransferProperties.getSaveFolderPath());
        File[] files = folder.listFiles();
        List<TransferFile> transferFiles = new ArrayList<>();

        if (!folder.exists() || files == null || files.length < 1) {
            return transferFiles;
        }
        for (File folderFile : files) {
            File[] listFiles = folderFile.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                File file = listFiles[0];
                TransferFile transferFile = new TransferFile();
                transferFile.setMd5(file.getParentFile().getName());
                transferFile.setName(file.getName());
                transferFile.setSize(file.length());
                transferFile.setSizeStr(fileSizeUnitChange(transferFile.getSize()));
                transferFile.setDownloadUrl("/download/downloadFile?code=" + transferFile.getMd5());
                transferFiles.add(transferFile);
            }
        }
        return transferFiles;

    }

    @Override
    public void downloadFile(String code, HttpServletRequest request, HttpServletResponse response) {
        String saveFolderPath = simpleTransferProperties.getSaveFolderPath();
        File folder = new File(saveFolderPath + "/" + code);
        File[] files = folder.listFiles();

        // 检查文件夹是否存在且至少有一个文件
        if (files == null || files.length == 0) {
            throw new RuntimeException("未找到文件");
        }
        File file = files[0];
        // 读取文件到流中
        try (InputStream inputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件未找到", e);
        } catch (IOException e) {
            throw new RuntimeException("读取文件时发生错误", e);
        }

        // 设置响应头
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8) + "\"");
    }


    @Override
    public void deleteFile(String fileMd5) {
        File folder = new File(simpleTransferProperties.getSaveFolderPath(), fileMd5);
        if (folder.exists()) {
            FileUtils.deleteFolder(folder, true);
        }
    }

    /**
     * 文件大小自动转换成合适的单位数值
     *
     * @param size 文件大小（单位：B）
     * @return String 合适的单位的文件大小字符串
     */
    private String fileSizeUnitChange(long size) {
        if (size < 1024) {
            return size + "B";
        }
        if (size >= 1024 * 1024) {
            return size / 1024 / 1024 + "MB";
        }
        return size / 1024 + "KB";
    }
}
