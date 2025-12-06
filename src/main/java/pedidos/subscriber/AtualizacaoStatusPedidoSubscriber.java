package pedidos.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pedidos.model.enums.StatusPedido;
import pedidos.service.AtualizacaoStatusPedidoService;
import pedidos.subscriber.representation.AtualizacaoStatusPedidoRepresentation;

@Slf4j
@Component
@RequiredArgsConstructor
public class AtualizacaoStatusPedidoSubscriber {

    private final AtualizacaoStatusPedidoService service;
    private final ObjectMapper objectMapper;


    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = {
            "${icompras.config.kafka.topics.pedidos-faturados}",
            "${icompras.config.kafka.topics.pedidos-enviados}"
    })
    public void receberAtualizacao(String payload){
        log.info("Recebendo atualização: {}", payload);

        try {

            var atualizacaoStatus =
                    objectMapper.readValue(payload, AtualizacaoStatusPedidoRepresentation.class);
            service.atualizarStatus(
                    atualizacaoStatus.codigo(),
                    atualizacaoStatus.status(),
                    atualizacaoStatus.urlNotaFiscal(),
                    atualizacaoStatus.codigoRastreio()
            );
            log.info("Atualização do pedido {} processada com sucesso.", atualizacaoStatus.codigo());
        }catch (Exception e){
            log.error("Erro ao desserializar a atualização do pedido: {}", e.getMessage());

        }
    }
}
