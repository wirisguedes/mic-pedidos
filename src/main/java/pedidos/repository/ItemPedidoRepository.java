package pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedidos.model.ItemPedido;
import pedidos.model.Pedido;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
    List<ItemPedido> findByPedido(Pedido pedido);
}
