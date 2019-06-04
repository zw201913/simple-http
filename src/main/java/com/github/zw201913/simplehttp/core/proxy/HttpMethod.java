package com.github.zw201913.simplehttp.core.proxy;

import com.github.zw201913.simplehttp.annotation.Field;
import com.github.zw201913.simplehttp.annotation.Header;
import com.github.zw201913.simplehttp.core.factory.BaseOkHttpClientFactory;
import com.github.zw201913.simplehttp.core.handler.FormDataJsonRequestParamsHandler;
import com.github.zw201913.simplehttp.core.handler.RequestParamsHandler;
import com.github.zw201913.simplehttp.core.handler.ResponseHandler;
import com.github.zw201913.simplehttp.core.handler.SimpleJsonRequestParamsHandler;
import com.github.zw201913.simplehttp.core.http.*;
import com.github.zw201913.simplehttp.support.CastUtils;
import com.github.zw201913.simplehttp.support.ClassUtils;
import com.github.zw201913.simplehttp.support.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Callback;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 执行方法调用
 *
 * @author zouwei
 */
@Slf4j
public class HttpMethod {

    private static final String CALLBACK = "CALLBACK";
    private static final String PROGRESS_LISTENER = "PROGRESS_LISTENER";
    private static final String RESPONSE_HANDLER = "RESPONSE_HANDLER";

    private final Method method;
    private final HttpProxy httpProxy;
    private final HttpMethodType httpMethodType;
    private final List<ParameterType> parameterTypes = Lists.newArrayList();

    public HttpMethod(Method method, HttpProxy httpProxy, HttpMethodType httpMethodType) {
        this.method = method;
        this.httpProxy = httpProxy;
        this.httpMethodType = httpMethodType;
        headleHeader();
    }

