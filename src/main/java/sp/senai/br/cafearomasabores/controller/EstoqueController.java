package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sp.senai.br.cafearomasabores.model.Movimentacao;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.model.Usuario;
import sp.senai.br.cafearomasabores.repository.MovimentacaoRepository;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * Controller para gerenciar movimentações de estoque
 * Responsável por entrada e saída de produtos do almoxarifado
 */
@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Retorna a interface de movimentação de estoque
     * GET /estoque
     *
     * @param model Model para passar dados para a view
     * @return Template estoque/movimentacao.html
     */
    @GetMapping
    public String movimentacao(Model model) {
        try {
            List<Produto> produtos = produtoRepository.findAll();
            model.addAttribute("produtos", produtos);

            List<Movimentacao> movimentacoes = movimentacaoRepository.findAll();
            // Ordenar por data descendente
            movimentacoes.sort((m1, m2) -> m2.getDataHora().compareTo(m1.getDataHora()));
            model.addAttribute("movimentacoes", movimentacoes);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar dados: " + e.getMessage());
        }

        return "estoque/movimentacao";
    }

    /**
     * Processa uma movimentação de entrada de estoque
     * POST /estoque/entrada
     *
     * @param produtoId ID do produto
     * @param quantidade Quantidade a ser movimentada
     * @param motivo Motivo da movimentação
     * @param session Session para obter usuário logado
     * @param model Model para passar dados para a view
     * @return Redirecionamento ou retorno de erro
     */
    @PostMapping("/entrada")
    public String registrarEntrada(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            HttpSession session,
            Model model) {

        try {
            // Validações
            if (quantidade == null || quantidade < 1) {
                model.addAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                model.addAttribute("erro", "Motivo é obrigatório!");
                return "redirect:/estoque";
            }

            // Obter produto
            Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Obter usuário da sessão
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            if (usuario == null) {
                // Usar usuário padrão para teste
                usuario = new Usuario(1L, "Sistema", "sistema", "sistema");
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

            model.addAttribute("sucesso", "Entrada de estoque registrada com sucesso!");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao registrar entrada: " + e.getMessage());
        }

        return "redirect:/estoque";
    }

    /**
     * Processa uma movimentação de saída de estoque
     * POST /estoque/saida
     *
     * @param produtoId ID do produto
     * @param quantidade Quantidade a ser movimentada
     * @param motivo Motivo da movimentação
     * @param session Session para obter usuário logado
     * @param model Model para passar dados para a view
     * @return Redirecionamento ou retorno de erro
     */
    @PostMapping("/saida")
    public String registrarSaida(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            HttpSession session,
            Model model) {

        try {
            // Validações
            if (quantidade == null || quantidade < 1) {
                model.addAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                model.addAttribute("erro", "Motivo é obrigatório!");
                return "redirect:/estoque";
            }

            // Obter produto
            Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Verificar estoque
            if (produto.getQuantidadeAtual() < quantidade) {
                model.addAttribute("erro", "Quantidade em estoque insuficiente! " +
                    "Disponível: " + produto.getQuantidadeAtual() + ", Solicitado: " + quantidade);
                return "redirect:/estoque";
            }

            // Obter usuário da sessão
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            if (usuario == null) {
                // Usar usuário padrão para teste
                usuario = new Usuario(1L, "Sistema", "sistema", "sistema");
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
                model.addAttribute("aviso", "⚠️ ATENÇÃO: Estoque abaixo do mínimo! " +
                    "Quantidade atual: " + produto.getQuantidadeAtual() +
                    ", Mínimo: " + produto.getEstoqueMinimo());
            }

            model.addAttribute("sucesso", "Saída de estoque registrada com sucesso!");

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao registrar saída: " + e.getMessage());
        }

        return "redirect:/estoque";
    }
}


