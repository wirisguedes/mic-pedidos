package pedidos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pedidos.client.ClientesClient;
import pedidos.client.ProdutosClient;
import pedidos.client.ServicoBancarioClient;
import pedidos.model.DadosPagamento;
import pedidos.model.ItemPedido;
import pedidos.model.Pedido;
import pedidos.model.enums.StatusPedido;
import pedidos.model.enums.TipoPagamento;
import pedidos.model.exception.ItemNaoEncontradoException;
import pedidos.repository.ItemPedidoRepository;
import pedidos.repository.PedidoRepository;
import pedidos.validator.PedidoValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoValidator pedidoValidator;
    private final ServicoBancarioClient servicoBancarioClient;
    private final ClientesClient apiClientesClient;
    private final ProdutosClient apiProdutosClient;

    @Transactional
    public Pedido criar(Pedido pedido) {
        pedidoValidator.validate(pedido);
        realizarPersistencia(pedido);
        enviarSolicitacaoPagamento(pedido);
        return pedido;
    }

    private void enviarSolicitacaoPagamento(Pedido pedido) {
        var chavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(chavePagamento);
    }

    private void realizarPersistencia(Pedido pedido) {
        pedidoRepository.save(pedido);
        itemPedidoRepository.saveAll(pedido.getItens());
    }

    public void atualizaStatusPagamento(Long codigoPedido, String chavePagamento, boolean sucesso, String observacoes) {

        var pedidoEncontrado = pedidoRepository.findByCodigoAndChavePagamento(codigoPedido, chavePagamento);

        if(pedidoEncontrado.isEmpty()) {
            var msg = String.format("Pedido com código %d e chave de pagamento %s não encontrado.", codigoPedido, chavePagamento);
           log.error(msg);

           return;
        }

        Pedido pedido = pedidoEncontrado.get();

        if(sucesso){

            pedido.setStatus(StatusPedido.PAGO);
        }else{
            pedido.setStatus(StatusPedido.ERRO_PAGAMENTO);
            pedido.setObservacoes(observacoes);
        }

        pedidoRepository.save(pedido);
    }

    @Transactional
    public void adicionarNovoPagamento(Long codigoPedido, String dadosCartao, TipoPagamento tipo) {

        var pedidoEncontrado = pedidoRepository.findById(codigoPedido);

        if(pedidoEncontrado.isEmpty()){
           throw new ItemNaoEncontradoException("Pedido não encontrado com o código: " + codigoPedido);
        }

        var pedido = pedidoEncontrado.get();

        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setTipoPagamento(tipo);
        dadosPagamento.setDados(dadosCartao);

        pedido.setDadosPagamento(dadosPagamento);
        pedido.setStatus(StatusPedido.REALIZADO);
        pedido.setObservacoes("Novo pagamento adicionado, aguardando processamento.");

        String novaChavePagamento = servicoBancarioClient.solicitarPagamento(pedido);
        pedido.setChavePagamento(novaChavePagamento);

        pedidoRepository.save(pedido);
    }

    public Optional<Pedido> carregarDadosCompletosPedido(Long codigo){
        Optional<Pedido> pedido = pedidoRepository.findById(codigo);
        pedido.ifPresent(this::carregarDadosCliente);
        pedido.ifPresent(this::carregarItensPedido);
        return pedido;
    }

    private void carregarDadosCliente(Pedido pedido){
        Long codigoCliente = pedido.getCodigoCliente();
        var response = apiClientesClient.obterDados(codigoCliente);
        pedido.setDadosCliente(response.getBody());

    }

    public void carregarItensPedido(Pedido pedido){
        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);
        pedido.setItens(itens);
        pedido.getItens().forEach(this::carregarDadosProduto);

    }

    private void carregarDadosProduto(ItemPedido item){
        Long codigoProduto = item.getCodigoProduto();
        var response = apiProdutosClient.obterDados(codigoProduto);
        item.setNome(response.getBody().nome());
    }


}
