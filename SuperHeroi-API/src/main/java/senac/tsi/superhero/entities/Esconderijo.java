package senac.tsi.superhero.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter @Setter
@ToString(exclude = {"grupo", "herois"})
@EqualsAndHashCode(exclude = {"grupo", "herois"})
@Schema(description = "Local secreto utilizado por grupos ou heróis")
public class Esconderijo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do esconderijo", example = "1")
    private Long id;

    @NotBlank
    @Schema(
            description = "Nome do esconderijo",
            example = "Batcaverna",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @NotBlank
    @Schema(
            description = "Localização do esconderijo",
            example = "Gotham City",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String localizacao;

    @OneToOne(mappedBy = "esconderijo")
    @JsonIgnore
    @Schema(description = "Grupo associado ao esconderijo", hidden = true)
    private Grupo grupo;

    @OneToMany(mappedBy = "esconderijoBase")
    @JsonIgnore
    @Schema(
            description = "Heróis que usam este esconderijo como base",
            hidden = true
    )
    private List<SuperHeroi> herois;
}