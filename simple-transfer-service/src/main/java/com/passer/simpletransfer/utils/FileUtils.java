package com.passer.simpletransfer.utils;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * <p>文件工具类</p>
 * <p>创建时间：2023/3/31</p>
 *
 * @author hj
 */
public class FileUtils {

    /**
     * 剪切文件
     *
     * @param filePath     原文件路径
     * @param targetFolder 目标文件夹路径
     * @throws IOException IO 异常
     */
    public static void moveFile(String filePath, String targetFolder) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件路径不正确！");
        }
        String fileName = file.getName();
        File targetFile = new File(targetFolder, fileName);
        boolean renameTo = file.renameTo(targetFile);
        if (!renameTo) {
            throw new IOException("文件移动失败！");
        }
    }

    /**
     * 复制文件
     *
     * @param filePath     原文件路径
     * @param targetFolder 目标文件夹路径
     */
    public static void copyFile(String filePath, String targetFolder) {
        File file = new File(filePath);
        String fileName = file.getName();
        File targetFile = new File(targetFolder, fileName);
        try (
                FileChannel in = new FileInputStream(file).getChannel();
                FileChannel out = new FileOutputStream(targetFile, true).getChannel()
        ) {
            out.transferFrom(in, 0, in.size());
        } catch (IOException e) {
            throw new RuntimeException("文件拷贝失败：" + e.getMessage());
        }
    }

    public static void deleteFolder(File folder, boolean deleteSelf) {
        int i = deleteFolder(folder, 0);
        if (deleteSelf && !folder.delete()) {
            i++;
        }
        if (i > 0) {
            throw new RuntimeException(i + "个文件无法删除");
        }
    }

    public static int deleteFolder(File folder, int failNum) {
        File[] files = folder.listFiles();
        if (files == null) {
            return failNum;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                String[] list = file.list();
                if (list != null && list.length > 0) {
                    failNum = deleteFolder(file, failNum);
                }
            }
            if (!file.delete()) {
                failNum++;
            }
        }
        return failNum;
    }

}
