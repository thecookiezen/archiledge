package com.thecookiezen.ladybugdb.spring.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * Registrar that scans for LadybugDB repositories and registers them as Spring
 * beans.
 */
public class LadybugDBRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableLadybugDBRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new LadybugDBRepositoryConfigurationExtension();
    }
}
