package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.Esconderijo;
import senac.tsi.superhero.repositories.EsconderijoRepository;

@Service
public class EsconderijoService {

    @Autowired
    private EsconderijoRepository repository;

    public Page<Esconderijo> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Esconderijo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Esconderijo não encontrado"
                ));
    }

    public Esconderijo salvar(Esconderijo esconderijo) {
        if (repository.existsByNomeIgnoreCase(esconderijo.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esconderijo com esse nome já existe"
            );
        }

        return repository.save(esconderijo);
    }

    public Esconderijo atualizar(Long id, Esconderijo atualizado) {
        Esconderijo e = buscarPorId(id);

        e.setNome(atualizado.getNome());
        e.setLocalizacao(atualizado.getLocalizacao());

        return repository.save(e);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Esconderijo não encontrado"
            );
        }
        repository.deleteById(id);
    }

    public Page<Esconderijo> buscarPorNome(String nome, Pageable pageable) {
        Page<Esconderijo> resultado = repository.findByNomeContainingIgnoreCase(nome, pageable);

        if (resultado.getTotalElements() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum esconderijo encontrado com esse nome"
            );
        }

        return resultado;
    }

}
