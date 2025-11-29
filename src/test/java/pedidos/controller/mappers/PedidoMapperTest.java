package pedidos.controller.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pedidos.controller.dto.ItemPedidoDTO;
import pedidos.controller.dto.NovoPedidoDTO;
import pedidos.model.DadosPagamento;
import pedidos.model.ItemPedido;
import pedidos.model.Pedido;
import pedidos.model.enums.StatusPedido;
import pedidos.model.enums.TipoPagamento;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PedidoMapper")
class PedidoMapperTest {

    @Mock
    private ItemPedidoMapper itemPedidoMapper;

    private PedidoMapper pedidoMapper;

    @BeforeEach
    void setUp() {
        pedidoMapper = new PedidoMapperImpl();
        ReflectionTestUtils.setField(pedidoMapper, "ITEM_PEDIDO_MAPPER", itemPedidoMapper);
    }

    @Test
    @DisplayName("Deve mapear NovoPedidoDTO para Pedido com sucesso")
    void deveMapearNovoPedidoDTOParaPedidoComSucesso() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertNotNull(pedido, "O pedido não deve ser nulo");
        assertEquals(1L, pedido.getCodigoCliente(), "O código do cliente deve ser 1");
        assertEquals(StatusPedido.REALIZADO, pedido.getStatus(), "O status deve ser REALIZADO");
        assertNotNull(pedido.getDataPedido(), "A data do pedido não deve ser nula");
        assertNotNull(pedido.getDadosPagamento(), "Os dados de pagamento não devem ser nulos");
        assertEquals(dadosPagamento, pedido.getDadosPagamento(), "Os dados de pagamento devem ser os mesmos do DTO");

        // Verifica itens
        assertNotNull(pedido.getItens(), "Os itens não devem ser nulos");
        assertEquals(2, pedido.getItens().size(), "Deve ter 2 itens");

        // Verifica total calculado (100.00 * 2 + 50.00 * 1 = 250.00)
        BigDecimal totalEsperado = new BigDecimal("250.00");
        assertEquals(0, totalEsperado.compareTo(pedido.getTotal()),
                "O total deve ser 250.00");

        // Verifica relação bidirecional
        pedido.getItens().forEach(item ->
                assertEquals(pedido, item.getPedido(), "Cada item deve ter referência ao pedido")
        );

