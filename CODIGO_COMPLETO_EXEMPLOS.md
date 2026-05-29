## 📖 GUIA COMPLETO DE CÓDIGO - SPRING SECURITY 6

### 1️⃣ ESTRUTURA DAS ENTIDADES

#### ✨ Usuario.java (COMPLETO)
```java
package sp.senai.br.cafearomasabores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do usuário é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Login é obrigatório")
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String senha;

    // ✅ RELACIONAMENTO ADICIONADO
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude          // Evita loop infinito no toString()
    @EqualsAndHashCode.Exclude // Evita problemas com equals/hashCode
    private List<Movimentacao> movimentacoes;
}
```

**Explicação dos parâmetros de relacionamento:**
- `mappedBy = "usuario"` → Movimentacao tem atributo "usuario"
- `cascade = CascadeType.ALL` → Deletar usuário deleta suas movimentações
- `fetch = FetchType.LAZY` → Carrega movimentações sob demanda (performance)

---

#### ✨ Produto.java (COMPLETO)
```java
package sp.senai.br.cafearomasabores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Número do lote é obrigatório")
    @Column(nullable = false)
    private String lote;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Min(value = 1, message = "Estoque mínimo deve ser maior que 0")
    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidadeAtual;

    // ✅ RELACIONAMENTO ADICIONADO
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Movimentacao> movimentacoes;
}
```

---

### 2️⃣ CONFIGURAÇÃO SPRING SECURITY

#### ✨ SecurityConfig.java (COMPLETO)
```java
package sp.senai.br.cafearomasabores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança (Spring Security 6)
 * Define autenticação in-memory, proteção de rotas e configuração de logout
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * ✅ BEAN 1: PasswordEncoder
     * Codifica senha com BCrypt (algoritmo seguro com salt aleatório)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ BEAN 2: UserDetailsService (In-Memory)
     * Define usuários de teste em memória (não persistem após reiniciar)
     * 
     * ADMIN: admin@cafe.com / admin123 → ROLE_ADMIN, ROLE_USER
     * USER:  user@cafe.com  / user123  → ROLE_USER
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin@cafe.com")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "USER")  // Múltiplos papéis
                .build();

        UserDetails usuario = User.builder()
                .username("user@cafe.com")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, usuario);
    }

    /**
     * ✅ BEAN 3: SecurityFilterChain
     * Define as regras de segurança da aplicação
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // PASSO 1: Autorização
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/login", "/css/**", "/js/**", "/images/**")
                        .permitAll()  // Permite acesso público
                    .anyRequest()
                        .authenticated()  // Tudo mais requer autenticação
            )
            
            // PASSO 2: Formulário de Login
            .formLogin(form -> form
                    .loginPage("/login")              // URL GET para exibir form
                    .loginProcessingUrl("/login")     // URL POST para processar
                    .defaultSuccessUrl("/home", true) // Redireciona após sucesso
                    .failureUrl("/login?error=true")  // Redireciona em caso de erro
                    .permitAll()                      // Permite acesso ao login
            )
            
            // PASSO 3: Logout
            .logout(logout -> logout
                    .logoutUrl("/logout")              // URL para fazer logout
                    .logoutSuccessUrl("/login?logout=true")  // Redireciona após logout
                    .invalidateHttpSession(true)       // Invalida a sessão
                    .clearAuthentication(true)         // Limpa autenticação
                    .permitAll()
            )
            
            // PASSO 4: CSRF (desabilitado para simplificar testes)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
```

---

### 3️⃣ CONTROLLERS COM SPRING SECURITY

#### ✨ LoginController.java (REFATORADO)
```java
package sp.senai.br.cafearomasabores.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    /**
     * Exibe a página de login
     * Parâmetros opcionais detectados via Spring Security:
     * - error=true → Erro de autenticação
     * - logout=true → Logout bem-sucedido
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("erro", "Usuário ou senha inválidos!");
        }
        
        if (logout != null) {
            model.addAttribute("sucesso", "Você foi desconectado com sucesso. Até logo!");
        }
        
        return "login";
    }

    /**
     * Método auxiliar para obter autenticação atual
     * Útil em qualquer classe para checar usuário logado
     */
    public static Authentication getAuthenticationAtual() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
```

