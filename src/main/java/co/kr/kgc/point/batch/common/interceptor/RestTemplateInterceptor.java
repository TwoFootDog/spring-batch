package co.kr.kgc.point.batch.common.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LogManager.getLogger();
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        // https 호출 시 403 에러 회피
        httpRequest.getHeaders().add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        requestLogging(httpRequest, bytes);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        responseLogging(response);

        return response;
    }

    private void requestLogging(HttpRequest httpRequest, byte[] bytes) throws UnsupportedEncodingException {
        log.debug("> Request URI : {}", httpRequest.getURI());
        log.debug("> Request Method : {}", httpRequest.getMethod());
        log.debug("> Request Headers : {}", httpRequest.getHeaders());
        log.debug("> Request Body : {}", new String(bytes, "UTF-8"));
    }

    private void responseLogging(ClientHttpResponse response) throws IOException {
        log.debug("> Response Status Code : {}", response.getStatusCode());
        log.debug("> Response Status text : {}", response.getStatusText());
        log.debug("> Response Headers : {}", response.getHeaders());
        log.debug("> Response Body : {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
    }
}
