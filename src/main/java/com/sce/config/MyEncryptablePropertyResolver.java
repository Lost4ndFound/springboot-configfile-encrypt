package com.sce.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyDetector;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import com.ulisesbocchio.jasyptspringboot.exception.DecryptionException;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

import java.util.Optional;

/**
 * @author: lsw
 * @date: 2023/3/24 16:07
 */
public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {

    // 处理解密
    private final StringEncryptor encryptor;
    // 属性探测器
    private final EncryptablePropertyDetector detector;

    public MyEncryptablePropertyResolver(StringEncryptor encryptor, EncryptablePropertyDetector detector) {
        this.encryptor = encryptor;
        this.detector = detector;
    }

    /**
     * 处理真正的解密逻辑
     * @param value 原始值
     * @return 如果值未加密，返回原值，如果加密，返回加密之后的值
     */
    @Override
    public String resolvePropertyValue(String value) {
        return Optional.ofNullable(value)
                .filter(detector::isEncrypted)  // 如果经过属性探测器确认的，才继续
                .map(resolvedValue -> {
                    try {
                        String unwrappedProperty = detector.unwrapEncryptedValue(resolvedValue.trim()); // 过滤加密规则后的字符串
                        return encryptor.decrypt(unwrappedProperty); // 解密
                    } catch (EncryptionOperationNotPossibleException e) {
                        throw new DecryptionException("Unable to decrypt: " + value + ". Decryption of Properties failed,  make sure encryption/decryption " +
                                "passwords match", e);
                    }
                }).orElse(value);
    }
}
