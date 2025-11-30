package pedidos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pedidos.controller.dto.RecebimentoCallBackPagamentoDTO;
import pedidos.service.PedidoService;

@RestController
@RequestMapping("/pedidos/callback-pagamentos")
@RequiredArgsConstructor
public class RecebimentoCallBackPagamentoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Object> atualizaStatusPagamento(
            @RequestBody RecebimentoCallBackPagamentoDTO body,
            @RequestHeader(required = true, name = "apiKey") String apiKey
            ) {

        pedidoService.atualizaStatusPagamento(body.codigo(), body.chavePagamento(), body.status(), body.observacoes());

        return ResponseEntity.ok().build();
    }
}
