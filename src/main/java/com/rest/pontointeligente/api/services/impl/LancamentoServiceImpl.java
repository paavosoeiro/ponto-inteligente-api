package com.rest.pontointeligente.api.services.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rest.pontointeligente.api.entities.Lancamento;
import com.rest.pontointeligente.api.repositories.LancamentoRepository;
import com.rest.pontointeligente.api.services.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	private static final Logger log = LoggerFactory.getLogger(LancamentoServiceImpl.class);
	
	@Autowired
	private LancamentoRepository lancamentoRepository;

	@Override
	public Page<Lancamento> findByFuncionarioId(Long funcionarioId, PageRequest pageRequest) {
		log.info("Buscando lancamentos para o funcionario ID {}", funcionarioId);
		return this.lancamentoRepository.findByFuncionarioId(funcionarioId, pageRequest);
	}

	@Override
	public Optional<Lancamento> findById(Long id) {
		log.info("Buscando um lancamento para o ID {}", id);
		return this.lancamentoRepository.findById(id);
	}

	@Override
	public Lancamento persist(Lancamento lancamento) {
		log.info("Persistindo o lancamento: {}", lancamento);
		return this.lancamentoRepository.save(lancamento);
	}

	@Override
	public void remove(Long id) {
		log.info("Removendo o lancamento ID {}", id);
		this.lancamentoRepository.deleteById(id);

	}

}
