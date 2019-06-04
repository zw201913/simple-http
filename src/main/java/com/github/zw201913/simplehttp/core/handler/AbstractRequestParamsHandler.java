package com.github.zw201913.simplehttp.core.handler;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import okhttp3.MediaType;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Collection;

/** @author zouwei */
public abstract class AbstractRequestParamsHandler implements RequestParamsHandler {

    protected static final MediaType DEFAULT_MEDIA_TYPE =
            MediaType.parse("application/octet-stream");

    static {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }
    /**
     * 获取文件的MimeType
     *
     * @param file
     * @return
     */
    protected static String getMimeType(File file) {
        Collection<MimeType> mimeTypes = getMimeTypes(file);
        if (CollectionUtils.isEmpty(mimeTypes)) {
            return DEFAULT_MEDIA_TYPE.toString();
        }
        return mimeTypes.iterator().next().toString();
    }

    /**
     * 获取文件的MimeType
     *
     * @param file
     * @return
     */
    protected static Collection<MimeType> getMimeTypes(File file) {
        Collection<MimeType> mimeTypes = MimeUtil.getMimeTypes(file);
        return mimeTypes;
    }
}
