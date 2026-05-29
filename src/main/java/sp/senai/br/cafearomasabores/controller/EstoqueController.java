package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sp.senai.br.cafearomasabores.model.Movimentacao;
import sp.senai.br.cafearomasabores.model.Produto;
import sp.senai.br.cafearomasabores.model.Usuario;
import sp.senai.br.cafearomasabores.repository.MovimentacaoRepository;
import sp.senai.br.cafearomasabores.repository.ProdutoRepository;
import sp.senai.br.cafearomasabores.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String movimentacao(Model model) {
        try {
            List<Produto> produtos = produtoRepository.findAll();
            model.addAttribute("produtos", produtos);

            List<Movimentacao> movimentacoes = movimentacaoRepository.findAll();
            // Ordena as movimentações se a lista não estiver vazia
            if (movimentacoes != null) {
                movimentacoes.sort((m1, m2) -> {
                    if (m1.getDataHora() == null || m2.getDataHora() == null) return 0;
                    return m2.getDataHora().compareTo(m1.getDataHora());
                });
            }
            model.addAttribute("movimentacoes", movimentacoes);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar dados: " + e.getMessage());
        }
        return "estoque/movimentacao";
    }

    private Usuario obterUsuarioLogado() {
        String loginAtivo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getLogin().equals(loginAtivo))
                .findFirst()
                .orElseGet(() -> {
                    Usuario sistemaUser = new Usuario();
                    sistemaUser.setNome("Administrador");
                    sistemaUser.setLogin(loginAtivo != null ? loginAtivo : "admin@cafe.com");
                    sistemaUser.setSenha("123456");
                    return usuarioRepository.save(sistemaUser);
                });
    }

    @PostMapping("/entrada")
    @Transactional
    public String registrarEntrada(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes) {

        try {
            if (quantidade == null || quantidade < 1) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(Movimentacao.TipoMovimentacao.ENTRADA);
            movimentacao.setQuantidade(quantidade);
            movimentacao.setMotivo(motivo);
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(obterUsuarioLogado());
            movimentacao.setDataHora(LocalDateTime.now()); // SOLUÇÃO DO ERRO 500: define a data obrigatória

            movimentacaoRepository.save(movimentacao);

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() + quantidade);
            produtoRepository.saveAndFlush(produto);

            redirectAttributes.addFlashAttribute("sucesso", "Entrada de estoque registrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar entrada: " + e.getMessage());
        }
        return "redirect:/estoque";
    }

    @PostMapping("/saida")
    @Transactional
    public String registrarSaida(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            RedirectAttributes redirectAttributes) {

        try {
            if (quantidade == null || quantidade < 1) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getQuantidadeAtual() < quantidade) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade em estoque insuficiente!");
                return "redirect:/estoque";
            }

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(Movimentacao.TipoMovimentacao.SAIDA);
            movimentacao.setQuantidade(quantidade);
            movimentacao.setMotivo(motivo);
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(obterUsuarioLogado());
            movimentacao.setDataHora(LocalDateTime.now()); // SOLUÇÃO DO ERRO 500: define a data obrigatória

            movimentacaoRepository.save(movimentacao);

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() - quantidade);
            produtoRepository.saveAndFlush(produto);

            redirectAttributes.addFlashAttribute("sucesso", "Saída de estoque registrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar saída: " + e.getMessage());
        }
        return "redirect:/estoque";
    }
}