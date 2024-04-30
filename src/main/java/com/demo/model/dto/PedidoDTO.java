package com.demo.model.dto;

import com.demo.model.Pedido;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class PedidoDTO {

	private Long id;
	private Long numeroControle;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate dataCadastro;
	private String nome;
	private BigDecimal valor;
	private Integer quantidade;
	private Long codigoCliente;

	public PedidoDTO() {

	}

	public PedidoDTO(Long id, LocalDate dataCadastro, String nome, BigDecimal valor, Integer quantidade,
					 Long codigoCliente, Long numeroControle) {
		this.id = id;
		this.dataCadastro = dataCadastro;
		this.nome = nome;
		this.valor = valor;
		this.quantidade = quantidade;
		this.codigoCliente = codigoCliente;
		this.numeroControle = numeroControle;
	}

	// Copia as propriedades básicas da classe Pedido, costumo usar isso pra
	// facilitar minha conversão de DTO
	public PedidoDTO(Pedido entity) {
		BeanUtils.copyProperties(entity, this);

		// Adiciona o cliente manualmente para garantir que a associação é mantida
		if (entity.getCliente() != null) {
			this.codigoCliente = entity.getCliente().getId();
		} else {
			this.codigoCliente = null;
		}
	}

}
