package com.rest.pontointeligente.api.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.rest.pontointeligente.api.controllers.FuncionarioController;
import com.rest.pontointeligente.api.dtos.FuncionarioDto;

public class FuncionarioResource extends ResourceSupport {

	private final FuncionarioDto funcionario;

	public FuncionarioResource(FuncionarioDto funcionario) {
		this.funcionario = funcionario;
		this.add(linkTo(FuncionarioController.class).slash(funcionario).withSelfRel());
	}

	public FuncionarioDto getFuncionario() {
		return this.funcionario;
	}
}
