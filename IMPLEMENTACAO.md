# Guia de Implementação - CafeAromaESabor

## 📝 Próximas Etapas de Desenvolvimento

Este documento fornece exemplos de como implementar a lógica de negócio nos `TODO`s deixados nos controllers.

---

## 1️⃣ LoginController - Autenticação de Usuários

### Antes (Atual)
```java
@PostMapping("/login")
public String autenticar(Model model) {
    // TODO: Lógica de processamento de login será implementada aqui
    return "redirect:/home";
}
```

### Depois (Exemplo de Implementação)
```java
@PostMapping("/login")
public String autenticar(
        @RequestParam String login,
        @RequestParam String senha,
        Model model,
        HttpSession session) {
    
    try {
        Optional<Usuario> usuario = usuarioRepository.findByLogin(login);
        
        if (usuario.isPresent() && usuario.get().getSenha().equals(senha)) {
            session.setAttribute("usuarioLogado", usuario.get());
            return "redirect:/home";
        } else {
            model.addAttribute("erro", "Usuário ou senha inválidos!");
            return "login";
        }
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao processar login");
        return "login";
    }
}
```

**Dependência necessária:**
```java
@Autowired
private UsuarioRepository usuarioRepository;
```

---

## 2️⃣ HomeController - Dashboard com Estatísticas

### Antes (Atual)
```java
@GetMapping("/home")
public String home(Model model) {
    // TODO: Lógica de inicialização do dashboard será implementada aqui
    return "home";
}
```

### Depois (Exemplo de Implementação)
```java
@GetMapping("/home")
public String home(Model model) {
    try {
        // Total de produtos
        long totalProdutos = produtoRepository.count();
        model.addAttribute("totalProdutos", totalProdutos);
        
        // Produtos com estoque crítico
        List<Produto> todosProdutos = produtoRepository.findAll();
        long productosCriticos = todosProdutos.stream()
            .filter(p -> p.getQuantidadeAtual() < p.getEstoqueMinimo())
            .count();
        model.addAttribute("productosCriticos", productosCriticos);
        
        // Movimentações recentes (últimos 7 dias)
        LocalDateTime seteDiasAtras = LocalDateTime.now().minusDays(7);
        List<Movimentacao> movimentacoesRecentes = movimentacaoRepository
            .findByDataHoraBetweenOrderByDataHoraDesc(seteDiasAtras, LocalDateTime.now());
        model.addAttribute("movimentacoesRecentes", movimentacoesRecentes.size());
        
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao carregar dashboard");
    }
    
    return "home";
}
```

**Dependências necessárias:**
```java
@Autowired
private ProdutoRepository produtoRepository;

@Autowired
private MovimentacaoRepository movimentacaoRepository;
```

---

## 3️⃣ ProdutoController - Listagem e Cadastro

### Listar Produtos - Antes
```java
@GetMapping
public String listar(Model model) {
    // TODO: Lógica de listagem de produtos será implementada aqui
    return "produto/listagem";
}
```

### Listar Produtos - Depois
```java
@GetMapping
public String listar(
        @RequestParam(required = false) String busca,
        Model model) {
    try {
        List<Produto> produtos;
        
        if (busca != null && !busca.trim().isEmpty()) {
            produtos = produtoRepository.findByNomeContainingIgnoreCase(busca);
        } else {
            produtos = produtoRepository.findAll();
        }
        
        model.addAttribute("produtos", produtos);
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao carregar produtos");
    }
    
    return "produto/listagem";
}
```

### Cadastrar Novo Produto - Antes
```java
@PostMapping
public String salvar(Model model) {
    // TODO: Lógica de salvamento de produto será implementada aqui
    return "redirect:/produto";
}
```

### Cadastrar Novo Produto - Depois
```java
@PostMapping
public String salvar(
        @Valid Produto produto,
        BindingResult result,
        Model model) {
    
    if (result.hasErrors()) {
        model.addAttribute("produto", produto);
        model.addAttribute("erro", "Por favor, verifique os dados informados");
        return "produto/form-inserir";
    }
    
    try {
        // Validação adicional
        if (produto.getQuantidadeAtual() > 0 && produto.getQuantidadeAtual() < produto.getEstoqueMinimo()) {
            model.addAttribute("aviso", "Atenção: Quantidade atual é menor que o estoque mínimo");
        }
        
        produtoRepository.save(produto);
        model.addAttribute("sucesso", "Produto cadastrado com sucesso!");
        
        return "redirect:/produto";
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
        return "produto/form-inserir";
    }
}
```

---

## 4️⃣ EstoqueController - Movimentações

### Interface de Movimentação - Antes
```java
@GetMapping
public String movimentacao(Model model) {
    // TODO: Lógica de preparação da interface de movimentação
    return "estoque/movimentacao";
}
```

### Interface de Movimentação - Depois
```java
@GetMapping
public String movimentacao(Model model) {
    try {
        List<Produto> produtos = produtoRepository.findAll();
        model.addAttribute("produtos", produtos);
        
        List<Movimentacao> movimentacoes = movimentacaoRepository.findAll();
        if (movimentacoes instanceof List) {
            movimentacoes.sort((m1, m2) -> m2.getDataHora().compareTo(m1.getDataHora()));
        }
        model.addAttribute("movimentacoes", movimentacoes);
        
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao carregar dados");
    }
    
    return "estoque/movimentacao";
}
```

