import { http } from "@/utils/http";

type ListResult = {
  respCode: number;
  msg: string;
  data: Array<any>;
};

type ObjectResult = {
  respCode: number;
  msg: string;
  data: Object;
};

export function getList(params?: object): Promise<ListResult> {
  return http.request({
    url: "/download/getList",
    method: "get",
    params
  });
}

export function deleteFileApi(data?: object): Promise<ObjectResult> {
  return http.request({
    url: "/download/deleteFile",
    method: "delete",
    data
  });
}
