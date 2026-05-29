## 📖 GUIA DE CONFIGURAÇÃO NO INTELLIJ IDEA

### 🎯 Objetivo
Validar a implementação, testar e executar o projeto com Spring Security no IntelliJ IDEA

---

## 📝 PASSO 1: Abrir o Projeto

### 1.1 - Iniciar IntelliJ
```
Clique em: File → Open
(ou Ctrl+O)
```

### 1.2 - Selecionar a Pasta do Projeto
```
Caminho: C:\Users\49077751807\IdeaProjects\Caf-Aroma-Sabor
    ↓
Clique em: "Open as Project" (ou duplo clique)
```

### 1.3 - Aguardar Indexação
```
Nova janela do IntelliJ abre
↓
Aguarde mensagem: "Indexing finished" (no rodapé)
↓
Agora o projeto está pronto
```

---

## 📁 PASSO 2: Verificar Estrutura de Arquivos

### 2.1 - Abrir Project View
```
View → Project View (ou Alt+1)
```

### 2.2 - Expandir a Pasta `src/main/java`
```
Café-Aroma-Sabor
└── src/main/java
    └── sp/senai/br/cafearomasabores
        ├── config
        │   └── SecurityConfig.java          ✨ NOVO
        ├── model
        │   ├── Usuario.java                  🔄 Atualizado
        │   ├── Produto.java                  🔄 Atualizado
        │   └── Movimentacao.java
        ├── repository
        │   ├── UsuarioRepository.java
        │   ├── ProdutoRepository.java
        │   └── MovimentacaoRepository.java
        ├── controller
        │   ├── LoginController.java           🔄 Refatorado
        │   ├── HomeController.java            🔄 Atualizado
        │   ├── ProdutoController.java
        │   └── EstoqueController.java         🔄 Corrigido
        └── CafeAromaSaboresApplication.java
```

---

## 🔍 PASSO 3: Validar Arquivos Atualizados

### 3.1 - Verificar SecurityConfig.java

```
1. Clique duplo em: SecurityConfig.java
2. Apareça o código da classe
3. Verificar se tem:
   ✅ @Configuration
   ✅ @EnableWebSecurity
   ✅ Bean: PasswordEncoder
   ✅ Bean: UserDetailsService
   ✅ Bean: SecurityFilterChain
```

**Atalho rápido:**
```
Ctrl+Shift+N (Navigate → Go to Class)
Digite: SecurityConfig
Pressione Enter
```

### 3.2 - Verificar Usuario.java

```
1. Clique duplo em: Usuario.java
2. Procure por: @OneToMany
3. Verifique:
   ✅ @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   ✅ private List<Movimentacao> movimentacoes;
```

### 3.3 - Verificar Produto.java

```
1. Clique duplo em: Produto.java
2. Procure por: @OneToMany
3. Verifique:
   ✅ @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
   ✅ private List<Movimentacao> movimentacoes;
```

### 3.4 - Verificar HomeController.java

```
1. Clique duplo em: HomeController.java
2. Procure por: Authentication authentication
3. Verifique:
   ✅ public String home(Model model, Authentication authentication)
   ✅ model.addAttribute("usuarioAutenticado", username);
   ✅ model.addAttribute("nomeUsuario", nomeExibicao);
```

---

## ⚙️ PASSO 4: Verificar Dependências (pom.xml)

### 4.1 - Abrir pom.xml

```
1. Clique duplo em: pom.xml (na raiz do projeto)
2. Procure por: <dependency>
```

### 4.2 - Verificar Spring Security

```
Procure por:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

✅ Deve estar presente
```

### 4.3 - Verificar Thymeleaf Extras

```
Procure por:
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
</dependency>

✅ Deve estar presente
```

### 4.4 - Carregar Dependências Maven

```
1. IntelliJ mostra notificação: "Maven projects need to be imported"
2. Clique em: "Import Changes"
   (ou Vista → Tool Windows → Maven)
```

---

## 🏗️ PASSO 5: Compilar o Projeto

### 5.1 - Usando IntelliJ

```
Build → Build Project
(Atalho: Ctrl+F9)
```

