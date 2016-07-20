package com.zhy.http.okhttp.service;

/**
*desc
*@author 王明远
*@日期： 2016/6/7 23:52
*@版权:Copyright    All rights reserved.
*/

public interface loadServerDataListener {
      void loadServerData(String response, int flag);
      void loadDataFailed(String response, int flag);
}
