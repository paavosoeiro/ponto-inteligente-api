package com.rest.pontointeligente.api.resources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import com.rest.pontointeligente.api.controllers.EmpresaController;
import com.rest.pontointeligente.api.entities.Empresa;

public class EmpresaResource extends ResourceSupport {
	
	private final Empresa empresa;
	
	public EmpresaResource(Empresa empresa) {
		this.empresa = empresa;
		this.add(linkTo(methodOn(EmpresaController.class).readEmpresa(empresa.getId())).withSelfRel());
		this.add(linkTo(methodOn(EmpresaController.class).readEmpresa(empresa.getId())).slash("funcionarios").withRel("funcionarios"));
	}
	
	public Empresa getEmpresa() {
		return this.empresa;
	}
}