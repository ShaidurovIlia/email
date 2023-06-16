package com.example.email.service;

import com.example.email.model.Employee;
import com.example.email.model.Fio;
import com.example.email.model.Institute;
import com.example.email.model.InstituteStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmailService {
    private final static String regFio = "${Fio}";
    private final static String regInstitute = "${InstituteName}";
    @Autowired
    private SendService sendService;
    @Autowired
    private FileStore fileService;

    public void generateOneMessage(InstituteStore instituteStore) throws IOException {
        String message = fileService.getMessage();
        String topic = fileService.getTopic();
        List<Institute> institutes = instituteStore.getInstitutes();
        Institute institute = institutes.stream().findFirst().get();
        List<Employee> employees = institute.getEmployees();
        Employee employee = employees.stream().findFirst().get();
        send(message, topic, institute.getName(), employee);
        employees.remove(employee);
        if (employees.isEmpty()) {
            institutes.remove(institute);
        }
    }

    private void send(String message, String topic, String instituteName, Employee employee) {
        Fio fio = employee.getFio();
        String emailAddress = employee.getEmail();
        String topicForSend = getCorrectTopic(topic, instituteName);
        String messageForSend = getCorrectMessage(message, instituteName, fio.toString());
        sendService.sendEmail(emailAddress, topicForSend, messageForSend);
    }

    private String getCorrectTopic(String topic, String instituteName) {
        if (!topic.contains(regInstitute)) {
            throw new UnsupportedOperationException("Не найден шаблон в заголовке для института!");
        }
        return topic.replace(regInstitute, instituteName);
    }

    private String getCorrectMessage(String message, String instituteName, String fio) {
        if (!message.contains(regInstitute) || !message.contains(regFio)) {
            throw new UnsupportedOperationException("Не найден шаблон в сообщении для института!");
        }
        return message.replace(regInstitute, instituteName).replace(regFio, fio);
    }
}
