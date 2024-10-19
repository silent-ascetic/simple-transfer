package com.passer.simpletransfer.service;

import com.passer.simpletransfer.model.TransferFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * <p>文件下载服务层接口</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
public interface FileDownloadService {
    List<TransferFile> getList();

    void downloadFile(String code, HttpServletRequest request, HttpServletResponse response);

    void deleteFile(String fileMd5);

}
