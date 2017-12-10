package com.dollapi;

import com.common.pay.PayAPI;
import com.dollapi.filter.SignFilter;
import com.dollapi.filter.WebLoginFilter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexu on 2017/9/6.
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    @Value("${aliAppId}")
    private String aliAppId;

    @Value("${aliAppPrivateKey}")
    private String aliAppPrivateKey;

    @Value("${aliAppPublicKey}")
    private String aliAppPublicKey;

    @Value("${aliPublicKey}")
    private String aliPublicKey;

    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        SignFilter signFilter = new SignFilter();
//        registrationBean.setFilter(signFilter);
//        List<String> urlPatterns = new ArrayList<String>();
//        urlPatterns.add("/*");
//        registrationBean.setUrlPatterns(urlPatterns);
//        return registrationBean;
//    }

    @Bean
    public FilterRegistrationBean filterWebLoginBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        WebLoginFilter webLoginFilter = new WebLoginFilter();
        registrationBean.setFilter(webLoginFilter);
        List<String> urlPatterns = new ArrayList<String>();
        urlPatterns.add("/admin/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setValidationInterval(10000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setMaxActive(50);
        dataSource.setMaxIdle(10);
        dataSource.setMinIdle(0);
        dataSource.setDefaultAutoCommit(false);
        return dataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public PayAPI api() {
        PayAPI api = PayAPI.instance().ali(aliAppId, aliAppPrivateKey, aliAppPublicKey, aliPublicKey, "json", "RSA");
        return api;
    }

}
