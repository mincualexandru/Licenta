package com.web.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Measurement;

@Repository("measurementDao")
public interface MeasurementDao extends CrudRepository<Measurement, Integer> {

	@Override
	Set<Measurement> findAll();

	List<Measurement> findAllByUserDeviceUserDeviceId(Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceId(String bodyFatPercentage, Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(String name, Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	Optional<Measurement> findByName(String hkquantitytypeidentifierheight);

	Set<Measurement> findAllByName(String scaleTypeMeasurement);

	@Query(value = "select * from ( select * from measurements where name = ?1 and user_device_id = ?2 order by measurement_id desc limit 3) measurements order by measurement_id asc;", nativeQuery = true)
	Set<Measurement> findLast3ByNameAndUserDeviceId(String name, Integer userDeviceId);

	Measurement findByEndDate(Timestamp timestamp);
}