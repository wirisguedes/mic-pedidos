package pedidos.controller.dto;

import pedidos.model.DadosPagamento;

import java.util.List;

public record NovoPedidoDTO(Long codigoCliente, DadosPagamento dadosPagamento, List<ItemPedidoDTO> itens) {
}
