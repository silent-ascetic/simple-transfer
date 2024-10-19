package com.passer.simpletransfer.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>文件上传服务层</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
public interface FileUploadService {
    void uploadFile(MultipartFile file);

    void uploadChunkFile(MultipartFile chunkFile, String fileName, Integer chunkNum);

    void mergeFile(String fileName, int totalChunks);

    void clearChunkFolder(String fileName);
}
