package com.passer.simpletransfer.controller;

import com.passer.simpletransfer.service.SimpleTransferService;
import com.passer.simpletransfer.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>简单传输控制器</p>
 * <p>创建时间：2023/2/1</p>
 *
 * @author hj
 */
@Slf4j
@Controller
public class SimpleTransferController {
    private final SimpleTransferService simpleTransferService;

    @Autowired
    public SimpleTransferController(SimpleTransferService simpleTransferService) {
        this.simpleTransferService = simpleTransferService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @ResponseBody
    @GetMapping("/getQrCode")
    public ResponseVo getQrCode() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        try {
            responseVo.setData(simpleTransferService.getQrCode());
        } catch (Exception e) {
            log.error(e.getMessage());
            responseVo.setMsg(e.getMessage());
            responseVo.setRespCode(500);
        }
        return responseVo;
    }

    @ResponseBody
    @RequestMapping("/clearFolder")
    public ResponseVo clearFolder() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        try {
            simpleTransferService.clearFolder();
        } catch (Exception e) {
            responseVo.setRespCode(500);
            responseVo.setMsg(e.getMessage());
        }
        return responseVo;
    }

    @ResponseBody
    @RequestMapping("/close")
    public ResponseVo close() {
        ResponseVo responseVo = new ResponseVo();
        responseVo.setRespCode(200);
        simpleTransferService.closeService();
        return responseVo;
    }
}
