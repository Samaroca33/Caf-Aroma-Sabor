## 🔐 IMPLEMENTAÇÃO SPRING SECURITY 6 - CAFÉ AROMA & SABOR

### 📋 RESUMO EXECUTIVO

Foi implementado um sistema completo de **Spring Security 6** com autenticação **in-memory** no projeto Café Aroma & Sabor. Todas as rotas agora exigem autenticação, com proteção via BCryptPasswordEncoder.

---

## 🎯 O QUE FOI IMPLEMENTADO

### ✅ 1. ENTIDADES JPA COM RELACIONAMENTOS

#### **Usuario.java** (atualizado)
- Adicionado: `@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`
- Relacionamento with Movimentacao
- Campos: id, nome, login, senha, movimentacoes

#### **Produto.java** (atualizado)
- Adicionado: `@OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)`
- Relacionamento with Movimentacao
- Campos: id, nome, descrição, preço, lote, dataValidade, estoqueMinimo, quantidadeAtual, movimentacoes

#### **Movimentacao.java** (existente)
- Já possui `@ManyToOne` com Usuario
- Já possui `@ManyToOne` com Produto
- Enum TipoMovimentacao (ENTRADA/SAIDA)

### ✅ 2. REPOSITÓRIOS

Todos os repositórios já estão implementados e prontos:
- ✅ **UsuarioRepository** → método: `findByLogin(String login)`
- ✅ **ProdutoRepository** → método: `findByNomeContainingIgnoreCase(String nome)`
- ✅ **MovimentacaoRepository** → métodos para busca por período

### ✅ 3. CONFIGURAÇÃO SPRING SECURITY

**Arquivo novo:** `SecurityConfig.java` (em `config/`)

**Credenciais de teste (in-memory):**
```
👤 ADMIN:
   - Username: admin@cafe.com
   - Password: admin123
   - Roles: ADMIN, USER

👤 USUÁRIO COMUM:
   - Username: user@cafe.com
   - Password: user123
   - Roles: USER
```

