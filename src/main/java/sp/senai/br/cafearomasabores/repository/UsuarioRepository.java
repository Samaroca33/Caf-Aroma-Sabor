package sp.senai.br.cafearomasabores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp.senai.br.cafearomasabores.model.Usuario;

import java.util.Optional;

/**
 * Repositório para operações de banco de dados da entidade Usuario
 * Fornece métodos de CRUD e consultas customizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo login
     * @param login Login do usuário
     * @return Optional contendo o usuário, se encontrado
     */
    Optional<Usuario> findByLogin(String login);
}