        // Verifica se o mapper de itens foi chamado
        verify(itemPedidoMapper, times(2)).map(any(ItemPedidoDTO.class));
    }

    @Test
    @DisplayName("Deve calcular total corretamente com múltiplos itens")
    void deveCalcularTotalCorretamenteComMultiplosItens() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = Arrays.asList(
                new ItemPedidoDTO(1L, 3, new BigDecimal("100.00")),
                new ItemPedidoDTO(2L, 2, new BigDecimal("50.00")),
                new ItemPedidoDTO(3L, 1, new BigDecimal("75.00"))
        );
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = Arrays.asList(
                criarItemPedido(1L, 1L, 3, new BigDecimal("100.00")),
                criarItemPedido(2L, 2L, 2, new BigDecimal("50.00")),
                criarItemPedido(3L, 3L, 1, new BigDecimal("75.00"))
        );

        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1), itensEsperados.get(2));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        // Total esperado: (100 * 3) + (50 * 2) + (75 * 1) = 300 + 100 + 75 = 475.00
        BigDecimal totalEsperado = new BigDecimal("475.00");
        assertEquals(0, totalEsperado.compareTo(pedido.getTotal()),
                "O total deve ser 475.00");
    }

    @Test
    @DisplayName("Deve definir status como REALIZADO após mapeamento")
    void deveDefinirStatusComoRealizadoAposMapeamento() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertEquals(StatusPedido.REALIZADO, pedido.getStatus(),
                "O status do pedido deve ser REALIZADO");
    }

    @Test
    @DisplayName("Deve definir data do pedido como data atual")
    void deveDefinirDataDoPedidoComoDataAtual() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertNotNull(pedido.getDataPedido(), "A data do pedido não deve ser nula");
        // Verifica se a data é aproximadamente agora (com margem de 1 segundo)
        long diffInSeconds = Math.abs(
                java.time.Duration.between(pedido.getDataPedido(), java.time.LocalDateTime.now()).getSeconds()
        );
        assertTrue(diffInSeconds < 2,
                "A data do pedido deve ser aproximadamente a data atual");
    }

    @Test
    @DisplayName("Deve estabelecer relação bidirecional entre Pedido e ItemPedido")
    void deveEstabelecerRelacaoBidirecionalEntrePedidoEItemPedido() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertNotNull(pedido.getItens(), "Os itens não devem ser nulos");
        pedido.getItens().forEach(item -> {
            assertNotNull(item.getPedido(), "Cada item deve ter referência ao pedido");
            assertSame(pedido, item.getPedido(),
                    "A referência do pedido em cada item deve ser a mesma instância");
        });
    }

    @Test
    @DisplayName("Deve mapear corretamente os dados de pagamento")
    void deveMaperarCorretamenteOsDadosDePagamento() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        dadosPagamento.setTipoPagamento(TipoPagamento.PIX);
        dadosPagamento.setDados("chave-pix-12345");

        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertNotNull(pedido.getDadosPagamento(), "Os dados de pagamento não devem ser nulos");
        assertEquals(TipoPagamento.PIX, pedido.getDadosPagamento().getTipoPagamento(),
                "O tipo de pagamento deve ser PIX");
        assertEquals("chave-pix-12345", pedido.getDadosPagamento().getDados(),
                "Os dados de pagamento devem ser 'chave-pix-12345'");
    }

    @Test
    @DisplayName("Deve mapear lista de ItemPedidoDTO para lista de ItemPedido")
    void deveMaperarListaDeItemPedidoDTOParaListaDeItemPedido() {
        // Arrange
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        List<ItemPedido> itensEsperados = criarItensPedido();

        when(itemPedidoMapper.map(itensDTOs.get(0))).thenReturn(itensEsperados.get(0));
        when(itemPedidoMapper.map(itensDTOs.get(1))).thenReturn(itensEsperados.get(1));

        // Act
        List<ItemPedido> itens = pedidoMapper.mapItens(itensDTOs);

        // Assert
        assertNotNull(itens, "A lista de itens não deve ser nula");
        assertEquals(2, itens.size(), "Deve ter 2 itens");
        verify(itemPedidoMapper, times(2)).map(any(ItemPedidoDTO.class));
    }

    @Test
    @DisplayName("Deve calcular total como zero quando não há itens")
    void deveCalcularTotalComoZeroQuandoNaoHaItens() {
        // Arrange
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = List.of();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(1L, dadosPagamento, itensDTOs);

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertNotNull(pedido.getTotal(), "O total não deve ser nulo");
        assertEquals(0, BigDecimal.ZERO.compareTo(pedido.getTotal()),
                "O total deve ser zero quando não há itens");
    }

    @Test
    @DisplayName("Deve preservar código do cliente durante o mapeamento")
    void devePreservarCodigoDoClienteDuranteOMapeamento() {
        // Arrange
        Long codigoCliente = 12345L;
        DadosPagamento dadosPagamento = criarDadosPagamento();
        List<ItemPedidoDTO> itensDTOs = criarItensPedidoDTO();
        NovoPedidoDTO novoPedidoDTO = new NovoPedidoDTO(codigoCliente, dadosPagamento, itensDTOs);

        List<ItemPedido> itensEsperados = criarItensPedido();
        when(itemPedidoMapper.map(any(ItemPedidoDTO.class)))
                .thenReturn(itensEsperados.get(0), itensEsperados.get(1));

        // Act
        Pedido pedido = pedidoMapper.map(novoPedidoDTO);

        // Assert
        assertEquals(codigoCliente, pedido.getCodigoCliente(),
                "O código do cliente deve ser preservado");
    }

    // Métodos auxiliares para criar objetos de teste

    private DadosPagamento criarDadosPagamento() {
        DadosPagamento dadosPagamento = new DadosPagamento();
        dadosPagamento.setTipoPagamento(TipoPagamento.CREDIT);
        dadosPagamento.setDados("1234-5678-9012-3456");
        return dadosPagamento;
    }

    private List<ItemPedidoDTO> criarItensPedidoDTO() {
        return Arrays.asList(
                new ItemPedidoDTO(1L, 2, new BigDecimal("100.00")),
                new ItemPedidoDTO(2L, 1, new BigDecimal("50.00"))
        );
    }

    private List<ItemPedido> criarItensPedido() {
        return Arrays.asList(
                criarItemPedido(1L, 1L, 2, new BigDecimal("100.00")),
                criarItemPedido(2L, 2L, 1, new BigDecimal("50.00"))
        );
    }

    private ItemPedido criarItemPedido(Long codigo, Long codigoProduto, Integer quantidade, BigDecimal valorUnitario) {
        ItemPedido item = new ItemPedido();
        item.setCodigo(codigo);
        item.setCodigoProduto(codigoProduto);
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        return item;
    }
}

