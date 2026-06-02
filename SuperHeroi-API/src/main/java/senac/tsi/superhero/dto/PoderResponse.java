package senac.tsi.superhero.dto;

import senac.tsi.superhero.entities.Poder;

import java.util.List;

public record PoderResponse(
        Long id,
        String nome,
        String descricao,
        List<HeroiResumo> herois,
        List<VilaoResumo> viloes
) {
    public record HeroiResumo(Long id, String nome) {}
    public record VilaoResumo(Long id, String nome) {}

    public static PoderResponse from(Poder poder) {
        List<HeroiResumo> herois = poder.getHerois() == null ? List.of() :
                poder.getHerois().stream()
                .map(h -> new HeroiResumo(h.getId(), h.getNome()))
                .toList();

        List<VilaoResumo> viloes = poder.getViloes() == null ? List.of() :
                poder.getViloes().stream()
                .map(v -> new VilaoResumo(v.getId(), v.getNome()))
                .toList();

        return new PoderResponse(
                poder.getId(),
                poder.getNome(),
                poder.getDescricao(),
                herois,
                viloes
        );
    }
}