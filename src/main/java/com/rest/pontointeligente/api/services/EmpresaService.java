package com.rest.pontointeligente.api.services;

import java.util.List;
import java.util.Optional;

import com.rest.pontointeligente.api.entities.Empresa;

public interface EmpresaService {

	/**
	 * 
	 * Retorna todas as empresas
	 * 
	 * @return List<Empresa>
	 */
	List<Empresa> getAll();

	/**
	 * 
	 * Retorna uma empresa dado um cnpj
	 * 
	 * @param cnpj
	 * @return Optional<Empresa>
	 */
	Optional<Empresa> findByCnpj(String cnpj);

	/**
	 * 
	 * Persiste uma empresa
	 * 
	 * @param empresa
	 * @return Empresa
	 */
	Empresa persist(Empresa empresa);

	/**
	 * 
	 * @param id
	 * @return Optional<Empresa>
	 */
	Optional<Empresa> findOne(Long id);

}
