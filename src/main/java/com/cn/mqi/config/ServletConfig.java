package com.cn.mqi.config;

import com.cn.mqi.base.PmSys;
import com.cn.mqi.pmClass.Fun_main;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

/**
 *@Author fengzhilong
 *@Date 2021/9/27 9:49
 *@Desc
 **/
@Slf4j
@Configuration
public class ServletConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {

        Fun_main.getParaIni();
        log.info("配置项目端口:" + PmSys.Server_Port);
        factory.setPort(PmSys.Server_Port);
    }
}
