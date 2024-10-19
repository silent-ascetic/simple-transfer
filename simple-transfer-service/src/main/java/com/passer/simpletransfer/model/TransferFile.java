package com.passer.simpletransfer.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>传输文件信息实体类</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Data
public class TransferFile implements Serializable {

    /**
     * 文件 md5 值
     */
    private String md5;

    private String name;
    /**
     * 文件大小，单位 B
     */
    private long size;

    /**
     * 文件大小字符串（自动转换单位）
     */
    private String sizeStr;

    /**
     * 文件下载路径
     */
    private String downloadUrl;
}
