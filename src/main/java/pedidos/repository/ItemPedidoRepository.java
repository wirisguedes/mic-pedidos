package pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedidos.model.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