### 5.2 - Verificar Erros

```
Na aba "Build" (rodapé):
- Se verde ✅ → Build successful
- Se vermelho ❌ → Erros de compilação

Clique no error para ir para a linha do erro
```

### 5.3 - Compilar com Maven (alternativa)

```
1. View → Tool Windows → Maven
2. Duplo clique em: clean → compile

Ou no terminal integrado:
./mvnw clean compile
```

---

## 🚀 PASSO 6: Executar a Aplicação

### 6.1 - Executar com IntelliJ

```
1. Localize: CafeAromaSaboresApplication.java
2. Clique direito → Run 'CafeAromaSaboresApplication'
   (ou Ctrl+Shift+F10)
```

### 6.2 - Observar a Console

```
Aguarde mensagens:
[INFO] Starting CafeAromaSaboresApplication
[INFO] s.s.b.c.CafeAromaSaboresApplication: Started in X.XXX seconds
```

### 6.3 - Erro? Debug

```
Se houver erro:
1. Clique direito em: CafeAromaSaboresApplication.java
2. Selecione: Debug (ou Shift+F9)
3. Breakpoints aparecerão

Ou execute no terminal:
./mvnw spring-boot:run
```

---

## 🌐 PASSO 7: Acessar a Aplicação

### 7.1 - Abrir Browser

```
URL: http://localhost:8080/login

Esperado: Página de login carrega ✅
```

### 7.2 - Fazer Login

```
Credenciais:
- Username: admin@cafe.com
- Password: admin123

Esperado: Redireciona para /home ✅
```

### 7.3 - Verificar Dashboard

```
Aparecem:
- Avatar com iniciais
- Nome do usuário
- Dashboard com estatísticas
- Botão "Sair"

✅ Tudo correto!
```

---

## 🧪 PASSO 8: Testar Funcionalidades

### Teste 1: Login Incorreto

```
1. Acesse: http://localhost:8080/logout (para sair se estiver logado)
2. Acesse: http://localhost:8080/login
3. Digite:
   - Username: admin@cafe.com
   - Password: senhaERRADA
4. Clique: Acessar o sistema

Esperado: Mensagem "Usuário ou senha inválidos!" ✅
```

### Teste 2: Logout

```
1. Estando logado em /home
2. Clique em: Sair

Esperado: Redireciona para /login?logout=true
         Mostra: "Você foi desconectado com sucesso" ✅
```

### Teste 3: Rota Protegida

```
1. Abra nova aba anônima (Ctrl+Shift+N)
2. Digite: http://localhost:8080/home

Esperado: Redireciona para /login ✅
```

### Teste 4: Diferentes Usuários

```
1. Logout (clique em Sair)
2. Login com:
   - Username: user@cafe.com
   - Password: user123

Esperado: Redireciona para /home com outro usuário ✅
```

---

## 🔧 PASSO 9: Debug de Problemas Comuns

### Problema 1: "Não encontra SecurityConfig"

```
Solução:
1. Botão direito na pasta: src/main/java
2. Mark Directory as → Sources Root
3. Clique direito no projeto → Reload from Disk
4. Build → Rebuild Project
```

### Problema 2: "Erro ao iniciar aplicação"

```
Solução:
1. Acesse: Run Configurations (Ctrl+Alt+R)
2. Selecione: CafeAromaSaboresApplication
3. Clique em: Edit
4. Verifique: VM options (deixe vazio)
5. Clique: Run
```

### Problema 3: "Porta 8080 em uso"

```
Solução 1: Mude a porta em application.properties
   server.port=8081

Solução 2: Mate o processo anterior
   Windows Task Manager → Encontre Java → End Task
```

### Problema 4: "Erro 404 em /login"

```
Solução:
1. Verifique se LoginController.java existe
2. Verifique se método tem @GetMapping("/login")
3. Rebuild Project (Ctrl+F9)
4. Restart Application
```

---

## 📊 PASSO 10: Acessar Logs

### 10.1 - Aba Run

```
Clique na aba "Run" (rodapé)
Mostra output da aplicação em tempo real
```

