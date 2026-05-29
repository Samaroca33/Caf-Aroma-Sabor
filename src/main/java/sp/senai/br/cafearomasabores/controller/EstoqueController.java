package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private MovimentacaoRepository movimentacaoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String movimentacao(Model model) {
        try {
            List<Produto> produtos = produtoRepository.findAll();
            model.addAttribute("produtos", produtos);

            List<Movimentacao> movimentacoes = movimentacaoRepository.findAll();
            movimentacoes.sort((m1, m2) -> m2.getDataHora().compareTo(m1.getDataHora()));
            model.addAttribute("movimentacoes", movimentacoes);

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao carregar dados: " + e.getMessage());
        }

        return "estoque/movimentacao";
    }

    @PostMapping("/entrada")
    public String registrarEntrada(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (quantidade == null || quantidade < 1) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Motivo é obrigatório!");
                return "redirect:/estoque";
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setId(1L);
                usuario.setNome("Sistema");
                usuario.setLogin("sistema");
                usuario.setSenha("sistema");
            }

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(Movimentacao.TipoMovimentacao.ENTRADA);
            movimentacao.setQuantidade(quantidade);
            movimentacao.setMotivo(motivo);
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(usuario);

            movimentacaoRepository.save(movimentacao);

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() + quantidade);
            produtoRepository.save(produto);

            redirectAttributes.addFlashAttribute("sucesso", "Entrada de estoque registrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar entrada: " + e.getMessage());
        }

        return "redirect:/estoque";
    }

    @PostMapping("/saida")
    public String registrarSaida(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (quantidade == null || quantidade < 1) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade deve ser maior que 0!");
                return "redirect:/estoque";
            }

            if (motivo == null || motivo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Motivo é obrigatório!");
                return "redirect:/estoque";
            }

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (produto.getQuantidadeAtual() < quantidade) {
                redirectAttributes.addFlashAttribute("erro", "Quantidade em estoque insuficiente! " +
                        "Disponível: " + produto.getQuantidadeAtual() + ", Solicitado: " + quantidade);
                return "redirect:/estoque";
            }

            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
            if (usuario == null) {
                usuario = new Usuario();
                usuario.setId(1L);
                usuario.setNome("Sistema");
                usuario.setLogin("sistema");
                usuario.setSenha("sistema");
            }

            Movimentacao movimentacao = new Movimentacao();
            movimentacao.setTipo(Movimentacao.TipoMovimentacao.SAIDA);
            movimentacao.setQuantidade(quantidade);
            movimentacao.setMotivo(motivo);
            movimentacao.setProduto(produto);
            movimentacao.setUsuario(usuario);

            movimentacaoRepository.save(movimentacao);

            produto.setQuantidadeAtual(produto.getQuantidadeAtual() - quantidade);
            produtoRepository.save(produto);

            if (produto.getQuantidadeAtual() < produto.getEstoqueMinimo()) {
                redirectAttributes.addFlashAttribute("aviso", "⚠️ ATENÇÃO: Estoque abaixo do mínimo! " +
                        "Quantidade atual: " + produto.getQuantidadeAtual() +
                        ", Mínimo: " + produto.getEstoqueMinimo());
            }

            redirectAttributes.addFlashAttribute("sucesso", "Saída de estoque registrada com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar saída: " + e.getMessage());
        }

        return "redirect:/estoque";
    }
}