package pedidos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pedidos.model.Pedido;
import pedidos.repository.ItemPedidoRepository;
import pedidos.repository.PedidoRepository;
import pedidos.validator.PedidoValidator;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;

    public Pedido criar(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
        return pedido;
    }
}
