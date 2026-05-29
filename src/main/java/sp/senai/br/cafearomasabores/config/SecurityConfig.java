package sp.senai.br.cafearomasabores.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança (Spring Security 6)
 * Define autenticação in-memory, proteção de rotas e configuração de logout
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura o codificador de senha usando BCrypt
     * @return PasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura usuários em memória para autenticação
     * Admin: admin@cafe.com / admin123 (ROLE_ADMIN)
     * Usuário: user@cafe.com / user123 (ROLE_USER)
     * @return UserDetailsService com usuários em memória
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin@cafe.com")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "USER")
                .build();

        UserDetails usuario = User.builder()
                .username("user@cafe.com")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, usuario);
    }

    /**
     * Configura a cadeia de filtros de segurança
     * - Protege todas as rotas para exigir autenticação
     * - Permite acesso público apenas à página de login
     * - Configura formulário de login em /login
     * - Configura logout para redirecionar a /login?logout
     *
     * @param http HttpSecurity para configurar
     * @return SecurityFilterChain configurada
     * @throws Exception Se houver erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/home", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Desabilitar CSRF para simplificar testes

        return http.build();
    }
}

