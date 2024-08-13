package com.joysuch.document.controller;

import com.joysuch.document.common.BaseResponse;
import com.joysuch.document.common.ErrorCode;
import com.joysuch.document.common.ResultUtils;
import com.joysuch.document.exception.BusinessException;
import com.joysuch.document.manager.CosManager;
import com.joysuch.document.manager.ExportManage;
import com.joysuch.document.request.DbDocumentExportRequest;
import com.joysuch.export.TableStructureExportClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileController
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 * {@code @description} 数据库文档及数据库操作管理
 * {@code @date} 2024/8/12 上午10:27
 */
@RestController
@RequestMapping("/document/db")
public class DbDocumentController {


    @Resource
    private ExportManage exportManage;


    @GetMapping("/export-ai")
    public BaseResponse<String> exportByAI() {
        return exportManage.export(null, true);
    }

    @GetMapping("/export")
    public BaseResponse<String> export(@RequestBody List<DbDocumentExportRequest> requests) {
        // 检查请求列表是否为空
        if (requests == null || requests.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Request list cannot be empty");
        }

        // 遍历请求列表以确保所有请求都是有效的
        for (DbDocumentExportRequest request : requests) {
            String databaseName = request.getDatabaseName();
            String databaseRemarks = request.getDatabaseRemarks();

            // 检查数据库名称和备注是否为空或空白
            if (StringUtils.isAnyBlank(databaseName, databaseRemarks)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Database name and remarks cannot be empty or blank");
            }
        }

        // 执行导出操作
        try {
            boolean isVerbose = false;
            Map<String, String> requestMap = new HashMap<>();
            for (DbDocumentExportRequest request : requests) {
                requestMap.put(request.getDatabaseName(), request.getDatabaseRemarks());
            }
            return exportManage.export(requestMap, isVerbose);
        } catch (Exception e) {
            // 处理可能出现的异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "An error occurred during export");
        }
    }


}
