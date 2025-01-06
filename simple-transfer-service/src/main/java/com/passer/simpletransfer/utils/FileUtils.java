package com.passer.simpletransfer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * <p>文件工具类</p>
 * <p>创建时间：2023/3/31</p>
 *
 * @author hj
 */
public class FileUtils {

    private static final CharSequence[] SPECIAL_SUFFIX = new CharSequence[]{"tar.bz2", "tar.Z", "tar.gz", "tar.xz"};


    public static File newFile(String filePath) {
        return StringUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    public static File newFile(File parent, String filePath) {
        return StringUtils.isEmpty(filePath) || parent == null ? null : new File(parent, filePath);
    }

    public static File newFile(String parentPath, String filePath) {
        return StringUtils.isEmpty(filePath) || StringUtils.isEmpty(parentPath) ? null : new File(parentPath, filePath);
    }

    public static File touch(String path) throws IOException {
        return path == null ? null : touch(newFile(path));
    }

    public static File touch(File file) throws IOException {
        if (null == file) {
            return null;
        }
        if (!file.exists()) {
            mkParentDirs(file);
        }

        return file.createNewFile() ? file : null;

    }

    public static File touch(File parent, String path) throws IOException {
        return touch(newFile(parent, path));
    }

    public static File touch(String parent, String path) throws IOException {
        return touch(newFile(parent, path));
    }

    public static File mkParentDirs(File file) throws IOException {
        if (null == file) {
            return null;
        }
        File parent = getParent(file, 1);
        return parent.mkdirs() ? parent : null;
    }

    public static File getParent(File file, int level) throws IOException {
        if (level >= 1 && null != file) {
            File parentFile = file.getCanonicalFile().getParentFile();
            return 1 == level ? parentFile : getParent(parentFile, level - 1);
        } else {
            return file;
        }
    }

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

    public static String extName(File file) {
        if (null == file) {
            return null;
        } else {
            return file.isDirectory() ? null : extName(file.getName());
        }
    }

    public static String extName(String fileName) {
        if (fileName == null) {
            return null;
        } else {
            int index = fileName.lastIndexOf(".");
            if (index == -1) {
                return "";
            } else {
                int secondToLastIndex = fileName.substring(0, index).lastIndexOf(".");
                String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
                if (StringUtils.containsAny(substr, SPECIAL_SUFFIX)) {
                    return substr;
                } else {
                    String ext = fileName.substring(index + 1);
                    return StringUtils.containsAny(ext, new Character[]{'\\','/'}) ? "" : ext;
                }
            }
        }
    }

}
