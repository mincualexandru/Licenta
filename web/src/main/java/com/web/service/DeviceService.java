package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.Device;
import com.web.model.TypeMeasurement;

public interface DeviceService {
	void save(Device device);

	Optional<Device> findById(Integer id);

	void delete(Device device);

	void deleteById(Integer id);

	Set<Device> findAll();

	Device findOneDeviceRandomByName(String string);

	Integer countByName(String string);

	void deleteByDeviceId(Integer deviceId);

	void createDevice(Device newDevice, Set<TypeMeasurement> typeMeasurements, String nameOfDevice, Integer price);

	String generateRandomSerialNumber();

	Optional<Device> findByName(String string);
}
