package com.web.serviceimpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.MeasurementDao;
import com.web.model.Measurement;
import com.web.service.MeasurementService;

@Service("measurementService")
public class MeasurementServiceImpl implements MeasurementService {

	@Autowired
	private MeasurementDao measurementDao;

	@Override
	public void save(Measurement measurement) {
		measurementDao.save(measurement);
	}

	@Override
	public Optional<Measurement> findById(Integer id) {
		return measurementDao.findById(id);
	}

	@Override
	public void delete(Measurement measurement) {
		measurementDao.delete(measurement);
	}

	@Override
	public void deleteById(Integer id) {
		measurementDao.deleteById(id);
	}

	@Override
	public Set<Measurement> findAll() {
		return measurementDao.findAll();
	}

	@Override
	public List<Measurement> findAllByUserDeviceUserDeviceId(Integer userDeviceId) {
		return measurementDao.findAllByUserDeviceUserDeviceId(userDeviceId);
	}

	@Override
	public Set<Measurement> findAllByNameAndUserDeviceUserDeviceId(String bodyFatPercentage, Integer userDeviceId) {
		return measurementDao.findAllByNameAndUserDeviceUserDeviceId(bodyFatPercentage, userDeviceId);
	}

	@Override
	public void buildMap(Map<String, Float> chartMap, Set<Measurement> bodyFatPercentageMeasurements) {
		for (Measurement measurement : bodyFatPercentageMeasurements) {
			LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String startDateFormatter = startDate.format(formatter);
			chartMap.put(startDateFormatter, measurement.getValue());
		}
	}

	@Override
	public Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return measurementDao.findAllByUserDeviceUserDeviceIdAndStartDateBetween(userDeviceId, timestampStartDate,
				timestampEndDate);
	}

}