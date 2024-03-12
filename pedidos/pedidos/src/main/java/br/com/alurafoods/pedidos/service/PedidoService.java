package br.com.alurafoods.pedidos.service;

import br.com.alurafoods.pedidos.dto.PedidoDto;
import br.com.alurafoods.pedidos.dto.StatusDto;
import br.com.alurafoods.pedidos.model.Pedido;
import br.com.alurafoods.pedidos.model.Status;
import br.com.alurafoods.pedidos.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private final ModelMapper modelMapper;

    public List<PedidoDto> obterTodos() {
        return repository.findAll().stream()
                .map(pedido -> modelMapper.map(pedido, PedidoDto.class))
                .collect(Collectors.toList());
    }
    public PedidoDto obterPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto criarPedido(PedidoDto dto) {
        Pedido pedido = modelMapper.map(dto, Pedido.class);

        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(Status.REALIZADO);
        pedido.getItens().forEach(item -> item.setPedido(pedido));
        Pedido salvo = repository.save(pedido);

        return modelMapper.map(pedido, PedidoDto.class);
    }

    public PedidoDto atualizaStatus(Long id, StatusDto dto) {
        Pedido pedido = repository.porIdComItens(id);

        if(pedido == null) {
            throw new EntityNotFoundException();
        }

        pedido.setStatus(dto.getStatus());
        repository.atualizaStatus(dto.getStatus(), pedido);
        return modelMapper.map(pedido, PedidoDto.class);
    }

    public void aprovaPagamentoPedido(Long id) {
        Pedido pedido = repository.porIdComItens(id);

        if(pedido == null){
            throw new EntityNotFoundException();
        }

        pedido.setStatus(Status.PAGO);
        repository.atualizaStatus(Status.PAGO, pedido);
    }
}