package pedidos.model;


import lombok.Data;
import pedidos.model.enums.TipoPagamento;

@Data
public class DadosPagamento {
    private String dados;
    private TipoPagamento tipoPagamento;
}
