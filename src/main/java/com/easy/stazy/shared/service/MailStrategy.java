package com.easy.stazy.shared.service;

public interface MailStrategy {
    void send(String template, Object user);
}

