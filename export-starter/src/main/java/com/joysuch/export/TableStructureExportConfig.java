package com.joysuch.export;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "export.client")
@ComponentScan
@Configuration
@Data
public class TableStructureExportConfig {


    private String dataSourceUrl;


    private String dataSourceUsername;


    private String dataSourcePassword;

    private String outDir;


    /**
     * 配置并返回一个TableStructureExportClient实例
     * 该方法主要负责创建一个TableStructureExportClient对象，用于导出表结构
     * 通过@Bean注解指示该方法将返回一个可以被Spring管理的bean对象
     *
     * @return 返回一个初始化好的TableStructureExportClient对象
     */
    @Bean
    public TableStructureExportClient tableStructureExportClient() {
        return new TableStructureExportClient(dataSourceUrl, dataSourceUsername, dataSourcePassword, outDir);
    }

}