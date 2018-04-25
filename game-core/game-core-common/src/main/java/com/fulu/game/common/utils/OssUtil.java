package com.fulu.game.common.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import com.fulu.game.common.properties.Config;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.io.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
public class OssUtil {

    @Autowired
    private Config config;

    /**
     * 上传文件
     * @param inputStream
     * @param fileName
     * @return
     */
    public String uploadFile(InputStream inputStream,String fileName){
        String endpoint =  config.getOss().getEndpoint();
        String bucketName =  config.getOss().getBucketName();
        OSSClient ossClient = new OSSClient(endpoint,config.getOss().getAccessKeyId(),config.getOss().getAccessKeySecret());
        String key = generateOssKey(fileName);
        PutObjectRequest request = new PutObjectRequest(bucketName, key, inputStream);
        ossClient.putObject(request);
        boolean exists = ossClient.doesObjectExist(bucketName, key);
        if(exists){
            ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
            return  config.getOss().getHost()+key;
        }
        return null;
    }



    private String generateOssKey(String fileName){
        StringBuilder sb = new StringBuilder();
        String uuid = UUID.randomUUID().toString().toLowerCase();
        String ext = FileUtil.extName(fileName);
        sb.append(DateUtil.thisYear())
          .append("/").append(DateUtil.thisMonth()+1)
          .append("/").append(DateUtil.thisDayOfMonth())
          .append("/").append(uuid).append(".").append(ext);
        return sb.toString();
    }

}
