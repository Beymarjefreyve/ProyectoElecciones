package com.universidad.elecciones.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot maneja recursos estáticos automáticamente desde /static/
    // No se necesita configuración adicional
}
