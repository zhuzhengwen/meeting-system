package com.ruoyi.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * LDAP 配置（ldap.enabled=true 时生效）
 */
@Configuration
@ConditionalOnProperty(name = "ldap.enabled", havingValue = "true")
public class LdapConfig
{
    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.base}")
    private String base;

    @Value("${ldap.userDn}")
    private String userDn;

    @Value("${ldap.password}")
    private String password;

    @Bean
    public LdapContextSource ldapContextSource()
    {
        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(base);
        source.setUserDn(userDn);
        source.setPassword(password);
        return source;
    }

    @Bean
    public LdapTemplate ldapTemplate()
    {
        LdapTemplate template = new LdapTemplate(ldapContextSource());
        // AD 环境通常需要关闭对象类映射的强制检查
        template.setIgnorePartialResultException(true);
        return template;
    }
}
