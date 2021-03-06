package com.web.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.web.model.Measurement;
import com.web.model.UserDevice;

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

	Measurement findByUserDeviceIdAndNameAndEndDate(Integer userDeviceId, String string);

	Set<Timestamp> findStartDateForMeasurementsBy();

	Optional<Measurement> findByStartDateAndUserDeviceUserDeviceIdAndName(Timestamp localDate, Integer userDeviceId,
			String string);

	Set<Measurement> findByStartDateBetween(Timestamp start, Timestamp end);

	boolean checkIfMeasurementIsPresent(Integer userDeviceId, String string);

	Optional<Measurement> findTopByOrderByStartDateDesc();

	void deleteByUserDeviceUserDeviceIdAndMeasurementId(Integer userDeviceId, Integer measurementId);

	Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);

	void saveAll(Set<Measurement> measurements);

	void createMeasurement(Integer exerciseId, Measurement measurement, UserDevice userDevice, String nameOfMeasurement,
			String unitOfMeasurement);

	Measurement createMeasurementForHeightOrWeight(Float value, UserDevice userDevice, String name,
			String unitOfMeasurement);
}
