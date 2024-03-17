package com.xmxisme.email.test;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

@Log4j2
public class Email {

    public static void main(String[] args) {
        code();
    }

    /****
     * 提取邮箱中的验证码
     *
     * @return java.lang.String 验证码
     * @author XiaoMingXin
     * @date 2024/3/17 0:41
    */
    public static List<String> code() {
        final String username = "1366042884@qq.com"; // 邮箱地址
        final String password = "nlll"; // 邮箱密码

        Properties props = new Properties();

        // 设置 POP3 服务器属性(收)
        props.put("mail.pop3.host", "pop.qq.com");
        props.put("mail.pop3.socketFactory.port", "995");
        props.put("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.pop3.auth", "true");

        // 设置 SMTP 服务器属性(发)
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.socketFactory.port", "465"); // 或者 587
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465"); // 或者 587

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // 接收邮件
            Store store = session.getStore("pop3s");
            store.connect("pop.qq.com", username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();
            for (Message message : messages) {
                try {
                    String subject = message.getSubject();
                    if (Objects.equals(subject, "Microsoft 帐户安全代码")) {
                        MimeMultipart multipart = (MimeMultipart)message.getContent();
                        String content = getTextFromMimeMultipart(multipart);
                        System.out.println(content);
                    }

                } catch (Exception e) {
                    log.error("e：", e);
                }

            }

            // 关闭连接
            inbox.close(false);
            store.close();

            // 发送邮件
//            Transport.send(createEmailMessage(session, username, "xmxisme@gmail.com", "Test Subject", "Test Content"));

        } catch (Exception e) {
            log.error("e：", e);
        }
        return null;
    }

    private static Message createEmailMessage(Session session, String from, String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    public static String getTextFromMimeMultipart(MimeMultipart multipart) throws MessagingException, IOException {
        StringBuilder text = new StringBuilder();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                text.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("multipart/*")) {
                text.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return text.toString();
    }

}
