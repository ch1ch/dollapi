package com.dollapi.util;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ImageUploadTools {

//    private static final String ALIYUN_OSS_ACCESS_ID="LTAIOtMNzHLxxs0w";
//    private static final String ALIYUN_OSS_ACCESS_KEY="ecqegSn6j30wA17KhcPynxnmkUDCzZ";
//    private static final String ALIYUN_OSS_BUCKET_NAME="doll";
//    public static final String ALIYUN_QR_URL = "http://doll.oss-cn-beijing.aliyuncs.com/";
    private static final String ALIYUN_OSS_ACCESS_ID="LTAIsELyQ2IYcIZ2";
    private static final String ALIYUN_OSS_ACCESS_KEY="jX06pkKE8SPVXcFFDa5nM1DhflBzn5";
    private static final String ALIYUN_OSS_BUCKET_NAME="efun-oss";
    public static final String ALIYUN_QR_URL = "http://efun-oss.oss-cn-beijing.aliyuncs.com/";

    private static Logger logger = LoggerFactory.getLogger(ImageUploadTools.class);


    public static String uploadQrFile(String fileName, InputStream is, String path) throws IOException {
        // 使用默认的OSS服务器地址创建OSSClient对象。
//        OSSClient client = new OSSClient("http://oss-cn-beijing.aliyuncs.com/",
        OSSClient client = new OSSClient("http://oss-cn-beijing.aliyuncs.com/",
                ALIYUN_OSS_ACCESS_ID, ALIYUN_OSS_ACCESS_KEY);

        String key = path + fileName;
        try {
            uploadFileQr(client, ALIYUN_OSS_BUCKET_NAME, key, is, fileName);
        } catch (Exception e) {
            logger.error(" OOSClient 上传图片失败 ");
            e.printStackTrace();
        } finally {
            // 不关闭client吗？
        }
        return ALIYUN_QR_URL + key;
    }

    // 上传文件
    private static void uploadFileQr(OSSClient client, String bucketName, String key, InputStream in, String fileName) throws Exception {

        ObjectMetadata objectMeta = new ObjectMetadata();
//        objectMeta.setContentType(fileName.substring(fileName.lastIndexOf(".")));
        objectMeta.setContentLength(in.available());
        objectMeta.setCacheControl("no-cache");
        objectMeta.setHeader("Pragma", "no-cache");
        objectMeta.setContentDisposition("inline;filename=" + fileName);
        client.putObject(bucketName, key, in, objectMeta);
    }

}
