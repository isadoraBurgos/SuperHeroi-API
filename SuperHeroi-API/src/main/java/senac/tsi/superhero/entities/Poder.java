package senac.tsi.superhero.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter @Setter
@ToString(exclude = {"herois", "viloes"})
@EqualsAndHashCode(exclude = {"herois", "viloes"})
@Schema(description = "Representa um poder ou habilidade")
public class Poder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do poder", example = "1")
    private Long id;

    @NotBlank
    @Schema(
            description = "Nome do poder",
            example = "Super força",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @Size(min = 5, max = 200)
    @Schema(
            description = "Descrição do poder",
            example = "Capacidade de levantar objetos extremamente pesados"
    )
    private String descricao;

    @ManyToMany(mappedBy = "poderes")
    @JsonIgnore
    @Schema(description = "Heróis que possuem esse poder", hidden = true)
    private List<SuperHeroi> herois;

    @ManyToMany(mappedBy = "poderes")
    @JsonIgnore
    @Schema(description = "Vilões que possuem esse poder", hidden = true)
    private List<Vilao> viloes;
}