### 10.2 - Procurar Mensagens

```
Dentro do Run output, procure:
- "Started CafeAromaSaboresApplication" ✅
- "Tomcat started on port(s)" ✅
- Qualquer erro destacado em vermelho ❌
```

### 10.3 - Copiar Logs

```
Clique direito no output
Copy All (ou Ctrl+A → Ctrl+C)
Cole em arquivo .txt
```

---

## 📚 PASSO 11: Explorar o Código

### 11.1 - Navigate (Ctrl+Shift+N)

```
Indo para uma classe específica:
1. Pressione: Ctrl+Shift+N
2. Digite: SecurityConfig
3. Navegue para a classe
```

### 11.2 - Find Usage (Ctrl+F7)

```
Achar onde uma classe é usada:
1. Clique em: SecurityConfig
2. Pressione: Ctrl+F7
3. Mostra todos os usos
```

### 11.3 - Type Hierarchy (Ctrl+H)

```
Ver relacionamentos de classes:
1. Clique em: User / UserDetailsService
2. Pressione: Ctrl+H
3. Mostra hierarquia
```

---

## ✅ CHECKLIST FINAL

```
ANTES DE COMEÇAR:
 ✅ Projeto aberto no IntelliJ
 ✅ Indexação finalizada
 ✅ Maven sincronizado

ARQUIVOS VERIFICADOS:
 ✅ SecurityConfig.java existe
 ✅ Usuario.java tem @OneToMany
 ✅ Produto.java tem @OneToMany
 ✅ HomeController.java recebe Authentication
 ✅ LoginController.java refatorado

DEPENDÊNCIAS:
 ✅ spring-boot-starter-security no pom.xml
 ✅ thymeleaf-extras-springsecurity6 no pom.xml

BUILD:
 ✅ Projeto compila sem erros (Ctrl+F9)
 ✅ Nenhum warning de compilação

EXECUÇÃO:
 ✅ Aplicação inicia sem erros
 ✅ http://localhost:8080/login acessível
 ✅ Login com admin@cafe.com/admin123 funciona
 ✅ Dashboard (/home) carrega com dados
 ✅ Logout (POST) funciona corretamente
 ✅ Rota protegida sem auth redireciona para login
```

---

## 🎓 ATALHOS ÚTEIS DO INTELLIJ

| Atalho | Ação |
|--------|------|
| `Ctrl+/` | Comentar linha |
| `Ctrl+Shift+/` | Comentar bloco |
| `Ctrl+Alt+L` | Formatar código |
| `Ctrl+Shift+O` | Otimizar imports |
| `Ctrl+F` | Find |
| `Ctrl+H` | Replace |
| `Ctrl+Shift+N` | Navigate to class |
| `Ctrl+Shift+F` | Find in files |
| `Ctrl+Shift+F10` | Run |
| `Shift+F9` | Debug |
| `F5` | Step into (debug) |
| `F8` | Step over (debug) |
| `F9` | Resume (debug) |
| `Ctrl+Alt+R` | Run Configurations |

---

## 📞 SUPORTE / DÚVIDAS

### Se o projeto não compila:
1. Clique direito no projeto
2. Maven → Reload projects
3. File → Invalidate Caches and Restart
4. Tente novamente

### Se a aplicação não inicia:
1. Verifique console por mensagens de erro
2. Procure por "stack trace" completo
3. Verifique se porta 8080 está disponível

### Se o login não funciona:
1. Verifique SecurityConfig.java
2. Cheque se username é exatamente "admin@cafe.com"
3. Cheque se password é exatamente "admin123"
4. Reinicie a aplicação

---

## 🎉 PARABÉNS!

Você configurou com sucesso:
✅ Spring Security 6
✅ Autenticação in-memory com BCrypt
✅ Proteção de rotas
✅ Login e Logout seguro
✅ Interface com dados do usuário atual

**Próximo passo:** Estudar como integrar com banco de dados para substituir InMemoryUserDetailsManager 📚

---

**Desenvolvido com ❤️ usando Spring Boot 3 e Spring Security 6**

