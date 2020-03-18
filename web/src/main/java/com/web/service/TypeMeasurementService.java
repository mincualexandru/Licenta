package com.web.service;

import java.util.Optional;
import java.util.Set;

import com.web.model.TypeMeasurement;

public interface TypeMeasurementService {

	void save(TypeMeasurement typeMeasurement);

	Optional<TypeMeasurement> findById(Integer id);

	void delete(TypeMeasurement typeMeasurement);

	void deleteById(Integer id);

	Set<TypeMeasurement> findAll();

	TypeMeasurement findByType(String chartOption);
}
