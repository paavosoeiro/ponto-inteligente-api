package com.rest.pontointeligente.api.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.rest.pontointeligente.api.entities.Lancamento;

public interface LancamentoService {

	/**
	 * 
	 * Retorna uma lista paginada de lançamentos de um determinado funcionario
	 * 
	 * @param funcionarioId
	 * @param pageRequest
	 * @return Page<Lancamento>
	 */
	Page<Lancamento> findByFuncionarioId(Long funcionarioId, PageRequest pageRequest);
	
	/**
	 * 
	 * Retorna um lancamento por ID
	 * 
	 * @param id
	 * @return Optional<Lancamento>
	 */
	Optional<Lancamento> findById(Long id);
	
	/**
	 * 
	 * Persiste um lancamento
	 * 
	 * @param lancamento
	 * @return Lancamento
	 */
	Lancamento persist(Lancamento lancamento);
	
	/**
	 * 
	 * Remove um lancamento
	 * 
	 * @param id
	 */
	void remove(Long id);
}
