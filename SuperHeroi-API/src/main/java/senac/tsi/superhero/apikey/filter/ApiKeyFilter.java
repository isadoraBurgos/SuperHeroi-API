package senac.tsi.superhero.apikey.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import senac.tsi.superhero.apikey.ApiKeyService;
import senac.tsi.superhero.apikey.enums.ApiKeyRole;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Autowired
    private ApiKeyService apiKeyService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!deveProteger(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        ApiKeyRole role = apiKeyService.buscarRoleAtiva(apiKey);

        if (role == null) {
            responderErro(request, response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized",
                    "X-API-Key ausente, inválida ou revogada");
            return;
        }

        if (!temPermissao(request.getMethod(), role)) {
            responderErro(request, response, HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden",
                    "A chave de API não possui nível de acesso suficiente para esta operação");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean deveProteger(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (method.equals("OPTIONS")) return false;

        if (path.startsWith("/api-keys")) return false;
        if (path.startsWith("/swagger-ui")) return false;
        if (path.startsWith("/swagger-ui.html")) return false;
        if (path.startsWith("/api-docs")) return false;
        if (path.startsWith("/v3/api-docs")) return false;
        if (path.startsWith("/h2-console")) return false;

        return method.equals("POST")
                || method.equals("PUT")
                || method.equals("DELETE");
    }

    private boolean temPermissao(String method, ApiKeyRole role) {
        if (role == ApiKeyRole.ADMIN) {
            return true;
        }

        if (role == ApiKeyRole.WRITE) {
            return method.equals("POST") || method.equals("PUT");
        }

        return false;
    }

    private void responderErro(HttpServletRequest request,
                               HttpServletResponse response,
                               int status,
                               String error,
                               String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {
                  "timestamp": "%s",
                  "status": %d,
                  "error": "%s",
                  "message": "%s",
                  "path": "%s"
                }
                """.formatted(LocalDateTime.now(), status, error, message, request.getRequestURI()));
    }
}
