package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
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

    @GetMapping("/novo")
    public String formularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/form-inserir";
    }

    @PostMapping("/salvar")
    @Transactional
    public String salvar(
            @RequestParam(required = false) Long id,
            @RequestParam String nome,
            @RequestParam(required = false) String descricao,
            @RequestParam String lote,
            @RequestParam LocalDate dataValidade,
            @RequestParam Integer quantidadeAtual,
            @RequestParam Integer estoqueMinimo,
            RedirectAttributes redirectAttributes) {

        try {
            if (nome == null || nome.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Nome do produto é obrigatório!");
                return "redirect:/produto/novo";
            }

            Produto produto;
            if (id != null) {
                // Modo Edição: busca o existente para atualizar
                produto = produtoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            } else {
                // Modo Inserção: cria um novo objeto
                produto = new Produto();
            }

            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setLote(lote);
            produto.setDataValidade(dataValidade);
            produto.setQuantidadeAtual(quantidadeAtual);
            produto.setEstoqueMinimo(estoqueMinimo);

            produtoRepository.save(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto salvo com sucesso!");
            return "redirect:/produto";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar produto: " + e.getMessage());
            return "redirect:/produto/novo";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        model.addAttribute("produto", produto);
        return "produto/form-inserir";
    }

    @PostMapping("/excluir/{id}")
    @Transactional
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir produto: " + e.getMessage());
        }
        return "redirect:/produto";
    }
}