# ERP Varejo API

API RESTful para validacao fiscal de notas fiscais em sistema de ERP de varejo brasileiro.

## Funcionalidades

- Autenticacao JWT (login e controle de acesso)
- Validacao fiscal de notas com multiplos itens
- Calculo automatizado de ICMS, PIS e COFINS
- Deteccao de divergencias entre impostos informados e calculados
- Documentacao Swagger automatica

## Stack

| Tecnologia | Versao |
|------------|--------|
| Java | 17+ |
| Maven | 3.9.16 (via Maven Wrapper) |
| Spring Boot | 3.2.5 |
| Spring Security | 6.2.x (via Spring Boot) |
| Spring Data JPA | 3.2.x (via Spring Boot) |
| H2 Database | 2.2.x (via Spring Boot) |
| jjwt (JWT) | 0.12.5 |
| SpringDoc OpenAPI | 2.5.0 |

## Como Executar

### Requisitos

- Java 17 ou superior (unico prerequisito)

### Maven Wrapper

O projeto utiliza **Maven Wrapper** (`./mvnw`), que garante que qualquer pessoa consiga buildar o projeto sem instalar Maven manualmente.

- O wrapper baixa automaticamente o Maven 3.9.16 na primeira execucao
- O Maven fica em cache em `~/.m2/wrapper/dists/` para execucoes seguintes
- Elimina problemas de versao entre ambientes (dev, CI, producao)
- **Nao e necessario ter Maven instalado na maquina** — so Java

> **Windows:** Use `.\mvnw` no lugar de `./mvnw`

### Passo a passo

```bash
# Clone o repositorio
git clone https://github.com/kazuyabr/irrah-tech-challenges-fiscal.git
cd irrah-tech-challenges-fiscal

# Execute a aplicacao
./mvnw spring-boot:run
```

A aplicacao estara disponivel em: `http://localhost:8000`

### Endpoints

| Metodo | URL | Descricao | Autenticacao |
|--------|-----|-----------|--------------|
| POST | /api/auth/login | Login e obter token JWT | Nao |
| POST | /api/fiscal/validar-nota | Validar nota fiscal | Sim (Bearer token) |
| GET | /swagger-ui.html | Documentacao Swagger | Nao |
| GET | /h2-console | Console do banco H2 | Nao |

## Credenciais

| Usuario | Email | Senha | Permissao |
|---------|-------|-------|-----------|
| Admin ERP | admin@erpvarejo.com | Admin@123 | Acesso total |
| Operador Caixa 01 | caixa01@erpvarejo.com | User@123 | Envio de notas |
| Operador Caixa 02 | caixa02@erpvarejo.com | User@123 | Envio de notas |

**Onde configurar:** `src/main/java/com/erpvarejo/config/DataInitializer.java`

## Como Testar

### 1. Fazer login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "caixa01@erpvarejo.com", "senha": "User@123"}'
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuario": "caixa01@erpvarejo.com"
}
```

### 2. Validar nota fiscal (com impostos corretos)

```bash
curl -X POST http://localhost:8080/api/fiscal/validar-nota \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -d '{
    "numeroNota": "NF-001",
    "ufOrigem": "PR",
    "ufDestino": "PR",
    "itens": [
      {
        "codigoProduto": "PROD-003",
        "nome": "Cerveja IPA 500ml",
        "categoria": "BEBIDAS_ALCOOLICAS",
        "quantidade": 5,
        "valorUnitario": 12.00,
        "desconto": 0.00,
        "impostosInformados": {
          "icms": 10.80,
          "pis": 0.00,
          "cofins": 0.00
        }
      }
    ]
  }'
```

Resposta esperada (status APROVADA):
```json
{
  "numeroNota": "NF-001",
  "status": "APROVADA",
  "valorTotalNota": 60.00,
  "totalImpostosCalculados": 10.80,
  "divergencias": []
}
```

### 3. Validar nota fiscal (com divergencias)

```bash
curl -X POST http://localhost:8080/api/fiscal/validar-nota \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <SEU_TOKEN>" \
  -d '{
    "numeroNota": "NF-002",
    "ufOrigem": "PR",
    "ufDestino": "RJ",
    "itens": [
      {
        "codigoProduto": "PROD-004",
        "nome": "Teclado Mecanico",
        "categoria": "ELETRONICOS",
        "quantidade": 1,
        "valorUnitario": 100.00,
        "desconto": 10.00,
        "impostosInformados": {
          "icms": 16.20,
          "pis": 1.49,
          "cofins": 6.84
        }
      }
    ]
  }'
