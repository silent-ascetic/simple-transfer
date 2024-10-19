package com.passer.simpletransfer.service.impl;

import com.passer.simpletransfer.config.SimpleTransferProperties;
import com.passer.simpletransfer.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>FileUploadService 实现类</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final SimpleTransferProperties simpleTransferProperties;

    @Autowired
    public FileUploadServiceImpl(SimpleTransferProperties simpleTransferProperties) {
        this.simpleTransferProperties = simpleTransferProperties;
    }

    @Override
    public void uploadFile(MultipartFile file) {
        File folder = new File(simpleTransferProperties.getSaveFolderPath());
        if (!folder.exists()) {
            throw new RuntimeException(folder.getAbsolutePath() + "：文件保存目录不存在");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new RuntimeException("无法获取上传文件的文件名");
        }
        String[] list = folder.list();
        if (list != null) {
            for (String s : list) {
                if (s.equals(filename)) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HH.mm.ss");
                    filename = simpleDateFormat.format(new Date()) + "-" + filename;
                }
            }
        }
        File targetFile = new File(folder, filename);
        try {
            file.transferTo(targetFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException("文件保存失败：" + e.getMessage());
        }
    }

    @Override
    public void uploadChunkFile(MultipartFile chunkFile, String fileName, Integer chunkNum) {
        File folder = new File(simpleTransferProperties.getSaveFolderPath());
        if (!folder.exists()) {
            throw new RuntimeException(folder.getAbsolutePath() + "：文件保存目录不存在");
        }
        File chunkFolder = new File(folder, "chunkFolder-" + fileName);
        if (!chunkFolder.exists() && !chunkFolder.mkdir()) {
            throw new RuntimeException(chunkFolder.getAbsolutePath() + "：分片文件保存目录不存在");
        }

        File targetFile = new File(chunkFolder, chunkNum + "");
        if (targetFile.exists() && targetFile.length() == chunkFile.getSize()) {
            return;
        }
        try {
            chunkFile.transferTo(targetFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(fileName + "文件第" + chunkNum + "个分片保存失败：" + e.getMessage());
        }
    }

    @Override
    public void mergeFile(String fileName, int totalChunks) {

        BufferedOutputStream outputStream;
        String folderName = "" + fileName.hashCode() + new Date().getTime();
        File folder = new File(simpleTransferProperties.getSaveFolderPath(), folderName);
        if (!folder.mkdir()) {
            throw new RuntimeException(fileName + "文件夹创建失败！");
        }
        File chunkFolder = new File(simpleTransferProperties.getSaveFolderPath(), "chunkFolder-" + fileName);
        if (!chunkFolder.exists()) {
            throw new RuntimeException(fileName + "文件分片未上传！");
        }

        File file = new File(folder, fileName);
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("合并文件失败：" + e.getMessage());
        }
        int len;
        for (int i = 1; i <= totalChunks; i++) {
            File chunkFile = new File(chunkFolder, i + "");
            try {
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(chunkFile));
                byte[] bt = new byte[1024 * 1024];// 每次读取1M,数组大小不能太大，会内存溢出
                while ((len = inputStream.read(bt)) != -1) {
                    outputStream.write(bt, 0, len);
                }
                inputStream.close();
                outputStream.flush();
            } catch (IOException e) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    log.error("文件输出流关闭失败：{}", ex.getMessage());
                }
                log.error("合并文件出错：{}。", e.getMessage());
                log.info("删除文件{}：{}", file.getName(), file.delete());
            } finally {
                // 无论合并成功或失败都要删除分片文件
                if (!chunkFile.delete()) {
                    log.error("分片文件：{} 删除失败", chunkFile.getName());
                }
            }
        }

        clearChunkFolder(fileName);

        try {
            outputStream.close();
        } catch (IOException e) {
            log.error("文件输出流关闭失败：{}", e.getMessage());
        }


        if (!file.exists()) {
            log.error("文件保存失败");
            throw new RuntimeException(fileName + "文件合并失败");
        }
    }

    @Override
    public void clearChunkFolder(String fileName) {
        String chunkFolderName = "chunkFolder-" + fileName;
        File chunkFolder = new File(simpleTransferProperties.getSaveFolderPath(), chunkFolderName);
        if (!chunkFolder.exists()) {
            return;
        }
        boolean isSuccess = true;
        File[] files = chunkFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                isSuccess = file.delete();
            }
        }
        if (isSuccess) {
            isSuccess = chunkFolder.delete();
        }
        if (!isSuccess) {
            log.error("{} 文件夹删除失败！请手动删除。", chunkFolderName);
        }

    }
}
