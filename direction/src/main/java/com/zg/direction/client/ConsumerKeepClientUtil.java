package com.zg.direction.client;

import com.zg.direction.adapter.ProviderRegister;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ProviderEntity;
import com.zg.network.common.client.BaseKeepClient;
import com.zg.network.entity.BaseRequest;


public class ConsumerKeepClientUtil {

    //同步返回结果
    public  static DTPResponse addSynRequest(String providerName, DTPRequest request) throws Exception {
        //从注册中心获取配置
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        ProviderEntity providerEntity = providerRegister.findPriorityNode(providerName);
        //添加一些配置信息
        request.className = providerEntity.className;
        request.providerName = providerEntity.providerName;
        request.path = providerEntity.path;

        //构建请求结构
        BaseRequest baseRequest=new BaseRequest();
        baseRequest.id= request.id;
        baseRequest.state="1";
        baseRequest.message=request;
        //获取客户端、发送请求
        BaseKeepClient baseKeepClient = BaseKeepClient.getInstance(providerEntity.host,providerEntity.port,DTPResponse.class);
        baseKeepClient.sendRequest(baseRequest);
     //   LockSupport.parkUntil(baseRequest,System.currentTimeMillis() + 10 * 1000);
        DTPResponse response = (DTPResponse)baseRequest.result;

        if (response == null) {
            response = new DTPResponse();
            response.id = request.id;
            response.success = false;
            response.error = request.id + "没有收到返回消息，可能服务变化" + request.path + "clieckversion" + providerEntity.clientVersion;
        }

        if (response != null && !response.success) {
            throw new Exception(response.error);
        }
        return response;

    }


}
