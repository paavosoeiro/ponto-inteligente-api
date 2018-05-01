package com.rest.pontointeligente.api.services;

import java.util.Optional;

import com.rest.pontointeligente.api.entities.Empresa;

public interface EmpresaService {
	
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

}
