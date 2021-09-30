package com.cn.mqi.config.resolver;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 *@Author fengzhilong
 *@Date 2021/9/28 10:54
 *@Desc
 **/
@Slf4j
public class RequestJsonArgumentResolver extends AbstractNamedValueMethodArgumentResolver {
    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {

        return new RequestJsonNamedValueInfo();
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        MultipartHttpServletRequest multipartRequest =
                WebUtils.getNativeRequest(servletRequest, MultipartHttpServletRequest.class);

        Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
        if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
            return mpArg;
        }

        Object arg = null;
        if (multipartRequest != null) {
            List<MultipartFile> files = multipartRequest.getFiles(name);
            if (!files.isEmpty()) {
                arg = (files.size() == 1 ? files.get(0) : files);
            }
        }
        if (arg == null) {
            RequestBodyWrapper jsonRequest = RequestUtils.toRequestBodyWrapper(servletRequest);
            //如果 请求体不是json或者json体是空，则按@RequestParameter的方式获得参数
            if (jsonRequest == null || jsonRequest.isEmpty()) {
                String[] paramValues = request.getParameterValues(name);
                if (paramValues != null) {
                    arg = (paramValues.length == 1 ? paramValues[0] : paramValues);
                } else {
                }
            } else {
                JSONObject jsonbody = jsonRequest.getJSONObject();
                if (jsonbody == null) {
                    return null;
                }
                //要解析的请求参数实体类型支持JSONObject，如果JSONObject则进入次判断
                if (JSONObject.class.isAssignableFrom(parameter.getParameterType()) ||
                        JSONObject.class.equals(parameter.getParameterType())) {
                    if (jsonbody.containsKey(name)) {
                        return jsonbody.getJSONObject(name);
                    }
                    return jsonbody;
                }
                //如果json结构体基础结构是非base，body的，则进入如下判断
                if (!jsonbody.containsKey("base") && !jsonbody.containsKey("body")) {
                    if (jsonbody.containsKey(name)) {
                        arg = getObject(jsonbody, name, parameter);

                    } else if (name.equals("body")) {
                        arg = JSONObject.toJavaObject(jsonbody, parameter.getParameterType());
                    }
//                    else if(!"base".equals(name) && !"body".equals(name) && !isWrapClass(parameter.getParameterType())){
//                        arg = JSONObject.toJavaObject(jsonbody, parameter.getParameterType());
//                    }
                } else {
                    //如果json结构体基础结构是基于base，body的，则进入如下判断
                    if (name.equals("base") || name.equals("body")) {
                        if (MapBody.class.isAssignableFrom(parameter.getParameterType()) ||
                                MapBody.class.equals(parameter.getParameterType())) {
                            JSONObject jsonObject = jsonbody.getJSONObject(name);
                            Map map = JSONObject.toJavaObject(jsonObject, Map.class);
                            arg = new MapBody(map);
                        } else {
                            arg = getObject(jsonbody, name, parameter);
                        }
                    } else {
                        if (JSONPath.contains(jsonbody, "$.body." + name)) {
                            arg = jsonbody.getJSONObject("body").getObject(name, parameter.getParameterType());
                        } else if (JSONPath.contains(jsonbody, "$.base." + name)) {
                            arg = jsonbody.getJSONObject("base").getObject(name, parameter.getParameterType());
                        } else {
                            arg = null; //jsonbody.getObject("body", parameter.getParameterType());
                        }
                    }
                }
            }
        }
        return arg;
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotations() == null || parameter.getParameterAnnotations().length == 0
                || parameter.hasParameterAnnotation(NotNull.class) || parameter.hasParameterAnnotation(NotBlank.class);
    }


    public static class RequestJsonNamedValueInfo extends NamedValueInfo {

        public RequestJsonNamedValueInfo() {
            super("", false, ValueConstants.DEFAULT_NONE);
        }
    }


    private Object getObject(JSONObject body, String name, MethodParameter parameter) {
        if (List.class.isAssignableFrom(parameter.getParameterType()) ||
                List.class.equals(parameter.getParameterType())) {
            Type type = parameter.getGenericParameterType();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                //获取参数的类型
                System.out.println(parameterizedType.getRawType());
                //获取参数的泛型列表
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                    Class t = (Class) actualTypeArguments[0];
                    System.out.println(t);
                    return body.getJSONArray(name).toJavaList(t);
                }
            }
            return body.getJSONArray(name).toJavaList(Object.class);
        }
        return body.getObject(name, parameter.getParameterType());
    }
}
