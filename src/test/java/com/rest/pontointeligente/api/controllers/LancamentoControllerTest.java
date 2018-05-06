package com.rest.pontointeligente.api.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.rest.pontointeligente.api.dtos.LancamentoDto;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.entities.Lancamento;
import com.rest.pontointeligente.api.enums.TipoEnum;
import com.rest.pontointeligente.api.services.FuncionarioService;
import com.rest.pontointeligente.api.services.LancamentoService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class LancamentoControllerTest {

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> httpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private LancamentoService lancamentoService;

	@MockBean
	private FuncionarioService funcionarioService;

	private static final String URL_BASE = "/api/lancamentos/";
	private static final Long ID_FUNCIONARIO = 1L;
	private static final Long ID_LANCAMENTO = 1L;
	private static final String TIPO = TipoEnum.INICIO_TRABALHO.name();
	private static final Date DATA = new Date();

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	@SuppressWarnings("unchecked")
	public void setConverters(HttpMessageConverter<?>[] converters) {
		this.httpMessageConverter = (HttpMessageConverter<Object>) Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);
		assertNotNull("the JSON message converter must not be null", this.httpMessageConverter);
	}

	@Before
	public void setUp() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void testAdd() throws Exception {
		Lancamento lancamento = createLancamento();
		BDDMockito.given(this.funcionarioService.findById(Mockito.anyLong()))
				.willReturn(Optional.of(new Funcionario()));
		BDDMockito.given(this.lancamentoService.persist(Mockito.any(Lancamento.class))).willReturn(lancamento);
		String lancamentoJson = json(createLancamentoDto());

		mockMvc.perform(post(URL_BASE).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(lancamentoJson)).andExpect(status().isOk())
				.andExpect(jsonPath("$.data.lancamento.id").value(ID_LANCAMENTO))
				.andExpect(jsonPath("$.data.lancamento.tipo", is(TIPO)))
				.andExpect(jsonPath("$.data.lancamento.data").value(this.dateFormat.format(DATA)))
				.andExpect(jsonPath("$.data.lancamento.funcionarioId").value(ID_FUNCIONARIO))
				.andExpect(jsonPath("$.errors").isEmpty());
	}

	@Test
	public void testAddInvalidFuncionarioId() throws Exception {
		BDDMockito.given(this.funcionarioService.findById(Mockito.anyLong())).willReturn(Optional.empty());
		String lancamentoJson = json(createLancamentoDto());

		mockMvc.perform(post(URL_BASE).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(lancamentoJson)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").value("Funcionario nao encontrado. ID inexistente."));
	}

	@Test
	@WithMockUser(username = "ceo@paavocorp.com", roles = {"ADMIN"})
	public void testRemove() throws Exception {
		BDDMockito.given(this.lancamentoService.findById(Mockito.anyLong()))
				.willReturn(Optional.of(new Lancamento()));
		mockMvc.perform(delete(URL_BASE + ID_LANCAMENTO).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	private Lancamento createLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(ID_LANCAMENTO);
		lancamento.setData(DATA);
		lancamento.setTipo(TipoEnum.valueOf(TIPO));
		lancamento.setFuncionario(new Funcionario());
		lancamento.getFuncionario().setId(ID_FUNCIONARIO);
		return lancamento;
	}

	private LancamentoDto createLancamentoDto() {
		LancamentoDto lancamentoDto = new LancamentoDto();
		lancamentoDto.setId(null);
		lancamentoDto.setData(this.dateFormat.format(DATA));
		lancamentoDto.setTipo(TIPO);
		lancamentoDto.setFuncionarioId(ID_FUNCIONARIO);
		return lancamentoDto;
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.httpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
