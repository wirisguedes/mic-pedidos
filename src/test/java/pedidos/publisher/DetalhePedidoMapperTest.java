package pedidos.publisher;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pedidos.client.representation.ClienteRepresentation;
import pedidos.model.Pedido;
import pedidos.model.enums.StatusPedido;
import pedidos.publisher.representation.DetalhePedidoRepresentation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DetalhePedidoMapperTest {

    private final DetalhePedidoMapper mapper = Mappers.getMapper(DetalhePedidoMapper.class);

    @Test
    void shouldMapPedidoToDetalheRepresentation() {
        Pedido pedido = new Pedido();
        pedido.setCodigo(123L);
        pedido.setCodigoCliente(10L);
        LocalDateTime date = LocalDateTime.of(2025, 11, 30, 14, 30);
        pedido.setDataPedido(date);
        pedido.setTotal(new BigDecimal("99.90"));
        pedido.setStatus(StatusPedido.PAGO);
        ClienteRepresentation cliente = new ClienteRepresentation(10L, "João", "12345678900", "Rua A", "12", "Bairro B", "joao@example.com", "99999-9999" , true);
        pedido.setDadosCliente(cliente);
        pedido.setItens(List.of());

        DetalhePedidoRepresentation rep = mapper.map(pedido);

        assertThat(rep.codigo()).isEqualTo(123L);
        assertThat(rep.codigoCliente()).isEqualTo(10L);
        assertThat(rep.nome()).isEqualTo("João");
        assertThat(rep.cpf()).isEqualTo("12345678900");
        assertThat(rep.logradouro()).isEqualTo("Rua A");
        assertThat(rep.numero()).isEqualTo("12");
        assertThat(rep.bairro()).isEqualTo("Bairro B");
        assertThat(rep.email()).isEqualTo("joao@example.com");
        assertThat(rep.telefone()).isEqualTo("99999-9999");
        assertThat(rep.dataPedido()).isEqualTo("2025-11-30");
        assertThat(rep.total()).isEqualByComparingTo(new BigDecimal("99.90"));
        assertThat(rep.status()).isEqualTo(StatusPedido.PAGO);
        assertThat(rep.itens()).isEmpty();
    }

    @Test
    void shouldHandleNullItensAndCliente() {
        Pedido pedido = new Pedido();
        pedido.setCodigo(1L);
        pedido.setCodigoCliente(2L);
        pedido.setDataPedido(LocalDateTime.of(2025, 1, 1, 0, 0));
        pedido.setItens(null);

        DetalhePedidoRepresentation rep = mapper.map(pedido);

        assertThat(rep.codigo()).isEqualTo(1L);
        assertThat(rep.codigoCliente()).isEqualTo(2L);
        assertThat(rep.nome()).isNull();
        assertThat(rep.itens()).isNull();
    }
}
