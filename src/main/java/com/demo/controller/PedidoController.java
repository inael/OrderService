package com.demo.controller;

import com.demo.model.dto.PedidoDTO;
import com.demo.service.PedidoService;
import com.demo.validator.chain.QuantidadeChainValidator;
import com.demo.validator.chain.IPedidoChainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    private final IPedidoChainValidator validatorChain;

    public PedidoController() {
        this.validatorChain = new QuantidadeChainValidator();
        //this.validatorChain.setNextValidator(....);
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public ResponseEntity<List<PedidoDTO>> criarPedidos(@RequestBody List<PedidoDTO> pedidos) {
        validatorChain.validate(pedidos);
        List<PedidoDTO> savedPedidos = pedidoService.salvarPedidos(pedidos);
        return ResponseEntity.ok(savedPedidos);
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PedidoDTO>> listarTodosPedidos() {
        List<PedidoDTO> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/controle/{numeroControle}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarPedidoPorNumeroControle(@PathVariable Long numeroControle) {

        return pedidoService.findByNumeroControle(numeroControle)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/por-data", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PedidoDTO>> buscarPedidosPorDataCadastro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro) {
        List<PedidoDTO> pedidos = pedidoService.findByDataCadastro(dataCadastro);
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPedido(@PathVariable Long id) {
        try {
            pedidoService.deletaPedidoPorId(id);
            return ResponseEntity.ok("Pedido deletado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
