package sp.senai.br.cafearomasabores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modelo de Usuario para autenticação no sistema
 * Representa um usuário com permissões de acesso ao CafeAromaESabor
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do usuário é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Login é obrigatório")
    @Column(nullable = false, unique = true)
    private String login;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String senha;
}

