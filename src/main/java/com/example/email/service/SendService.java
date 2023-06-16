package com.example.email.service;

public interface SendService {

    void sendEmail(String toAddress, String subject, String message);
}
