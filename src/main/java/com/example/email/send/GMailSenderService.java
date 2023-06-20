package com.example.email.send;

import com.example.email.service.SendService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Properties;

@Service
public class GMailSenderService extends javax.mail.Authenticator implements SendService {
    @Value("${mail.host}")
    private String mailHost;
    @Value("${mail.transport.protocol}")
    private String transportProtocol;
    @Value("${mail.smtp.auth}")
    private boolean smtpAuth;
    @Value("${smtp.port}")
    private String smtpPort;
    @Value("${mail.smtp.socketFactory.port}")
    private String socketFactoryPort;
    @Value("${mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    @Value("${mail.smtp.socketFactory.fallback}")
    private String socketFactoryFallback;
    @Value("${mail.smtp.quitwait}")
    private String quitwait;
    @Value("${mail.smtp.starttls.enable}")
    private String enable;
    @Value("${mail.username}")
    private String user;
    @Value("${mail.password}")
    private String password;

    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    @PostConstruct
    public void init() {

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", transportProtocol);
        props.setProperty("mail.host", mailHost);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.socketFactory.port", socketFactoryPort);
        props.put("mail.smtp.socketFactory.class", socketFactoryClass);
        props.put("mail.smtp.socketFactory.fallback", socketFactoryFallback);
        props.setProperty("mail.smtp.quitwait", quitwait);

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    @Override
    public void sendEmail(String toAddress, String subject, String textMessage) {
        MimeMessage message = new MimeMessage(session);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(textMessage.getBytes(), "text/plain"));
        try {
            message.setSender(new InternetAddress(user));
            message.setSubject(subject);
            message.setDataHandler(handler);

            if (toAddress.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}