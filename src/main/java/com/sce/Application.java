package com.sce;

import com.sce.config.MyEncryptablePropertyDetector;
import com.sce.config.MyEncryptablePropertyFilter;
import com.sce.config.MyEncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import com.ulisesbocchio.jasyptspringboot.util.Singleton;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: lsw
 * @date: 2023/3/24 11:00
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    /**
     * 加入 StringEncryptor 加密解密类
     *      beanName 必须为 jasyptStringEncryptor 才能是自定义的生效
     *      configProps 为jasypt框架中读取的配置类，就不用自己读取了
     */
    @Bean("customEnc")
    public StringEncryptor jasyptStringEncryptor(Singleton<JasyptEncryptorConfigurationProperties> configProps){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        JasyptEncryptorConfigurationProperties jasyptProperties = configProps.get();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(jasyptProperties.getPassword());
        config.setAlgorithm(jasyptProperties.getAlgorithm());
        config.setKeyObtentionIterations(jasyptProperties.getKeyObtentionIterations());
        config.setPoolSize(jasyptProperties.getPoolSize());
        config.setProviderName(jasyptProperties.getProviderName());
        config.setSaltGeneratorClassName(jasyptProperties.getSaltGeneratorClassname());
        config.setIvGeneratorClassName(jasyptProperties.getIvGeneratorClassname());
        config.setStringOutputType(jasyptProperties.getStringOutputType());
        encryptor.setConfig(config);
        return encryptor;
    }

    /**
     * 自定义属性探测器
     *  beanName为 encryptablePropertyDetector
     */
    @Bean(name = "encryptablePropertyDetector")
    public EncryptablePropertyDetector encryptablePropertyDetector() {
        return new MyEncryptablePropertyDetector();
    }

    @Bean("encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver(@Qualifier("customEnc") StringEncryptor jasyptStringEncryptor, EncryptablePropertyDetector encryptablePropertyDetector) {
        return new MyEncryptablePropertyResolver(jasyptStringEncryptor, encryptablePropertyDetector);
    }

    /**
     * 自定义的属性拦截器
     * @param configProps Jasypt官方读取的配置集合
     * @return 自定义属性拦截器
     */
    @Bean(name="encryptablePropertyFilter")
    public EncryptablePropertyFilter encryptablePropertyFilter(Singleton<JasyptEncryptorConfigurationProperties> configProps) {
        return new MyEncryptablePropertyFilter(configProps.get());
    }


}