    /** 处理参数上的注解 */
    private void headleHeader() {
        Parameter[] parameters = method.getParameters();
        if (Objects.isNull(parameters) || parameters.length <= 0) {
            return;
        }
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> type = parameter.getType();
            Header header = parameter.getDeclaredAnnotation(Header.class);
            Field field = parameter.getDeclaredAnnotation(Field.class);
            // 没有Header和Field注解
            if (Objects.isNull(header) && Objects.isNull(field)) {
                ParameterType parameterType = new ParameterType();
                parameterType.setIndex(i);
                parameterType.setType(type);
                parameterType.setKey(StringUtils.EMPTY);
                parameterType.setParameterPart(ParameterType.ParameterPart.NONE);
                parameterTypes.add(parameterType);
            }
            // Header注解
            if (!Objects.isNull(header)) {
                ParameterType parameterType = new ParameterType();
                parameterType.setIndex(i);
                parameterType.setType(type);
                parameterType.setKey(header.value());
                parameterType.setParameterPart(ParameterType.ParameterPart.HEADER);
                parameterTypes.add(parameterType);
            }
            // Field注解
            if (!Objects.isNull(field)) {
                ParameterType parameterType = new ParameterType();
                parameterType.setIndex(i);
                parameterType.setType(type);
                parameterType.setKey(field.value());
                parameterType.setParameterPart(ParameterType.ParameterPart.FIELD);
                parameterTypes.add(parameterType);
            }
        }
    }

    /**
     * 执行具体的方法
     *
     * @param args
     * @return
     * @throws Throwable
     */
    public Object execute(Object[] args) throws Throwable {
        if (Objects.isNull(httpMethodType)) {
            return null;
        }
        Map<String, Object> params = Maps.newHashMap();
        Map<String, File[]> files = Maps.newHashMap();
        Map<String, String> headers = Maps.newHashMap();
        // 可能包含params和files
        Map<String, Object> data = Maps.newHashMap();
        // 处理所有的ParameterType
        Map<String, Object> handlers = handleAllParameterType(args, headers, data);
        // 处理准备好的请求数据，把简单数据和文件分开
        handleData(data, params, files);

        Callback callback = (Callback) handlers.get(CALLBACK);
        ProgressListener progressListener = (ProgressListener) handlers.get(PROGRESS_LISTENER);
        ResponseHandler responseHandler = (ResponseHandler) handlers.get(RESPONSE_HANDLER);

        Object result = null;
        if (Objects.equals(httpMethodType.getMethodType(), HttpMethodType.MethodType.GET)) {
            // get请求
            result = handleGetHttp(headers, params, files, callback, progressListener);
        } else if (Objects.equals(httpMethodType.getMethodType(), HttpMethodType.MethodType.HEAD)) {
            // head请求
            result = handleHeadHttp(headers, params, files, callback, progressListener);
        } else if (Objects.equals(httpMethodType.getMethodType(), HttpMethodType.MethodType.POST)) {
            // post请求
            result = handlePostHttp(headers, params, files, callback, progressListener);
        } else if (Objects.equals(httpMethodType.getMethodType(), HttpMethodType.MethodType.PUT)) {
            // put请求
            result = handlePutHttp(headers, params, files, callback, progressListener);
        } else if (Objects.equals(
                httpMethodType.getMethodType(), HttpMethodType.MethodType.PATCH)) {
            // patch请求
            result = handlePatchHttp(headers, params, files, callback, progressListener);
        } else if (Objects.equals(
                httpMethodType.getMethodType(), HttpMethodType.MethodType.DELETE)) {
            // delete请求
            result = handleDeleteHttp(headers, params, files, callback, progressListener);
        }
        return handleResult(result, responseHandler);
    }

    /**
     * 处理响应
     *
     * @param result
     * @param responseHandler
     * @return
     * @throws IOException
     */
    private Object handleResult(Object result, ResponseHandler responseHandler) throws Exception {
        Class<?> returnType = method.getReturnType();
        String methodName = method.getName();
        /** 如果返回类型是Response */
        if (Objects.equals(returnType, Response.class)) {
            if (!(result instanceof Response)) {
                String logstr = methodName + "方法的返回类型和真实返回值类型不同";
                log.error(logstr);
                throw new IllegalArgumentException(logstr);
            }
            return result;
        }
        /** 如果返回类型是void，result是Response类型，需要先关闭Response，直接返回null */
        if (Objects.equals(returnType, void.class) && result instanceof Response) {
            Response response = (Response) result;
            response.close();
            return null;
        }
        // 如果是其他类型，需要自定义处理器
        Response response = null;
        try {
            response = (Response) result;
            if (Objects.equals(returnType, String.class)) {
                if (response.isSuccessful()) {
                    return response.body().string();
                }
                String message = response.message();
                log.error(message);
                throw new Exception(message);
            }
            if (Objects.isNull(responseHandler)) {
                String logstr = "需要指定一个ResponseHandler处理返回的Response";
                log.error(logstr);
                throw new IllegalArgumentException(logstr);
            }
            return responseHandler.handle(response);
        } finally {
            if (!Objects.isNull(response)) {
                response.close();
            }
        }
    }

    @Data
    private static class ParameterType {
        private String methodName;
        private int index;
        private String key;
        private Class<?> type;
        private ParameterPart parameterPart;

        enum ParameterPart {
            HEADER,
            FIELD,
            NONE
        }
    }

    /**
     * 处理准备好的请求数据，把简单数据和文件分开
     *
     * @param data
     * @param params
     * @param files
     */
    private void handleData(
            Map<String, Object> data, Map<String, Object> params, Map<String, File[]> files) {
        if (CollectionUtils.isEmpty(data)) {
            return;
        }
        data.forEach(
                (k, v) -> {
                    Class<?> clazz = v.getClass();
                    if (ClassUtils.isFile(clazz)) {
                        files.put(k, new File[] {(File) v});
                    } else if (ClassUtils.isFileArray(clazz)) {
                        files.put(k, (File[]) v);
                    } else {
                        params.put(k, v);
                    }
                });
    }

    /**
     * 将请求参数和请求头数据封装成ParameterType列表
     *
     * @param args
     * @param headers
     * @param data
     */
    private Map<String, Object> handleAllParameterType(
            Object[] args, Map<String, String> headers, Map<String, Object> data)
            throws IllegalAccessException {

        Map<String, Object> handlers = Maps.newHashMap();
        String methodName = method.getName();
        // 检查header
        for (ParameterType parameterType : parameterTypes) {
            ParameterType.ParameterPart parameterPart = parameterType.getParameterPart();

            Class<?> type = parameterType.getType();
            int index = parameterType.getIndex();
            Object arg = args[index];
            if (Objects.isNull(arg)) {
                continue;
            }
            if (Objects.equals(type, Callback.class)) {
                Object callbackObject = handlers.get(CALLBACK);
                if (Objects.isNull(callbackObject)) {
                    handlers.put(CALLBACK, arg);
                } else {
                    log.warn("存在多个Callback，只有最后一个会生效");
                }
                continue;
            }
            if (Objects.equals(type, ProgressListener.class)) {
                Object progressListener = handlers.get(PROGRESS_LISTENER);
                if (Objects.isNull(progressListener)) {
                    handlers.put(PROGRESS_LISTENER, arg);
                } else {
                    log.warn("存在多个ProgressListener，只有最后一个会生效");
                }
                continue;
            }
            if (Objects.equals(type, ResponseHandler.class)) {
                Object responseHandler = handlers.get(RESPONSE_HANDLER);
                if (Objects.isNull(responseHandler)) {
                    handlers.put(RESPONSE_HANDLER, arg);
                } else {
                    log.warn("存在多个ResponseHandler，只有最后一个会生效");
                }
                continue;
            }
            if (Objects.equals(parameterPart, ParameterType.ParameterPart.NONE)) {
                if (ClassUtils.isSimpleType(type)) {
                    String logstr = methodName + "方法请求参数不合规则";
                    log.error(logstr);
                    throw new IllegalArgumentException(logstr);
                }
                if (Objects.equals(type, Map.class)) {
                    Map<String, Object> map = (Map<String, Object>) arg;
                    data.putAll(map);
                } else if (Objects.equals(type, Collection.class)) {
                    log.warn(methodName + "方法不支持没注解的" + type.getName() + "类型");
                } else {
                    handleObject(arg, data, headers, true);
                }
            } else if (Objects.equals(parameterPart, ParameterType.ParameterPart.HEADER)) {
                String key = parameterType.getKey();
                if (StringUtils.isBlank(key)) {
                    if (ClassUtils.isSimpleType(type)) {
                        String logstr = methodName + "方法的Header参数缺少指定的key值";
                        log.error(logstr);
                        throw new IllegalArgumentException(logstr);
                    } else {
                        handleObject(arg, data, headers, false);
                    }
                } else {
                    headers.put(key, CastUtils.castString(arg));
                }
            } else if (Objects.equals(parameterPart, ParameterType.ParameterPart.FIELD)) {
                String key = parameterType.getKey();
                if (StringUtils.isBlank(key)) {
                    if (ClassUtils.isSimpleType(type)) {
                        String logstr = methodName + "方法的Field参数缺少指定的key值";
                        log.error(logstr);
                        throw new IllegalArgumentException(logstr);
                    } else {
                        handleObject(arg, data, headers, true);
                    }
                } else {
                    data.put(key, args[index]);
                }
            }
        }
        return handlers;
    }

    /**
     * 处理自定义对象
     *
     * @param arg
     * @param data
     * @param header
     */
    private void handleObject(
            Object arg, Map<String, Object> data, Map<String, String> header, boolean isField)
            throws IllegalAccessException {
        Class<?> clazz = arg.getClass();
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(arg);
            if (field.isAnnotationPresent(Field.class)) {
                Field f = field.getDeclaredAnnotation(Field.class);
                String key = f.value();
                if (!StringUtils.isBlank(f.value())) {
                    name = key;
                }
                data.put(name, value);
            } else if (field.isAnnotationPresent(Header.class)) {
                Header h = field.getDeclaredAnnotation(Header.class);
                String key = h.value();
                if (!StringUtils.isBlank(h.value())) {
                    name = key;
                }
                header.put(name, CastUtils.castString(value));
            } else {
                if (isField) {
                    data.put(name, value);
                } else {
                    header.put(name, CastUtils.castString(value));
                }
            }
        }
    }

    /**
     * 发送get请求
     *
     * @param headers
     * @param params
     * @param files
     * @return
     */
    private Object handleGetHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleSimpleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                (okHttpClientFactory) -> HttpUtils.getHttp(okHttpClientFactory));
    }

    private Object handlePostHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                okHttpClientFactory -> HttpUtils.postHttp(okHttpClientFactory));
    }

    private Object handlePutHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                okHttpClientFactory -> HttpUtils.putHttp(okHttpClientFactory));
    }

    private Object handleHeadHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleSimpleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                (okHttpClientFactory) -> HttpUtils.headHttp(okHttpClientFactory));
    }

    private Object handleDeleteHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                okHttpClientFactory -> HttpUtils.deleteHttp(okHttpClientFactory));
    }

    private Object handlePatchHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener)
            throws IOException {
        return handleHttp(
                headers,
                params,
                files,
                callback,
                progressListener,
                okHttpClientFactory -> HttpUtils.patchHttp(okHttpClientFactory));
    }

    private Object handleSimpleHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener,
            HttpFactory httpFactory)
            throws IOException {
        String url = httpMethodType.getUrl();
        BaseOkHttpClientFactory okHttpClientFactory = httpMethodType.getOkHttpClientFactory();
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("请求url不能为空");
        }
        if (!CollectionUtils.isEmpty(files)) {
            log.warn("该请求不支持上传文件");
        }
        if (!Objects.isNull(progressListener)) {
            log.warn("该请求不支持监听器");
        }
        AbstractHttp http = httpFactory.newHttp(okHttpClientFactory);

        if (Objects.isNull(callback)) {
            return http.send(url, headers, params);
        } else {
            http.sendAsync(url, headers, params, callback);
            return null;
        }
    }

    private Object handleHttp(
            Map<String, String> headers,
            Map<String, Object> params,
            Map<String, File[]> files,
            Callback callback,
            ProgressListener progressListener,
            HttpFactory httpFactory)
            throws IOException {
        String url = httpMethodType.getUrl();
        BaseOkHttpClientFactory okHttpClientFactory = httpMethodType.getOkHttpClientFactory();
        Class<? extends RequestParamsHandler> handlerClass = httpMethodType.getHandlerClass();
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("请求url不能为空");
        }
        AbstractHttp http = httpFactory.newHttp(okHttpClientFactory);
        if (Objects.equals(handlerClass, SimpleJsonRequestParamsHandler.class)
                && !CollectionUtils.isEmpty(files)) {
            handlerClass = FormDataJsonRequestParamsHandler.class;
        }
        http.handler(handlerClass);
        if (Objects.isNull(callback)) {
            return http.send(url, headers, params, files, progressListener);
        } else {
            http.sendAsync(url, headers, params, files, progressListener, callback);
            return null;
        }
    }

    interface HttpFactory {
        AbstractHttp newHttp(BaseOkHttpClientFactory okHttpClientFactory);
    }
}
