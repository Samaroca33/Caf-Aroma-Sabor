# Setup e Guia de Testes - CafeAromaESabor

## 🚀 Configuração Inicial

### 1. Criar Banco de Dados MySQL

Abra o MySQL Workbench ou terminal e execute:

```sql
-- Criar database
CREATE DATABASE cafearomasabor;

-- Usar database
USE cafearomasabor;

-- (Opcional) Criar usuário específico
CREATE USER 'cafe_user'@'localhost' IDENTIFIED BY 'senha123';
GRANT ALL PRIVILEGES ON cafearomasabor.* TO 'cafe_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configurar application.properties

Edite `src/main/resources/application.properties`:

```properties
# Para conexão com usuário "root" e senha vazia
spring.datasource.url=jdbc:mysql://localhost:3306/cafearomasabor
spring.datasource.username=root
spring.datasource.password=

# OU para usuário específico
spring.datasource.url=jdbc:mysql://localhost:3306/cafearomasabor
spring.datasource.username=cafe_user
spring.datasource.password=senha123
```

**Importante:** Se usar senha no MySQL, adicione também:
```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 3. Executar a Aplicação

#### Opção 1: Via Maven (Linux/Mac)
```bash
cd CafeAromaSabores
./mvnw spring-boot:run
```

#### Opção 2: Via Maven (Windows)
```powershell
cd CafeAromaSabores
mvnw.cmd spring-boot:run
```

#### Opção 3: Via IDE
- Clique direito em `CafeAromaSaboresApplication.java`
- Selecione "Run 'CafeAromaSaboresApplication'"

#### Opção 4: Criar arquivo JAR
```bash
./mvnw clean package
java -jar target/CafeAromaSabores-0.0.1-SNAPSHOT.jar
```

### 4. Validar Inicialização

Procure por mensagem similar no console:
```
Started CafeAromaSaboresApplication in 5.123 seconds
```

---

## 🧪 Testes Manuais

### Teste 1: Acessar Login

**URL:** `http://localhost:8080/login`

**Esperado:**
- ✅ Página de login carregada com sucesso
- ✅ Formulário com campos "Usuário" e "Senha"
- ✅ Botão "Entrar" visível
- ✅ Estilo com tema de café (cores marrom/bege)

### Teste 2: Acessar Home (sem autenticação)

**URL:** `http://localhost:8080/home`

**Esperado:**
- ✅ Página carregada (em produção, seria redirecionada para login)
- ✅ Título "Dashboard" visível
- ✅ Cards com estatísticas
- ✅ Menu de navegação funcional

### Teste 3: Acessar Listagem de Produtos

**URL:** `http://localhost:8080/produto`

**Esperado:**
- ✅ Página de listagem carregada
- ✅ Botão "Novo Produto" visível
- ✅ Mensagem "Nenhum produto cadastrado ainda" (primeira vez)
- ✅ Coluna com headers: ID, Nome, Lote, Data, Quantidade, Mínimo, Status, Ações

### Teste 4: Acessar Formulário de Novo Produto

**URL:** `http://localhost:8080/produto/novo`

**Esperado:**
- ✅ Formulário carregado com sucesso
- ✅ Campos: Nome, Descrição, Lote, Data Validade, Quantidade, Mínimo
- ✅ Botões "Salvar" e "Cancelar" visíveis
- ✅ Todos campos responsivos

### Teste 5: Acessar Interface de Estoque

**URL:** `http://localhost:8080/estoque`

**Esperado:**
- ✅ Interface carregada com dois formulários (Entrada/Saída)
- ✅ Tabela para histórico de movimentações (vazia inicialmente)
- ✅ Campos para seleção de produto disponíveis
- ✅ Botões de ação responsivos

---

## 📊 Teste de Banco de Dados

### Verificar Tabelas Criadas

```sql
USE cafearomasabor;
SHOW TABLES;
```

**Esperado:**
```
+------------------------+
| Tables_in_cafearomasabor |
+------------------------+
| usuario                 |
| produto                 |
| movimentacao            |
+------------------------+
```

### Verificar Estrutura de Tabelas

```sql
DESC usuario;
DESC produto;
DESC movimentacao;
```

### Inserir Dados de Teste

```sql
-- Inserir usuário de teste
INSERT INTO usuario (nome, login, senha) 
VALUES ('João Silva', 'joao', 'senha123');

-- Inserir produto de teste
INSERT INTO produto (nome, descricao, lote, data_validade, estoque_minimo, quantidade_atual)
VALUES ('Café Premium Arábica', 'Café 100% arábica, torra média', 'LOTE-001', '2025-12-31', 20, 100);

INSERT INTO produto (nome, descricao, lote, data_validade, estoque_minimo, quantidade_atual)
VALUES ('Café Robusta', 'Café robusta, torra forte', 'LOTE-002', '2025-11-30', 30, 5);
```

### Consultar Dados

```sql
-- Ver todos os produtos
SELECT * FROM produto;

-- Ver produtos com estoque crítico
SELECT * FROM produto WHERE quantidade_atual < estoque_minimo;

-- Ver movimentações
SELECT * FROM movimentacao;
```

---

## 🔍 Troubleshooting

### Erro: "Access denied for user 'root'@'localhost'"

