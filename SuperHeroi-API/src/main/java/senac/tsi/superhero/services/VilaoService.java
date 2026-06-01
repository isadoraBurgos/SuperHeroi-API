package senac.tsi.superhero.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.entities.Vilao;
import senac.tsi.superhero.repositories.VilaoRepository;

@Service
public class VilaoService {

    @Autowired
    private VilaoRepository repository;

    public Page<Vilao> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Vilao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vilão não encontrado"
                ));
    }

    public Vilao salvar(Vilao vilao) {
        if (repository.existsByNomeIgnoreCase(vilao.getNome())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vilão com esse nome já existe"
            );
        }

        return repository.save(vilao);
    }

    public Vilao atualizar(Long id, Vilao vilaoAtualizado) {
        Vilao vilao = buscarPorId(id);

        vilao.setNome(vilaoAtualizado.getNome());
        vilao.setNivelPoder(vilaoAtualizado.getNivelPoder());
        vilao.setPoderes(vilaoAtualizado.getPoderes());
        vilao.setHerois(vilaoAtualizado.getHerois());

        return repository.save(vilao);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Vilão não encontrado"
            );
        }
        repository.deleteById(id);
    }

    public Page<Vilao> buscarPorNome(String nome, Pageable pageable) {
        Page<Vilao> resultado = repository.findByNomeContainingIgnoreCase(nome, pageable);

        if (resultado.getTotalElements() == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhum vilão encontrado com esse nome"
            );
        }

        return resultado;
    }
}