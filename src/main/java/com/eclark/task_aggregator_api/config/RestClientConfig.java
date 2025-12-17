package com.eclark.task_aggregator_api.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setDefaultConnectionConfig(
                ConnectionConfig.custom()
                    .setSocketTimeout(Timeout.ofSeconds(10))
                    .setConnectTimeout(Timeout.ofSeconds(10))
                    .build()
            ).build();

        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setResponseTimeout(Timeout.ofSeconds(10))
                    .build()
            )
            .build();

        return builder
            .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
            .build();
    }

    @Bean
    public RestClient googleRestClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor interceptor = 
            new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);

        // Tell it which client registration to use
        interceptor.setClientRegistrationIdResolver(request -> "google");

        return RestClient.builder()
            .requestInterceptor(interceptor)
            .build();
    }
}
