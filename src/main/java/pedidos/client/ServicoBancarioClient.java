package pedidos.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pedidos.model.Pedido;

import java.util.UUID;

@Component
@Slf4j
public class ServicoBancarioClient {

    public String solicitarPagamento(Pedido pedido) {
        log.info("Solicitando pagamento para o pedido de código: {}", pedido.getCodigo());
        return UUID.randomUUID().toString();
    }
}
