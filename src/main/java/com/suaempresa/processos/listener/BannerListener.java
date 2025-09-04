package com.suaempresa.processos.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BannerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    System.out.println("\nSistema de gerenciamento de processos para contabilidade");
        System.out.println(
            "H   H  OOO  RRRR  III ZZZZZ  OOO  N   N\n" +
            "H   H O   O R   R  I     Z  O   O NN  N\n" +
            "HHHHH O   O RRRR   I    Z   O   O N N N\n" +
            "H   H O   O R  R   I   Z    O   O N  NN\n" +
            "H   H  OOO  R   R III ZZZZZ  OOO  N   N\n"
        );
        System.out.println("Desenvolvedor: Bruno Judson");
        System.out.println("Versão: 1.0");
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
