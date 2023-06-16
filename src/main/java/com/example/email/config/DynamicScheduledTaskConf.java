package com.example.email.config;

import com.example.email.model.InstituteStore;
import com.example.email.service.EmailService;
import com.example.email.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
@EnableScheduling
public class DynamicScheduledTaskConf implements SchedulingConfigurer {

    @Value("${scheduled.task.delay.end}")
    private long endDelay;
    @Value("${scheduled.task.delay.start}")
    private long startDelay;

    private EmailService emailService;

    private InstituteStore instituteStore;

    @Autowired
    public DynamicScheduledTaskConf(EmailService emailService, FileStore fileService) throws IOException {
        this.emailService = emailService;
        this.instituteStore = fileService.getStore();
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> {
                    try {
                        doSend();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                triggerContext -> {
                    Optional<Instant> lastCompletionTime =
                            Optional.ofNullable(triggerContext.lastCompletion());
                    return lastCompletionTime.orElseGet(Instant::now)
                            .plusSeconds(getRandomDelay());
                }
        );
    }

    public void doSend() throws IOException {

        System.out.println(instituteStore.getInstitutes().size() + " / До отправки");
        System.out.println(Instant.now());
        if (!instituteStore.getInstitutes().isEmpty()) {
            emailService.generateOneMessage(instituteStore);
            if (instituteStore.getInstitutes().isEmpty()) {
                System.out.println("Отправка закончена!!!!!!");
            }
        }
        System.out.println(instituteStore.getInstitutes().size() + " / После отправки");
    }

    private long getRandomDelay() {
        return ThreadLocalRandom.current().nextLong(startDelay, endDelay + 1);
    }
}
