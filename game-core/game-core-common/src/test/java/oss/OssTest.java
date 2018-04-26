package oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.*;

public class OssTest {


    private static String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static String accessKeyId = "LTAIcQRqtJrF5Arb";
    private static String accessKeySecret = "3jiWeZ89SoGob5TljUEU2MmgZCo7qP";
    private static String bucketName = "app-testing-new";

    public static void main(String[] args) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            String key = "wangbin.jpg";
            PutObjectRequest request = new PutObjectRequest(bucketName, key, createSampleFile());
            ossClient.putObject(request);
            boolean exists = ossClient.doesObjectExist(bucketName, key);
            System.out.println("Does object " + bucketName + " exist? " + exists + "\n");
            ossClient.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ossClient.shutdown();
        }

    }



    private static File createSampleFile() throws IOException {
        File file = new File("E:\\imgs","1524641088366.jpg");
        return file;
    }

}
