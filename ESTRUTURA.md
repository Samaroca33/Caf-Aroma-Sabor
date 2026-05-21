# CafeAromaESabor - Documentação da Estrutura

## 📋 Visão Geral

Este documento descreve a estrutura completa da aplicação **CafeAromaESabor**, um sistema de gestão de estoque de café desenvolvido com Spring Boot 4.0.6 e Thymeleaf.

---

## 🏗️ Estrutura do Projeto

```
src/main/java/sp/senai/br/cafearomasabores/
├── model/
│   ├── Usuario.java          - Modelo para usuários do sistema
│   ├── Produto.java          - Modelo para produtos de café
│   └── Movimentacao.java     - Modelo para movimentações de estoque
├── repository/
│   ├── UsuarioRepository.java      - Operações DB de Usuários
│   ├── ProdutoRepository.java      - Operações DB de Produtos
│   └── MovimentacaoRepository.java - Operações DB de Movimentações
├── controller/
│   ├── LoginController.java       - Gerenciar autenticação
│   ├── HomeController.java        - Página inicial/dashboard
│   ├── ProdutoController.java     - Gerenciar produtos
│   └── EstoqueController.java     - Gerenciar movimentações
└── CafeAromaSaboresApplication.java - Classe principal

src/main/resources/
├── application.properties       - Configurações MySQL e Thymeleaf
├── templates/
│   ├── login.html             - Login da aplicação
│   ├── home.html              - Dashboard principal
│   ├── produto/
│   │   ├── listagem.html      - Listagem de produtos
│   │   └── form-inserir.html  - Formulário de cadastro
│   └── estoque/
│       └── movimentacao.html  - Interface de movimentação
└── static/                     - Arquivos estáticos (CSS, JS, imagens)
```

---

## 📊 Modelos de Dados (Models)

### **1. Usuario**
Representa um usuário do sistema para autenticação.

**Atributos:**
- `id` (Long) - Identificador único
- `nome` (String) - Nome do usuário (obrigatório)
- `login` (String) - Login único (obrigatório)
- `senha` (String) - Senha do usuário (obrigatória)

**Validações:**
- `@NotBlank` em nome, login e senha

### **2. Produto**
Representa um produto de café no estoque.

**Atributos:**
- `id` (Long) - Identificador único
- `nome` (String) - Nome do produto (obrigatório)
- `descricao` (String) - Descrição detalhada
- `lote` (String) - Número do lote (obrigatório)
- `dataValidade` (LocalDate) - Data de validade
- `estoqueMinimo` (Integer) - Quantidade mínima (mínimo 1)
- `quantidadeAtual` (Integer) - Quantidade em estoque (mínimo 0)

**Validações:**
- `@NotBlank` em nome e lote
- `@Min(1)` em estoque mínimo
- `@Min(0)` em quantidade atual

### **3. Movimentacao**
Registra entrada/saída de produtos do estoque.

**Atributos:**
- `id` (Long) - Identificador único
- `tipo` (TipoMovimentacao) - ENTRADA ou SAIDA (obrigatório)
- `quantidade` (Integer) - Quantidade movimentada (mínimo 1)
- `dataHora` (LocalDateTime) - Data/hora do registro (automático)
- `motivo` (String) - Razão da movimentação (obrigatório)
- `produto` (Produto) - Produto envolvido
- `usuario` (Usuario) - Usuário responsável

**Enum TipoMovimentacao:**
- `ENTRADA` - Entrada de estoque
- `SAIDA` - Saída de estoque

**Recurso Especial:**
- `@PrePersist`: Define data/hora ao salvar

---

## 🛢️ Repositories

Estendem `JpaRepository` para operações CRUD e consultas customizadas.

### **UsuarioRepository**
```java
Optional<Usuario> findByLogin(String login)
```
Busca um usuário pelo login.

### **ProdutoRepository**
```java
List<Produto> findByNomeContainingIgnoreCase(String nome)
List<Produto> findByLote(String lote)
```
Busca produtos por nome ou lote.

### **MovimentacaoRepository**
```java
List<Movimentacao> findByProdutoOrderByDataHoraDesc(Produto produto)
List<Movimentacao> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime, LocalDateTime)
```
Busca movimentações por produto ou período.

---

## 🎮 Controllers e Rotas

### **LoginController**
Gerencia autenticação de usuários.

| Rota | Método | Descrição |
|------|--------|-----------|
| `/login` | GET | Retorna tela de login |
| `/login` | POST | Processa autenticação |

**View Retornada:** `login.html`

### **HomeController**
Retorna o dashboard principal.

| Rota | Método | Descrição |
|------|--------|-----------|
| `/home` | GET | Dashboard com estatísticas |

**View Retornada:** `home.html`

**Dados Esperados no Model:**
- `totalProdutos` - Quantidade total de produtos
- `productosCriticos` - Produtos com estoque crítico
- `movimentacoesRecentes` - Movimentações recentes

### **ProdutoController**
Gerencia CRUD de produtos.

