package per.nonlone.test.stress;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import per.nonlone.frameworkExtend.datasource.MultipleDataSource;
import per.nonlone.frameworkExtend.mybatis.multisource.*;
import per.nonlone.utilsExtend.StringUtils;

import javax.sql.DataSource;
import java.util.*;

/**
 * 数据源配置
 */
@Configuration
@Slf4j
public class DataSourceConfiguration implements EnvironmentAware {

    private static final String BEAN_CLASS_PREFIX_MAP = "classPrefixMap";

    private static final String DEFAULT_PROPERTIES_PREFIX = "mysql";

    private static final String DEFAULT_DATASOURCE_KEY = "oms";

    private static final String DEFAULT_CONNECTION_PROPERTIES = "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000;druid.keepAlive=true";


    private Environment environment;


    /**
     * 构造类前缀映射Map
     *
     * @return
     */
    @Bean(name = BEAN_CLASS_PREFIX_MAP)
    public Map<String, String> classPrefixMap() {
        Map<String, String> classPrefixMap = new LinkedHashMap<>();
        // 订单主表
        classPrefixMap.put("per.nonlone.test.stress.TestAMapper", "testa");
        classPrefixMap.put("per.nonlone.test.stress.TestBMapper", "testb");

        return classPrefixMap;
    }


    /**
     * 构造多数据源
     *
     * @param classPrefixMap
     * @return
     */
    @Bean
    public MultipleDataSource multipleDataSource(@Qualifier(BEAN_CLASS_PREFIX_MAP) Map<String, String> classPrefixMap) {
        Map<Object, Object> multipleDataSourceMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(classPrefixMap)) {
            try {
                for (Map.Entry<String, String> entry : classPrefixMap.entrySet()) {
                    DataSource dataSource = getDruidDataSource(entry.getValue(), DEFAULT_PROPERTIES_PREFIX, DEFAULT_CONNECTION_PROPERTIES);
                    multipleDataSourceMap.put(entry.getValue(), dataSource);
                }
                if (multipleDataSourceMap.containsKey(DEFAULT_DATASOURCE_KEY)) {
                    return new MultipleDataSource(multipleDataSourceMap, (DataSource) multipleDataSourceMap.get(DEFAULT_DATASOURCE_KEY));
                } else {
                    Collection<Object> dataSourceSets = multipleDataSourceMap.values();
                    if (!CollectionUtils.isEmpty(dataSourceSets)) {
                        return new MultipleDataSource(multipleDataSourceMap, (DataSource) dataSourceSets.toArray()[0]);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("multipleDataSource error %s", e.getMessage()), e);
            }
        }
        return null;
    }

    private Properties buildPropertiesFromEnvironment(Properties properties,String propertiesPrefix,String propertiesKey){
        String value  = environment.getProperty(propertiesPrefix+"."+propertiesKey);
        if(StringUtils.isNotBlank(value)){
            properties.put(propertiesKey,value);
        }
        return properties;
    }


    /**
     * 获取数据源
     *
     * @param propertiesPrefix
     * @param defaultPropertiesPrefix
     * @param connectionProperties
     * @return
     * @throws Exception
     */
    private DataSource getDruidDataSource(String propertiesPrefix, String defaultPropertiesPrefix, String connectionProperties) throws Exception {
        try {
            Properties props = new Properties();

            // 构建基础信息
            buildPropertiesFromEnvironment(props,propertiesPrefix,DruidDataSourceFactory.PROP_URL);
            buildPropertiesFromEnvironment(props,propertiesPrefix,DruidDataSourceFactory.PROP_USERNAME);
            buildPropertiesFromEnvironment(props,propertiesPrefix,DruidDataSourceFactory.PROP_PASSWORD);

            // 构建默认信息
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_DRIVERCLASSNAME);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_INITIALSIZE);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_MINIDLE);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_MAXACTIVE);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_MAXWAIT);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_TIMEBETWEENEVICTIONRUNSMILLIS);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_MINEVICTABLEIDLETIMEMILLIS);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_VALIDATIONQUERY);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_TESTWHILEIDLE);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_TESTONBORROW);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_TESTONRETURN);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_FILTERS);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_REMOVEABANDONED);
            buildPropertiesFromEnvironment(props,defaultPropertiesPrefix,DruidDataSourceFactory.PROP_LOGABANDONED);
            props.put(DruidDataSourceFactory.PROP_CONNECTIONPROPERTIES, connectionProperties);

            DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);
            druidDataSource.setName(propertiesPrefix);
            druidDataSource.init();
            return druidDataSource;
        } catch (Exception e) {
            throw new Exception(String.format("getDruidDataSource error %s propertiesPrefix<%s> defaultPropertiesPrefix<%s> connectionProperties<%s>", e.getMessage(), propertiesPrefix, defaultPropertiesPrefix, connectionProperties), e);
        }
    }


    private DataSource getHikariDataSource(String propertiesPrefix, String defaultPropertiesPrefix, String connectionProperties) throws Exception {
        try {
            // 构建基础信息
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(environment.getProperty(propertiesPrefix + "."+DruidDataSourceFactory.PROP_URL));
            hikariConfig.setUsername(environment.getProperty(propertiesPrefix + "."+DruidDataSourceFactory.PROP_USERNAME));
            hikariConfig.setPassword(environment.getProperty(propertiesPrefix + "."+DruidDataSourceFactory.PROP_PASSWORD));
            hikariConfig.setConnectionTestQuery(environment.getProperty(defaultPropertiesPrefix + "."+DruidDataSourceFactory.PROP_VALIDATIONQUERY));
            hikariConfig.setAutoCommit(true);
            hikariConfig.setMinimumIdle(Integer.parseInt(environment.getProperty(defaultPropertiesPrefix + "."+DruidDataSourceFactory.PROP_MINIDLE)));
            hikariConfig.setMaximumPoolSize(Integer.parseInt(environment.getProperty(defaultPropertiesPrefix + "."+DruidDataSourceFactory.PROP_MAXACTIVE)));
            hikariConfig.setIdleTimeout(Integer.parseInt(environment.getProperty(defaultPropertiesPrefix + "."+DruidDataSourceFactory.PROP_MINEVICTABLEIDLETIMEMILLIS)));
            hikariConfig.setPoolName(propertiesPrefix);
            HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
            return hikariDataSource;
        } catch (Exception e) {
            throw new Exception(String.format("getHikariDataSource error %s propertiesPrefix<%s> defaultPropertiesPrefix<%s> connectionProperties<%s>", e.getMessage(), propertiesPrefix, defaultPropertiesPrefix, connectionProperties), e);
        }
    }


    @Bean
    public MyBatisDataSourceSelector myBatisDataSourceSelector(@Autowired MultipleDataSource multipleDataSource, @Qualifier(BEAN_CLASS_PREFIX_MAP) Map<String, String> classPrefixMap) {
        return new ClassPrefixMyBatisDataSourceSelector(multipleDataSource, classPrefixMap);
    }

    /**
     * MyBatis 拦截器
     *
     * @return
     */
    @Bean
    public Interceptor[] mybatisInterceptors(@Autowired MyBatisDataSourceSelector myBatisDataSourceSelector) {
        return new Interceptor[]{
                new MultiDataSourceExecutorInteceptor(myBatisDataSourceSelector),
                new MultiDataSourceExecutorFinishInteceptor(myBatisDataSourceSelector),
                new MultiDataSourceExecutorCloseInteceptor(myBatisDataSourceSelector),

        };
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
