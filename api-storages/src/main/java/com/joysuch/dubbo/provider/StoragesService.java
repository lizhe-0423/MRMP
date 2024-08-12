package com.joysuch.dubbo.provider;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.joysuch.storage.manager.CosManager;
import com.joysuch.storage.manager.ExportManage;
import com.qcloud.cos.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;

/**
 * Default {@link RpcService}
 *
 *
 * @since 2.6.5
 */
@Service(version = "${rpc.service.version}")
@Slf4j
public class StoragesService implements RpcService {

    @Value("${rpc.service.name}")
    private String serviceName;

    @Resource
    private CosManager cosManager;

    @Resource
    private ExportManage exportManage;

    public String sayName(String name) {
        RpcContext rpcContext = RpcContext.getContext();
        return String.format("Service [name :%s , port : %d] %s(\"%s\") : Hello,%s",
                serviceName,
                rpcContext.getLocalPort(),
                rpcContext.getMethodName(),
                name,
                name);
    }

    @Override
    public String fileUpload(String key, File file) {
        log.info("执行api-storage======StoragesService=======fileUpload方法");
        PutObjectResult putObjectResult = cosManager.putObject(key, file);
        if (putObjectResult != null) {
            return "文件上传成功";
        }
        return "文件上传失败";
    }



    @Override
    public String fileDownload() {
        return "";
    }

    @Override
    public String documentExport() {
        return "";
    }

    @Override
    public String fileUploadByPath(String key, String filePath) {
        log.info("执行api-storage======StoragesService=======fileUpload方法");
        PutObjectResult putObjectResult = cosManager.putObject(key, filePath);
        if (putObjectResult != null) {
            return "文件上传成功";
        }
        return "文件上传失败";
    }
}