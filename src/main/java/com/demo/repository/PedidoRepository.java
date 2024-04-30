package com.demo.repository;

import com.demo.model.Cliente;
import com.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	// Método para encontrar pedidos por cliente
	List<Pedido> findByCliente(Cliente cliente);

	// Método para encontrar pedidos por data de cadastro
	List<Pedido> findByDataCadastro(LocalDate dataCadastro);
	
	// Metodo para encontrar um numero de controle
	Optional<Pedido> findByNumeroControle(Long numeroControle);

}
