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
@ToString(exclude = {"herois", "viloes", "esconderijo"})
@EqualsAndHashCode(exclude = {"herois", "viloes", "esconderijo"})
@Schema(description = "Grupo de heróis")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID do grupo", example = "1")
    private Long id;

    @NotBlank
    @Schema(
            description = "Nome do grupo",
            example = "Vingadores",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nome;

    @ManyToMany
    @JoinTable(
            name = "grupo_heroi",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "heroi_id")
    )
    @JsonIgnore
    @Schema(description = "Lista de heróis do grupo", hidden = true)
    private List<SuperHeroi> herois;

    @ManyToMany
    @JoinTable(
            name = "grupo_vilao",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "vilao_id")
    )
    @JsonIgnore
    @Schema(description = "Lista de vilões do grupo", hidden = true)
    private List<Vilao> viloes;

    @OneToOne
    @JoinColumn(name = "esconderijo_id")
    @JsonIgnore
    @Schema(description = "Esconderijo do grupo", hidden = true)
    private Esconderijo esconderijo;
}