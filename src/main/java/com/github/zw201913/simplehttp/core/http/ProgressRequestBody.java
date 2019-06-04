package com.github.zw201913.simplehttp.core.http;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

/**
 * 有进度条的RequestBody
 *
 * @author zouwei
 */
public class ProgressRequestBody extends RequestBody {

    private final MultipartBody multipartBody;

    private ProgressListener progressListener;

    private long currentLength;

    /**
     * 构造函数
     *
     * @param multipartBody
     */
    public ProgressRequestBody(MultipartBody multipartBody) {
        this.multipartBody = multipartBody;
    }

    /**
     * 构造函数
     *
     * @param multipartBody
     * @param progressListener
     */
    public ProgressRequestBody(MultipartBody multipartBody, ProgressListener progressListener) {
        this(multipartBody);
        this.progressListener = progressListener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return multipartBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        // 转一下
        BufferedSink bufferedSink =
                Okio.buffer(
                        new ForwardingSink(sink) {
                            @Override
                            public void write(Buffer source, long byteCount) throws IOException {
                                super.write(source, byteCount);
                                // 回调进度
                                if (!Objects.isNull(progressListener)) {
                                    // 这里可以获取到写入的长度
                                    currentLength += byteCount;
                                    progressListener.onProgress(contentLength(), currentLength);
                                }
                            }
                        });
        // 写数据
        multipartBody.writeTo(bufferedSink);
        // 刷新一下数据
        bufferedSink.flush();
    }

    @Override
    public long contentLength() throws IOException {
        return multipartBody.contentLength();
    }
}
