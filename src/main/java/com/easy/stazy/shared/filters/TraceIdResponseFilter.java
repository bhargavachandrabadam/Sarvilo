package com.easy.stazy.shared.filters;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdResponseFilter extends OncePerRequestFilter {
    private final Tracer tracer;
    /**
     * Adds the current trace id to the response header if available.
     *
     * @param request     the HttpServletRequest to process
     * @param response    the HttpServletResponse to add the trace id header
     * @param filterChain the FilterChain to continue the request
     * @throws ServletException if any error occurs during the process
     * @throws IOException      if any error occurs during the process
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try{
            var currentSpan = tracer.currentSpan();
            if(currentSpan != null){
                var traceId = currentSpan.context().traceId();
                log.info("Adding trace id to response header: {}",traceId);
                response.setHeader("X-Trace-Id",traceId);
            }
        }
        catch(Exception e){
            log.error("Error adding trace id to response header: {}",e.getMessage());
        }
        filterChain.doFilter(request,response);

    }
}
