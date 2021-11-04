/*
 * @file : kr.co.kgc.point.batch.common.interceptor.RestTemplateInterceptor.java
 * @desc : RestTemplate에 의한 외부 API 호출 시, 선/후 처리를 명시한 Interceptor 클래스. https 요청 시 403 에러 회피 로직 적용
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.interceptor;

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
import java.util.UUID;

public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LogManager.getLogger();

    /*
     * @method : intercept
     * @desc : RestTemplate 전/후 처리 로직. UUID 생성 로직 및 https 요청 시 403 에러 회피 로직 적용
     * @param : httpRequest, bytes(request body)
     * @return :
     * */
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        /* UUID 생성 */
        String uuid = UUID.randomUUID().toString();
        httpRequest.getHeaders().add("UUID", uuid);

        /* https 호출 시 403 에러 회피*/
        httpRequest.getHeaders().add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        /* 요청 로깅 / 요청 / 응답 로깅 */
        requestLogging(httpRequest, bytes, uuid);
        ClientHttpResponse response = clientHttpRequestExecution.execute(httpRequest, bytes);
        responseLogging(response, uuid);

        return response;
    }

    /*
     * @method : requestLogging
     * @desc : RestTemplate 호출 전 로깅 메소드
     * @param :
     * @return :
     * */
    private void requestLogging(HttpRequest httpRequest, byte[] bytes, String uuid) throws UnsupportedEncodingException {
        log.info("[" + uuid + "] [REQ] [" + httpRequest.getURI() + "] [" + httpRequest.getMethod() + "] [" +
                httpRequest.getHeaders() + "] [" + new String(bytes, "UTF-8") + "]");
    }

    /*
     * @method : responseLogging
     * @desc : RestTemplate 호출 후 로깅 메소드
     * @param :
     * @return :
     * */
    private void responseLogging(ClientHttpResponse response, String uuid) throws IOException {
        log.info("[" + uuid + "] [RES] [" + response.getStatusCode() + "] [" + response.getHeaders() + "] [" +
                StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()) + "]");
    }
}
