package com.joysuch.dubbo.provider;

import java.io.File;

public interface RpcService {
    String sayName(String name);
    /**
     * 文件上传
     * @param key 文件id
     * @param file 文件
     */
    String fileUpload(String key, File file);


    /**
     * 文件上传 直接传路径
     * @param key 文件id
     * @param filePath 文件路径
     */
    String fileUploadByPath(String key, String filePath);

    /**
     * 文件下载
     */
    String fileDownload();

    /**
     * 文档导出
     */
    String documentExport();

}
