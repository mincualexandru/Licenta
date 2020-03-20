package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.TypeMeasurement;

@Repository("typeMeasurementDao")
public interface TypeMeasurementDao extends CrudRepository<TypeMeasurement, Integer> {

	@Override
	Set<TypeMeasurement> findAll();

	TypeMeasurement findByType(String chartOption);
}