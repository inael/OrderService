package com.demo.controller;


import com.demo.model.dto.PedidoDTO;
import com.demo.service.PedidoService;
import com.demo.validator.chain.IPedidoChainValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PedidoController.class)
public class PedidoControllerIntegrationTest {

    @MockBean
    private PedidoService pedidoService;

    @MockBean
    private IPedidoChainValidator validatorChain;

    @Autowired
    private MockMvc mockMvc;

    private  PedidoDTO pedido;
    private  List<PedidoDTO> pedidos;
    @BeforeEach
    public void setUp() throws Exception {
        pedido = new PedidoDTO();
        pedido.setNumeroControle(123l);
        pedido.setQuantidade(4);
        pedido.setDataCadastro(LocalDate.now());
        pedido.setCodigoCliente(11l);
        pedido.setNome("Banana");
        pedido.setValor(new BigDecimal(4));
        pedidos = Arrays.asList(pedido);
    }

    @Test
    void criarPedidos_ShouldReturnSavedPedidos() throws Exception {
        when(pedidoService.salvarPedidos(pedidos)).thenReturn(pedidos);
        String content = asJsonString(pedidos);
        mockMvc.perform(post("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();


    }

    @Test
    public void listarTodosPedidos_ShouldReturnAllPedidos() throws Exception {
        when(pedidoService.findAll()).thenReturn(pedidos);

        mockMvc.perform(get("/pedidos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void buscarPedidoPorId_ShouldReturnPedido() throws Exception {
        Long id = 1L;
        when(pedidoService.findById(id)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/pedidos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    public static String asJsonString(final Object obj) {
        try {
            // Crie um ObjectMapper e configure o módulo para datas
            ObjectMapper mapper = new ObjectMapper();
            JavaTimeModule module = new JavaTimeModule();

            // Define o serializador para LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            module.addSerializer(LocalDate.class, new LocalDateSerializer(formatter));

            // Adicione o módulo ao ObjectMapper
            mapper.registerModule(module);

            // Desabilite a escrita de datas como timestamps para garantir o uso do formato personalizado
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Converta o objeto para JSON
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

}
