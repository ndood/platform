package com.fulu.game.common.utils;

import cn.hutool.core.date.DateUtil;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class MailUtil {


    public static boolean sendMail(String emailTitle,String emailContent , String[] targetEmail){

        try {
            Properties pr = new Properties();
            pr.put("mail.transport.protocol", "smtp");// 连接协议
            pr.put("mail.smtp.host", "smtp.qiye.aliyun.com");// 主机名
            pr.put("mail.smtp.port", 465);// 端口号
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
            pr.put("mail.debug", "true");// 设置是否显示debug信息 true 会在控制台显示相关信息
            // 得到会话对象
            Session session = Session.getInstance(pr);
            // 获取邮件对象
            Message message = new MimeMessage(session);
            // 设置发件人邮箱地址
            message.setFrom(new InternetAddress("yangxudong@fulu.com"));
            // 设置收件人邮箱地址 
            InternetAddress[] ia = new InternetAddress[targetEmail.length];
            for(int i = 0 ; i < ia.length ; i++){
                ia[i] = new InternetAddress(targetEmail[i]);
            }
            message.setRecipients(Message.RecipientType.TO, ia);
            // 设置邮件标题
            message.setSubject(emailTitle);
            // 设置邮件内容
            message.setText(emailContent);
            // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户
            transport.connect("yangxudong@fulu.com", "Yang37520");// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        
    }
}
