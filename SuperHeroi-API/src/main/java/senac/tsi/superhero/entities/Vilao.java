package senac.tsi.superhero.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import senac.tsi.superhero.enums.NivelPoder;

import java.util.List;

@Entity
@Getter @Setter
@ToString(exclude = {"herois", "poderes", "grupos"})
@EqualsAndHashCode(exclude = {"herois", "poderes", "grupos"})
@Schema(description = "Entidade que representa um vilão")
public class Vilao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do vilão", example = "1")
    private Long id;

    @NotBlank
    @Schema(
            description = "Nome do vilão",
            example = "Coringa",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Nível de poder do vilão", example = "ALTO")
    private NivelPoder nivelPoder;

    @ManyToMany(mappedBy = "viloes")
    @JsonIgnore
    @Schema(description = "Lista de heróis relacionados ao vilão", hidden = true)
    private List<SuperHeroi> herois;

    @ManyToMany
    @JoinTable(
            name = "vilao_poder",
            joinColumns = @JoinColumn(name = "vilao_id"),
            inverseJoinColumns = @JoinColumn(name = "poder_id")
    )
    @JsonIgnore
    @Schema(description = "Lista de poderes do vilão", hidden = true)
    private List<Poder> poderes;

    @ManyToMany(mappedBy = "viloes")
    @JsonIgnore
    @Schema(description = "Grupos aos quais o vilão pertence", hidden = true)
    private List<Grupo> grupos;
}