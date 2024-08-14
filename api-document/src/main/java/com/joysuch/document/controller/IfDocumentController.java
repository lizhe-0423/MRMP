package com.joysuch.document.controller;


import com.joysuch.document.common.BaseResponse;
import com.joysuch.document.common.ErrorCode;
import com.joysuch.document.exception.BusinessException;
import com.joysuch.document.manager.DocManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * IfDocumentController
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 * {@code @description} 接口文档管理
 * {@code @date} 2024/8/13 下午3:14
 */
@RestController
@RequestMapping("/document/if")
public class IfDocumentController {
    @Resource
    private DocManager docManager;

    @PostMapping("/export")
    public BaseResponse<String> export(@RequestParam String projectDir, @RequestParam String outputDir, @RequestParam String filePath) throws IOException, InterruptedException, IOException {
        //todo 需要动态支持选择工程目录
        if(StringUtils.isEmpty(projectDir)||StringUtils.isEmpty(outputDir)||StringUtils.isEmpty(filePath)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数不能为空");
        }
        docManager.doGenerate(projectDir,outputDir, filePath);
        return null;
    }
}