#### ✨ HomeController.java (ATUALIZADO)
```java
package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.repository.MovimentacaoRepository;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /**
     * Home com dados do usuário autenticado
     * 
     * ✅ NEW PARAMETER: Authentication authentication
     * - Injetado automaticamente pelo Spring Security
     * - Contém dados do usuário logado
     */
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        try {
            // Dashboard stats
            long totalProdutos = produtoRepository.count();
            model.addAttribute("totalProdutos", totalProdutos);

            List<Produto> todosProdutos = produtoRepository.findAll();
            long productosCriticos = todosProdutos.stream()
                .filter(p -> p.getQuantidadeAtual() < p.getEstoqueMinimo())
                .count();
            model.addAttribute("productosCriticos", productosCriticos);

            LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
            long movimentacoesRecentes = movimentacaoRepository
                .findByDataHoraBetweenOrderByDataHoraDesc(seteDiasAtras, LocalDateTime.now())
                .size();
            model.addAttribute("movimentacoesRecentes", movimentacoesRecentes);
            
            // ✅ NOVO: Informações do usuário autenticado
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();  // "admin@cafe.com"
                model.addAttribute("usuarioAutenticado", username);
                
                // Extrai primeira letra do nome para avatar
                String nomeExibicao = username.contains("@") 
                    ? username.split("@")[0].substring(0, 1).toUpperCase() + 
                      username.split("@")[0].substring(1)
                    : username;
                model.addAttribute("nomeUsuario", nomeExibicao);
                
                // Opcional: Obter roles/autoridades
                var roles = authentication.getAuthorities();
                // roles.stream().map(auth -> auth.getAuthority()).forEach(System.out::println);
            }

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());
        }

        return "home";
    }
}
```

---

### 4️⃣ TEMPLATES THYMELEAF

#### ✨ login.html (COM SPRING SECURITY)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="pt-BR">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Café Aroma & Sabor — Login</title>
    <link rel="stylesheet" th:href="@{/css/almoxarifado.css}"/>
</head>
<body>

<div id="page-login" class="page active">
    <div class="login-card">
        <h1>Café Aroma & Sabor</h1>
        <p>Sistema de Almoxarifado</p>

        <!-- ✅ ERRO DO SPRING SECURITY -->
        <!-- Detecta automaticamente ?error=true na URL -->
        <div th:if="${param.error}" class="error-message">
            <span>Usuário ou senha inválidos! Tente novamente.</span>
        </div>

        <!-- ✅ LOGOUT BEM-SUCEDIDO -->
        <!-- Detecta automaticamente ?logout=true na URL -->
        <div th:if="${param.logout}" class="success-message">
            <span>Você foi desconectado com sucesso. Até logo!</span>
        </div>

        <!-- ✅ FORMULÁRIO COM SPRING SECURITY -->
        <!-- 
        IMPORTANTE:
        - th:action="@{/login}" → Gera URL correta /login
        - method="POST" → Obrigatório para Spring Security
        - name="username" → Campo de username (padrão Spring Security)
        - name="password" → Campo de password (padrão Spring Security)
        -->
        <form method="POST" th:action="@{/login}">
            <div class="form-group">
                <label>Usuário</label>
                <input 
                    type="text" 
                    name="username" 
                    placeholder="admin@cafe.com ou user@cafe.com" 
                    autocomplete="username" 
                    required/>
            </div>
            
            <div class="form-group">
                <label>Senha</label>
                <input 
                    type="password" 
                    name="password" 
                    placeholder="••••••••" 
                    autocomplete="current-password" 
                    required/>
            </div>
            
            <button type="submit" class="btn btn-primary btn-full">
                Acessar o sistema
            </button>
        </form>

        <!-- Credenciais de teste -->
        <div style="margin-top: 24px; padding-top: 24px; font-size: 0.8rem;">
            <p><strong>Credenciais de Teste:</strong></p>
            <p><strong>Admin:</strong> admin@cafe.com / admin123</p>
            <p><strong>User:</strong> user@cafe.com / user123</p>
        </div>
    </div>
</div>