| Rota | Método | Descrição |
|------|--------|-----------|
| `/produto` | GET | Lista todos os produtos |
| `/produto/novo` | GET | Formulário de novo produto |
| `/produto` | POST | Salva novo produto |

**Views Retornadas:**
- `produto/listagem.html` - Lista de produtos
- `produto/form-inserir.html` - Formulário de cadastro

**Dados Esperados no Model:**
- `produtos` - Lista de produtos (GET)

### **EstoqueController**
Gerencia movimentações de estoque.

| Rota | Método | Descrição |
|------|--------|-----------|
| `/estoque` | GET | Interface de movimentação |
| `/estoque/entrada` | POST | Registra entrada de estoque |
| `/estoque/saida` | POST | Registra saída de estoque |

**View Retornada:** `estoque/movimentacao.html`

**Dados Esperados no Model:**
- `produtos` - Lista de produtos disponíveis
- `movimentacoes` - Histórico de movimentações

---

## 🎨 Templates HTML (Thymeleaf)

Todos os templates utilizam **Thymeleaf** para dinâmica com Spring.

### **login.html**
- Formulário de autenticação
- Mensagens de erro
- Estilo responsivo

### **home.html**
- Dashboard com cards de estatísticas
- Navegação entre seções
- Menu principal

### **produto/listagem.html**
- Tabela de produtos
- Botão para criar novo produto
- Status de estoque (OK/CRÍTICO)
- Ações de editar/deletar

### **produto/form-inserir.html**
- Formulário de cadastro de produto
- Campos: nome, descrição, lote, data, quantidade, mínimo
- Validação HTML5

### **estoque/movimentacao.html**
- Dois formulários lado a lado (Entrada/Saída)
- Tabela com histórico de movimentações
- Badges para tipo de movimentação

---

## ⚙️ Configuração (application.properties)

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/cafearomasabor
spring.datasource.username=root
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Thymeleaf
spring.thymeleaf.cache=false

# Server
server.port=8080
```

**Importante:** Substitua `root` e senha pela credencial do seu MySQL.

---

## 🔧 Dependências do Projeto

✅ **Spring Web** - Framework web  
✅ **Spring Data JPA** - ORM e persistência  
✅ **Thymeleaf** - Template engine  
✅ **Validation** - Validações (@NotBlank, @Min, etc)  
✅ **MySQL Driver** - Conector MySQL  
✅ **Lombok** - Redução de boilerplate (@Data, @NoArgsConstructor)  

---

## 📝 Preparação para Desenvolvimento

Todos os controllers e métodos possuem comentários `TODO` indicando onde implementar a lógica de negócio:

```java
// TODO: Lógica de listagem de produtos será implementada aqui
// - Buscar todos os produtos do banco de dados
// - Aplicar filtros se necessário
// - Adicionar lista ao model
```

**Próximos Passos:**
1. ✅ Criar base de dados MySQL: `cafearomasabor`
2. ✅ Testar conexão (application.properties)
3. ⏳ Implementar lógica de autenticação
4. ⏳ Implementar operações CRUD
5. ⏳ Adicionar trata de erros e mensagens

---

## 🗂️ Estrutura do Banco de Dados

O Hibernate criará automaticamente as tabelas:

**usuario**
```sql
CREATE TABLE usuario (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);
```

**produto**
```sql
CREATE TABLE produto (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    lote VARCHAR(255) NOT NULL,
    data_validade DATE NOT NULL,
    estoque_minimo INT NOT NULL,
    quantidade_atual INT NOT NULL
);
```

**movimentacao**
```sql
CREATE TABLE movimentacao (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tipo VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    data_hora DATETIME NOT NULL,
    motivo TEXT NOT NULL,
    produto_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

---

## 📚 Exemplos de Uso do Thymeleaf

### Iteração sobre lista:
```html
<tr th:each="produto : ${produtos}">
    <td th:text="${produto.nome}">Nome</td>
</tr>
```

### Condicionais:
```html
<span th:if="${produto.quantidadeAtual >= produto.estoqueMinimo}" 
      class="status-ok">OK</span>
```

### Links com @{} expresssion:
```html
<a th:href="@{/produto/novo}">Novo Produto</a>
```

### Formatação de datas:
```html
<td th:text="${#dates.format(produto.dataValidade, 'dd/MM/yyyy')}">Data</td>
```

---

## 🚀 Como Executar

1. **Criar banco de dados:**
```sql
CREATE DATABASE cafearomasabor;
```

2. **Atualizar application.properties** com credenciais MySQL

3. **Executar aplicação:**
```bash
./mvnw spring-boot:run
```

4. **Acessar em:** `http://localhost:8080/login`

---

## 📞 Notas Importantes

- ✅ Todos os modelos usam **Lombok** para reduzir código
- ✅ Validações implementadas com **Jakarta Validation**
- ✅ Controllers preparados com `Model` parameter para Thymeleaf
- ✅ Templates organizados em subpastas (`/produto`, `/estoque`)
- ✅ application.properties totalmente configurado
- ⏳ Lógica de negócio pronta para ser implementada nos TODO's

---

**Desenvolvido com ☕ para CafeAromaESabor**

