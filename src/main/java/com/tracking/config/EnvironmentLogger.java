package com.tracking.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentLogger {

    private static final Logger logger = LoggerFactory.getLogger(EnvironmentLogger.class);

    @EventListener(ApplicationReadyEvent.class)
    public void logEnvironmentVariables(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        
        logger.info("=== Environment Variables Debug ===");
        logger.info("PGHOST: {}", env.getProperty("PGHOST", "NOT_SET"));
        logger.info("PGPORT: {}", env.getProperty("PGPORT", "NOT_SET"));
        logger.info("PGDATABASE: {}", env.getProperty("PGDATABASE", "NOT_SET"));
        logger.info("PGUSER: {}", env.getProperty("PGUSER", "NOT_SET"));
        logger.info("PGPASSWORD: {}", env.getProperty("PGPASSWORD", "NOT_SET") != null ? "***SET***" : "NOT_SET");
        logger.info("DATABASE_URL: {}", env.getProperty("DATABASE_URL", "NOT_SET") != null ? "***SET***" : "NOT_SET");
        logger.info("SPRING_PROFILES_ACTIVE: {}", env.getProperty("SPRING_PROFILES_ACTIVE", "NOT_SET"));
        logger.info("=====================================");
    }
}
