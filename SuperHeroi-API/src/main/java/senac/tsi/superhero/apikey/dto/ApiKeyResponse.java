package senac.tsi.superhero.apikey.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import senac.tsi.superhero.apikey.enums.ApiKeyRole;

import java.time.LocalDateTime;

public record ApiKeyResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Isabella")
        String usuario,

        @Schema(description = "Valor que deve ser enviado no header X-API-Key")
        String chave,

        @Schema(example = "WRITE")
        ApiKeyRole role,

        @Schema(example = "true")
        boolean ativa,

        LocalDateTime criadaEm
) {
}
