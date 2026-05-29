package sp.senai.br.cafearomasabores.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller para gerenciar autenticação de usuários com Spring Security
 * Responsável por retornar a tela de login
 * O logout é gerenciado automaticamente pelo Spring Security
 */
@Controller
public class LoginController {

    /**
     * Retorna a página de login com suporte a mensagens de erro/logout via Spring Security
     * GET /login
     * 
     * Parâmetros opcionais:
     * - error=true: exibe mensagem de erro de autenticação
     * - logout=true: exibe mensagem de sucesso de logout
     *
     * @param error Parâmetro opcional indicado erro de autenticação
     * @param logout Parâmetro opcional indicando logout bem-sucedido
     * @param model Model para passar dados para a view
     * @return Template login.html
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("erro", "Usuário ou senha inválidos!");
        }
        
        if (logout != null) {
            model.addAttribute("sucesso", "Você foi desconectado com sucesso. Até logo!");
        }
        
        return "login";
    }

    /**
     * Obtém a autenticação atual do usuário logado
     * Útil para controllers e views que precisam acessar dados do usuário autenticado
     *
     * @return Authentication atual ou null se não autenticado
     */
    public static Authentication getAuthenticationAtual() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}


