package com.rest.pontointeligente.api.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.rest.pontointeligente.api.entities.Funcionario;
import com.rest.pontointeligente.api.enums.PerfilEnum;

public class JwtUserFactory {

	private JwtUserFactory() {
	}

	/**
	 * 
	 * @param funcionario
	 * @return JwtUser
	 */
	public static JwtUser create(Funcionario funcionario) {
		return new JwtUser(funcionario.getId(), funcionario.getEmail(), funcionario.getSenha(),
				mapToGrantedAuthorities(funcionario.getPerfil()));
	}

	/**
	 * 
	 * @param perfilEnum
	 * @return List<GrantedAuthority>
	 */
	private static List<GrantedAuthority> mapToGrantedAuthorities(PerfilEnum perfilEnum) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(perfilEnum.toString()));
		return authorities;
	}

}
