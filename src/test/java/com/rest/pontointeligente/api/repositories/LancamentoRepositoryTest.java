package com.rest.pontointeligente.api.repositories;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.rest.pontointeligente.api.entities.Empresa;
import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.entities.Lancamento;
import com.rest.pontointeligente.api.enums.PerfilEnum;
import com.rest.pontointeligente.api.enums.TipoEnum;
import com.rest.pontointeligente.api.utils.PasswordUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired
	private FuncionarioRepository funcionarioRepository;

	@Autowired
	private LancamentoRepository lancamentoRepository;

	private Long funcionarioId;

	@Before
	public void setUp() throws Exception {
		Empresa empresa = this.empresaRepository.save(criarEmpresaFicticia());
		Funcionario funcionario = this.funcionarioRepository.save(criarFuncionarioFicticio(empresa));
		this.funcionarioId = funcionario.getId();

		this.lancamentoRepository.save(criarLancamentoFicticio(funcionario));
		this.lancamentoRepository.save(criarLancamentoFicticio(funcionario));
	}

	@After
	public final void tearDown() {
		this.empresaRepository.deleteAll();
		this.funcionarioRepository.deleteAll();
		this.lancamentoRepository.deleteAll();
	}

	@Test
	public void testFindByFuncionarioId() {
		List<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId);
		assertEquals(2, lancamentos.size());
	}

	@Test
	public void testFindByFuncionarioIdPageable() {
		PageRequest page = PageRequest.of(0, 10);
		Page<Lancamento> lancamentos = this.lancamentoRepository.findByFuncionarioId(funcionarioId, page);
		assertEquals(2, lancamentos.getTotalElements());
	}

	private Lancamento criarLancamentoFicticio(Funcionario funcionario) {
		Lancamento lancamento = new Lancamento();
		lancamento.setData(new Date());
		lancamento.setTipo(TipoEnum.INICIO_ALMOCO);
		lancamento.setFuncionario(funcionario);
		return lancamento;
	}

	private Funcionario criarFuncionarioFicticio(Empresa empresa) throws NoSuchAlgorithmException {
		Funcionario funcionario = new Funcionario();
		funcionario.setNome("Lorenzo Lamas");
		funcionario.setPerfil(PerfilEnum.ROLE_USUARIO);
		funcionario.setSenha(PasswordUtils.gerarBCript("123456"));
		funcionario.setCpf("70386298084");
		funcionario.setEmail("email@email.com");
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
