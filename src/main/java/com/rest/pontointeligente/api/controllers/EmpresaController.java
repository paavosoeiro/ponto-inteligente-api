package com.rest.pontointeligente.api.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
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
import com.rest.pontointeligente.api.response.Response;
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
	 * @return ResponseEntity<Response<List<EmpresaDto>>>
	 */
	@GetMapping
	public ResponseEntity<Response<List<EmpresaDto>>> readRmpresas() {
		log.info("Buscando todas as empresas.");
		Response<List<EmpresaDto>> response = new Response<>();
		List<EmpresaDto> empresasDto = new ArrayList<>();

		this.empresaService.getAll().forEach(emp -> empresasDto.add(this.convertEmpresaToDto(emp)));

		response.setData(empresasDto);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param id
	 * @param result
	 * @return ResponseEntity<Response<EmpresaDto>>
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<EmpresaDto>> readEmpresa(@PathVariable("id") Long id) {
		log.info("Buscando empresa a partir do ID: {}", id);
		Response<EmpresaDto> response = new Response<>();

		Optional<Empresa> empresa = this.empresaService.findOne(id);

		if (!empresa.isPresent()) {
			log.error("Empresa não encontrada para ID: {}", id);
			throw new EmpresaNotFoundException(id);
		}

		response.setData(this.convertEmpresaToDto(empresa.get()));

		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> add(@RequestBody EmpresaDto empresaDto) {
		log.info("Cadastrando nova empresa {}", empresaDto);
		Empresa empresa = this.empresaService.persist(this.convertDtoToEmpresa(empresaDto));
		
		Link linkEmpresaResource = new EmpresaResource(empresa).getLink("self");
		
		return ResponseEntity.created(URI.create(linkEmpresaResource.getHref())).build();
	}

	/**
	 * 
	 * @param empresa
	 * @return EmpresaDto
	 */
	private EmpresaDto convertEmpresaToDto(Empresa empresa) {
		EmpresaDto dto = new EmpresaDto();
		dto.setId(empresa.getId());
		dto.setCnpj(empresa.getCnpj());
		dto.setRazaoSocial(empresa.getRazaoSocial());
		return dto;
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
	private void validateData(EmpresaDto dto, BindingResult result) {
		
	}
}
