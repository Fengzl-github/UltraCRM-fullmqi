package com.cn.mqi;

import com.cn.mqi.words.sens.WordBannerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@EnableScheduling
@EnableAsync
@EnableCaching
@ServletComponentScan(basePackages = "com.cn.mqi")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class UltraCrmFullmqiApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
        SpringApplication.run(UltraCrmFullmqiApplication.class, args);
    }


    @PostConstruct
    public void sysinitConfig() {

        log.info("项目初始化...");
//        Fun_main.getParaIni();

        WordBannerService.getWordBannerService().init();
    }

    @PreDestroy
    public void destory(){

        log.info("UltraCRM-fullmqi服务已停止...");
    }

}
