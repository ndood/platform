package com.fulu.game.common.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.PutObjectRequest;
import com.fulu.game.common.properties.Config;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class OssUtil {

    @Autowired
    private Config configProperties;

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    public String uploadFile(InputStream inputStream,String fileName){
        String endpoint =  configProperties.getOss().getEndpoint();
        String bucketName =  configProperties.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint,configProperties.getOss().getAccessKeyId(),configProperties.getOss().getAccessKeySecret());
        try {
            String key = generateOssKey(fileName);
            PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream);
            ossClient.putObject(request);
            boolean exists = ossClient.doesObjectExist(bucketName, key);
            if(exists){
                ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
                return  configProperties.getOss().getHost()+key;
            }
        }
        finally {
             ossClient.shutdown();
        }
        return null;
    }

    /**
     * 删除oss文件
     * @param fileName
     */
    public void deleteFile(String ... fileName){
        String endpoint =  configProperties.getOss().getEndpoint();
        String bucketName =  configProperties.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint,configProperties.getOss().getAccessKeyId(), configProperties.getOss().getAccessKeySecret());
        try {
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(formatOssKey(fileName)));
            log.info("oss删除文件:{}",deleteObjectsResult.getDeletedObjects());
        }
        catch (Exception e){
            log.error("oss删除异常",e);
        }
        finally {
            ossClient.shutdown();
        }
    }

    /**
     * 文件url路径格式化成ossKey
     * @param fileName
     * @return
     */
    private List<String> formatOssKey(String ... fileName){
        List<String> result = new ArrayList<>();
        for(String path:fileName){
            try {
                URL url = new URL(path);
                String key = url.getPath().substring(1);
                result.add(key);
            }catch (Exception e){
                log.error("文件路径格式化异常:",e);
            }
        }
        return result;
    }


    /**
     * 获取文件url路径
     * @param fileName
     * @return
     */
    private String generateOssKey(String fileName){
        StringBuilder sb = new StringBuilder();
        String uuid = GenIdUtil.GetGUID();
        String ext = FileUtil.extName(fileName);
        sb.append(DateUtil.thisYear())
          .append("/").append(DateUtil.thisMonth()+1)
          .append("/").append(DateUtil.thisDayOfMonth())
          .append("/").append(uuid).append(".").append(ext);
        return sb.toString();
    }

}
