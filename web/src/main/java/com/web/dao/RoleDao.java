package com.web.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Role;

@Repository("roleDao")
public interface RoleDao extends CrudRepository<Role, Integer> {

	@Override
	Set<Role> findAll();

	Optional<Role> findByName(String chooseRoleName);

}
