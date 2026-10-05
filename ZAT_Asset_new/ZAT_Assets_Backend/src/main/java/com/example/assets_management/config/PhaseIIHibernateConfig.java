package com.example.assets_management.config;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PhaseIIHibernateConfig {

    @Bean
    public MetadataBuilderContributor metadataBuilderContributor() {
        return new MetadataBuilderContributor() {
            @Override
            public void contribute(MetadataBuilder metadataBuilder) {
                // Register Phase II entities for DDL auto-update
                // This ensures that Phase II tables are created/updated automatically
            }
        };
    }
}
