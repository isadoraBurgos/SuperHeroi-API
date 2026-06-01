package senac.tsi.superhero.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o nível de poder de um personagem")
public enum NivelPoder {

    @Schema(description = "Personagem com baixo nível de poder")
    BAIXO,

    @Schema(description = "Personagem com nível de poder médio")
    MEDIO,

    @Schema(description = "Personagem com alto nível de poder")
    ALTO,

    @Schema(description = "Personagem com poder em escala cósmica")
    COSMICO
}
