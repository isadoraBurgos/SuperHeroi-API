package senac.tsi.superhero.dto;

import senac.tsi.superhero.entities.SuperHeroi;
import senac.tsi.superhero.enums.NivelPoder;

import java.util.List;

public record SuperHeroiResponse(
        Long id,
        String nome,
        String nomeReal,
        NivelPoder nivelPoder,
        List<PoderResumo> poderes ,
        List<GrupoResumo> grupos

) {
    public record GrupoResumo(Long id, String nome) {}
    public record PoderResumo(Long id, String nome) {}

    public static SuperHeroiResponse from(SuperHeroi heroi) {
        List<GrupoResumo> grupos = heroi.getGrupos() == null ? List.of() :
                heroi.getGrupos().stream()
                .map(g -> new GrupoResumo(g.getId(), g.getNome()))
                .toList();

        List<PoderResumo> poderes = heroi.getPoderes() == null ? List.of() :
                heroi.getPoderes().stream()
                .map(p -> new PoderResumo(p.getId(), p.getNome()))
                .toList();

        return new SuperHeroiResponse(
                heroi.getId(),
                heroi.getNome(),
                heroi.getNomeReal(),
                heroi.getNivelPoder(),
                poderes,
                grupos

        );
    }
}