package com.joysuch.storage.controller;

import com.joysuch.storage.common.BaseResponse;
import com.joysuch.storage.common.ErrorCode;
import com.joysuch.storage.common.ResultUtils;
import com.joysuch.storage.exception.BusinessException;
import com.joysuch.storage.manager.CosManager;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.io.File;

/**
 * FileController
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 * @description 文件管理
 * @date 2024/8/12 上午10:27
 */
@RestController
@RequestMapping("/storage/file")
public class FileController {


    @Resource
    private CosManager cosManager;

    /**
     * 上传文件
     * <p>
     * 通过POST请求接收前端上传的文件，并将其存储到COS（Cloud Object Storage）中
     *
     * @param file 上传的文件对象，通过@RequestParam指定接收前端表单中name为"file"的数据
     * @return 返回一个基础响应对象，包含上传结果信息如果上传成功，返回文件的上传结果对象；
     * 如果上传失败，则抛出运行时异常
     * <p>
     * 使用@PostMapping注解指定该方法为处理POST请求，请求路径为/upload
     */
    @PostMapping("/upload")
    public BaseResponse<PutObjectResult> upload(@RequestParam("file") MultipartFile file) {
        //todo rpc调用户 进行鉴权
        if (file == null || file.getSize() == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件为空");
        }
        // 获取文件名，用于在COS中识别文件
        String name = file.getName();
        // 使用cosManager将文件上传到COS，并获取上传结果
        PutObjectResult putObjectResult = cosManager.putObject(name, (File) file);

        // 检查上传结果，如果成功则返回成功响应，否则抛出异常
        if (putObjectResult != null) {
            return ResultUtils.success(putObjectResult);
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
    }

    /**
     * 通过路径上传文件
     *
     * @param filePath 文件的路径
     * @return 返回上传结果的封装对象
     */
    @PostMapping("/upload-path")
    public BaseResponse<PutObjectResult> uploadByPath(@RequestParam("/filePath") String filePath) {
        //todo rpc调用户 进行鉴权
        String name = filePath.substring(filePath.lastIndexOf("/") + 1);
        // 使用COS管理器上传文件
        PutObjectResult putObjectResult = cosManager.putObject(name, filePath);
        // 判断上传结果是否成功
        if (putObjectResult != null) {
            // 上传成功，返回成功结果
            return ResultUtils.success(putObjectResult);
        }
        // 上传失败，抛出异常
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
    }

    /**
     * 使用GET方法从COS（Cloud Object Storage）下载指定的文件
     *
     * @param key            COS中文件的唯一标识符，用于指定要下载的文件
     * @param outputFilePath 下载文件的本地保存路径
     * @return 包含下载文件元数据的BaseResponse对象如果文件成功下载，
     * 则返回一个表示操作成功的BaseResponse对象，其中包含文件的元数据；
     * 如果下载失败，则抛出一个运行时异常
     */
    @GetMapping("/download")
    public BaseResponse<ObjectMetadata> download(@RequestParam("/key") String key, @RequestParam("/outputFilePath") String outputFilePath) {
        ObjectMetadata objectMetadata = cosManager.getObject(key, outputFilePath);
        if (objectMetadata != null) {
            return ResultUtils.success(objectMetadata);
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
    }
}
