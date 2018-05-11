package com.rest.pontointeligente.api.controllers;

import java.net.URI;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rest.pontointeligente.api.dtos.PersonDto;
import com.rest.pontointeligente.api.entities.Person;

@RestController
@RequestMapping("/api/person")
public class PersonController {
	
	@Autowired
	private ModelMapper mapper;

	@GetMapping
	public String greeting(@RequestParam(name = "name", defaultValue = "Stranger") String name) {
		return "Greetings ".concat(name);
	}

	@PostMapping
	public ResponseEntity<PersonDto> add(@RequestBody PersonDto person) throws Exception {
		Person p = mapper.map(person, Person.class);
		return ResponseEntity.created(new URI("/")).body(new PersonDto());
	}

}
