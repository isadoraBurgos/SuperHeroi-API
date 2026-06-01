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
import senac.tsi.superhero.entities.Poder;
import senac.tsi.superhero.services.IdempotencyService;
import senac.tsi.superhero.services.PoderService;

@RestController
@RequestMapping("/poderes")
@Tag(name = "Poderes", description = "Endpoints para gerenciamento dos poderes cadastrados na API")
public class PoderController {

    @Autowired
    private PoderService service;

    @Autowired
    private IdempotencyService idempotencyService;

    @Operation(
            summary = "Listar poderes",
            description = "Retorna uma lista paginada de poderes cadastrados na API. Requer uma API Key válida e respeita o limite de requisições por IP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de poderes retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Poder>>> listar(
            @ParameterObject Pageable pageable) {

        Page<Poder> pagina = service.listar(pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Poder>> pagedModel =
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
            summary = "Buscar poder por ID",
            description = "Busca um poder específico pelo ID informado. Retorna 404 caso o poder não exista."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Poder encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Poder não encontrado para o ID informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Poder>> buscar(@PathVariable Long id) {

        Poder poder = service.buscarPorId(id);

        return ResponseEntity.ok(toModel(poder));
    }

    @Operation(
            summary = "Criar poder",
            description = "Cria um novo poder. Exige o header X-Idempotency-Key para evitar processamento duplicado e uma API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Poder criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou header X-Idempotency-Key ausente"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para criar poderes"),
            @ApiResponse(responseCode = "409", description = "Conflito: chave de idempotência já utilizada ou poder com dados já existentes"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Poder>> criar(
            @Parameter(description = "Chave única para garantir idempotência no POST", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid Poder poder) {

        idempotencyService.registrar(idempotencyKey);

        Poder novo = service.salvar(poder);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(novo));
    }

    @Operation(
            summary = "Atualizar poder",
            description = "Atualiza os dados de um poder existente. Exige API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Poder atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou ID inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para atualizar poderes"),
            @ApiResponse(responseCode = "404", description = "Poder não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outro poder com os dados informados"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Poder>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Poder poder) {

        Poder atualizado = service.atualizar(id, poder);

        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(
            summary = "Deletar poder",
            description = "Remove um poder pelo ID informado. Exige API Key com permissão ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Poder deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para deletar poderes"),
            @ApiResponse(responseCode = "404", description = "Poder não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: poder não pode ser removido por possuir relacionamentos ativos"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar poderes por nome",
            description = "Consulta poderes pelo nome informado, retornando os resultados de forma paginada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro nome ausente, vazio ou inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Nenhum poder encontrado para o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Poder>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable) {

        Page<Poder> pagina = service.buscarPorNome(nome, pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Poder>> pagedModel =
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

    private EntityModel<Poder> toModel(Poder poder) {

        return EntityModel.of(
                poder,

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PoderController.class)
                                .buscar(poder.getId())
                ).withSelfRel(),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PoderController.class)
                                .listar(Pageable.unpaged())
                ).withRel("lista"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PoderController.class)
                                .atualizar(poder.getId(), null)
                ).withRel("update"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PoderController.class)
                                .deletar(poder.getId())
                ).withRel("delete")
        );
    }
}