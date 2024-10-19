package com.passer.simpletransfer.service;

/**
 * <p>简单传输服务层接口</p>
 * <p>创建时间：2023/2/10</p>
 *
 * @author hj
 */
public interface SimpleTransferService {
    void clearFolder();

    void closeService();

    String getQrCode();
}
