package pedidos.controller.mappers;


import org.mapstruct.Mapper;
import pedidos.controller.dto.ItemPedidoDTO;
import pedidos.model.ItemPedido;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

    ItemPedido map(ItemPedidoDTO dto);
}
