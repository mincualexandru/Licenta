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

	@Transactional
	@Modifying
	@Query(value = "delete from device_type_measurement where device_id = ?1 and type_measurement_id = ?2", nativeQuery = true)
	void deleteByDeviceIdAndTypeMeasurementId(Integer deviceId, Integer typeMeasurementId);

	@Query(value = "select * from types_measurements where type = 'HKQuantityTypeIdentifierActiveEnergyBurned' or type = 'HKQuantityTypeIdentifierActiveEnergyAccumulated' or type = 'HKQuantityTypeIdentifierBodyMass' or type = 'HKQuantityTypeIdentifierHeight';", nativeQuery = true)
	Set<TypeMeasurement> findTypeMeasurementsForFitBuddy();
}