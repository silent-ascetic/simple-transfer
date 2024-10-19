import { http } from "@/utils/http";

type RespResult = {
  respCode: number;
  msg: string;
  data: Object;
};

type StringResult = {
  respCode: number;
  msg: string;
  data: string;
};

export function uploadChunkFileApi(data?: object): Promise<RespResult> {
  return http.request({
    url: "/upload/uploadChunkFile",
    method: "post",
    data
  });
}


export function mergeFileApi(data?: object): Promise<RespResult> {
  return http.request({
    url: "/upload/mergeFile",
    method: "post",
    data
  });
}

export function getQrCodeApi(): Promise<StringResult> {
  return http.request({
    url: "/getQrCode",
    method: "get"
  });
}


export function closeApi(): Promise<StringResult> {
  return http.request({
    url: "/close",
    method: "get"
  });
}