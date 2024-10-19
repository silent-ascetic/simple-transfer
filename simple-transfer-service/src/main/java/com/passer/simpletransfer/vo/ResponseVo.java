package com.passer.simpletransfer.vo;

import lombok.Data;

/**
 * <p>响应 VO</p>
 * <p>创建时间：2023/2/2</p>
 *
 * @author hj
 */
@Data
public class ResponseVo {
    int respCode;
    String msg;
    Object data;
}
