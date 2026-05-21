package sp.senai.br.cafearomasabores.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Modelo de Movimentacao para rastreamento de entrada/saída de produtos
 * Registra todas as movimentações de estoque com data, hora, usuário e motivo
 */
@Entity
@Table(name = "movimentacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    @Min(value = 1, message = "Quantidade deve ser maior que 0")
    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @NotBlank(message = "Motivo da movimentação é obrigatório")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /**
     * Enum para tipos de movimentação
     */
    public enum TipoMovimentacao {
        ENTRADA("Entrada de Estoque"),
        SAIDA("Saída de Estoque");

        private final String descricao;

        TipoMovimentacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @PrePersist
    protected void onCreate() {
        dataHora = LocalDateTime.now();
    }
}

