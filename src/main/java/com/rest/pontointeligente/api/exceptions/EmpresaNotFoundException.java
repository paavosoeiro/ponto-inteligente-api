package com.rest.pontointeligente.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmpresaNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1588326881557877488L;

	public EmpresaNotFoundException(Long id) {
		super("could not found empresa id: " + id);
	}

}
