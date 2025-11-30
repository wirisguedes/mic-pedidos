package pedidos.controller.dto;

public record RecebimentoCallBackPagamentoDTO(Long codigo, String chavePagamento, boolean status, String observacoes) {
}
