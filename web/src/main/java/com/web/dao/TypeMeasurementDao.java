package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.TypeMeasurement;

@Repository("typeMeasurementDao")
public interface TypeMeasurementDao extends CrudRepository<TypeMeasurement, Integer> {

	@Override
	Set<TypeMeasurement> findAll();

	TypeMeasurement findByType(String chartOption);

	@Query(value = "select * from types_measurements where type = 'HKQuantityTypeIdentifierActiveEnergyBurned' or type = 'HKQuantityTypeIdentifierActiveEnergyAccumulated' or type = 'HKQuantityTypeIdentifierBodyMass' or type = 'HKQuantityTypeIdentifierHeight';", nativeQuery = true)
	Set<TypeMeasurement> findTypeMeasurementsForFitBuddy();

	@Transactional
	@Modifying
	@Query(value = "delete from device_type_measurement where device_id = ?1", nativeQuery = true)
	void deleteAllByDeviceId(Integer deviceId);
}