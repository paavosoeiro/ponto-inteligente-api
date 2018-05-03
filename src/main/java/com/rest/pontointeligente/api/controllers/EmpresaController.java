package com.rest.pontointeligente.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pontointeligente.api.dtos.EmpresaDto;
import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.exceptions.EmpresaNotFoundException;
import com.rest.pontointeligente.api.resources.EmpresaResource;
import com.rest.pontointeligente.api.services.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

	private static final Logger log = LoggerFactory.getLogger(EmpresaController.class);

	@Autowired
	private EmpresaService empresaService;

	/**
	 * 
	 * @return Resources<EmpresaResource>
	 */
	@GetMapping
	public Resources<EmpresaResource> readEmpresas() {
		log.info("Buscando todas as empresas.");
		List<EmpresaResource> empresas = this.empresaService.getAll().stream().map(EmpresaResource::new)
				.collect(Collectors.toList());
		return new Resources<>(empresas);
	}

	/**
	 * 
	 * @param id
	 * @return EmpresaResource
	 */
	@GetMapping(value = "/{id}")
	public EmpresaResource readEmpresa(@PathVariable("id") Long id) {
		log.info("Buscando empresa a partir do ID: {}", id);
		Empresa empresa = this.empresaService.findOne(id).orElseThrow(() -> new EmpresaNotFoundException(id));
		return new EmpresaResource(empresa);
	}

	/**
	 * 
	 * @param empresaDto
	 * @return ResponseEntity<?>
	 */
	@PostMapping
	public ResponseEntity<?> add(@RequestBody EmpresaDto empresaDto) {
		log.info("Cadastrando nova empresa {}", empresaDto);
		Empresa empresa = this.empresaService.persist(this.convertDtoToEmpresa(empresaDto));

		Link linkEmpresaResource = new EmpresaResource(empresa).getLink("self");

		return ResponseEntity.created(URI.create(linkEmpresaResource.getHref())).build();
	}

	/**
	 * 
	 * @param dto
	 * @return Empresa
	 */
	private Empresa convertDtoToEmpresa(EmpresaDto dto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(dto.getCnpj());
		empresa.setRazaoSocial(dto.getRazaoSocial());
		return empresa;
	}

	/**
	 * 
	 * Validate input data
	 * 
	 * @param dto
	 * @param result
	 */
	@SuppressWarnings("unused")
	private void validateData(EmpresaDto dto, BindingResult result) {

	}
}
