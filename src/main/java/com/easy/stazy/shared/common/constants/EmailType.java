package com.easy.stazy.shared.common.constants;//package com.encatch.stazy.shared.constants;
//
//import com.encatch.stazy.shared.service.MailStrategy;
//import com.encatch.stazy.shared.service.WelcomeMailStrategy;
//import com.encatch.stazy.shared.service.ResetMailStrategy;
//
//public enum EmailType {
//    WELCOME("welcome.ftl", new WelcomeMailStrategy()),
//    RESET("reset.ftl", new ResetMailStrategy());
//
//    private final String template;
//    private final MailStrategy strategy;
//
//    EmailType(String template, MailStrategy strategy) {
//        this.template = template;
//        this.strategy = strategy;
//    }
//
//    public void send(Object user) {
//        strategy.send(template, user);
//    }
//
//    public String getTemplate() {
//        return template;
//    }
//}
//
