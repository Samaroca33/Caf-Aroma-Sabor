package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.repository.MovimentacaoRepository;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller para gerenciar a página inicial da aplicação
 * Responsável por retornar informações gerais do dashboard
 */
@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /**
     * Retorna a página inicial/home
     * GET /home
     *
     * @param model Model para passar dados para a view
     * @return Template home.html
     */
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
            long movimentacoesRecentes = movimentacaoRepository
                .findByDataHoraBetweenOrderByDataHoraDesc(seteDiasAtras, LocalDateTime.now())
                .size();
            model.addAttribute("movimentacoesRecentes", movimentacoesRecentes);

        } catch (Exception e) {
            model.addAttribute("totalProdutos", 0);
            model.addAttribute("productosCriticos", 0);
            model.addAttribute("movimentacoesRecentes", 0);
            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());
        }

        return "home";
    }
}

