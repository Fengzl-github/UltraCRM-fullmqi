package com.cn.mqi.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.cn.common.vo.ResCode;
import com.cn.common.vo.ResData;
import com.cn.mqi.config.resolver.RequestJsonArgumentResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *@Author fengzhilong
 *@Date 2021/9/27 16:27
 *@Desc
 **/
@Slf4j
@Configuration
public class MvcConfig extends WebMvcConfigurationSupport {

    /*配置此方法后,还是会出现乱码.其编码方式默认为UTF-8，但仍然出现乱码情况，由于SpringBoot的编码方式是跟随系统设置，因此将编码强制到Http请求和response响应中,在配置文件中添加配置*/
    /*@Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        //解决返回值中文乱码
        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        log.info("处理字符串中文乱码...");
        return converter;
    }*/


    /**
     * 消息类型转换配置
     * @param converters
     */
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        super.configureMessageConverters(converters);
        //1.先定义一个converter消息转换对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息
        FastJsonConfig fastConfig = new FastJsonConfig();
        fastConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat, //优化JSON格式
                SerializerFeature.DisableCircularReferenceDetect, //禁止循环引用
                /*
                 * WriteNullListAsEmpty  ：List字段如果为null,输出为[],而非null
                 * WriteNullStringAsEmpty ： 字符类型字段如果为null,输出为"",而非null
                 * DisableCircularReferenceDetect ：消除对同一对象循环引用的问题，默认为false（如果不配置有可能会进入死循环）
                 * WriteNullBooleanAsFalse：Boolean字段如果为null,输出为false,而非null
                 * WriteMapNullValue：是否输出值为null的字段,默认为false
                 */
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty
//                SerializerFeature.WriteMapNullValue
        );

        fastConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        fastConfig.getSerializeConfig().configEnumAsJavaBean(
                ResCode.class
        );
        fastConfig.setCharset(Charset.defaultCharset());
        //3.在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastConfig);
        fastConverter.setSupportedMediaTypes(getUpportedMediaTypes());
        //4.将convert添加到converters当中.
        converters.add(fastConverter);
        //配置返回中文编码
//        converters.add(responseBodyConverter());


    }


    /**
     * Content-Type不能含有通配符，强制要求用户自己配置MediaType
     * @return
     */
    @Bean
    public List<MediaType> getUpportedMediaTypes() {
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        mediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
        mediaTypeList.add(MediaType.APPLICATION_ATOM_XML);
        mediaTypeList.add(MediaType.APPLICATION_FORM_URLENCODED);
        mediaTypeList.add(MediaType.APPLICATION_OCTET_STREAM);
        mediaTypeList.add(MediaType.APPLICATION_PDF);
        mediaTypeList.add(MediaType.APPLICATION_RSS_XML);
        mediaTypeList.add(MediaType.APPLICATION_XHTML_XML);
        mediaTypeList.add(MediaType.APPLICATION_XML);
        mediaTypeList.add(MediaType.IMAGE_GIF);
        mediaTypeList.add(MediaType.IMAGE_JPEG);
        mediaTypeList.add(MediaType.IMAGE_PNG);
        mediaTypeList.add(MediaType.TEXT_EVENT_STREAM);
        mediaTypeList.add(MediaType.TEXT_HTML);
        mediaTypeList.add(MediaType.TEXT_MARKDOWN);
        mediaTypeList.add(MediaType.TEXT_PLAIN);
        mediaTypeList.add(MediaType.TEXT_XML);

        return mediaTypeList;
    }


    //自定义解析参数
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);


        argumentResolvers.add(new RequestJsonArgumentResolver());

    }


    @Override
    protected void addViewControllers(ViewControllerRegistry registry) {
        super.addViewControllers(registry);

        registry.addViewController("/").setViewName("index");
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("设置静态资源");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/webapp/");
    }

    @Override
    protected void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        log.info("设置视图前缀/后缀");
        viewResolver.setPrefix("/");
        viewResolver.setSuffix(".html");

        registry.viewResolver(viewResolver);
    }
}
