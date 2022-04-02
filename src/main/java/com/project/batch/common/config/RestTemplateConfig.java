/*
 * @file : com.project.batch.common.config.RestTemplateConfig.java
 * @desc : RestTemplate에 대한 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.config;

import com.project.batch.common.interceptor.RestTemplateInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    private static final int MAX_OPEN_CONNECTION_COUNT = 100; // 최대 오픈되는 커넥션 수
    private static final int CONNECTION_COUNT_PER_ROUTE = 5; // IP, 포트 1쌍에 대해 수행할 커넥션 수
    private static final int CONNECTION_REQUEST_TIMEOUT = 3000; // 연결요청 시간 초과(ms)
    private static final int CONNECTION_TIMEOUT = 3000; // 연결 시간 초과(ms)
    private static final int SOCKET_TIMEOUT = 3000; // 소켓 시간 초과(ms)


    /*
     * @method : poolingHttpClientConnectionManager
     * @desc : 커넥션 정보 설정(최대 오픈 커넥션 수 / IP, 포트 1쌍에 대한 커넥션 수 등)
     * @param :
     * @return :
     * */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_OPEN_CONNECTION_COUNT);
        connectionManager.setDefaultMaxPerRoute(CONNECTION_COUNT_PER_ROUTE);
        return connectionManager;
    }

    /*
     * @method : requestConfig
     * @desc : 연결요청 시간, 연결 시간, 소켓 시간 등 설정
     * @param :
     * @return :
     * */
    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
    }

    /*
     * @method : httpClient
     * @desc : poolingHttpClientConnectionManager와 requestConfig를 HttpClient Bean에 등록
     * @param :
     * @return :
     * */
    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
            return HttpClientBuilder
            .create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    /*
     * @method : restTemplate
     * @desc : 위에 설정한 restTemplate 설정으로 restTemplate Bean 생성. RestTemplate 전/후 처리 Interceptor 적용
     * @param :
     * @return :
     * */
    @Bean
    public RestTemplate restTemplate(HttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.setInterceptors(Collections.singletonList(new RestTemplateInterceptor())); // Interceptor 적용

        return restTemplate;
    }
}
