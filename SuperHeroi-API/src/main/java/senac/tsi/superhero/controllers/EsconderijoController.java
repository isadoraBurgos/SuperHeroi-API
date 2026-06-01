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
import senac.tsi.superhero.entities.Esconderijo;
import senac.tsi.superhero.services.EsconderijoService;
import senac.tsi.superhero.services.IdempotencyService;

@RestController
@RequestMapping("/Esconderijos")
@Tag(name = "Esconderijos", description = "Endpoints para gerenciamento dos esconderijos cadastrados na API")
public class EsconderijoController {

    @Autowired
    private EsconderijoService service;

    @Autowired
    private IdempotencyService idempotencyService;

    @Operation(
            summary = "Listar esconderijos",
            description = "Retorna uma lista paginada de esconderijos cadastrados. Requer uma API Key válida e respeita o limite de requisições por IP."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de esconderijos retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Esconderijo>>> listar(
            @ParameterObject Pageable pageable) {

        Page<Esconderijo> pagina = service.listar(pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Esconderijo>> pagedModel =
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
            summary = "Buscar esconderijo por ID",
            description = "Busca um esconderijo específico pelo ID informado. Retorna 404 caso o esconderijo não exista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Esconderijo encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Esconderijo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Esconderijo>> buscar(@PathVariable Long id) {

        Esconderijo esconderijo = service.buscarPorId(id);

        return ResponseEntity.ok(toModel(esconderijo));
    }

    @Operation(
            summary = "Criar esconderijo",
            description = "Cria um novo esconderijo. Exige o header X-Idempotency-Key para evitar processamento duplicado e uma API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Esconderijo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou header X-Idempotency-Key ausente"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para criar esconderijos"),
            @ApiResponse(responseCode = "409", description = "Conflito: chave de idempotência já utilizada ou esconderijo com dados já existentes"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Esconderijo>> criar(
            @Parameter(description = "Chave única para garantir idempotência no POST", required = true)
            @RequestHeader("X-Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid Esconderijo esconderijo) {

        idempotencyService.registrar(idempotencyKey);

        Esconderijo novo = service.salvar(esconderijo);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toModel(novo));
    }

    @Operation(
            summary = "Atualizar esconderijo",
            description = "Atualiza os dados de um esconderijo existente. Exige API Key com permissão WRITE ou ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Esconderijo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, JSON mal formatado ou ID inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para atualizar esconderijos"),
            @ApiResponse(responseCode = "404", description = "Esconderijo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: já existe outro esconderijo com os dados informados"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Esconderijo>> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Esconderijo esconderijo) {

        Esconderijo atualizado = service.atualizar(id, esconderijo);

        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(
            summary = "Deletar esconderijo",
            description = "Remove um esconderijo pelo ID informado. Exige API Key com permissão ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Esconderijo deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido ou mal formatado"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida, inativa ou sem permissão para deletar esconderijos"),
            @ApiResponse(responseCode = "404", description = "Esconderijo não encontrado para o ID informado"),
            @ApiResponse(responseCode = "409", description = "Conflito: esconderijo não pode ser deletado por estar relacionado a outros recursos"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        service.deletar(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Buscar esconderijos por nome",
            description = "Consulta esconderijos pelo nome informado, com retorno paginado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetro nome ausente, vazio ou inválido"),
            @ApiResponse(responseCode = "401", description = "API Key ausente, inválida ou inativa"),
            @ApiResponse(responseCode = "404", description = "Nenhum esconderijo encontrado para o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisições excedido. Aguarde o tempo indicado no header Retry-After")
    })
    @GetMapping("/buscar")
    public ResponseEntity<PagedModel<EntityModel<Esconderijo>>> buscarPorNome(
            @RequestParam String nome,
            @ParameterObject Pageable pageable) {

        Page<Esconderijo> pagina = service.buscarPorNome(nome, pageable);

        var lista = pagina.getContent().stream()
                .map(this::toModel)
                .toList();

        PagedModel<EntityModel<Esconderijo>> pagedModel =
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

    private EntityModel<Esconderijo> toModel(Esconderijo esconderijo) {

        return EntityModel.of(
                esconderijo,

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(EsconderijoController.class)
                                .buscar(esconderijo.getId())
                ).withSelfRel(),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(EsconderijoController.class)
                                .listar(Pageable.unpaged())
                ).withRel("lista"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(EsconderijoController.class)
                                .atualizar(esconderijo.getId(), null)
                ).withRel("update"),

                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(EsconderijoController.class)
                                .deletar(esconderijo.getId())
                ).withRel("delete")
        );
    }
}