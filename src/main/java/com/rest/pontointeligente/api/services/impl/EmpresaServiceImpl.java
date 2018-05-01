package com.rest.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.repositories.EmpresaRepository;
import com.rest.pontointeligente.api.services.EmpresaService;

@Service
public class EmpresaServiceImpl implements EmpresaService {
	
	private static final Logger log = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	
	@Autowired
	private EmpresaRepository empresaRepository;
	
	@Override
	public Optional<Empresa> findByCnpj(String cnpj) {
		log.info("Buscando empresa pelo cnpj {}", cnpj);
		return Optional.ofNullable(this.empresaRepository.findByCnpj(cnpj));
	}

	@Override
	public Empresa persist(Empresa empresa) {
		log.info("Salvando empresa: {}", empresa);
		return this.empresaRepository.save(empresa);
	}

}
