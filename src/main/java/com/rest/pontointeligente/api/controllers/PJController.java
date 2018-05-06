package com.rest.pontointeligente.api.controllers;

import java.security.NoSuchAlgorithmException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pontointeligente.api.dtos.PJDto;
import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.enums.PerfilEnum;
import com.rest.pontointeligente.api.response.Response;
import com.rest.pontointeligente.api.services.EmpresaService;
import com.rest.pontointeligente.api.services.FuncionarioService;
import com.rest.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("/api/pj-controller")
@CrossOrigin(origins = "*")
public class PJController {

	private static final Logger log = LoggerFactory.getLogger(PJController.class);

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private FuncionarioService funcionarioService;

	/**
	 * 
	 * @param pjDto
	 * @param bindingResult
	 * @return ResponseEntity<Response<PJDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<PJDto>> cadastrar(@Valid @RequestBody PJDto pjDto, BindingResult bindingResult)
			throws NoSuchAlgorithmException {
		log.info("Cadastrando PJ: {}", pjDto.toString());
		Response<PJDto> response = new Response<>();

		validateData(pjDto, bindingResult);
		Empresa empresa = this.convertDtoEmpresa(pjDto);
		Funcionario funcionario = this.convertDtoToFuncionario(pjDto, bindingResult);

		if (bindingResult.hasErrors()) {
			log.error("Erro validando dados de cadastro do PJ: {}", bindingResult.getAllErrors());
			bindingResult.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		this.empresaService.persist(empresa);
		funcionario.setEmpresa(empresa);
		this.funcionarioService.persist(funcionario);

		response.setData(this.convertFuncionarioToDto(funcionario));

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param pjDto
	 * @param result
	 */
	private void validateData(PJDto pjDto, BindingResult result) {
		this.empresaService.findByCnpj(pjDto.getCnpj())
				.ifPresent(emp -> result.addError(new ObjectError("empresa", "Empresa já existente.")));

		this.funcionarioService.findByCPf(pjDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.funcionarioService.findByEmail(pjDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email já existente.")));
	}

	/**
	 * 
	 * @param pjDto
	 * @return Empresa
	 */
	private Empresa convertDtoEmpresa(PJDto pjDto) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(pjDto.getCnpj());
		empresa.setRazaoSocial(pjDto.getRazaoSocial());
		return empresa;
	}

	/**
	 * 
	 * @param pjDto
	 * @param bindingResult
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario convertDtoToFuncionario(PJDto pjDto, BindingResult bindingResult)
			throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(pjDto.getNome());
		funcionario.setCpf(pjDto.getCpf());
		funcionario.setEmail(pjDto.getEmail());
		funcionario.setPerfil(PerfilEnum.ROLE_ADMIN);
		funcionario.setSenha(PasswordUtils.gerarBCript(pjDto.getSenha()));
		return funcionario;
	}

	/**
	 * 
	 * @param funcionario
	 * @return PJDto
	 */
	private PJDto convertFuncionarioToDto(Funcionario funcionario) {
		PJDto dto = new PJDto();
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setCpf(funcionario.getCpf());
		dto.setEmail(funcionario.getEmail());
		dto.setRazaoSocial(funcionario.getEmpresa().getRazaoSocial());
		return dto;
	}

}
