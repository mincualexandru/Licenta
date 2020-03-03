package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.UserDevice;

public interface UserDeviceService {
	void save(UserDevice userDevice);

	Optional<UserDevice> findById(Integer id);

	void delete(UserDevice userDevice);

	void deleteById(Integer id);

	Set<UserDevice> findAll();

	Set<UserDevice> findAllByBoughtAndUserAccountId(boolean b, Integer userId);
}
