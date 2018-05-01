package com.rest.pontointeligente.api.utils;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtilsTest {

	private static final String SENHA = "123456";
	private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	@Test
	public void testGerarBCript() throws NoSuchAlgorithmException {
		assertNull(PasswordUtils.gerarBCript(null));
	}
	
	@Test
	public void testGerarSenhaNull() throws NoSuchAlgorithmException {
		String hash = PasswordUtils.gerarBCript(SENHA);
		assertTrue(bCryptPasswordEncoder.matches(SENHA, hash));
	}

}
