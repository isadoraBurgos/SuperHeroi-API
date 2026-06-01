package senac.tsi.superhero.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("X-API-Key"))
                .components(new Components()
                        .addSecuritySchemes("X-API-Key",
                                new SecurityScheme()
                                        .name("X-API-Key")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Chave de API utilizada para autenticar requisições em endpoints protegidos.")))
                .info(new Info()
                        .title("API Super-Heróis")
                        .version("1.0.0")
                        .description("""
                                API REST desenvolvida para gerenciamento de um universo de super-heróis, permitindo o cadastro, consulta, atualização e remoção de entidades relacionadas a personagens, habilidades, organizações e esconderijos.
                                
                                A aplicação foi construída utilizando Spring Boot, Spring Data JPA, H2 Database, Spring HATEOAS e Springdoc OpenAPI, seguindo boas práticas de desenvolvimento de APIs REST.
                                
                                Entidades disponíveis:
                                
                                • Super-Heróis: personagens principais com identidade secreta e nível de poder.
                                • Vilões: antagonistas com diferentes níveis de ameaça e habilidades.
                                • Poderes: habilidades que podem ser associadas a heróis e vilões.
                                • Grupos: equipes, organizações e alianças entre personagens.
                                • Esconderijos: bases estratégicas utilizadas pelos grupos.
                                • API Keys: gerenciamento de autenticação e autorização da API.
                                
                                Recursos implementados:
                                
                                ✓ Operações CRUD completas para todas as entidades.
                                ✓ Paginação utilizando Pageable.
                                ✓ Consultas personalizadas por nome.
                                ✓ Relacionamentos One-to-One, One-to-Many e Many-to-Many.
                                ✓ Enum para classificação de níveis de poder.
                                ✓ Navegação entre recursos utilizando HATEOAS.
                                ✓ Documentação interativa com Swagger/OpenAPI 3.
                                ✓ Validação de dados com Bean Validation.
                                ✓ Tratamento global de exceções com @ControllerAdvice.
                                ✓ Banco de dados H2 para persistência local.
                                ✓ Autenticação baseada em X-API-Key.
                                ✓ Controle de permissões por níveis de acesso.
                                ✓ Idempotência em operações POST através do header X-Idempotency-Key.
                                ✓ Rate Limiting por cliente/IP com retorno HTTP 429 (Too Many Requests).
                                ✓ Configuração de CORS para origens autorizadas.
                                ✓ Versionamento de API através do header X-API-Version.
                                
                                Autenticação:
                                
                                Para acessar endpoints protegidos, gere uma chave através do endpoint /api-keys e envie o valor retornado no header:
                                
                                X-API-Key: sua-chave
                                
                                Níveis de acesso disponíveis:
                                
                                • READ → Permite operações de consulta (GET).
                                • WRITE → Permite consultas, criação e atualização (GET, POST e PUT).
                                • ADMIN → Permite acesso completo (GET, POST, PUT e DELETE).
                                
                                Versionamento:
                                
                                Os endpoints de versionamento utilizam o header:
                                
                                X-API-Version
                                
                                Versões disponíveis:
                                
                                • v1
                                • v2
                                
                                Segurança e Controle:
                                
                                A API implementa mecanismos de autenticação, autorização, controle de requisições, validação de dados e prevenção de duplicidade de operações, garantindo maior confiabilidade, segurança e integridade das informações.
                                """));
    }
}