package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.Poder;
import senac.tsi.superhero.repositories.PoderRepository;

@Service
public class PoderService {

    @Autowired
    private PoderRepository repository;

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
}
