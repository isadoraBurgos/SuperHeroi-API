package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.Grupo;
import senac.tsi.superhero.repositories.GrupoRepository;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository repository;

    public Page<Grupo> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Grupo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Grupo não encontrado"
                ));
    }

    public Grupo salvar(Grupo grupo) {
        if (repository.existsByNomeIgnoreCase(grupo.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Grupo com esse nome já existe"
            );
        }

        return repository.save(grupo);
    }

    public Grupo atualizar(Long id, Grupo grupoAtualizado) {
        Grupo grupo = buscarPorId(id);

        grupo.setNome(grupoAtualizado.getNome());

        // Relacionamentos (opcional)
        if (grupoAtualizado.getHerois() != null) {
            grupo.setHerois(grupoAtualizado.getHerois());
        }
        if (grupoAtualizado.getEsconderijo() != null) {
            grupo.setEsconderijo(grupoAtualizado.getEsconderijo());
        }

        return repository.save(grupo);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Grupo não encontrado"
            );
        }
        repository.deleteById(id);
    }

    public Page<Grupo> buscarPorNome(String nome, Pageable pageable) {
        Page<Grupo> resultado = repository.findByNomeContainingIgnoreCase(nome, pageable);

        if (resultado.getTotalElements() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum grupo encontrado com esse nome"
            );
        }

        return resultado;
    }
}
