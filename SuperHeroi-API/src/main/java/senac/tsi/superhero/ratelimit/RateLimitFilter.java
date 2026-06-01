package senac.tsi.superhero.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Order(0)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String HEADER_LIMIT = "X-Rate-Limit-Limit";
    private static final String HEADER_REMAINING = "X-Rate-Limit-Remaining";
    private static final String HEADER_RESET = "X-Rate-Limit-Reset";
    private static final String HEADER_RETRY_AFTER = "Retry-After";

    @Value("${app.rate-limit.max-requests:5}")
    private int maxRequests;

    @Value("${app.rate-limit.window-seconds:60}")
    private long windowSeconds;

    private final Map<String, Deque<Long>> requestsByClient = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (deveIgnorar(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = obterIpCliente(request);
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSeconds;

        Deque<Long> timestamps = requestsByClient.computeIfAbsent(clientIp, key -> new ConcurrentLinkedDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() <= windowStart) {
                timestamps.pollFirst();
            }

            int usadas = timestamps.size();

            if (usadas >= maxRequests) {
                long primeiraRequisicao = timestamps.peekFirst();
                long resetEm = primeiraRequisicao + windowSeconds;
                long retryAfter = Math.max(1, resetEm - now);

                response.setStatus(429);
                response.setContentType("application/json;charset=UTF-8");
                response.setHeader(HEADER_LIMIT, String.valueOf(maxRequests));
                response.setHeader(HEADER_REMAINING, "0");
                response.setHeader(HEADER_RESET, String.valueOf(resetEm));
                response.setHeader(HEADER_RETRY_AFTER, String.valueOf(retryAfter));

                response.getWriter().write("""
                        {
                          "timestamp": "%s",
                          "status": 429,
                          "error": "Too Many Requests",
                          "message": "Limite de requisições excedido para este cliente/IP.",
                          "path": "%s",
                          "limit": %d,
                          "retryAfter": %d
                        }
                        """.formatted(LocalDateTime.now(), request.getRequestURI(), maxRequests, retryAfter));
                return;
            }

            timestamps.addLast(now);

            int restantes = Math.max(0, maxRequests - timestamps.size());
            long resetEm = timestamps.peekFirst() + windowSeconds;

            response.setHeader(HEADER_LIMIT, String.valueOf(maxRequests));
            response.setHeader(HEADER_REMAINING, String.valueOf(restantes));
            response.setHeader(HEADER_RESET, String.valueOf(resetEm));
        }

        filterChain.doFilter(request, response);
    }

    private boolean deveIgnorar(HttpServletRequest request) {
        String path = request.getRequestURI();

        if (request.getMethod().equals("OPTIONS")) return true;

        return path.startsWith("/swagger-ui")
                || path.startsWith("/swagger-ui.html")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/h2-console")
                || path.startsWith("/actuator");
    }

    private String obterIpCliente(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
