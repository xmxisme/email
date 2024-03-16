package com.xmxisme.email;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Properties;
import javax.mail.*;

@SpringBootTest
class EmailApplicationTests {

    @Test
    void contextLoads() {
        final String username = "xmxisme@gmail.com"; // 你的谷歌邮箱地址
        final String password = "Qm159+@#$"; // 你的邮箱密码或应用程序专用密码

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Store store = session.getStore("pop3s");
            store.connect("pop.gmail.com", username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                // 获取邮件内容
                String subject = message.getSubject();
                if (subject != null && subject.equals("Your Verification Code")) {
                    Object content = message.getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        // 在这里解析验证码
                        System.out.println("验证码: " + body);
                    }
                }
            }

            // 关闭连接
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
