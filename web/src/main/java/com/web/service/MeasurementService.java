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

	void buildMap(Map<String, Float> chartMap, Set<Measurement> bodyFatPercentageMeasurements);

	Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);
}
