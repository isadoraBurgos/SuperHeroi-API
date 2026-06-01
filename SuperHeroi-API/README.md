# SuperHeroi REST API

API REST em Spring Boot para gerenciamento de super-heróis, vilões, poderes, grupos e esconderijos.

## Requisitos atendidos

- Spring Boot com Maven
- Java 17 ou superior
- Banco H2 configurado
- Spring Data JPA
- 5 entidades principais
- Relacionamentos One-to-One, One-to-Many e Many-to-Many
- Bean Validation
- Enum `NivelPoder`
- CRUD completo por entidade
- Listagens paginadas com Pageable
- Consulta personalizada por nome em cada entidade
- Swagger/OpenAPI com descrições e respostas HTTP
- HATEOAS com EntityModel e PagedModel

## Acesso local

- Swagger: `/swagger-ui.html`
- H2 Console: `/h2-console`
- JDBC URL: `jdbc:h2:mem:superhero-db`
- Usuário: `sa`
- Senha: vazio
