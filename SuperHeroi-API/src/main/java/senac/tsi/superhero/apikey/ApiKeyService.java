package senac.tsi.superhero.apikey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import senac.tsi.superhero.apikey.dto.ApiKeyRequest;
import senac.tsi.superhero.apikey.dto.ApiKeyResponse;
import senac.tsi.superhero.apikey.enums.ApiKeyRole;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository repository;

    public ApiKeyResponse gerar(ApiKeyRequest request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setUsuario(request.usuario());
        apiKey.setDescricao(request.descricao() == null ? "" : request.descricao());
        apiKey.setRole(request.role() == null ? ApiKeyRole.WRITE : request.role());
        apiKey.setChave(UUID.randomUUID().toString());

        return toResponse(repository.save(apiKey));
    }

    public List<ApiKeyResponse> listar() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApiKeyResponse revogar(Long id) {
        ApiKey apiKey = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Chave de API não encontrada"));

        apiKey.setAtiva(false);
        return toResponse(repository.save(apiKey));
    }

    public ApiKeyRole buscarRoleAtiva(String chave) {
        if (chave == null || chave.isBlank()) {
            return null;
        }

        return repository.findByChaveAndAtivaTrue(chave)
                .map(ApiKey::getRole)
                .orElse(null);
    }

    private ApiKeyResponse toResponse(ApiKey apiKey) {
        return new ApiKeyResponse(
                apiKey.getId(),
                apiKey.getUsuario(),
                apiKey.getChave(),
                apiKey.getRole(),
                apiKey.isAtiva(),
                apiKey.getCriadaEm()
        );
    }
}
