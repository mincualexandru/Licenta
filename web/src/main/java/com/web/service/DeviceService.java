package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Device;

public interface DeviceService {
	void save(Device device);

	Optional<Device> findById(Integer id);

	void delete(Device device);

	void deleteById(Integer id);

	Set<Device> findAll();

	Device findOneDeviceRandomByName(String string);

	Integer countByName(String string);
}
