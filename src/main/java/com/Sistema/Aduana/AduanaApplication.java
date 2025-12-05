package com.Sistema.Aduana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AduanaApplication {
    public static void main(String[] args) {
        SpringApplication.run(AduanaApplication.class, args);
        System.out.println("Sistema Aduanero API iniciado en http://localhost:8080/api");
        System.out.println("Endpoints disponibles:");
        System.out.println("  POST   /api/auth/login");
        System.out.println("  POST   /api/auth/register");
        System.out.println("  GET    /api/auth/me");
        System.out.println("  GET    /api/dashboard/[aduana|pdi|sag]");
        System.out.println("  GET    /api/reportes");
        System.out.println("  POST   /api/pdi/control");
        System.out.println("  POST   /api/sag/inspeccion");
    }
}