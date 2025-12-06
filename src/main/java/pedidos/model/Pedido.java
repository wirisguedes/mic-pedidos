package pedidos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pedidos.client.representation.ClienteRepresentation;
import pedidos.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @Column(name = "codigo_cliente", nullable = false)
    private Long codigoCliente;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    @Column(name = "total", precision = 16, scale = 2)
    private BigDecimal total;

    @Column(name = "chave_pagamento", length = 500)
    private String chavePagamento;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @Column(name = "codigo_rastreio", length = 500)
    private String codigoRastreio;

    @Column(name = "url_nf", length = 1000)
    private String urlNotaFiscal;

    @Transient
    private DadosPagamento dadosPagamento;

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> itens;

    @Transient
    private ClienteRepresentation dadosCliente;

}

