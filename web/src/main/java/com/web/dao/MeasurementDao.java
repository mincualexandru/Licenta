package com.web.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.Measurement;

@Repository("measurementDao")
public interface MeasurementDao extends CrudRepository<Measurement, Integer> {

	@Override
	Set<Measurement> findAll();

	List<Measurement> findAllByUserDeviceUserDeviceId(Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceId(String bodyFatPercentage, Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(String name, Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	Optional<Measurement> findByName(String hkquantitytypeidentifierheight);

	Set<Measurement> findAllByName(String scaleTypeMeasurement);

	@Query(value = "select * from ( select * from measurements where name = ?1 and user_device_id = ?2 order by measurement_id desc limit 3) measurements order by measurement_id asc;", nativeQuery = true)
	Set<Measurement> findLast3ByNameAndUserDeviceId(String name, Integer userDeviceId);

	Measurement findByEndDate(Timestamp timestamp);

	@Transactional
	@Modifying
	@Query(value = "delete from measurements where user_device_id = ?1 and from_xml = true", nativeQuery = true)
	void deleteAllByUserDeviceId(Integer userDeviceId);

	Optional<Measurement> findByNameAndUserDeviceUserDeviceId(String scaleTypeMeasurement, Integer userDeviceId);

	@Query(value = "select * from measurements where user_device_id is NULL and from_xml = 0;", nativeQuery = true)
	Set<Measurement> findAllByUserDeviceUserDeviceIdAndFromXmlAndEndDate(Integer userDeviceId, boolean fromXml);

	@Query(value = "select * from measurements where user_device_id = ?1 and name = ?2 and end_date is null;", nativeQuery = true)
	Measurement findByUserDeviceIdAndNameAndEndDate(Integer userDeviceId, String string);

	@Query(value = "select start_date,user_device_id from measurements where user_device_id = 14 or user_device_id = 18 and name ='HKQuantityTypeIdentifierActiveEnergyAccumulated' or name = 'HKQuantityTypeIdentifierActiveEnergyBurned';", nativeQuery = true)
	Set<Timestamp> findStartDateForMeasurementsBy();

	Optional<Measurement> findByStartDateAndUserDeviceUserDeviceIdAndName(Timestamp localDate, Integer userDeviceId,
			String string);

	Set<Measurement> findByStartDateBetween(Timestamp start, Timestamp end);

	Optional<Measurement> findTopByOrderByStartDateDesc();

	@Transactional
	@Modifying
	@Query(value = "delete from measurements where user_device_id = ?1 and measurement_id = ?2", nativeQuery = true)
	void deleteByUserDeviceUserDeviceIdAndMeasurementId(Integer userDeviceId, Integer measurementId);
}