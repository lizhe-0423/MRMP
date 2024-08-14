package com.joysuch.document.manager;


import com.joysuch.document.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * Cos 对象存储操作
 *
 * @author <a href="https://github.com/lizhe-0423">蓝莓</a>
 */
@Component
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key           唯一键
     * @param localFilePath 本地文件路径
     */
    public PutObjectResult putObject(String key, String localFilePath) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                new File(localFilePath));
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 根据指定的键值和输出文件路径从COS（Cloud Object Storage）获取对象并保存到本地
     * <p>
     * 此方法用于从COS中获取特定的对象（文件），并将其保存到指定的本地路径中它首先构造一个GetObjectRequest对象，
     * 该对象包含COS桶的名称和要获取的对象的键值然后，使用此请求对象和一个表示本地输出文件路径的File对象，
     * 从COS中获取对象并将其保存到指定的本地路径中
     *
     * @param key            COS中要获取的对象的键值
     * @param outputFilePath 本地文件系统中用于保存获取对象的路径
     * @return 返回一个ObjectMetadata对象，该对象包含获取到的对象的元数据信息
     */
    public ObjectMetadata getObject(String key, String outputFilePath) {
        // 创建一个GetObjectRequest对象，指定COS桶名称和对象键值
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);

        // 使用GetObjectRequest对象和本地文件路径，从COS获取对象并保存到本地
        return cosClient.getObject(getObjectRequest, new File(outputFilePath + File.separator + key));
    }
}
