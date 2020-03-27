package com.web.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.web.model.Measurement;

public interface MeasurementService {

	void save(Measurement measurement);

	Optional<Measurement> findById(Integer id);

	void delete(Measurement measurement);

	void deleteById(Integer id);

	Set<Measurement> findAll();

	List<Measurement> findAllByUserDeviceUserDeviceId(Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceId(String bodyFatPercentage, Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(String name, Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	void buildMap(Map<String, Float> chartMap, Set<Measurement> measurementsBetweenTimestamps);

	Optional<Measurement> findByName(String hkquantitytypeidentifierheight);

	Set<Measurement> findAllByName(String scaleTypeMeasurement);

	Set<Measurement> findLast3ByNameAndUserDeviceId(String name, Integer userDeviceId);

	Measurement findByEndDate(Timestamp timestamp);

	void deleteAllByUserDeviceId(Integer userDeviceId);

	Optional<Measurement> findByNameAndUserDeviceUserDeviceId(String scaleTypeMeasurement, Integer userDeviceId);

	Set<Measurement> findAllByUserDeviceUserDeviceIdAndFromXml(Integer userDeviceId, boolean b);
}