**Configurações:**
- ✅ Autenticação in-memory com BCryptPasswordEncoder
- ✅ Proteção de todas as rotas (exceto /login e /css/**)
- ✅ Formulário de login em /login
- ✅ Logout via POST para /logout
- ✅ Redirecionamento pós-logout: /login?logout=true
- ✅ CSRF desabilitado para simplificar testes

### ✅ 4. CONTROLLERS ATUALIZADOS

#### **LoginController.java** (refatorado)
```java
@GetMapping("/login")
public String login(@RequestParam(required = false) String error,
                    @RequestParam(required = false) String logout,
                    Model model) {
    // Exibe mensagem de erro ou logout
}

// O logout é gerenciado automaticamente pelo Spring Security
```

#### **HomeController.java** (atualizado)
```java
@GetMapping("/home")
public String home(Model model, Authentication authentication) {
    // Agora recebe objeto Authentication do Spring Security
    // Exibe: usuarioAutenticado, nomeUsuario
}
```

#### **ProdutoController.java** (já tem @Autowired)
#### **EstoqueController.java** (corrigido construtores)

### ✅ 5. TEMPLATES THYMELEAF ATUALIZADOS

#### **login.html** (Spring Security)
```html
<!-- Campos esperados pelo Spring Security -->
<form method="POST" th:action="@{/login}">
    <input type="text" name="username" ... />
    <input type="password" name="password" ... />
</form>

<!-- Exibe erros do Spring Security -->
<div th:if="${param.error}">Usuário ou senha inválidos!</div>

<!-- Exibe mensagem de logout bem-sucedido -->
<div th:if="${param.logout}">Você foi desconectado com sucesso.</div>
```

#### **home.html** (Logout Seguro + Dados do Usuário)
```html
<!-- Exibe dados do usuário autenticado -->
<div class="user-info">
    <div class="avatar" th:text="${nomeUsuario}">US</div>
    <div class="user-name" th:text="${usuarioAutenticado}">Usuário</div>
</div>

<!-- Formulário de LOGOUT seguro via POST -->
<form method="POST" th:action="@{/logout}">
    <button type="submit">Sair</button>
</form>
```

---

## 🚀 COMO USAR

### 1️⃣ INICIAR A APLICAÇÃO

```bash
# Dentro do diretório do projeto
cd C:\Users\49077751807\IdeaProjects\Caf-Aroma-Sabor

# Executar com Maven wrapper
.\mvnw.cmd spring-boot:run
```

Server iniciará em: **http://localhost:8080**

### 2️⃣ FAZER LOGIN

Acesse: http://localhost:8080/login

Credenciais de teste:
- **Admin:** admin@cafe.com / admin123
- **User:** user@cafe.com / user123

### 3️⃣ ACESSAR O DASHBOARD

Após login bem-sucedido, será redirecionado para: http://localhost:8080/home

### 4️⃣ FAZER LOGOUT

Clique em "Sair" no menu lateral (formulário POST seguro)
Será redirecionado para: http://localhost:8080/login?logout=true

---

## 📁 ESTRUTURA DE ARQUIVOS

```
src/main/java/sp/senai/br/cafearomasabores/
├── config/
│   └── SecurityConfig.java         ✨ NOVO
├── model/
│   ├── Usuario.java                🔄 ATUALIZADO (adicionar @OneToMany)
│   ├── Produto.java                🔄 ATUALIZADO (adicionar @OneToMany)
│   └── Movimentacao.java           ✅ INTACTO
├── repository/
│   ├── UsuarioRepository.java      ✅ OK
│   ├── ProdutoRepository.java      ✅ OK
│   └── MovimentacaoRepository.java ✅ OK
└── controller/
    ├── LoginController.java        🔄 REFATORADO
    ├── HomeController.java         🔄 ATUALIZADO
    ├── ProdutoController.java      ✅ OK (tem @Autowired)
    └── EstoqueController.java      🔄 CORRIGIDO

src/main/resources/
├── templates/
│   ├── login.html                  🔄 ATUALIZADO (Spring Security)
│   ├── home.html                   🔄 ATUALIZADO (logout POST + usuário)
│   └── [...outros templates]       ✅ OK
└── application.properties          ✅ OK

pom.xml                             🔄 ATUALIZADO (spring-security + thymeleaf-extras)
```

---

## 🔑 PONTOS-CHAVE DA IMPLEMENTAÇÃO

### 1. **Autenticação In-Memory**
```java
@Bean
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    return new InMemoryUserDetailsManager(admin, usuario);
}
```

### 2. **Proteção de Rotas**
```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers("/login", "/css/**").permitAll()  // Livre
    .anyRequest().authenticated()                        // Protegido
)
```

### 3. **Logout Seguro via POST**
```java
.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout=true")
)
```

### 4. **Acesso ao Usuário Autenticado**
```java
@GetMapping("/home")
public String home(Authentication authentication) {
    String username = authentication.getName();  // "admin@cafe.com"
    Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
}
```

---

## 💡 PRÓXIMOS PASSOS RECOMENDADOS

### Para Produção:
1. ✅ **Integrar com Banco de Dados**
   - Substituir `InMemoryUserDetailsManager` por `UserDetailsService` customizado
   - Usar `UsuarioRepository` para buscar usuários no banco

2. ✅ **Hash de Senhas**
   - Armazenar senhas com BCrypt no banco de dados
   - Exemplo: `passwordEncoder.encode("senha123")`

3. ✅ **Autorização por Papéis**
   - Proteger rotas por Role: `@PreAuthorize("hasRole('ADMIN')")`
   - Exemplo: `/admin/**` apenas para ADMIN

4. ✅ **CSRF Protection**
   - Reativar CSRF (remove `csrf(csrf -> csrf.disable())`)
   - Adicionar tokens CSRF aos formulários: `<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>`

5. ✅ **Session Management**
   - Configurar timeout de sessão
   - Adicionar "Remember Me" (persistent tokens)

---

## 🧪 FLUXO DE TESTE

### Teste 1: Login com Credenciais Corretas
1. Acesse http://localhost:8080/login
2. Digite: admin@cafe.com / admin123
3. ✅ Esperado: Redirecionado para /home

### Teste 2: Login com Credenciais Inválidas
1. Acesse http://localhost:8080/login
2. Digite: admin@cafe.com / senhaerrada
3. ✅ Esperado: Mensagem "Usuário ou senha inválidos!"

### Teste 3: Acesso Protegido Sem Autenticação
1. Sem estar logado, acesse http://localhost:8080/produto
2. ✅ Esperado: Redirecionado para /login

### Teste 4: Logout
1. Estando logado em http://localhost:8080/home
2. Clique em "Sair"
3. ✅ Esperado: Redirecionado para /login?logout=true com mensagem de sucesso

### Teste 5: Diferença entre Roles
1. Login com admin@cafe.com (ROLE_ADMIN)
2. Verificar em "Spring Security Auditing": admin tem role ADMIN,USER
3. Login com user@cafe.com (ROLE_USER)
4. Verificar em "Spring Security Auditing": user tem apenas role USER

---

## 📚 DEPENDÊNCIAS ADICIONADAS

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Thymeleaf + Spring Security Integration -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

---

## 🔒 SEGURANÇA

### BCryptPasswordEncoder
- Força do hash: 10 (padrão)
- Algoritmo: Blowfish com salt aleatório
- Exemplo de hash: `$2a$10$F4QGqvQXd5xH5mxM6xH5m.eH9...`

### CSRF
- Desabilitado por padrão (simplificar testes)
- ⚠️ Reativar em produção!

### Session
- Padrão: 30 minutos
- Invalida automaticamente ao fazer logout

---

## ✨ RESUMO FINAL

| Componente | Status | Detalhes |
|-----------|--------|----------|
| Spring Security | ✅ Implementado | In-memory auth com BCrypt |
| Entidades | ✅ Atualizado | Relacionamentos @OneToMany |
| Repositórios | ✅ Pronto | Todos com métodos personalizados |
| Controllers | ✅ Atualizado | Integrados com Authentication |
| Templates | ✅ Atualizado | Login e logout com Spring Security |
| Compilação | ✅ Sucesso | Build sem erros |

---

**Projeto pronto para uso! 🎉**

Desenvolvido com Spring Boot 3, Spring Security 6 e Thymeleaf.
Java 21 | Maven 4 | H2 Database

