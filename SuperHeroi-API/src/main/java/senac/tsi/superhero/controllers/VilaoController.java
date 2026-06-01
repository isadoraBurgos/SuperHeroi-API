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
import senac.tsi.superhero.entities.Vilao;
import senac.tsi.superhero.services.IdempotencyService;
import senac.tsi.superhero.services.VilaoService;

@RestController
@RequestMapping("/viloes")
@Tag(name = "Vilões", description = "Endpoints para gerenciamento dos vilões cadastrados na API")
public class VilaoController {

    @Autowired
    private VilaoService service;

    @Autowired
    private IdempotencyService idempotencyService;

    @Operation(
            summary = "Listar vilões",
            description = "Retorna uma lista paginada de vilões cadastrados na API. Requer uma API Key válida e respeita o limite de requisições por IP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de vilões retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Vilao>>> listar(
            @ParameterObject Pageable pageable) {

        Page<Vilao> pagina = service.listar(pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Vilao>> pagedModel =
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
            summary = "Buscar vilão por ID",
            description = "Busca um vilão específico pelo ID informado. Retorna 404 caso o vilão não exista."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vilão encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Vilão não encontrado para o ID informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Vilao>> buscar(@PathVariable Long id) {

        Vilao vilao = service.buscarPorId(id);

        return ResponseEntity.ok(toModel(vilao));
    }

    @Operation(
            summary = "Criar vilão",
            description = "Cria um novo vilão. Exige o header X-Idempotency-Key para evitar processamento duplicado e uma API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vilão criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou header X-Idempotency-Key ausente"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para criar vilões"),
            @ApiResponse(responseCode = "409", description = "Conflito: chave de idempotência já utilizada ou vilão com dados já existentes"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Vilao>> criar(
            @Parameter(description = "Chave única para garantir idempotência no POST", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid Vilao vilao) {

        idempotencyService.registrar(idempotencyKey);

        Vilao novo = service.salvar(vilao);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(novo));
    }

    @Operation(
            summary = "Atualizar vilão",
            description = "Atualiza os dados de um vilão existente. Exige API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vilão atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou ID inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para atualizar vilões"),
            @ApiResponse(responseCode = "404", description = "Vilão não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outro vilão com os dados informados"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Vilao>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Vilao vilao) {

        Vilao atualizado = service.atualizar(id, vilao);

        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(
            summary = "Deletar vilão",
            description = "Remove um vilão pelo ID informado. Exige API Key com permissão ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vilão deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para deletar vilões"),
            @ApiResponse(responseCode = "404", description = "Vilão não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: vilão não pode ser removido por possuir relacionamentos ativos"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar vilões por nome",
            description = "Consulta vilões pelo nome informado, retornando os resultados de forma paginada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro nome ausente, vazio ou inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Nenhum vilão encontrado para o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Vilao>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable) {

        Page<Vilao> pagina = service.buscarPorNome(nome, pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Vilao>> pagedModel =
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

    private EntityModel<Vilao> toModel(Vilao vilao) {

        return EntityModel.of(
                vilao,

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(VilaoController.class)
                                .buscar(vilao.getId())
                ).withSelfRel(),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(VilaoController.class)
                                .listar(Pageable.unpaged())
                ).withRel("lista"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(VilaoController.class)
                                .atualizar(vilao.getId(), null)
                ).withRel("update"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(VilaoController.class)
                                .deletar(vilao.getId())
                ).withRel("delete")
        );
    }
}