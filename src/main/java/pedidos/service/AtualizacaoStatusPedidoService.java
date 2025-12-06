package pedidos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pedidos.model.enums.StatusPedido;
import pedidos.repository.PedidoRepository;

@Service
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoService {

    private final PedidoRepository pedidoRepository;


    @Transactional
    public void atualizarStatus(Long codigo,
                                StatusPedido status,
                                String urlNotaFiscal,
                                String codigoRastreio) {

        pedidoRepository.findById(codigo).ifPresent(pedido -> {
            pedido.setStatus(status);
            pedido.setUrlNotaFiscal(urlNotaFiscal);
            pedido.setCodigoRastreio(codigoRastreio);

        });

    }

}
