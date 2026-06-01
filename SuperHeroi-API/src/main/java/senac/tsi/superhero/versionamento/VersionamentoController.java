package senac.tsi.superhero.versionamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(
        name = "Versionamentos",
        description = "Endpoints de demonstração do versionamento da API usando o cabeçalho X-API-Version"
)
public class VersionamentoController {

    @Operation(
            summary = "V1 - Listar heróis em formato simples",
            description = "Demonstra a versão 1 de um endpoint usando o header X-API-Version. " +
                    "Envie X-API-Version: 1 para acessar esta versão.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            description = "Versão da API. Para este endpoint, use o valor 1.",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "1"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta da versão 1 retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Header X-API-Version inválido")
    })
    @GetMapping(value = "/versionamentos/herois", headers = "X-API-Version=1")
    public ResponseEntity<Map<String, Object>> listarHeroisV1(
            @RequestHeader("X-API-Version") String versao) {

        return ResponseEntity.ok(Map.of(
                "versao", versao,
                "endpoint", "/versionamentos/herois",
                "descricao", "Versão 1 com dados simples dos heróis.",
                "dados", List.of(
                        Map.of("id", 1, "nome", "Homem-Aranha"),
                        Map.of("id", 2, "nome", "Mulher-Maravilha")
                )
        ));
    }

    @Operation(
            summary = "V1 - Listar poderes em formato simples",
            description = "Demonstra a versão 1 de outro endpoint usando o header X-API-Version. " +
                    "Envie X-API-Version: 1 para acessar esta versão.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            description = "Versão da API. Para este endpoint, use o valor 1.",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "1"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta da versão 1 retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Header X-API-Version inválido")
    })
    @GetMapping(value = "/versionamentos/poderes", headers = "X-API-Version=1")
    public ResponseEntity<Map<String, Object>> listarPoderesV1(
            @RequestHeader("X-API-Version") String versao) {

        return ResponseEntity.ok(Map.of(
                "versao", versao,
                "endpoint", "/versionamentos/poderes",
                "descricao", "Versão 1 com dados simples dos poderes.",
                "dados", List.of(
                        Map.of("id", 1, "nome", "Super Força"),
                        Map.of("id", 2, "nome", "Invisibilidade")
                )
        ));
    }

    @Operation(
            summary = "V2 - Listar heróis em formato detalhado",
            description = "Demonstra a versão 2 de um endpoint usando o header X-API-Version. " +
                    "Envie X-API-Version: 2 para acessar esta versão.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            description = "Versão da API. Para este endpoint, use o valor 2.",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "2"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta da versão 2 retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Header X-API-Version inválido")
    })
    @GetMapping(value = "/versionamentos/herois/detalhes", headers = "X-API-Version=2")
    public ResponseEntity<Map<String, Object>> listarHeroisV2(
            @RequestHeader("X-API-Version") String versao) {

        return ResponseEntity.ok(Map.of(
                "versao", versao,
                "endpoint", "/versionamentos/herois/detalhes",
                "descricao", "Versão 2 com dados detalhados dos heróis.",
                "novidades", "A versão 2 inclui nome real, nível de poder e metadados.",
                "dados", List.of(
                        Map.of(
                                "id", 1,
                                "nome", "Homem-Aranha",
                                "nomeReal", "Peter Parker",
                                "nivelPoder", "MEDIO"
                        ),
                        Map.of(
                                "id", 2,
                                "nome", "Mulher-Maravilha",
                                "nomeReal", "Diana Prince",
                                "nivelPoder", "ALTO"
                        )
                )
        ));
    }

    @Operation(
            summary = "V2 - Listar poderes em formato detalhado",
            description = "Demonstra a versão 2 de outro endpoint usando o header X-API-Version. " +
                    "Envie X-API-Version: 2 para acessar esta versão.",
            parameters = {
                    @Parameter(
                            name = "X-API-Version",
                            description = "Versão da API. Para este endpoint, use o valor 2.",
                            required = true,
                            in = ParameterIn.HEADER,
                            example = "2"
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resposta da versão 2 retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Header X-API-Version inválido")
    })
    @GetMapping(value = "/versionamentos/poderes/detalhes", headers = "X-API-Version=2")
    public ResponseEntity<Map<String, Object>> listarPoderesV2(
            @RequestHeader("X-API-Version") String versao) {

        return ResponseEntity.ok(Map.of(
                "versao", versao,
                "endpoint", "/versionamentos/poderes/detalhes",
                "descricao", "Versão 2 com dados detalhados dos poderes.",
                "novidades", "A versão 2 inclui descrição e classificação dos poderes.",
                "dados", List.of(
                        Map.of(
                                "id", 1,
                                "nome", "Super Força",
                                "descricao", "Capacidade de levantar objetos extremamente pesados",
                                "classificacao", "FÍSICO"
                        ),
                        Map.of(
                                "id", 2,
                                "nome", "Invisibilidade",
                                "descricao", "Capacidade de ficar invisível por tempo limitado",
                                "classificacao", "ESTRATÉGICO"
                        )
                )
        ));
    }
}
