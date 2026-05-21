package sp.senai.br.cafearomasabores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp.senai.br.cafearomasabores.model.Produto;

import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Produto
 * Fornece métodos de CRUD e consultas customizadas para gestão de produtos
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    /**
     * Busca produtos pelo nome (busca parcial)
     * @param nome Nome ou parte do nome do produto
     * @return Lista de produtos encontrados
     */
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Busca produtos pelo número de lote
     * @param lote Número do lote
     * @return Lista de produtos com o lote especificado
     */
    List<Produto> findByLote(String lote);
}

