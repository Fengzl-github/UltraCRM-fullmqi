package com.cn.mqi.config.resolver;


import com.alibaba.fastjson.JSONObject;
import com.cn.common.utils.RemoteAddrUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import static com.cn.mqi.config.resolver.LogConstant.REQUEST_ID;
import static com.cn.mqi.config.resolver.LogConstant.REQUEST_ID_PREFIX;

/**
 * 转换封装json文本成为JsonObject对象，方便后面使用
 * Created by yml on 2016/11/1.
 */
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "RequestWrapperFilter")
public class RequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("=================== 初始化过滤器 ===================");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            if (!isFilterExcludeRequest(request)) {
                String requestUuid = REQUEST_ID_PREFIX + System.currentTimeMillis();
                MDC.put(REQUEST_ID, requestUuid);
                request.setAttribute(REQUEST_ID, requestUuid);
                log.info("-----------------------------------------------------------");
                log.info("                                                           ");
                log.info(" ## request method -> {}", request.getMethod());
                log.info(" ## request uri -> {}", request.getRequestURI());
                log.info(" ## request ip -> {}", RemoteAddrUtils.getIpAddr(request));
                if (servletRequest.getContentType() != null
                        && (servletRequest.getContentType().toLowerCase().contains("json")
                )) {
                    RequestBodyWrapper myRequestWrapper = new RequestBodyWrapper((HttpServletRequest) servletRequest);
                    log.info(" ## request params -> {}", myRequestWrapper.getJsonText());
                    log.info("                                                           ");
                    log.info("-----------------------------------------------------------");
                    filterChain.doFilter(myRequestWrapper, servletResponse);
                } else {
                    log.info(" ## request params -> {}", JSONObject.toJSONString(request.getParameterMap()));
                    log.info("                                                           ");
                    log.info("-----------------------------------------------------------");
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFilterExcludeRequest(HttpServletRequest request) {
        String strUri = request.getRequestURI();
        return strUri.equals("/") || strUri.startsWith("/page") || strUri.endsWith(".ico") || strUri.startsWith("/assets") || strUri.startsWith("/images");
    }

    @Override
    public void destroy() {

    }
}