</body>
</html>
```

#### ✨ home.html (COM LOGOUT SEGURO E DADOS DO USUÁRIO)
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" 
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security" 
      lang="pt-BR">
<head>
    <meta charset="UTF-8"/>
    <title>Café Aroma & Sabor — Dashboard</title>
</head>
<body>

<div id="page-app" class="page active">
    
    <!-- SIDEBAR -->
    <nav class="sidebar">
        <div class="sidebar-brand">
            <h2>Aroma & Sabor</h2>
        </div>
        
        <div class="sidebar-nav">
            <a class="nav-link active" th:href="@{/home}">
                Dashboard
            </a>
            <a class="nav-link" th:href="@{/produto}">
                Cadastro de Produto
            </a>
            <a class="nav-link" th:href="@{/estoque}">
                Gestão de Estoque
            </a>
        </div>

        <!-- ✅ FOOTER COM USUÁRIO E LOGOUT SEGURO -->
        <div class="sidebar-footer">
            
            <!-- Informações do usuário autenticado (vem do HomeController) -->
            <div class="user-info">
                <!-- Avatar com iniciais do usuário -->
                <div class="avatar" 
                     th:text="${nomeUsuario != null ? nomeUsuario.substring(0, 2).toUpperCase() : 'US'}">
                    US
                </div>
                <div class="user-details">
                    <!-- Username: admin@cafe.com ou user@cafe.com -->
                    <div class="user-name" th:text="${usuarioAutenticado != null ? usuarioAutenticado : 'Usuário'}">
                        Usuário
                    </div>
                    <div class="user-role">Logado</div>
                </div>
            </div>

            <!-- ✅ LOGOUT SEGURO VIA POST -->
            <!-- 
            IMPORTANTE:
            - method="POST" → Mais seguro que GET
            - th:action="@{/logout}" → URL gerenciada pelo Spring Security
            - O formulário é enviado quando o botão é clicado
            -->
            <form method="POST" th:action="@{/logout}" style="display: inline; width: 100%;">
                <button type="submit" class="nav-link" style="
                    background: none;
                    border: none;
                    color: rgba(245,237,224,.5);
                    cursor: pointer;
                    width: 100%;
                    text-align: left;
                    padding: 12px 20px;">
                    ↩ Sair
                </button>
            </form>
        </div>
    </nav>

    <!-- MAIN CONTENT -->
    <div class="main-content">
        <div class="topbar">
            <div class="topbar-title">Dashboard</div>
        </div>
        
        <div class="content-area">
            <!-- KPI CARDS -->
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-label">Total de Produtos</div>
                    <div class="kpi-value" th:text="${totalProdutos != null ? totalProdutos : 0}">
                        0
                    </div>
                </div>
                
                <div class="kpi-card alert-card">
                    <div class="kpi-label">Estoque Mínimo ⚠</div>
                    <div class="kpi-value" th:text="${productosCriticos != null ? productosCriticos : 0}">
                        0
                    </div>
                </div>
                
                <div class="kpi-card">
                    <div class="kpi-label">Movimentações Recentes</div>
                    <div class="kpi-value" th:text="${movimentacoesRecentes != null ? movimentacoesRecentes : 0}">
                        0
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
```

---

### 5️⃣ POM.XML - DEPENDÊNCIAS

```xml
<!-- SPRING SECURITY -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- THYMELEAF + SPRING SECURITY -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>
```

---

## 🎯 FLUXO DE AUTENTICAÇÃO

```
1. Usuário acessa http://localhost:8080/home
                ↓
2. Spring Security intercepta a requisição
                ↓
3. Verifica se está autenticado
                ↓
4. NÃO ESTÁ? → Redireciona para /login
                ↓
5. Usuário preenche form e clica "Acessar"
                ↓
6. POST /login com username + password
                ↓
7. Spring Security valida credenciais:
   - Busca usuário em InMemoryUserDetailsManager
   - Compara password com BCrypt
                ↓
8. Credenciais VÁLIDAS?
   - SIM → Cria sessão + Authentication
   - NÃO → Redireciona para /login?error=true
                ↓
9. Usuário logado, redireciona para /home
                ↓
10. HomeController recebe Authentication do Spring
                ↓
11. View exibe dados do usuário
                ↓
12. Usuário clica "Sair"
                ↓
13. POST /logout
                ↓
14. Spring Security invalida sessão
                ↓
15. Redireciona para /login?logout=true
```

---

## 📝 CHECKLIST DE VERIFICAÇÃO

- ✅ SecurityConfig.java criado em `config/`
- ✅ Usuario.java possui `@OneToMany(mappedBy = "usuario")`
- ✅ Produto.java possui `@OneToMany(mappedBy = "produto")`
- ✅ LoginController refatorado
- ✅ HomeController recebe Authentication
- ✅ login.html com name="username" e name="password"
- ✅ home.html com formulário POST para logout
- ✅ pom.xml com spring-boot-starter-security
- ✅ pom.xml com thymeleaf-extras-springsecurity6
- ✅ Projeto compila sem erros
- ✅ Credenciais de teste: admin@cafe.com / admin123

---

**Documentação gerada com Spring Boot 3 e Spring Security 6**

