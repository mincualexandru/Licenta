package com.web.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.Measurement;

@Repository("measurementDao")
public interface MeasurementDao extends CrudRepository<Measurement, Integer> {

	@Override
	Set<Measurement> findAll();

	List<Measurement> findAllByUserDeviceUserDeviceId(Integer userDeviceId);

	Set<Measurement> findAllByNameAndUserDeviceUserDeviceId(String bodyFatPercentage, Integer userDeviceId);

	Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate);
}