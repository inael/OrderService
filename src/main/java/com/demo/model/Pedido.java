package com.demo.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidade que representa um pedido no sistema. Um pedido está associado a um
 * cliente.
 */
@Setter
@Getter
@Entity
@Table(name = "tb_pedido")
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Essa anotação com a constrint unique garante que não haja duplicatas
	@Column(nullable = false, unique = true)
	private Long numeroControle;

	private LocalDate dataCadastro;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "produto_id", referencedColumnName = "id")
	private Produto produto;

	private Integer quantidade;

	// Relacionamento com o cliente. O pedido pertence a um cliente
	// e respectivamente ele possui um codigo unico.
	@ManyToOne
	@JoinColumn(name = "cliente_id")
	private Cliente cliente;

	@Column(precision = 10, scale = 2)
	private BigDecimal valorTotal;
	public Pedido() {
	}

	public Pedido(Long id, LocalDate dataCadastro, String nome, BigDecimal valor, Integer quantidade, Cliente cliente,
			Long numeroControle) {
		this.id = id;
		this.dataCadastro = dataCadastro;
		this.produto = new Produto(nome, valor);
		this.quantidade = quantidade;
		this.cliente = cliente;
		this.numeroControle = numeroControle;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pedido other = (Pedido) obj;
		return Objects.equals(id, other.id);
	}

}
