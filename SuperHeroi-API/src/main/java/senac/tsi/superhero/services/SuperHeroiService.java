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
public class SuperHeroiService {

    @Autowired
    private SuperHeroiRepository repository;

    @Autowired
    private PoderRepository poderRepository;

    public Page<SuperHeroi> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public SuperHeroi buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Herói não encontrado"
                ));
    }

    public SuperHeroi salvar(SuperHeroi heroi) {
        if (repository.existsByNomeIgnoreCase(heroi.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Herói com esse nome já existe"
            );
        }

        return repository.save(heroi);
    }

    public SuperHeroi atualizar(Long id, SuperHeroi heroiAtualizado) {
        SuperHeroi heroi = buscarPorId(id);

        heroi.setNome(heroiAtualizado.getNome());
        heroi.setNomeReal(heroiAtualizado.getNomeReal());
        heroi.setNivelPoder(heroiAtualizado.getNivelPoder());

        if (heroiAtualizado.getViloes() != null) {
            heroi.setViloes(heroiAtualizado.getViloes());
        }
        if (heroiAtualizado.getEsconderijoBase() != null) {
            heroi.setEsconderijoBase(heroiAtualizado.getEsconderijoBase());
        }

        return repository.save(heroi);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Herói não encontrado"
            );
        }
        repository.deleteById(id);
    }

    public Page<SuperHeroi> buscarPorNome(String nome, Pageable pageable) {
        Page<SuperHeroi> resultado = repository.findByNomeContainingIgnoreCase(nome, pageable);

        if (resultado.getTotalElements() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum herói encontrado com esse nome"
            );
        }

        return resultado;
    }

    public SuperHeroi adicionarPoder(Long heroiId, Long poderId) {
        SuperHeroi heroi = buscarPorId(heroiId);

        Poder poder = poderRepository.findById(poderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Poder não encontrado"
                ));

        if (heroi.getPoderes() == null) {
            heroi.setPoderes(new ArrayList<>());
        }

        boolean jaExiste = heroi.getPoderes().stream()
                .anyMatch(p -> p.getId().equals(poderId));

        if (jaExiste) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Herói já possui esse poder"
            );
        }

        heroi.getPoderes().add(poder);
        return repository.save(heroi);
    }

    public SuperHeroi removerPoder(Long heroiId, Long poderId) {
        SuperHeroi heroi = buscarPorId(heroiId);

        if (heroi.getPoderes() == null || heroi.getPoderes().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Poder não encontrado neste herói"
            );
        }

        boolean removido = heroi.getPoderes().removeIf(p -> p.getId().equals(poderId));

        if (!removido) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Poder não encontrado neste herói"
            );
        }

        return repository.save(heroi);
    }
}