package senac.tsi.superhero.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import senac.tsi.superhero.dto.SuperHeroiResponse;
import senac.tsi.superhero.entities.SuperHeroi;
import senac.tsi.superhero.services.IdempotencyService;
import senac.tsi.superhero.services.SuperHeroiService;

@RestController
@RequestMapping("/herois")
@Tag(name = "Heróis", description = "Endpoints para gerenciamento dos heróis cadastrados na API")
public class SuperHeroiController {

    @Autowired
    private SuperHeroiService service;

    @Autowired
    private IdempotencyService idempotencyService;

    @Operation(
            summary = "Listar heróis",
            description = "Retorna uma lista paginada de heróis cadastrados na API. Requer uma API Key válida e respeita o limite de requisições por IP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de heróis retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SuperHeroiResponse>>> listar(
            @ParameterObject Pageable pageable) {

        Page<SuperHeroi> pagina = service.listar(pageable);

        var lista = pagina.getContent().stream()
                .map(h -> toModel(SuperHeroiResponse.from(h)))
                .toList();

        PagedModel<EntityModel<SuperHeroiResponse>> pagedModel =
                PagedModel.of(
                        lista,
                        new PagedModel.PageMetadata(
                                pagina.getSize(),
                                pagina.getNumber(),
                                pagina.getTotalElements()
                        )
                );

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(
            summary = "Buscar herói por ID",
            description = "Busca um herói específico pelo ID informado. Retorna 404 caso o herói não exista."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herói encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Herói não encontrado para o ID informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SuperHeroiResponse>> buscar(@PathVariable Long id) {

        SuperHeroi heroi = service.buscarPorId(id);

        return ResponseEntity.ok(toModel(SuperHeroiResponse.from(heroi)));
    }

    @Operation(
            summary = "Criar herói",
            description = "Cria um novo herói. Exige o header X-Idempotency-Key para evitar processamento duplicado e uma API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Herói criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou header X-Idempotency-Key ausente"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para criar heróis"),
            @ApiResponse(responseCode = "409", description = "Conflito: chave de idempotência já utilizada ou herói com dados já existentes"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PostMapping
    public ResponseEntity<EntityModel<SuperHeroiResponse>> criar(
            @Parameter(description = "Chave única para garantir idempotência no POST", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid SuperHeroi heroi) {

        idempotencyService.registrar(idempotencyKey);

        SuperHeroi novoHeroi = service.salvar(heroi);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(SuperHeroiResponse.from(novoHeroi)));
    }

    @Operation(
            summary = "Atualizar herói",
            description = "Atualiza os dados de um herói existente. Exige API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herói atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou ID inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para atualizar heróis"),
            @ApiResponse(responseCode = "404", description = "Herói não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outro herói com os dados informados"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SuperHeroiResponse>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid SuperHeroi heroi) {

        SuperHeroi atualizado = service.atualizar(id, heroi);

        return ResponseEntity.ok(toModel(SuperHeroiResponse.from(atualizado)));
    }

    @Operation(
            summary = "Deletar herói",
            description = "Remove um herói pelo ID informado. Exige API Key com permissão ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Herói deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para deletar heróis"),
            @ApiResponse(responseCode = "404", description = "Herói não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: herói não pode ser removido por possuir relacionamentos ativos"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar heróis por nome",
            description = "Consulta heróis pelo nome informado, retornando os resultados de forma paginada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro nome ausente, vazio ou inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Nenhum herói encontrado para o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<SuperHeroiResponse>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable) {

        Page<SuperHeroi> pagina = service.buscarPorNome(nome, pageable);

        var lista = pagina.getContent().stream()
                .map(h -> toModel(SuperHeroiResponse.from(h)))
                .toList();

        PagedModel<EntityModel<SuperHeroiResponse>> pagedModel =
                PagedModel.of(
                        lista,
                        new PagedModel.PageMetadata(
                                pagina.getSize(),
                                pagina.getNumber(),
                                pagina.getTotalElements()
                        )
                );

        return ResponseEntity.ok(pagedModel);
    }

    @Operation(summary = "Adicionar poder ao herói")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Poder adicionado ao herói com sucesso"),
            @ApiResponse(responseCode = "404", description = "Herói ou poder não encontrado"),
            @ApiResponse(responseCode = "409", description = "Herói já possui esse poder")
    })
    @PostMapping("/{id}/poderes/{poderId}")
    public ResponseEntity<EntityModel<SuperHeroiResponse>> adicionarPoder(
            @PathVariable Long id,
            @PathVariable Long poderId) {

        SuperHeroi atualizado = service.adicionarPoder(id, poderId);
        return ResponseEntity.ok(toModel(SuperHeroiResponse.from(atualizado)));
    }

    @Operation(summary = "Remover poder do herói")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Poder removido do herói com sucesso"),
            @ApiResponse(responseCode = "404", description = "Herói ou poder não encontrado")
    })
    @DeleteMapping("/{id}/poderes/{poderId}")
    public ResponseEntity<EntityModel<SuperHeroiResponse>> removerPoder(
            @PathVariable Long id,
            @PathVariable Long poderId) {

        SuperHeroi atualizado = service.removerPoder(id, poderId);
        return ResponseEntity.ok(toModel(SuperHeroiResponse.from(atualizado)));
    }

    private EntityModel<SuperHeroiResponse> toModel(SuperHeroiResponse heroi) {

        return EntityModel.of(
                heroi,

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(SuperHeroiController.class)
                                .buscar(heroi.id())
                ).withSelfRel(),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(SuperHeroiController.class)
                                .listar(Pageable.unpaged())
                ).withRel("lista"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(SuperHeroiController.class)
                                .atualizar(heroi.id(), null)
                ).withRel("update"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(SuperHeroiController.class)
                                .deletar(heroi.id())
                ).withRel("delete")
        );
    }
}