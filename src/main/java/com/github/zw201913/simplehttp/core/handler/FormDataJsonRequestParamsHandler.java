package com.github.zw201913.simplehttp.core.handler;

import com.github.zw201913.simplehttp.support.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * 支持发送文件和JSON实体类
 *
 * @author zouwei
 */
@Slf4j
public class FormDataJsonRequestParamsHandler extends AbstractRequestParamsHandler {

    @Override
    public RequestBody handle(Map<String, Object> params, Map<String, File[]> files) {
        if (!CollectionUtils.isEmpty(params) || !CollectionUtils.isEmpty(files)) {
            return newFormDataRequestBody(params, files);
        }
        return null;
    }

    /**
     * 创建formdata请求体
     *
     * @param params
     * @param files
     * @return
     */
    private RequestBody newFormDataRequestBody(
            Map<String, Object> params, Map<String, File[]> files) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(
                    (key, value) ->
                            builder.addFormDataPart(
                                    key,
                                    "",
                                    RequestBody.create(
                                            MediaType.parse(JSON_UTF8),
                                            JsonUtils.obj2String(value))));
        }
        if (!CollectionUtils.isEmpty(files)) {
            files.forEach(
                    (key, value) -> {
                        if (!Objects.isNull(value) && value.length > 0) {
                            for (File file : value) {
                                builder.addFormDataPart(
                                        key,
                                        file.getName(),
                                        RequestBody.create(
                                                okhttp3.MediaType.parse(getMimeType(file)), file));
                            }
                        }
                    });
        }
        return builder.build();
    }
}
