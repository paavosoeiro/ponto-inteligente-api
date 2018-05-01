package com.rest.pontointeligente.api.services;

import java.util.Optional;

import com.rest.pontointeligente.api.entities.Funcionario;

public interface FuncionarioService {
	
	/**
	 * 
	 * Persiste um funcionario
	 * 
	 * @param funcionario
	 * @return Funcionario
	 */
	Funcionario persist(Funcionario funcionario);
	
	/**
	 * 
	 * Busca um funcionario pelo CPF
	 * 
	 * @param cpf
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> findByCPf(String cpf);
	
	/**
	 * 
	 * Busca um funcionario pelo email
	 * 
	 * @param email
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> findByEmail(String email);
	
	/**
	 * Busca um funcionario pelo Id
	 * 
	 * @param id
	 * @return Optional<Funcionario>
	 */
	Optional<Funcionario> findById(Long id);	
	
}
