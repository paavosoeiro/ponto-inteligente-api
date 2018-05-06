package com.rest.pontointeligente.api.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.ResourceSupport;

import com.rest.pontointeligente.api.controllers.LancamentoController;
import com.rest.pontointeligente.api.dtos.LancamentoDto;

public class LancamentoResource extends ResourceSupport {

	public final LancamentoDto lancamento;

	public LancamentoResource(LancamentoDto lancamento) {
		this.lancamento = lancamento;
		this.add(linkTo(LancamentoController.class).withRel("lancamentos"));
		this.add(linkTo(LancamentoController.class).slash("funcionario").slash(lancamento.getFuncionarioId())
				.withRel("funcionario"));
		this.add(linkTo(
				methodOn(LancamentoController.class, lancamento.getId().get()).findById(lancamento.getId().get()))
						.withSelfRel());
	}

	public LancamentoDto getLancamento() {
		return this.lancamento;
	}

}
