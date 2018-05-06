package com.rest.pontointeligente.api.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.validation.Valid;

import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pontointeligente.api.dtos.LancamentoDto;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.entities.Lancamento;
import com.rest.pontointeligente.api.enums.TipoEnum;
import com.rest.pontointeligente.api.resources.LancamentoResource;
import com.rest.pontointeligente.api.response.Response;
import com.rest.pontointeligente.api.services.FuncionarioService;
import com.rest.pontointeligente.api.services.LancamentoService;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin(origins = "*")
public class LancamentoController {

	private static final Logger log = LoggerFactory.getLogger(LancamentoController.class);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private LancamentoService lancamentoService;

	@Autowired
	private FuncionarioService funcionarioService;

	@Value("${pagination.page_size}")
	private int pageSize;

	/**
	 * 
	 * @param id
	 * @param page
	 * @param ord
	 * @param dir
	 * @return ResponseEntity<Response<Page<LancamentoResource>>>
	 */
	@GetMapping("/funcionario/{funcionarioId}")
	public ResponseEntity<Response<Page<LancamentoResource>>> findByFuncionarioId(@PathVariable("funcionarioId") Long funcionarioId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "ord", defaultValue = "id") String ord,
			@RequestParam(value = "dir", defaultValue = "DESC") String dir) {
		log.info("Buscando lancamentos por ID do funcionario: {}, pagina: {}", funcionarioId, page);
		Response<Page<LancamentoResource>> response = new Response<>();

		PageRequest pageRequest = PageRequest.of(page, this.pageSize, Direction.valueOf(dir), ord);
		Page<Lancamento> lancamentos = this.lancamentoService.findByFuncionarioId(funcionarioId, pageRequest);
		Page<LancamentoResource> lancamentoResource = lancamentos
				.map(lancamento -> new LancamentoResource(this.convertLancamentoToDto(lancamento)));

		response.setData(lancamentoResource);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param id
	 * @return ResponseEntity<Response<LancamentoResource>>
	 */
	@GetMapping("/{id}")
	public ResponseEntity<Response<LancamentoResource>> findById(@PathVariable("id") Long id) {
		log.info("Buscando lancamento por ID: {}", id);
		Response<LancamentoResource> response = new Response<>();
		Optional<Lancamento> lancamento = this.lancamentoService.findById(id);

		if (!lancamento.isPresent()) {
			log.info("Lancamento nao encontrado para o ID: {}", id);
			response.getErrors().add("Lancamento nao encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		LancamentoResource lancamentoResource = new LancamentoResource(this.convertLancamentoToDto(lancamento.get()));

		response.setData(lancamentoResource);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoResource>>
	 * @throws ParseException
	 */
	@PostMapping
	public ResponseEntity<Response<LancamentoResource>> add(@Valid @RequestBody LancamentoDto lancamentoDto,
			BindingResult result) throws ParseException {
		log.info("Adicionando lancamento: {}", lancamentoDto.toString());
		Response<LancamentoResource> response = new Response<>();
		validateData(lancamentoDto, result);
		Lancamento lancamento = this.convertDtoToLancamento(lancamentoDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persist(lancamento);
		LancamentoResource lancamentoResource = new LancamentoResource(this.convertLancamentoToDto(lancamento));

		response.setData(lancamentoResource);
		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param id
	 * @param lancamentoDto
	 * @param result
	 * @return ResponseEntity<Response<LancamentoResource>>
	 * @throws ParseException
	 */
	@PatchMapping(value = "/{id}")
	public ResponseEntity<Response<LancamentoResource>> update(@PathVariable("id") Long id,
			@Valid @RequestBody LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		log.info("Adicionando lancamento: {}", lancamentoDto.toString());
		Response<LancamentoResource> response = new Response<>();
		validateData(lancamentoDto, result);
		lancamentoDto.setId(Optional.of(id));
		Lancamento lancamento = this.convertDtoToLancamento(lancamentoDto, result);

		if (result.hasErrors()) {
			log.error("Erro validando lancamento: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		lancamento = this.lancamentoService.persist(lancamento);
		LancamentoResource lancamentoResource = new LancamentoResource(this.convertLancamentoToDto(lancamento));

		response.setData(lancamentoResource);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param id
	 * @return ResponseEntity<Response<String>>
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Response<String>> remove(@PathVariable("id") Long id) {
		log.info("Removendo lancamento: {}", id);
		Response<String> response = new Response<>();

		Optional<Lancamento> lancamento = this.lancamentoService.findById(id);

		if (!lancamento.isPresent()) {
			log.info("Erro ao remover lancamento ID: {} nao encontrado.", id);
			response.getErrors().add("Erro ao remover lancamento. Registro nao encontrado para o id " + id);
			return ResponseEntity.badRequest().body(response);
		}

		this.lancamentoService.remove(id);

		return ResponseEntity.ok(response);
	}

	/**
	 * 
	 * @param lancamentoDto
	 * @param result
	 */
	private void validateData(LancamentoDto lancamentoDto, BindingResult result) {
		if (lancamentoDto.getFuncionarioId() == null) {
			result.addError(new ObjectError("funcionario", "Funcionario nao informado."));
			return;
		}

		log.info("Validando funcionario id {}: ", lancamentoDto.getFuncionarioId());
		Optional<Funcionario> funcionario = this.funcionarioService.findById(lancamentoDto.getFuncionarioId());
		if (!funcionario.isPresent()) {
			result.addError(new ObjectError("funcionario", "Funcionario nao encontrado. ID inexistente."));
		}
	}

	/**
	 * 
	 * @param lancamento
	 * @return LancamentoDto
	 */
	private LancamentoDto convertLancamentoToDto(Lancamento lancamento) {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(Optional.of(lancamento.getId()));
		lancamentoDto.setData(this.dateFormat.format(lancamento.getData()));
		lancamentoDto.setTipo(lancamento.getTipo().toString());
		lancamentoDto.setDescricao(lancamento.getDescricao());
		lancamentoDto.setLocalizacao(lancamento.getLocalizacao());
		lancamentoDto.setFuncionarioId(lancamento.getFuncionario().getId());

		return lancamentoDto;
	}

	/**
	 * 
	 * @param lancamentoDto
	 * @param result
	 * @return Lancamento
	 * @throws ParseException
	 */
	private Lancamento convertDtoToLancamento(LancamentoDto lancamentoDto, BindingResult result) throws ParseException {
		Lancamento lancamento = new Lancamento();

		if (lancamentoDto.getId().isPresent()) {
			Optional<Lancamento> lanc = this.lancamentoService.findById(lancamentoDto.getId().get());
			if (lanc.isPresent()) {
				lancamento = lanc.get();
			} else {
				result.addError(new ObjectError("lancamento", "Lancamento nao encontrado."));
			}
		} else {
			lancamento.setFuncionario(new Funcionario());
			lancamento.getFuncionario().setId(lancamentoDto.getFuncionarioId());
		}

		lancamento.setDescricao(lancamentoDto.getDescricao());
		lancamento.setLocalizacao(lancamentoDto.getLocalizacao());
		lancamento.setData(this.dateFormat.parse(lancamentoDto.getData()));

		if (EnumUtils.isValidEnum(TipoEnum.class, lancamentoDto.getTipo())) {
			lancamento.setTipo(TipoEnum.valueOf(lancamentoDto.getTipo()));
		} else {
			result.addError(new ObjectError("tipo", "Tipo invalido."));
		}

		return lancamento;
	}
}
