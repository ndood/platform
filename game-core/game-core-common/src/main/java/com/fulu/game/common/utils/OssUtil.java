package com.fulu.game.common.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.PutObjectRequest;
import com.fulu.game.common.exception.SystemException;
import com.fulu.game.common.properties.Config;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class OssUtil {

    @Autowired
    private Config configProperties;

    public static final String TEMP = "temp/";

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    public String uploadFile(InputStream inputStream, String fileName) {
        String endpoint = configProperties.getOss().getEndpoint();
        String bucketName = configProperties.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint, configProperties.getOss().getAccessKeyId(), configProperties.getOss().getAccessKeySecret());
        try {
            String key = generateTempOssKey(fileName);
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream);
            ossClient.putObject(request);
            boolean exists = ossClient.doesObjectExist(bucketName, key);
            if (exists) {
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
                return configProperties.getOss().getHost() + key;
            }
        } finally {
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * 激活oss文件,删除临时上传的文件
     * @param fileUrl
     * @return
     */
    public String activateOssFile(String fileUrl){
        if(StringUtils.isBlank(fileUrl)){
            log.error("oss激活文件名为空");
            return null;
        }
        String sourceKey = formatOssKey(fileUrl);
        if(!sourceKey.contains("temp")){
            return fileUrl;
        }
        String destinationKey = sourceKey.replace(TEMP,"");
        String endpoint = configProperties.getOss().getEndpoint();
        String bucketName = configProperties.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint, configProperties.getOss().getAccessKeyId(), configProperties.getOss().getAccessKeySecret());
        try {
            ossClient.copyObject(bucketName,sourceKey,bucketName,destinationKey);
            boolean exists = ossClient.doesObjectExist(bucketName, destinationKey);
            if (exists) {
                DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(CollectionUtil.newArrayList(sourceKey)));
                log.info("oss删除文件:{}", deleteObjectsResult.getDeletedObjects());
                ossClient.setObjectAcl(bucketName, destinationKey, CannedAccessControlList.PublicRead);
                return configProperties.getOss().getHost() + destinationKey;
            }
        }finally {
            ossClient.shutdown();
        }
        return null;
    }


    /**
     * 删除oss文件
     * @param fileUrl
     */
    public void deleteFile(String... fileUrl) {
        String endpoint = configProperties.getOss().getEndpoint();
        String bucketName = configProperties.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint, configProperties.getOss().getAccessKeyId(), configProperties.getOss().getAccessKeySecret());
        try {
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(formatOssKey(fileUrl)));
            log.info("oss删除文件:{}", deleteObjectsResult.getDeletedObjects());
        } catch (Exception e) {
            log.error("oss删除异常", e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 文件url路径格式化成ossKey
     * @param fileUrl
     * @return
     */
    private String formatOssKey(String fileUrl) {
        URL url = null;
        try {
            url = new URL(fileUrl);
        } catch (Exception e) {
            throw new SystemException(SystemException.ExceptionCode.URL_PATH_ERROR);
        }
        return url.getPath().substring(1);
    }


    /**
     * 文件url路径格式化成ossKey
     * @param fileUrl
     * @return
     */
    private List<String> formatOssKey(String... fileUrl) {
        List<String> result = new ArrayList<>();
        for (String path : fileUrl) {
            try {
                URL url = new URL(path);
                String key = url.getPath().substring(1);
                result.add(key);
            } catch (Exception e) {
                log.error("文件路径格式化异常:", e);
            }
        }
        return result;
    }


    /**
     * 获取文件url路径
     *
     * @param fileName
     * @return
     */
    private String generateOssKey(String fileName) {
        StringBuilder sb = new StringBuilder();
        String uuid = GenIdUtil.GetGUID();
        String ext = FileUtil.extName(fileName);
        sb.append(DateUtil.thisYear())
                .append("/").append(DateUtil.thisMonth() + 1)
                .append("/").append(DateUtil.thisDayOfMonth())
                .append("/").append(uuid).append(".").append(ext);
        return sb.toString();
    }

    /**
     * 创建临时的temp
     *
     * @param fileName
     * @return
     */
    private String generateTempOssKey(String fileName) {
        StringBuilder sb = new StringBuilder();
        String uuid = GenIdUtil.GetGUID();
        String ext = FileUtil.extName(fileName);
        sb.append(TEMP)
                .append(DateUtil.thisYear())
                .append("/").append(DateUtil.thisMonth() + 1)
                .append("/").append(DateUtil.thisDayOfMonth())
                .append("/").append(uuid).append(".").append(ext);
        return sb.toString();
    }


}
