package senac.tsi.superhero.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import senac.tsi.superhero.apikey.enums.ApiKeyRole;

import java.time.LocalDateTime;

@Entity
@Data
@Schema(description = "Chave de API usada para autenticação via header X-API-Key")
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID da chave", example = "1")
    private Long id;

    @Column(nullable = false)
    @Schema(
            description = "Nome do usuário ou aplicação dona da chave",
            example = "Isabella",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String usuario;

    @Column(nullable = false)
    @Schema(
            description = "Descrição da finalidade da chave",
            example = "Chave usada pelo painel administrativo"
    )
    private String descricao;

    @Column(nullable = false, unique = true, length = 80)
    @Schema(
            description = "Valor da chave de API",
            example = "uuid-gerado-automaticamente"
    )
    private String chave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(
            description = "Nível de acesso da chave: READ, WRITE ou ADMIN",
            example = "WRITE"
    )
    private ApiKeyRole role = ApiKeyRole.WRITE;

    @Column(nullable = false)
    @Schema(
            description = "Indica se a chave está ativa",
            example = "true"
    )
    private boolean ativa = true;

    @Column(nullable = false)
    @Schema(description = "Data de criação da chave")
    private LocalDateTime criadaEm = LocalDateTime.now();
}