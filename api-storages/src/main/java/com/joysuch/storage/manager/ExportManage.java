package com.joysuch.storage.manager;

import com.joysuch.export.TableStructureExportClient;
import com.joysuch.storage.common.BaseResponse;
import com.joysuch.storage.common.ErrorCode;
import com.joysuch.storage.common.ResultUtils;
import com.joysuch.storage.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class ExportManage {
    @Resource
    TableStructureExportClient tableStructureExportClient;


    /**
     * 使用AI技术导出数据库文档
     * 此方法主要负责触发数据库文档的生成过程，无论是否使用AI功能
     *
     * @param isAi 表示是否使用AI功能进行文档生成，目前此参数未在方法内使用，预留以备将来可能的功能扩展
     * @return 成功或失败的提示信息，用于告知调用者导出操作的结果
     */
    public BaseResponse<String> exportByAI(boolean isAi) {
        try {
            // 调用远程服务开始文档生成，此处的null参数代表使用默认配置，第二个参数true表示启用详细日志模式
            tableStructureExportClient.documentGeneration(null, isAi);
            // 返回成功提示信息
            return ResultUtils.success("导出数据库文档成功");
        } catch (Exception e) {
            // 如果导出过程中出现异常，记录错误日志并返回失败提示信息
            log.error("使用AI技术导出数据库文档到本地失败,堆栈信息如下：", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "使用AI技术导出数据库文档到本地失败");
        }
    }

    /**
     * 导出数据库文档
     *
     * @param oldTableBaseMaps 数据库表的基本信息映射，键为表名，值为表对应的配置信息
     * @param isAi             是否使用人工智能辅助生成文档
     * @return 返回导出结果的信息，成功或失败的提示
     * <p>
     * 本方法通过调用tableStructureExportClient的documentGeneration方法来生成数据库文档
     * 如果生成过程中发生异常，将记录错误日志并返回失败提示
     */
    public BaseResponse<String> export(Map<String, String> oldTableBaseMaps, boolean isAi) {
        try {
            tableStructureExportClient.documentGeneration(oldTableBaseMaps, isAi);
            // 返回成功提示信息
            return ResultUtils.success("导出数据库文档成功");
        } catch (Exception e) {
            // 如果导出过程中出现异常，记录错误日志并返回失败提示信息
            log.error("导出数据库文档到本地失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "导出数据库文档到本地失败");
        }
    }
}
