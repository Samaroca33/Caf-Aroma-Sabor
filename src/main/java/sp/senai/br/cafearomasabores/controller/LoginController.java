package sp.senai.br.cafearomasabores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sp.senai.br.cafearomasabores.model.Usuario;
import sp.senai.br.cafearomasabores.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller para gerenciar autenticação de usuários
 * Responsável por retornar a tela de login e processar tentativas de autenticação
 */
@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Retorna a página de login
     * GET /login
     *
     * @param model Model para passar dados para a view
     * @return Template login.html
     */
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

    /**
     * Processa o envio do formulário de login
     * POST /login
     *
     * @param login Login do usuário
     * @param senha Senha do usuário
     * @param model Model para passar dados para a view
     * @param session HttpSession para manter usuário logado
     * @return Redirecionamento ou retorno de erro
     */
    @PostMapping("/login")
    public String autenticar(
            @RequestParam String login,
            @RequestParam String senha,
            Model model,
            HttpSession session) {

        try {
            // TODO: Implementar validação real de senha com hash/criptografia
            Optional<Usuario> usuarioOpt = usuarioRepository.findByLogin(login);

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                // TODO: Comparar senha com hash (usar BCrypt em produção)
                if (usuario.getSenha().equals(senha)) {
                    session.setAttribute("usuarioLogado", usuario);
                    return "redirect:/home";
                }
            }

            model.addAttribute("erro", "Usuário ou senha inválidos!");
            return "login";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao processar login: " + e.getMessage());
            return "login";
        }
    }

    /**
     * Realiza logout do usuário
     * GET /logout
     *
     * @param session HttpSession para invalidar
     * @return Redirecionamento para login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}


