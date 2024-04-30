package com.demo.model.dto;

import com.demo.model.Cliente;
import org.springframework.beans.BeanUtils;

public class ClienteDTO {

	private Long id;
	private String nome;

	public ClienteDTO(Long id) {
		this.id = id;
	}

	public ClienteDTO(Long id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public ClienteDTO(Cliente entity) {
		BeanUtils.copyProperties(entity, this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
