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

/**
 * Controller para gerenciar a página inicial da aplicação
 * Responsável por retornar informações gerais do dashboard
 * Integrado com Spring Security para obter dados do usuário autenticado
 */
@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    /**
     * Retorna a página inicial/home com dados do dashboard e usuário autenticado
     * GET /home
     *
     * Requer autenticação via Spring Security
     * Exibe:
     * - Total de produtos cadastrados
     * - Produtos com estoque crítico (abaixo do mínimo)
     * - Movimentações dos últimos 7 dias
     * - Nome do usuário autenticado
     *
     * @param model Model para passar dados para a view
     * @param authentication Objeto de autenticação do Spring Security (injetado automaticamente)
     * @return Template home.html
     */
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
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

            // Informações do usuário autenticado
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                model.addAttribute("usuarioAutenticado", username);
                // Extrair o nome (primeira parte do email se em formato email)
                String nomeExibicao = username.contains("@")
                    ? username.split("@")[0].substring(0, 1).toUpperCase() +
                      username.split("@")[0].substring(1)
                    : username;
                model.addAttribute("nomeUsuario", nomeExibicao);
            }

        } catch (Exception e) {
            model.addAttribute("totalProdutos", 0);
            model.addAttribute("productosCriticos", 0);
            model.addAttribute("movimentacoesRecentes", 0);
            model.addAttribute("erro", "Erro ao carregar dashboard: " + e.getMessage());
        }

        return "home";
    }
}

