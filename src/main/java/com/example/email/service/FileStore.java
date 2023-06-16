package com.example.email.service;

import com.example.email.model.InstituteStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FileStore {
    private final static String CLASS_PATH = "classpath:";

    private Resource messageResource;
    private Resource topicResource;

    private Resource emailsResource;

    @Autowired
    public FileStore(ResourceLoader resourceLoader,
                     @Value("${file.path.message}") String messagePath,
                     @Value("${file.path.topic}") String topicPath,
                     @Value("${file.path.emails}") String emailsPath
    ) {
        this.messageResource = resourceLoader.getResource(CLASS_PATH + messagePath);
        this.topicResource = resourceLoader.getResource(CLASS_PATH + topicPath);
        this.emailsResource = resourceLoader.getResource(CLASS_PATH + emailsPath);
    }

    public String getMessage() throws IOException {
        byte[] messageData = FileCopyUtils.copyToByteArray(messageResource.getInputStream());
        return new String(messageData, StandardCharsets.UTF_8);
    }

    public String getTopic() throws IOException {
        byte[] messageData = FileCopyUtils.copyToByteArray(topicResource.getInputStream());
        return new String(messageData, StandardCharsets.UTF_8);
    }

    public InstituteStore getStore() throws IOException {
        Yaml yaml = new Yaml();
        return yaml.loadAs(emailsResource.getInputStream(), InstituteStore.class);
    }
}
