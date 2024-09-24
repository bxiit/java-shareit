package ru.practicum.shareit.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Component
public class LocaleConfig extends AcceptHeaderLocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = super.resolveLocale(request);
        LocaleContextHolder.setDefaultLocale(Locale.ROOT);
        LocaleContextHolder.setLocale(Locale.ROOT);
        return locale;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