### Entrada de Estoque - Antes
```java
@PostMapping("/entrada")
public String registrarEntrada(Model model) {
    // TODO: Lógica de entrada de estoque
    return "redirect:/estoque";
}
```

### Entrada de Estoque - Depois
```java
@PostMapping("/entrada")
public String registrarEntrada(
        @RequestParam Long produtoId,
        @RequestParam Integer quantidade,
        @RequestParam String motivo,
        HttpSession session,
        Model model) {
    
    try {
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Criar movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(Movimentacao.TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setMotivo(motivo);
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        
        movimentacaoRepository.save(movimentacao);
        
        // Atualizar estoque do produto
        produto.setQuantidadeAtual(produto.getQuantidadeAtual() + quantidade);
        produtoRepository.save(produto);
        
        model.addAttribute("sucesso", "Entrada registrada com sucesso!");
        
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao registrar entrada: " + e.getMessage());
    }
    
    return "redirect:/estoque";
}
```

### Saída de Estoque - Antes
```java
@PostMapping("/saida")
public String registrarSaida(Model model) {
    // TODO: Lógica de saída de estoque
    return "redirect:/estoque";
}
```

### Saída de Estoque - Depois
```java
@PostMapping("/saida")
public String registrarSaida(
        @RequestParam Long produtoId,
        @RequestParam Integer quantidade,
        @RequestParam String motivo,
        HttpSession session,
        Model model) {
    
    try {
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Verificar estoque
        if (produto.getQuantidadeAtual() < quantidade) {
            model.addAttribute("erro", "Quantidade em estoque insuficiente! " +
                "Disponível: " + produto.getQuantidadeAtual());
            return "redirect:/estoque";
        }
        
        // Criar movimentação
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(Movimentacao.TipoMovimentacao.SAIDA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setMotivo(motivo);
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        
        movimentacaoRepository.save(movimentacao);
        
        // Atualizar estoque do produto
        produto.setQuantidadeAtual(produto.getQuantidadeAtual() - quantidade);
        produtoRepository.save(produto);
        
        // Verificar se ficou abaixo do mínimo
        if (produto.getQuantidadeAtual() < produto.getEstoqueMinimo()) {
            model.addAttribute("aviso", "⚠️ Estoque abaixo do mínimo! Quantidade atual: " + 
                produto.getQuantidadeAtual() + ", Mínimo: " + produto.getEstoqueMinimo());
        }
        
        model.addAttribute("sucesso", "Saída registrada com sucesso!");
        
    } catch (Exception e) {
        model.addAttribute("erro", "Erro ao registrar saída: " + e.getMessage());
    }
    
    return "redirect:/estoque";
}
```

---

## 🛠️ Melhorias Recomendadas

### 1. Criar uma classe Service
```java
@Service
public class EstoqueService {
    
    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Autowired
    private MovimentacaoRepository movimentacaoRepository;
    
    public void registrarEntrada(Long produtoId, Integer quantidade, 
                                  String motivo, Usuario usuario) {
        // Lógica centralizada
    }
    
    public void registrarSaida(Long produtoId, Integer quantidade, 
                               String motivo, Usuario usuario) {
        // Lógica centralizada
    }
}
```

### 2. Adicionar Tratamento de Exceções Global
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("erro", "Erro inesperado: " + e.getMessage());
        return "error";
    }
}
```

### 3. Adicionar Validation com @Valid
```java
@PostMapping
public String salvar(@Valid @ModelAttribute("produto") Produto produto,
                     BindingResult result) {
    if (result.hasErrors()) {
        return "produto/form-inserir";
    }
    // ... salvar
}
```

### 4. Criar Método de Autenticação com Interceptor
```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session.getAttribute("usuarioLogado") == null && 
            !request.getRequestURI().contains("/login")) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
```

---

## 🧪 Exemplos de Testes

### Teste de Repository
```java
@DataJpaTest
class ProdutoRepositoryTest {
    
    @Autowired
    private ProdutoRepository repository;
    
    @Test
    void testFindByNome() {
        Produto produto = new Produto();
        produto.setNome("Café Premium");
        repository.save(produto);
        
        List<Produto> resultado = repository.findByNomeContainingIgnoreCase("café");
        
        assertThat(resultado).isNotEmpty();
        assertThat(resultado.get(0).getNome()).contains("Café");
    }
}
```

### Teste de Controller
```java
@WebMvcTest(ProdutoController.class)
class ProdutoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private ProdutoRepository repository;
    
    @Test
    void testListarProdutos() throws Exception {
        mockMvc.perform(get("/produto"))
            .andExpect(status().isOk())
            .andExpect(view().name("produto/listagem"));
    }
}
```

---

## 📌 Checklist de Implementação

- [ ] Implementar autenticação em LoginController
- [ ] Implementar listagem em ProdutoController
- [ ] Implementar cadastro em ProdutoController
- [ ] Implementar entrada de estoque em EstoqueController
- [ ] Implementar saída de estoque em EstoqueController
- [ ] Adicionar mensagens de feedback (sucesso/erro)
- [ ] Criar camada Service para lógica de negócio
- [ ] Implementar validações adicionais
- [ ] Adicionar tratamento de exceções global
- [ ] Criar testes unitários
- [ ] Criar testes de integração
- [ ] Melhorar UI/UX dos templates
- [ ] Adicionar paginação nas listas
- [ ] Adicionar busca e filtros
- [ ] Implementar autenticação com Spring Security (futuro)

---

**Desenvolvido com ☕ para CafeAromaESabor**

