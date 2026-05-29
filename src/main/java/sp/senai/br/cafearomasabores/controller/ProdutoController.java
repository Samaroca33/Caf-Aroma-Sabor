package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para gerenciar produtos
 * Responsável por listagem, cadastro, atualização e exclusão de produtos
 */
@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Retorna a listagem de todos os produtos
     * GET /produto
     *
     * @param busca Parâmetro opcional para buscar por nome
     * @param model Model para passar dados para a view
     * @return Template produto/listagem.html
     */
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
            model.addAttribute("erro", "Erro ao carregar produtos: " + e.getMessage());
            model.addAttribute("produtos", List.of());
        }

        return "produto/listagem";
    }

    /**
     * Retorna o formulário para criar novo produto
     * GET /produto/novo
     *
     * @param model Model para passar dados para a view
     * @return Template produto/form-inserir.html
     */
    @GetMapping("/novo")
    public String formularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/form-inserir";
    }

    /**
     * Processa o cadastro de um novo produto
     * POST /produto
     *
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param lote Número do lote
     * @param dataValidade Data de validade
     * @param quantidadeAtual Quantidade atual em estoque
     * @param estoqueMinimo Estoque mínimo
     * @param model Model para passar dados para a view
     * @return Redirecionamento ou retorno de erro
     */
    @PostMapping
    public String salvar(
            @RequestParam(required = false) Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String lote,
            @RequestParam LocalDate dataValidade,
            @RequestParam Integer quantidadeAtual,
            @RequestParam Integer estoqueMinimo,
            Model model) {

        try {
            // Validações básicas
            if (nome == null || nome.trim().isEmpty()) {
                model.addAttribute("erro", "Nome do produto é obrigatório!");
                return "produto/form-inserir";
            }

            if (lote == null || lote.trim().isEmpty()) {
                model.addAttribute("erro", "Número do lote é obrigatório!");
                return "produto/form-inserir";
            }

            if (estoqueMinimo < 1) {
                model.addAttribute("erro", "Estoque mínimo deve ser maior que 0!");
                return "produto/form-inserir";
            }

            if (quantidadeAtual < 0) {
                model.addAttribute("erro", "Quantidade atual não pode ser negativa!");
                return "produto/form-inserir";
            }

            // Se id informado, atualizar produto existente, caso contrário criar novo
            Produto produto;
            if (id != null) {
                produto = produtoRepository.findById(id)
                        .orElse(new Produto());
            } else {
                produto = new Produto();
            }
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setLote(lote);
            produto.setDataValidade(dataValidade);
            produto.setQuantidadeAtual(quantidadeAtual);
            produto.setEstoqueMinimo(estoqueMinimo);

            produtoRepository.save(produto);

            return "redirect:/produto";
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
            return "produto/form-inserir";
        }
    }

    /**
     * Retorna o formulário para editar um produto existente
     * GET /produto/editar/{id}
     */
    @GetMapping("/editar/{id}")
    public String editar(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id).orElse(new Produto());
        model.addAttribute("produto", produto);
        return "produto/form-inserir";
    }

    /**
     * Exclui um produto pelo id
     * POST /produto/{id}/excluir
     */
    @PostMapping("/{id}/excluir")
    public String excluir(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        try {
            produtoRepository.deleteById(id);
        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao excluir produto: " + e.getMessage());
        }
        return "redirect:/produto";
    }
}
