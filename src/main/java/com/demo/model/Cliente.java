package com.demo.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um cliente no sistema. Um cliente pode ter vários
 * pedidos associados a ele. Futuramente pode ser uma classe a ser utilizada não
 * só para pedidos
 */
@Setter
@Getter
@Entity
@Table(name = "tb_cliente")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	// Um cliente pode ter muitos pedidos.
	// Usei CascadeType.ALL para garantir que
	// se um cliente for removido, seus pedidos também sejam removidos.
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Pedido> pedidos = new ArrayList<>();

	public Cliente() {
	}

	public Cliente(Long id, String nome) {
		this.id = id;
		this.nome = nome;
	}
	public Cliente(Long id) {
		this.id = id;
	}
}
