package senac.tsi.superhero.apikey;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.tsi.superhero.apikey.dto.ApiKeyRequest;
import senac.tsi.superhero.apikey.dto.ApiKeyResponse;

import java.util.List;

@RestController
@RequestMapping("/api-keys")
@Tag(name = "API Keys", description = "Geração e gerenciamento de chaves de API")
public class ApiKeyController {

    @Autowired
    private ApiKeyService service;

    @Operation(summary = "Gerar chave de API")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chave de API gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ApiKeyResponse> gerar(
            @RequestBody(
                    description = "Dados para geração de uma chave de API. " +
                            "READ permite apenas consultas GET. " +
                            "WRITE permite operações de leitura, criação e atualização (GET, POST e PUT). " +
                            "ADMIN possui acesso total, incluindo exclusão de dados (DELETE).",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Exemplo de geração de chave",
                                    value = """
                                    {
                                      "usuario": "Isadora",
                                      "descricao": "Chave usada pelo painel administrativo",
                                      "role": "WRITE"
                                    }
                                    """
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody @Valid ApiKeyRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerar(request));
    }

    @Operation(summary = "Listar chaves de API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chaves listadas com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Revogar chave de API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chave revogada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Chave não encontrada")
    })
    @PutMapping("/{id}/revogar")
    public ResponseEntity<ApiKeyResponse> revogar(@PathVariable Long id) {
        return ResponseEntity.ok(service.revogar(id));
    }
}