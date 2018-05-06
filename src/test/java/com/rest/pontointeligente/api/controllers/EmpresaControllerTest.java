package com.rest.pontointeligente.api.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
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

import com.rest.pontointeligente.api.dtos.EmpresaDto;
import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.repositories.EmpresaRepository;
import com.rest.pontointeligente.api.services.EmpresaService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
public class EmpresaControllerTest {

	private MediaType contentType = new MediaType(MediaTypes.HAL_JSON.getType(), MediaTypes.HAL_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;

	private HttpMessageConverter<Object> httpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private EmpresaService empresaService;
	
	@Autowired
	private EmpresaRepository empresaRepository;

	private List<Empresa> empresas = new ArrayList<>();

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
		
		empresaRepository.deleteAllInBatch();
		
		this.empresas.add(this.empresaService.persist(createEmpresa("17589329000132", "Empresa 1")));
		this.empresas.add(this.empresaService.persist(createEmpresa("19665691000170", "Empresa 2")));
	}

	@Test
	@WithMockUser
	public void testReadEmpresas() throws Exception {
		this.mockMvc.perform(get("/api/empresas")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$._embedded.empresaResourceList", hasSize(2)))
				.andExpect(jsonPath("$._embedded.empresaResourceList[0].empresa.id",
						is(this.empresas.get(0).getId().intValue())))
				.andExpect(jsonPath("$._embedded.empresaResourceList[0].empresa.razaoSocial",
						is(this.empresas.get(0).getRazaoSocial())));
	}

	@Test
	@WithMockUser
	public void testReadEmpresa() throws Exception {
		this.mockMvc.perform(get("/api/empresas/" + this.empresas.get(0).getId()))
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.empresa.id", is(this.empresas.get(0).getId().intValue())));
	}

	@Test
	@WithMockUser
	public void testAdd() throws Exception {
		String empresaJson = json(createEmpresaDto("21376612000153", "Empresa Teste"));
		this.mockMvc.perform(post("/api/empresas").contentType(contentType).content(empresaJson))
				.andExpect(status().isCreated());
	}

	private Empresa createEmpresa(String cnpj, String razaoSocial) {
		Empresa empresa = new Empresa();
		empresa.setCnpj(cnpj);
		empresa.setRazaoSocial(razaoSocial);
		return empresa;
	}

	private EmpresaDto createEmpresaDto(String cnpj, String razaoSocial) {
		EmpresaDto empresa = new EmpresaDto();
		empresa.setCnpj(cnpj);
		empresa.setRazaoSocial(razaoSocial);
		return empresa;
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.httpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
