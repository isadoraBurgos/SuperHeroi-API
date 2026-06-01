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
@ToString(exclude = {"poderes", "grupos", "viloes", "esconderijoBase"})
@EqualsAndHashCode(exclude = {"poderes", "grupos", "viloes", "esconderijoBase"})
@Schema(description = "Entidade que representa um super-herói")
public class SuperHeroi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do herói", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Nome do herói", example = "Homem-Aranha", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank
    @Schema(description = "Nome real do herói", example = "Peter Parker", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nomeReal;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Nível de poder do herói", example = "MEDIO")
    private NivelPoder nivelPoder;

    @ManyToMany
    @JoinTable(
            name = "heroi_poder",
            joinColumns = @JoinColumn(name = "heroi_id"),
            inverseJoinColumns = @JoinColumn(name = "poder_id")
    )
    @JsonIgnore
    @Schema(description = "Lista de poderes do herói", hidden = true)
    private List<Poder> poderes;

    @ManyToMany(mappedBy = "herois")
    @JsonIgnore
    @Schema(description = "Grupos aos quais o herói pertence", hidden = true)
    private List<Grupo> grupos;

    @ManyToOne
    @JoinColumn(name = "esconderijo_base_id")
    @JsonIgnore
    @Schema(description = "Esconderijo base do herói", hidden = true)
    private Esconderijo esconderijoBase;

    @ManyToMany
    @JoinTable(
            name = "heroi_vilao",
            joinColumns = @JoinColumn(name = "heroi_id"),
            inverseJoinColumns = @JoinColumn(name = "vilao_id")
    )
    @JsonIgnore
    @Schema(description = "Vilões enfrentados pelo herói", hidden = true)
    private List<Vilao> viloes;
}