```

Resposta esperada (status DIVERGENTE):
```json
{
  "numeroNota": "NF-002",
  "status": "DIVERGENTE",
  "valorTotalNota": 90.00,
  "totalImpostosCalculados": 19.13,
  "divergencias": [
    {
      "codigoProduto": "PROD-004",
      "imposto": "ICMS",
      "valorInformado": 16.20,
      "valorCorreto": 10.80,
      "mensagem": "Divergencia de ICMS: Operacao interestadual (PR -> RJ) deve aplicar 12% sobre a base R$ 90,00."
    }
  ]
}
```

## Regras Fiscais

### ICMS

| Condicao | Aliquota |
|----------|----------|
| Venda interna (mesmo estado) | 18% |
| Venda interestadual (estados diferentes) | 12% |
| Produto da categoria CESTA_BASICA | 0% (isento) |

### PIS

| Condicao | Aliquota |
|----------|----------|
| Padrao | 1,65% |
| Bebidas Alcoolicas (monofasico) | 0% |

### COFINS

| Condicao | Aliquota |
|----------|----------|
| Padrao | 7,60% |
| Bebidas Alcoolicas (monofasico) | 0% |

### Base de Calculo

```
Base de Calculo = (Quantidade x Valor Unitario) - Desconto
Valor do Imposto = Base de Calculo x (Aliquota / 100)
```

### Margem de Tolerancia

A API aceita variacoes de arredondamento de ate R$ 0,02 por imposto.

**Onde configurar:** `src/main/java/com/erpvarejo/service/FiscalService.java` (constante `MARGEM_TOLERANCIA`)

## Configuracoes

### Porta do Servidor

**Padrao:** 8080
**Onde alterar:** `src/main/resources/application.properties`
```properties
server.port=8080
```

### Chave Secreta JWT

**Onde alterar:** `src/main/resources/application.properties`
```properties
jwt.secret=SuaChaveSecretaAqui (minimo 32 caracteres)
```

### Expiracao do Token JWT

**Padrao:** 86400000 ms (24 horas)
**Onde alterar:** `src/main/resources/application.properties`
```properties
jwt.expiracao=86400000
```

### Banco de Dados H2

**Onde alterar:** `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:h2:mem:erpvarejo
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### CORS (Acesso Externo)

**Onde alterar:** `src/main/java/com/erpvarejo/config/SecurityConfig.java`
```java
configuration.setAllowedOrigins(List.of("*"));  // Alterar para dominio especifico em producao
```

### Regras de Impostos

**Onde alterar:** `src/main/java/com/erpvarejo/service/FiscalService.java`
```java
private static final BigDecimal ICMS_INTERNO = new BigDecimal("18");
private static final BigDecimal ICMS_INTERESTADUAL = new BigDecimal("12");
private static final BigDecimal PIS_PADRAO = new BigDecimal("1.65");
private static final BigDecimal COFINS_PADRAO = new BigDecimal("7.60");
```

### Categorias de Produtos

**Onde alterar:** `src/main/java/com/erpvarejo/enums/CategoriaProduto.java`
```java
public enum CategoriaProduto {
    ELETRONICOS,
    CESTA_BASICA,
    BEBIDAS_ALCOOLICAS
}
```

### Usuarios e Produtos Iniciais

**Onde alterar:** `src/main/java/com/erpvarejo/config/DataInitializer.java`

## Estrutura do Projeto

```
src/main/java/com/erpvarejo/
├── ErpvarejoApplication.java    # Ponto de entrada
├── config/
│   ├── SecurityConfig.java      # Configuracao de seguranca
│   ├── JwtAuthFilter.java       # Filtro de autenticacao JWT
│   ├── SwaggerConfig.java       # Configuracao do Swagger
│   └── DataInitializer.java     # Populacao de dados iniciais
├── controller/
│   ├── AuthController.java      # Endpoint de autenticacao
│   └── FiscalController.java    # Endpoint de validacao fiscal
├── dto/
│   ├── LoginRequest.java        # Dados de entrada do login
│   ├── LoginResponse.java       # Dados de saida do login
│   ├── NotaFiscalRequest.java   # Dados de entrada da nota
│   ├── NotaFiscalResponse.java  # Dados de saida da nota
│   ├── ItemNotaRequest.java     # Dados de cada item
│   ├── ImpostosInformados.java  # Impostos informados pelo usuario
│   └── DivergenciaResponse.java # Divergencia encontrada
├── enums/
│   ├── CategoriaProduto.java    # Categorias de produto
│   ├── Uf.java                  # Unidades federativas
│   ├── StatusNota.java          # Status da nota
│   └── RoleUsuario.java         # Roles de usuario
├── exception/
│   └── GlobalExceptionHandler.java  # Tratamento de excecoes
├── model/
│   ├── Usuario.java             # Entidade de usuario
│   └── Produto.java             # Entidade de produto
├── repository/
│   ├── UsuarioRepository.java   # Acesso a dados de usuarios
│   └── ProdutoRepository.java   # Acesso a dados de produtos
└── service/
    ├── AuthService.java         # Logica de autenticacao
    └── FiscalService.java       # Logica de calculo de impostos
```

## Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes especificos
./mvnw test -Dtest=FiscalServiceTest
```

A saida dos testes exibe `@DisplayName` descritivos para cada teste, facilitando a identificacao do que esta sendo validado.

## Observacoes Tecnicas

- Senhas armazenadas com hash BCrypt
- Tokens JWT com expiracao configuravel
- Calculos financeiros usando BigDecimal
- Arredondamento HALF_UP (padrao brasileiro)
- Dados iniciais carregados via CommandLineRunner
- H2 Console disponivel em `/h2-console` para inspecao do banco
