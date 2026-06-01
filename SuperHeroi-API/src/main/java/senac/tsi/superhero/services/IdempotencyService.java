package senac.tsi.superhero.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final Set<String> chavesProcessadas = ConcurrentHashMap.newKeySet();

    public void registrar(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Header X-Idempotency-Key é obrigatório para operações POST"
            );
        }

        if (!chavesProcessadas.add(chave)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Chave de idempotência já utilizada. A operação não será processada novamente."
            );
        }
    }
}
