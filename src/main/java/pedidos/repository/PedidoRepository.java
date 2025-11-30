package pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pedidos.model.Pedido;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByCodigoAndChavePagamento(Long codigo, String chavePagamento);
}
