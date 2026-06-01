package com.study.yagoomap.global.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청 1건당 1줄의 액세스 로그를 남기고, MDC 에 traceId 를 심는다.
 * traceId 는 모든 로그 패턴(logback-spring.xml)에 함께 출력되며 응답 헤더(X-Request-Id)로도 돌려준다.
 * 한 요청에서 발생한 로그를 traceId 로 묶어 추적할 수 있다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String TRACE_ID = "traceId";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = request.getHeader(REQUEST_ID_HEADER);
        if (traceId == null || traceId.isBlank() || traceId.length() > 64) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
        }
        MDC.put(TRACE_ID, traceId);
        response.setHeader(REQUEST_ID_HEADER, traceId);

        long startNanos = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
            int status = response.getStatus();
            String method = request.getMethod();
            String uri = uri(request);
            if (status >= 500) {
                log.error("{} {} -> {} ({}ms)", method, uri, status, elapsedMs);
            } else if (status >= 400) {
                log.warn("{} {} -> {} ({}ms)", method, uri, status, elapsedMs);
            } else {
                log.info("{} {} -> {} ({}ms)", method, uri, status, elapsedMs);
            }
            MDC.remove(TRACE_ID);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/webjars")
                || path.equals("/favicon.ico")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".html")
                || path.endsWith(".ico")
                || path.endsWith(".png")
                || path.endsWith(".map");
    }

    private static String uri(HttpServletRequest request) {
        String query = request.getQueryString();
        return query == null ? request.getRequestURI() : request.getRequestURI() + "?" + query;
    }
}
