package pedidos.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedidos.controller.dto.AdicaoNovoPagamentoDTO;
import pedidos.controller.dto.NovoPedidoDTO;
import pedidos.controller.mappers.PedidoMapper;
import pedidos.model.ErroResposta;
import pedidos.model.exception.ItemNaoEncontradoException;
import pedidos.model.exception.ValidationException;
import pedidos.publisher.DetalhePedidoMapper;
import pedidos.publisher.representation.DetalhePedidoRepresentation;
import pedidos.service.PedidoService;

@RestController
@RequestMapping("pedidos")
@RequiredArgsConstructor
public class PedidoController {


    private final PedidoService pedidoService;
    private final PedidoMapper mapper;
    private final DetalhePedidoMapper detalhePedidoMapper;

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody NovoPedidoDTO dto) {

        try {
            var pedido =  mapper.map(dto);
            var novoPedido = pedidoService.criar(pedido);
            return ResponseEntity.ok(novoPedido.getCodigo());
        } catch (ValidationException e) {
          var erro = new ErroResposta("Erro de validação", e.getField(), e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }

    }

    @PostMapping("pagamentos")
    public ResponseEntity<Object> adicionarNovoPagamento(@RequestBody AdicaoNovoPagamentoDTO dto) {

        try {

            pedidoService.adicionarNovoPagamento(dto.codigoPedido(), dto.dados(), dto.tipoPagamento());
            return ResponseEntity.noContent().build();
        } catch (ItemNaoEncontradoException e) {
            var erro = new ErroResposta("Item não encontrado", "codigoPedido", e.getMessage());
            return ResponseEntity.badRequest().body(erro);
        }
    }


    @GetMapping("{codigo}")
    public ResponseEntity<DetalhePedidoRepresentation> obterDetalhesDoPedido(@PathVariable Long codigo) {
        return pedidoService
                .carregarDadosCompletosPedido(codigo)
                .map(detalhePedidoMapper::map)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
