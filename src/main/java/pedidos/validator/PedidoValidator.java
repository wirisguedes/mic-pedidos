package pedidos.validator;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pedidos.client.ClientesClient;
import pedidos.client.ProdutosClient;
import pedidos.client.representation.ClienteRepresentation;
import pedidos.client.representation.ProdutoRepresentation;
import pedidos.model.ItemPedido;
import pedidos.model.Pedido;
import pedidos.model.exception.ValidationException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoValidator {

    private final ProdutosClient produtosClient;
    private final ClientesClient clientesClient;

    public void validate(Pedido pedido) {

        Long codigoCliente = pedido.getCodigoCliente();
        validarCliente(codigoCliente);
        pedido.getItens().forEach(this::validarItem);

    }

    private void validarCliente(Long codigoCliente){

        try {
            var response = clientesClient.obterDados(codigoCliente);
            ClienteRepresentation cliente = response.getBody();
            log.info("Cliente de código {} encontrado: {}", cliente.codigo(), cliente.nome());

            if (!cliente.ativo()) {
                throw new ValidationException("codigoCliente", "Cliente está inativo");
            }
        } catch (FeignException.NotFound e)
        {
            var message = String.format("Cliente de código %d não encontrado", codigoCliente);

            throw  new ValidationException("codigoCliente", message);
        }

    }

    private void validarItem(ItemPedido item){

        try{
            var response = produtosClient.obterDados(item.getCodigoProduto());
            ProdutoRepresentation produto = response.getBody();
            log.info("Produto de código {} encontrado: {}", produto.codigo(), produto.nome());

            if (!produto.ativo()) {
                throw new ValidationException("codigoProduto", "Produto está inativo");
            }
        } catch (FeignException.NotFound e)
        {
            var message = String.format("Produto de código %d não encontrado", item.getCodigoProduto());

            throw  new ValidationException("codigoProduto", message);
        }

    }

}
