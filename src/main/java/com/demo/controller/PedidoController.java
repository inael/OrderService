package com.demo.controller;

import com.demo.exception.ClienteNotFoundException;
import com.demo.model.dto.PedidoDTO;
import com.demo.service.PedidoService;
import com.demo.validator.chain.QuantidadeChainValidator;
import com.demo.validator.chain.IPedidoChainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

    /**
     * Cria um ou mais novos pedidos.
     *
     * @param pedidos Lista de pedidos a serem criados.
     * @return ResponseEntity com os pedidos salvos ou uma mensagem de erro.
     */
    @PostMapping(consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public ResponseEntity<Object> criarPedidos(@RequestBody List<PedidoDTO> pedidos) {
        try {
            validatorChain.validate(pedidos);
            List<PedidoDTO> savedPedidos = pedidoService.salvarPedidos(pedidos);
            return ResponseEntity.ok(savedPedidos);
        } catch (ClienteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Lista todos os pedidos cadastrados.
     *
     * @return ResponseEntity com a lista de todos os pedidos.
     */
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PedidoDTO>> listarTodosPedidos() {
        List<PedidoDTO> pedidos = pedidoService.findAll();
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Busca um pedido pelo seu ID.
     *
     * @param id O ID do pedido.
     * @return ResponseEntity com o pedido encontrado ou uma resposta de não encontrado.
     */
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca um pedido pelo número de controle.
     *
     * @param numeroControle O número de controle do pedido.
     * @return ResponseEntity com o pedido encontrado ou uma resposta de não encontrado.
     */
    @GetMapping(value = "/controle/{numeroControle}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> buscarPedidoPorNumeroControle(@PathVariable Long numeroControle) {

        return pedidoService.findByNumeroControle(numeroControle)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Busca pedidos por data de cadastro.
     *
     * @param dataCadastro A data de cadastro para buscar os pedidos.
     * @return ResponseEntity com os pedidos encontrados ou uma resposta de sem conteúdo se nenhum for encontrado.
     */
    @GetMapping(value = "/por-data", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PedidoDTO>> buscarPedidosPorDataCadastro(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCadastro) {
        List<PedidoDTO> pedidos = pedidoService.findByDataCadastro(dataCadastro);
        return pedidos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(pedidos);
    }

    /**
     * Deleta um pedido pelo seu ID.
     *
     * @param id O ID do pedido a ser deletado.
     * @return ResponseEntity com uma mensagem de sucesso ou uma resposta de não encontrado.
     */
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
