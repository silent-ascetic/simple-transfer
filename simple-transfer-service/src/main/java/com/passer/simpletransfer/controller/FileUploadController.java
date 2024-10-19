package com.passer.simpletransfer.controller;

import com.passer.simpletransfer.service.FileUploadService;
import com.passer.simpletransfer.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>文件上传控制器</p>
 * <p>创建时间：2023/2/1</p>
 *
 * @author hj
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @RequestMapping("/uploadFile")
    public ResponseVo uploadFile(MultipartFile file) {
        ResponseVo responseVo = new ResponseVo();
        if (file == null || file.isEmpty()) {
            responseVo.setRespCode(300);
            responseVo.setMsg("请选择文件后上传");
            return responseVo;
        }
        responseVo.setRespCode(200);
        responseVo.setMsg("文件上传成功");
        log.info("{} 上传开始", file.getOriginalFilename());
        try {
            fileUploadService.uploadFile(file);
            log.info("{} 上传完成", file.getOriginalFilename());
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg("系统错误，请稍后重试");
            log.info("{} 上传失败：{}", file.getOriginalFilename(), e.getMessage());
        }
        return responseVo;
    }

    @RequestMapping("/uploadChunkFile")
    public ResponseVo uploadChunkFile(
            @RequestParam("chunkFile") MultipartFile chunkFile,
            @RequestParam("fileName") String fileName,
            @RequestParam("chunkNum") Integer chunkNum
    ) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        responseVo.setMsg("第" + chunkNum + "个分片文件上传成功");
        log.info("{} 第{}个分片开始上传。", fileName, chunkNum);
        try {
            fileUploadService.uploadChunkFile(chunkFile, fileName, chunkNum);
            log.info("{} 第{}个分片完成上传。", fileName, chunkNum);
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg("系统错误，请稍后重试");
            log.error("{} 第{}个分片上传失败：{}", fileName, chunkNum, e.getMessage());
        }
        return responseVo;
    }

    @RequestMapping("/mergeFile")
    public ResponseVo mergeFile(
            @RequestParam("fileName") String fileName,
            @RequestParam("totalChunks") Integer totalChunks
    ) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        responseVo.setMsg(fileName + " 上传完成");
        log.info("{} 的{}个分片开始合并。", fileName, totalChunks);
        try {
            fileUploadService.mergeFile(fileName, totalChunks);
            log.info("{} 的{}个分片完成合并。", fileName, totalChunks);
        } catch (Exception e) {
            fileUploadService.clearChunkFolder(fileName);
            responseVo.setRespCode(500);
            responseVo.setMsg("系统错误，请稍后重试");
            log.error("{} 的{}个分片合并失败：{}", fileName, totalChunks, e.getMessage());
        }
        return responseVo;
    }
}
