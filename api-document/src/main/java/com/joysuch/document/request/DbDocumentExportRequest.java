package com.joysuch.document.request;


import lombok.Data;

/**
 * DbDocumentExportRequest
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 * @description 数据库文档导出请求接口
 * @date 2024/8/13 上午10:19
 */
@Data
public class DbDocumentExportRequest {
    private String databaseName;
    private String databaseRemarks;
}