**Solução:**
- Verifique credenciais em `application.properties`
- Certifique-se que MySQL está rodando
- Tente conectar diretamente: `mysql -u root -p`

**Windows:**
```powershell
# Iniciar MySQL
mysql.server start
# ou
net start MySQL80
```

### Erro: "Unknown database 'cafearomasabor'"

**Solução:**
```sql
-- Criar database
CREATE DATABASE cafearomasabor;
```

### Erro: "Cannot resolve table 'usuario'"

**Isso é apenas warning do IDE.** Tabelas serão criadas ao iniciar aplicação com `ddl-auto=update`

### Porta 8080 já em uso

**Windows:**
```powershell
# Encontrar processo usando porta 8080
netstat -ano | findstr 8080

# Matar processo (onde PID é o ID do processo)
taskkill /PID <PID> /F
```

**Linux/Mac:**
```bash
# Encontrar processo
lsof -i :8080

# Matar processo
kill -9 <PID>
```

**Ou mudar porta em `application.properties`:**
```properties
server.port=8081
```

### Erro de Reload de Arquivo

Se templates não carregam mudança:
1. Para aplicação (Ctrl+C)
2. Limpe cache: `mvn clean`
3. Reinicie

---

## 📈 Testes de Funcionalidade (Com Implementação)

Quando implementar os TODO's, teste:

### Teste de Autenticação
```
1. Acessar /login
2. Tentar logar com dados inválidos → Deve mostrar erro
3. Logar com dados válidos → Deve ir para /home
4. Acessar /produto sem estar logado → Deve ir para /login
```

### Teste de CRUD de Produtos
```
1. Criar novo produto com dados válidos → Deve salvar
2. Tentar criar com obrigatórios vazios → Deve erro ou rejeição
3. Listar produtos → Deve mostrar todos criados
4. Editar produto → Deve atualizar
5. Deletar produto → Deve remover da lista
```

### Teste de Movimentação
```
1. Registrar entrada de estoque → Deve aumentar quantidade
2. Registrar saída → Deve diminuir quantidade
3. Tentar saída maior que estoque → Deve erro
4. Verificar histórico → Deve listar todas movimentações
```

### Teste de Validações
```
1. Produto com estoque < mínimo → Badge "CRÍTICO"
2. Produto normal → Badge "OK"
3. Dashboard → Contadores corretos
```

---

## 🧩 Estrutura de Diretórios Final

```
CafeAromaSabores/
├── src/
│   ├── main/
│   │   ├── java/sp/senai/br/cafearomasabores/
│   │   │   ├── model/
│   │   │   │   ├── Usuario.java ✅
│   │   │   │   ├── Produto.java ✅
│   │   │   │   └── Movimentacao.java ✅
│   │   │   ├── repository/
│   │   │   │   ├── UsuarioRepository.java ✅
│   │   │   │   ├── ProdutoRepository.java ✅
│   │   │   │   └── MovimentacaoRepository.java ✅
│   │   │   ├── controller/
│   │   │   │   ├── LoginController.java ✅
│   │   │   │   ├── HomeController.java ✅
│   │   │   │   ├── ProdutoController.java ✅
│   │   │   │   └── EstoqueController.java ✅
│   │   │   └── CafeAromaSaboresApplication.java ✅
│   │   └── resources/
│   │       ├── application.properties ✅
│   │       ├── templates/
│   │       │   ├── login.html ✅
│   │       │   ├── home.html ✅
│   │       │   ├── produto/
│   │       │   │   ├── listagem.html ✅
│   │       │   │   └── form-inserir.html ✅
│   │       │   └── estoque/
│   │       │       └── movimentacao.html ✅
│   │       └── static/ (CSS, JS, imagens)
│   └── test/
│       └── java/...
├── pom.xml ✅
├── ESTRUTURA.md ✅ (Documento)
└── IMPLEMENTACAO.md ✅ (Documento)
```

---

## 📋 Checklist Final

- [x] Models criados (Usuario, Produto, Movimentacao)
- [x] Repositories criados com métodos customizados
- [x] Controllers criados com rotas mapeadas
- [x] Templates HTML com Thymeleaf criados
- [x] application.properties configurado
- [x] Lombok integrado
- [x] Validações adicionadas
- [x] Comentários TODO deixados
- [x] Documentação completa
- [x] Exemplo de implementação
- [ ] Implementar lógica nos TODO's
- [ ] Testar todos endpoints
- [ ] Adicionar tratamento de erros
- [ ] Criar testes unitários
- [ ] Deploy em produção

---

## 🎯 Próximos Passos Recomendados

1. **Adicionar Spring Security** para autenticação mais robusta
2. **Criar camada Service** para lógica de negócio
3. **Adicionar paginação** nas listagens
4. **Criar relatórios** de movimentação
5. **Adicionar busca avançada** com filtros
6. **Implementar API REST** para mobile
7. **Adicionar gráficos** no dashboard
8. **Configurar HTTPS** para produção
9. **Criar backup automático** do banco
10. **Adicionar notificações** de estoque crítico

---

**Desenvolvido com ☕ para CafeAromaESabor - SENAI**

Data: 2026-05-14
Versão: 0.0.1-SNAPSHOT
Status: ✅ Base estruturada e pronta para desenvolvimento

