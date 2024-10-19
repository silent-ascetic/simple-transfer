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

export function getUserListApi(params?: object): Promise<ListResult> {
  return http.request({
    url: "/chat/getUserList",
    method: "get",
    params
  });
}

