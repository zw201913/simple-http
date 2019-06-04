package com.github.zw201913.simplehttp.core.handler;

import com.github.zw201913.simplehttp.support.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Map;

/**
 * 只支持JSON实体类
 *
 * @author zouwei
 */
@Slf4j
public class SimpleJsonRequestParamsHandler extends AbstractRequestParamsHandler {

    @Override
    public RequestBody handle(Map<String, Object> params, Map<String, File[]> files) {
        if (!CollectionUtils.isEmpty(params)) {
            return newRequestBody(params);
        }
        return null;
    }
    /**
     * 创建简单json请求体
     *
     * @param params
     * @return
     */
    private RequestBody newRequestBody(Map<String, Object> params) {
        if (CollectionUtils.isEmpty(params)) {
            log.error("request params is empty");
            throw new IllegalArgumentException("request params is empty");
        }
        return RequestBody.create(MediaType.parse(JSON_UTF8), JsonUtils.obj2String(params));
    }
}
