package senac.tsi.superhero.apikey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import senac.tsi.superhero.apikey.enums.ApiKeyRole;

public record ApiKeyRequest(

        @NotBlank
        @Schema(
                description = "Nome do usuário ou aplicação dona da chave",
                example = "Isadora",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String usuario,

        @Schema(
                description = "Descrição da finalidade da chave",
                example = "Chave usada pelo painel administrativo"
        )
        String descricao,

        @Schema(
                description = "Nível de acesso da chave: READ, WRITE ou ADMIN",
                example = "WRITE"
        )
        ApiKeyRole role

) {
}