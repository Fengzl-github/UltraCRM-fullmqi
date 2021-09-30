package com.cn.mqi.config;

import com.cn.mqi.base.PmSys;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 *@Author fengzhilong
 *@Date 2021/9/23 16:22
 *@Desc 数据源配置
 **/
@Slf4j
@Configuration
public class DataSourceConfig {


    @Bean(name = "callthinkDataSource")
    @Qualifier("callthinkDataSource")
    public static DataSource callthinkDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        if ("mysql".equals(PmSys.data_type.toLowerCase())) {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            //jdbc:mysql://39.96.15.143:7982/saas?autoReconnect=true
            dataSource.setJdbcUrl("jdbc:mysql://" + PmSys.data_host + "/" + PmSys.data_base + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=true");
            dataSource.setUsername(PmSys.data_uid);
        } else if ("oracle".equals(PmSys.data_type.toLowerCase())) {
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
            //jdbc:oracle:thin:@localhost:1521:ORCL
            dataSource.setJdbcUrl("jdbc:oracle:thin:@" + PmSys.data_host + ":ORCL");
            dataSource.setUsername(PmSys.data_uid);
        }
        dataSource.setPassword(PmSys.data_pwd);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(180000);
        dataSource.setMaximumPoolSize(10);
        dataSource.setConnectionInitSql("SELECT 1");
        log.info("callthink数据源配置,数据库类型:{}", PmSys.data_type);

        return dataSource;
    }


    @Bean(name = "crmDataSource")
    @Qualifier("crmDataSource")
    public static DataSource crmDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        if ("mysql".equals(PmSys.data_type_crm.toLowerCase())) {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            //jdbc:mysql://39.96.15.143:7982/saas?autoReconnect=true
            dataSource.setJdbcUrl("jdbc:mysql://" + PmSys.data_host_crm + "/" + PmSys.data_base_crm + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=true");
            dataSource.setUsername(PmSys.data_uid_crm);
            dataSource.setConnectionInitSql("SELECT 1");
        } else if ("oracle".equals(PmSys.data_type_crm.toLowerCase())) {
            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
            //jdbc:oracle:thin:@localhost:1521:ORCL
            dataSource.setJdbcUrl("jdbc:oracle:thin:@" + PmSys.data_host_crm);
            dataSource.setUsername(PmSys.data_uid_crm);
            dataSource.setConnectionInitSql("SELECT * FROM DUAL");
        }
        dataSource.setPassword(PmSys.data_pwd_crm);
        dataSource.setMinimumIdle(5);
        dataSource.setIdleTimeout(180000);
        dataSource.setMaximumPoolSize(10);
        log.info("crm数据源配置,数据库类型:{}", PmSys.data_type_crm);

        return dataSource;
    }

    @Bean(name = "callthinkJdbc")
    public JdbcTemplate callthinkJDBC(@Qualifier("callthinkDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "crmJdbc")
    public JdbcTemplate crmJDBC(@Qualifier("crmDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    public static JdbcTemplate getCallthinkJdbc() {
        return new JdbcTemplate(callthinkDataSource());
    }


    public static JdbcTemplate getCrmJdbc() {
        return new JdbcTemplate(crmDataSource());
    }


}
