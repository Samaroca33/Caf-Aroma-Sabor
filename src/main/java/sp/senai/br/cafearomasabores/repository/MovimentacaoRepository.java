package sp.senai.br.cafearomasabores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sp.senai.br.cafearomasabores.model.Movimentacao;
import sp.senai.br.cafearomasabores.model.Produto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Movimentacao
 * Fornece métodos de CRUD e consultas customizadas para rastreamento de estoque
 */
@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    /**
     * Busca movimentações de um produto específico
     * @param produto Produto a ser consultado
     * @return Lista de movimentações do produto
     */
    List<Movimentacao> findByProdutoOrderByDataHoraDesc(Produto produto);

    /**
     * Busca movimentações em um período de data/hora
     * @param dataInicio Data/hora de início
     * @param dataFim Data/hora de fim
     * @return Lista de movimentações no período
     */
    List<Movimentacao> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime dataInicio, LocalDateTime dataFim);
}

