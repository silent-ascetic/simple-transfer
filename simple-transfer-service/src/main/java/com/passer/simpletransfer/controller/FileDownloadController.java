package com.passer.simpletransfer.controller;

import com.passer.simpletransfer.service.FileDownloadService;
import com.passer.simpletransfer.vo.ResponseVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>文件下载控制器</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Slf4j
@RestController
@RequestMapping("/download")
public class FileDownloadController {
    private final FileDownloadService fileDownloadService;

    @Autowired
    public FileDownloadController(FileDownloadService fileDownloadService) {
        this.fileDownloadService = fileDownloadService;
    }

    @RequestMapping("/getList")
    public ResponseVo getList() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        try {
            responseVo.setData(fileDownloadService.getList());
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg("系统错误");
            log.error(e.getMessage());
        }
        return responseVo;
    }


    @RequestMapping("/downloadFile")
    public void downloadFile(
            @RequestParam("code") String code,
            HttpServletRequest request, HttpServletResponse response
    ) {
        log.info("下载：" + code);
        try {
            fileDownloadService.downloadFile(code, request, response);
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(500);
        }
    }

    @ResponseBody
    @DeleteMapping("/deleteFile")
    public ResponseVo deleteFile(@RequestParam("fileMd5") String fileMd5) {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        try {
            fileDownloadService.deleteFile(fileMd5);
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg(e.getMessage());
        }
        return responseVo;
    }
}
