# Idempotência aplicada nos POSTs

Header obrigatório para todos os POSTs:

```http
X-Idempotency-Key: qualquer-chave-unica
```

Comportamento implementado:

- Se o header `X-Idempotency-Key` não for enviado: retorna `400 BAD_REQUEST`.
- Se a mesma chave for reutilizada em outro POST: retorna `409 CONFLICT`.
- Se já existir um recurso com o mesmo `nome`: retorna `409 CONFLICT`.
- Se estiver tudo correto: cria o recurso e retorna `201 CREATED`.

Arquivos alterados/adicionados:

- `services/IdempotencyService.java`
- `PoderController.java`
- `GrupoController.java`
- `SuperHeroiController.java`
- `VilaoController.java`
- `EsconderijoController.java`
- Todos os `Repository.java`: adicionado `existsByNomeIgnoreCase`
- Todos os `Service.java`: validação de duplicidade no método `salvar`
