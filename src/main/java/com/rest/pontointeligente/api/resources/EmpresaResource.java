package com.rest.pontointeligente.api.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ResourceSupport;

import com.rest.pontointeligente.api.controllers.EmpresaController;
import com.rest.pontointeligente.api.entities.Empresa;

public class EmpresaResource extends ResourceSupport	{
	
	private final Empresa empresa;
	
	public EmpresaResource(Empresa empresa) {
		this.empresa = empresa;
		this.add(linkTo(EmpresaController.class).slash(empresa.getId()).withSelfRel());
	}
	
	public Empresa getEmpresa() {
		return this.empresa;
	}
}
