package pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedidos.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
