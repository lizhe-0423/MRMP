package com.joysuch.export;

import cn.smallbun.screw.core.Configuration;
import cn.smallbun.screw.core.engine.EngineConfig;
import cn.smallbun.screw.core.engine.EngineFileType;
import cn.smallbun.screw.core.engine.EngineTemplateType;
import cn.smallbun.screw.core.execute.DocumentationExecute;
import cn.smallbun.screw.core.process.ProcessConfig;
import com.joysuch.export.manager.SparkAIManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import javax.annotation.Resource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class TableStructureExportClient {

    @Resource
    private SparkAIManager sparkAIManager;


    private final String dataSourceUrl;


    private final String dataSourceUsername;


    private final String dataSourcePassword;

    private static final String PDF_CONVERSION_PATH_TEMPLATE = "%s\\%s.docx";


    private String outDir;

    public TableStructureExportClient(String dataSourceUrl, String dataSourceUsername, String dataSourcePassword, String outDir) {

        this.dataSourceUrl = dataSourceUrl;
        this.dataSourceUsername = dataSourceUsername;
        this.dataSourcePassword = dataSourcePassword;
        this.outDir = outDir;
    }

    private final ThreadPoolExecutor taskExecutor = new ThreadPoolExecutor(15, 30, 5,
            TimeUnit.MINUTES, new LinkedBlockingDeque<>(200));

    /**
     * 文档生成
     * 该方法用于根据提供的数据源或已有的映射信息生成数据库文档
     * 它首先尝试使用提供的旧表映射信息，如果没有提供，则从数据源连接中获取数据库列表和相关信息
     * 之后，使用多线程为每个数据库生成文档
     *
     * @param oldTableBaseMaps 旧表映射信息，可选参数，用于加速文档生成过程
     * @param isAi             是否使用AI助手来获取数据库备注
     */
    public void documentGeneration(Map<String, String> oldTableBaseMaps, boolean isAi, List<String> designatedTableName) {

        if(designatedTableName==null){
            designatedTableName = new ArrayList<>();
        }

        // 数据库名称
        String databaseName;
        // 数据库备注
        String databaseRemarks;
        // 使用并发哈希表存储数据库名称及其备注
        ConcurrentHashMap<String, String> tableMaps = new ConcurrentHashMap<>();
        // 如果提供了旧表映射信息，则将其加入到当前的表映射中
        if (oldTableBaseMaps != null) {
            oldTableBaseMaps.forEach(tableMaps::putIfAbsent);
        } else {
            // 尝试从数据源连接中获取数据库列表和相关信息
            try (Connection connection = DriverManager.getConnection(dataSourceUrl, dataSourceUsername, dataSourcePassword)) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet rs = metaData.getCatalogs();
                while (rs.next()) {
                    databaseName = rs.getString(1);
                    // 默认数据库备注为"databaseRemarks"，如果可用的话
                    databaseRemarks = isAi ? sparkAIManager.sendMessageAndGetResponse("数据库" + databaseName + "的备注是什么？[注:请尽量简短]", 5) : "databaseRemarks";
                    tableMaps.put(databaseName, databaseRemarks);
                }
            } catch (Exception e) {
                log.error("获取数据库列表失败", e);
            }
        }
        // 使用计数器来同步所有文档生成任务的完成
        CountDownLatch countDownLatch = new CountDownLatch(tableMaps.size());
        // 对于每个数据库，启动一个线程来生成文档
        List<String> finalDesignatedTableName = designatedTableName;
        tableMaps.forEach((key, value) -> taskExecutor.execute(() -> {
            try {
                generateDoc(key, value, finalDesignatedTableName);
            } catch (Exception e) {
                // 适当的异常处理逻辑
                log.error("生成文档失败", e);
            } finally {
                countDownLatch.countDown();
            }
        }));
        // 等待所有文档生成任务完成
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            log.error("等待线程失败", e);
            Thread.currentThread().interrupt();
        } finally {
            // 关闭任务执行器，释放资源
            taskExecutor.shutdown();
        }
    }


    /**
     * 根据指定的数据库名称和数据库备注生成文档
     *
     * @param databaseName    数据库名称，用于连接数据库
     * @param databaseRemarks 数据库备注，用于文档生成
     */
    public void generateDoc(String databaseName, String databaseRemarks,List<String> designatedTableName) {
        // 配置数据库连接池
        HikariConfig hikariConfig = getHikariConfig(databaseName);

        // 设置输出目录
        if (outDir == null || outDir.isEmpty()) {
            outDir = "doc" + File.separator;
        }

        // 使用try-with-resources确保资源正确关闭
        try (HikariDataSource dataSource = new HikariDataSource(hikariConfig)) {
            // 构建引擎配置
            EngineConfig engineConfig = EngineConfig.builder()
                    .fileOutputDir(outDir)
                    .openOutputDir(true)
                    .fileType(EngineFileType.WORD)
                    .produceType(EngineTemplateType.freemarker)
                    .fileName(databaseName).build();

            // 构建忽略配置
            ArrayList<String> ignoreTableName = new ArrayList<>();
            ArrayList<String> ignorePrefix = new ArrayList<>();
            ProcessConfig processConfig = ProcessConfig.builder()
                    .designatedTableName(designatedTableName)
                    .designatedTablePrefix(new ArrayList<>())
                    .designatedTableSuffix(new ArrayList<>())
                    .ignoreTableName(ignoreTableName)
                    .ignoreTablePrefix(ignorePrefix).build();

            // 构建整体配置
            Configuration config = Configuration.builder()
                    .version("1.0.0")
                    .description(databaseRemarks)
                    .dataSource(dataSource)
                    .engineConfig(engineConfig)
                    .produceConfig(processConfig)
                    .build();

            // 执行文档生成
            new DocumentationExecute(config).execute();
        } catch (Exception e) {
            log.error("生成文档失败", e);
        }
    }

    /**
     * 获取HikariCP连接池配置对象
     *
     * @param databaseName 数据库名称，用于构建数据源URL
     * @return HikariConfig 配置好的HikariCP连接池配置对象
     */
    private @NotNull HikariConfig getHikariConfig(String databaseName) {
        // 创建HikariCP连接池配置实例
        HikariConfig hikariConfig = new HikariConfig();
        // 设置MySQL数据库驱动类名称
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // 组装并设置数据库连接URL
        hikariConfig.setJdbcUrl(dataSourceUrl + databaseName);
        // 设置数据库用户名
        hikariConfig.setUsername(dataSourceUsername);
        // 设置数据库密码
        hikariConfig.setPassword(dataSourcePassword);
        // 设置可以获取tables remarks信息
        hikariConfig.addDataSourceProperty("useInformationSchema", "true");
        // 设置最小空闲连接数
        hikariConfig.setMinimumIdle(2);
        // 设置最大连接池大小
        hikariConfig.setMaximumPoolSize(5);
        // 返回配置好的HikariCP连接池配置对象
        return hikariConfig;
    }

}
