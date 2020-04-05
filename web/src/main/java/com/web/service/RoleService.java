package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Role;

public interface RoleService {

	void save(Role role);

	Optional<Role> findById(Integer id);

	void delete(Role role);

	void deleteById(Integer id);

	Set<Role> findAll();

	Optional<Role> findByName(String string);

}
