package sp.senai.br.cafearomasabores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "produto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotBlank(message = "Número do lote é obrigatório")
    @Column(nullable = false)
    private String lote;

    @Column(nullable = false)
    private LocalDate dataValidade;

    @Min(value = 1, message = "Estoque mínimo deve ser maior que 0")
    @Column(nullable = false)
    private Integer estoqueMinimo;

    @Min(value = 0, message = "Quantidade atual não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidadeAtual;

    // Relacionamento bidirecional com Movimentacao
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Movimentacao> movimentacoes;
}