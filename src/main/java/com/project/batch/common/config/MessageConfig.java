/*
 * @file : com.project.batch.common.config.MessageConfig.java
 * @desc : resources/messageCode.yml에 명시된 메시지 코드 및 메시지를 사용하기 위해 정의한 설정 파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.config;

import net.rakugakibox.util.YamlResourceBundle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


@Configuration
public class MessageConfig implements WebMvcConfigurer {

    /*
     * @method : localeResolver
     * @desc : 세션에 지역설정. default는 KOREAN = 'ko'
     * @param :
     * @return :
     * */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.KOREAN);
        return slr;
    }

    /*
     * @method : localeChangeInterceptor
     * @desc : 지역설정을 변경하는 인터셉터. 요청시 파라미터에 lang 정보를 지정
     * @param :
     * @return :
     * */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /*
     * @method : addInterceptors
     * @desc : 인터셉터를 시스템 레지스트리에 등록
     * @param :
     * @return :
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /*
     * @method : messageSource
     * @desc : yml 파일을 참조하는 MessageSource 선언(resources/messageCode.yml). MessageSource의 getMessage 메소를 사용해서
     *         resources/messageCode.yml 접근
     * @param :
     * @return :
     * */
    @Bean
    public MessageSource messageSource(
            @Value("${spring.messages.basename}") String basename,  // applicaion.yaml의 spring.message.basename
            @Value("${spring.messages.encoding}") String encoding) {
        YamlMessageSource ms = new YamlMessageSource();
        ms.setBasename(basename);
        ms.setDefaultEncoding(encoding);
        ms.setAlwaysUseMessageFormat(true);
        ms.setUseCodeAsDefaultMessage(true);
        ms.setFallbackToSystemLocale(true);
        return ms;
    }

    /*
     * @class : YamlMessageSource
     * @desc : locale 정보에 따라 다른 yaml 파일을 읽도록 처리
     * @param :
     * @return :
     * */
    private static class YamlMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
        }
    }
}