package com.rest.pontointeligente.api.services;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.repositories.FuncionarioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioServiceTest {

	@MockBean
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private FuncionarioService funcionarioService;

	@Before
	public void setUp() {
		BDDMockito.given(this.funcionarioRepository.save(Mockito.any(Funcionario.class))).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findById(Mockito.anyLong()))
				.willReturn(Optional.ofNullable(new Funcionario()));
		BDDMockito.given(this.funcionarioRepository.findByCpf(Mockito.anyString())).willReturn(new Funcionario());
		BDDMockito.given(this.funcionarioRepository.findByEmail(Mockito.anyString())).willReturn(new Funcionario());
	}

	@Test
	public void testPersist() {
		Funcionario funcionario = this.funcionarioService.persist(new Funcionario());
		assertNotNull(funcionario);
	}

	@Test
	public void testFindByCPf() {
		Optional<Funcionario> funcionario = this.funcionarioService.findByCPf("32703259085");
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testFindByEmail() {
		Optional<Funcionario> funcionario = this.funcionarioService.findByEmail("email@email.com");
		assertTrue(funcionario.isPresent());
	}

	@Test
	public void testFindById() {
		Optional<Funcionario> funcionario = this.funcionarioService.findById(1L);
		assertTrue(funcionario.isPresent());
	}

}
