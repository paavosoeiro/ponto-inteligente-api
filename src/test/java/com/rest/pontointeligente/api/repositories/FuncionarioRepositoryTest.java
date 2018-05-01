package com.rest.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.NoSuchAlgorithmException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.enums.PerfilEnum;
import com.rest.pontointeligente.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FuncionarioRepositoryTest {

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	private static final String EMAIL = "email@email.com";
	private static final String CPF = "70386298084";

	@Before
	public void setUp() throws Exception {
		Empresa empresa = this.empresaRepository.save(criarEmpresaFicticia());
		this.funcionarioRepository.save(criarFuncionarioFicticio(empresa));
	}

	@After
	public final void tearDown() {
		this.empresaRepository.deleteAll();
		this.funcionarioRepository.deleteAll();
	}

	@Test
	public void testFindByCpf() {
		Funcionario funcionario = this.funcionarioRepository.findByCpf(CPF);
		assertEquals(CPF, funcionario.getCpf());
	}

	@Test
	public void testFindByEmail() {
		Funcionario funcionario = this.funcionarioRepository.findByEmail(EMAIL);
		assertEquals(EMAIL, funcionario.getEmail());
	}

	@Test
	public void testFindByCpfOrEmail() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, EMAIL);
		assertNotNull(funcionario);
	}

	@Test
	public void testFindByCpfOrEmailEmailInvalid() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail(CPF, "email@invalid.com");
		assertNotNull(funcionario);
	}

	@Test
	public void testFindByCpfOrEmailCpfInvalid() {
		Funcionario funcionario = this.funcionarioRepository.findByCpfOrEmail("74158995485", EMAIL);
		assertNotNull(funcionario);
	}

	private Funcionario criarFuncionarioFicticio(Empresa empresa) throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Lorenzo Lamas");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCript("123456"));
		funcionario.setCpf(CPF);
		funcionario.setEmail(EMAIL);
		funcionario.setEmpresa(empresa);
		return funcionario;
	}

	private Empresa criarEmpresaFicticia() {
		Empresa empresa = new Empresa();
		empresa.setRazaoSocial("Empresa Ficticia");
		empresa.setCnpj("16981959000195");
		return empresa;
	}

}
