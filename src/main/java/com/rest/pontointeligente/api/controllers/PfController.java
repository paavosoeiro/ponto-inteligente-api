package com.rest.pontointeligente.api.controllers;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

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

import com.rest.pontointeligente.api.dtos.PfDto;
import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.enums.PerfilEnum;
import com.rest.pontointeligente.api.response.Response;
import com.rest.pontointeligente.api.services.EmpresaService;
import com.rest.pontointeligente.api.services.FuncionarioService;
import com.rest.pontointeligente.api.utils.PasswordUtils;

@RestController
@RequestMapping("api/pf-controller")
@CrossOrigin(origins = "*")
public class PfController {

	private static final Logger log = LoggerFactory.getLogger(PfController.class);

	@Autowired
	private EmpresaService empresaService;

	@Autowired
	private FuncionarioService funcionarioService;

	/**
	 * 
	 * @param pfDto
	 * @param result
	 * @return ResponseEntity<Response<PfDto>>
	 * @throws NoSuchAlgorithmException
	 */
	@PostMapping
	public ResponseEntity<Response<PfDto>> cadastrar(@Valid @RequestBody PfDto pfDto, BindingResult result)
			throws NoSuchAlgorithmException {
		log.info("Cadastrando PF: {}", pfDto.toString());
		Response<PfDto> response = new Response<>();

		validateData(pfDto, result);
		Funcionario funcionario = this.convertDtoToFuncionario(pfDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro PF: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Optional<Empresa> empresa = this.empresaService.findByCnpj(pfDto.getCnpj());
		empresa.ifPresent(emp -> funcionario.setEmpresa(emp));
		this.funcionarioService.persist(funcionario);
		
		response.setData(this.convertFuncionarioToDto(funcionario));
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param pfDto
	 * @param result
	 */
	private void validateData(PfDto pfDto, BindingResult result) {
		Optional<Empresa> empresa = this.empresaService.findByCnpj(pfDto.getCnpj());
		if (!empresa.isPresent()) {
			result.addError(new ObjectError("empresa", "Empresa nao cadastrada."));
		}

		this.funcionarioService.findByCPf(pfDto.getCpf())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "CPF já existente.")));

		this.funcionarioService.findByEmail(pfDto.getEmail())
				.ifPresent(func -> result.addError(new ObjectError("funcionario", "Email ja existente.")));
	}

	/**
	 * 
	 * @param pfDto
	 * @param result
	 * @return Funcionario
	 * @throws NoSuchAlgorithmException
	 */
	private Funcionario convertDtoToFuncionario(PfDto pfDto, BindingResult result) throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome(pfDto.getNome());
		funcionario.setEmail(pfDto.getEmail());
		funcionario.setCpf(pfDto.getCpf());
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCript(pfDto.getSenha()));
		pfDto.getQtdHorasAlmoco()
				.ifPresent(qtdHorasAlmoco -> funcionario.setQtdHorasAlmoco(Float.valueOf(qtdHorasAlmoco)));
		pfDto.getQtdHorasTrabalhoDia().ifPresent(
				qtdHorasTrabalhoDia -> funcionario.setQtdHorasTrabalhoDia(Float.valueOf(qtdHorasTrabalhoDia)));
		pfDto.getValorHora().ifPresent(valorHora -> funcionario.setValorHora(new BigDecimal(valorHora)));
		return funcionario;
	}

	/**
	 * 
	 * @param funcionario
	 * @return PfDto
	 */
	private PfDto convertFuncionarioToDto(Funcionario funcionario) {
		PfDto dto = new PfDto();
		dto.setId(funcionario.getId());
		dto.setNome(funcionario.getNome());
		dto.setEmail(funcionario.getEmail());
		dto.setCpf(funcionario.getCpf());
		dto.setCnpj(funcionario.getEmpresa().getCnpj());
		funcionario.getQtdHorasAlmocoOpt()
				.ifPresent(qtdHorasAlmoco -> dto.setQtdHorasAlmoco(Optional.of(Float.toString(qtdHorasAlmoco))));
		funcionario.getQtdHorasTrabalhoDiaOpt()
				.ifPresent(qtdHorasTrabDia -> dto.setQtdHorasTrabalhoDia(Optional.of(Float.toString(qtdHorasTrabDia))));
		funcionario.getValorHoraOpt().ifPresent(valorHora -> dto.setValorHora(Optional.of(valorHora.toString())));
		return dto;
	}
}
