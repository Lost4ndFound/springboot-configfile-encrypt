package com.sce.config;

import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyFilter;
import com.ulisesbocchio.jasyptspringboot.properties.JasyptEncryptorConfigurationProperties;
import org.springframework.core.env.PropertySource;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 自定义的属性过滤器
 * @author: lsw
 * @date: 2023/3/24 16:21
 */
public class MyEncryptablePropertyFilter implements EncryptablePropertyFilter {

    /** jasypt 的所有配置*/
    JasyptEncryptorConfigurationProperties jasyptProperties;

    public MyEncryptablePropertyFilter(JasyptEncryptorConfigurationProperties jasyptProperties) {
        this.jasyptProperties = jasyptProperties;
    }

    @Override
    public boolean shouldInclude(PropertySource<?> source, String name) {
        List<String> excludeNames = jasyptProperties.getProperty().getFilter().getExcludeNames();
        List<String> includeNames = jasyptProperties.getProperty().getFilter().getIncludeNames();
        if (CollectionUtils.isEmpty(includeNames) && CollectionUtils.isEmpty(excludeNames)) {
            return true;
        }

        if (isMatch(source.getName(), excludeNames) || isMatch(source.getName(), excludeNames)) {
            return false;
        }

        return CollectionUtils.isEmpty(includeNames) ||
                isMatch(source.getName(), includeNames) ||
                isMatch(name, includeNames);

    }

    /**
     * 正则判断，如果满足，返回true，如果不满足，返回false
     * @param name 配置的key
     * @param patterns 正则列表
     * @return 如果满足，返回true，如果不满足，返回false
     */
    private boolean isMatch(String name, List<String> patterns) {
        return name != null && !CollectionUtils.isEmpty(patterns) && patterns.stream().anyMatch(name::matches);
    }
}
