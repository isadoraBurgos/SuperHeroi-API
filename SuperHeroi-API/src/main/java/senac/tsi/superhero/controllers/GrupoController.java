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
import senac.tsi.superhero.entities.Grupo;
import senac.tsi.superhero.services.GrupoService;
import senac.tsi.superhero.services.IdempotencyService;

@RestController
@RequestMapping("/grupos")
@Tag(name = "Grupos", description = "Endpoints para gerenciamento dos grupos cadastrados na API")
public class GrupoController {

    @Autowired
    private GrupoService service;

    @Autowired
    private IdempotencyService idempotencyService;

    @Operation(
            summary = "Listar grupos",
            description = "Retorna uma lista paginada de grupos cadastrados na API. Requer uma API Key válida e respeita o limite de requisições por IP."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de grupos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Grupo>>> listar(
            @ParameterObject Pageable pageable) {

        Page<Grupo> pagina = service.listar(pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Grupo>> pagedModel =
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
            summary = "Buscar grupo por ID",
            description = "Busca um grupo específico pelo ID informado. Retorna 404 caso o grupo não exista."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Grupo>> buscar(@PathVariable Long id) {

        Grupo grupo = service.buscarPorId(id);

        return ResponseEntity.ok(toModel(grupo));
    }

    @Operation(
            summary = "Criar grupo",
            description = "Cria um novo grupo. Exige o header X-Idempotency-Key para evitar processamento duplicado e uma API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Grupo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou header X-Idempotency-Key ausente"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para criar grupos"),
            @ApiResponse(responseCode = "409", description = "Conflito: chave de idempotência já utilizada ou grupo com dados já existentes"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Grupo>> criar(
            @Parameter(description = "Chave única para garantir idempotência no POST", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid Grupo grupo) {

        idempotencyService.registrar(idempotencyKey);

        Grupo novo = service.salvar(grupo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(novo));
    }

    @Operation(
            summary = "Atualizar grupo",
            description = "Atualiza os dados de um grupo existente. Exige API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Grupo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou ID inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para atualizar grupos"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outro grupo com os dados informados"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Grupo>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Grupo grupo) {

        Grupo atualizado = service.atualizar(id, grupo);

        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(
            summary = "Deletar grupo",
            description = "Remove um grupo pelo ID informado. Exige API Key com permissão ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Grupo deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para deletar grupos"),
            @ApiResponse(responseCode = "404", description = "Grupo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: grupo não pode ser removido por possuir relacionamentos ativos"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar grupos por nome",
            description = "Consulta grupos pelo nome informado, retornando os resultados de forma paginada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro nome ausente, vazio ou inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Nenhum grupo encontrado para o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Grupo>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable) {

        Page<Grupo> pagina = service.buscarPorNome(nome, pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Grupo>> pagedModel =
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

    @Operation(summary = "Adicionar herói ao grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herói adicionado ao grupo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo ou herói não encontrado"),
            @ApiResponse(responseCode = "409", description = "Herói já pertence ao grupo")
    })
    @PostMapping("/{id}/herois/{heroiId}")
    public ResponseEntity<EntityModel<Grupo>> adicionarHeroi(
            @PathVariable Long id,
            @PathVariable Long heroiId) {

        Grupo atualizado = service.adicionarHeroi(id, heroiId);
        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(summary = "Remover herói do grupo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Herói removido do grupo com sucesso"),
            @ApiResponse(responseCode = "404", description = "Grupo ou herói não encontrado")
    })
    @DeleteMapping("/{id}/herois/{heroiId}")
    public ResponseEntity<EntityModel<Grupo>> removerHeroi(
            @PathVariable Long id,
            @PathVariable Long heroiId) {

        Grupo atualizado = service.removerHeroi(id, heroiId);
        return ResponseEntity.ok(toModel(atualizado));
    }

    private EntityModel<Grupo> toModel(Grupo grupo) {

        return EntityModel.of(
                grupo,

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(GrupoController.class)
                                .buscar(grupo.getId())
                ).withSelfRel(),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(GrupoController.class)
                                .listar(Pageable.unpaged())
                ).withRel("lista"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(GrupoController.class)
                                .atualizar(grupo.getId(), null)
                ).withRel("update"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(GrupoController.class)
                                .deletar(grupo.getId())
                ).withRel("delete")
        );
    }
}