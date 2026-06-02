package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.Poder;
import senac.tsi.superhero.entities.SuperHeroi;
import senac.tsi.superhero.repositories.PoderRepository;
import senac.tsi.superhero.repositories.SuperHeroiRepository;

import java.util.ArrayList;

@Service
public class PoderService {

    @Autowired
    private PoderRepository repository;

    @Autowired
    private SuperHeroiRepository heroiRepository;

    public Page<Poder> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Poder buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Poder não encontrado"
                ));
    }

    public Poder salvar(Poder poder) {
        if (repository.existsByNomeIgnoreCase(poder.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Poder com esse nome já existe"
            );
        }

        return repository.save(poder);
    }

    public Poder atualizar(Long id, Poder atualizado) {
        Poder p = buscarPorId(id);

        p.setNome(atualizado.getNome());
        p.setDescricao(atualizado.getDescricao());

        return repository.save(p);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Poder não encontrado"
            );
        }
        repository.deleteById(id);
    }

    public Page<Poder> buscarPorNome(String nome, Pageable pageable) {
        Page<Poder> resultado = repository.findByNomeContainingIgnoreCase(nome, pageable);

        if (resultado.getTotalElements() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum poder encontrado com esse nome"
            );
        }

        return resultado;
    }

    public Poder adicionarHeroi(Long poderId, Long heroiId) {
        Poder poder = buscarPorId(poderId);

        SuperHeroi heroi = heroiRepository.findById(heroiId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Herói não encontrado"
                ));

        if (poder.getHerois() == null) {
            poder.setHerois(new ArrayList<>());
        }

        boolean jaExiste = poder.getHerois().stream()
                .anyMatch(h -> h.getId().equals(heroiId));

        if (jaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Herói já possui esse poder"
            );
        }

        poder.getHerois().add(heroi);
        return repository.save(poder);
    }

    public Poder removerHeroi(Long poderId, Long heroiId) {
        Poder poder = buscarPorId(poderId);

        if (poder.getHerois() == null || poder.getHerois().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Herói não encontrado neste poder"
            );
        }

        boolean removido = poder.getHerois().removeIf(h -> h.getId().equals(heroiId));

        if (!removido) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Herói não encontrado neste poder"
            );
        }

        return repository.save(poder);
    }
}