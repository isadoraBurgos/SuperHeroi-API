package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.SuperHeroi;
import senac.tsi.superhero.repositories.SuperHeroiRepository;

@Service
public class SuperHeroiService {

    @Autowired
    private SuperHeroiRepository repository;

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
        heroi.setPoderes(heroiAtualizado.getPoderes());
        heroi.setViloes(heroiAtualizado.getViloes());
        heroi.setEsconderijoBase(heroiAtualizado.getEsconderijoBase());

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
}
