# Rate Limiting

Configuração aplicada:
- Limite pequeno: 5 requisições por 60 segundos por cliente/IP.
- Identificação por IP remoto ou `X-Forwarded-For`.
- Headers retornados:
  - `X-Rate-Limit-Limit`
  - `X-Rate-Limit-Remaining`
  - `X-Rate-Limit-Reset`
  - `Retry-After` quando o limite é excedido
- Status ao exceder: `429 Too Many Requests`.
