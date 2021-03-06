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

import com.web.dao.ExerciseDao;
import com.web.dao.MeasurementDao;
import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.service.MeasurementService;

@Service("measurementService")
public class MeasurementServiceImpl implements MeasurementService {

	@Autowired
	private MeasurementDao measurementDao;

	@Autowired
	private ExerciseDao exerciseDao;

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
	public void buildMap(Map<String, Float> chartMap, Set<Measurement> measurementsBetweenTimestamps) {
		for (Measurement measurement : measurementsBetweenTimestamps) {
			LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String startDateFormatter = startDate.format(formatter);
			chartMap.put(startDateFormatter, measurement.getValue());
		}
	}

	@Override
	public Set<Measurement> findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(String name, Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return measurementDao.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(name, userDeviceId,
				timestampStartDate, timestampEndDate);
	}

	@Override
	public Optional<Measurement> findByName(String hkquantitytypeidentifierheight) {
		return measurementDao.findByName(hkquantitytypeidentifierheight);
	}

	@Override
	public Set<Measurement> findAllByName(String scaleTypeMeasurement) {
		return measurementDao.findAllByName(scaleTypeMeasurement);
	}

	@Override
	public Set<Measurement> findLast3ByNameAndUserDeviceId(String name, Integer userDeviceId) {
		return measurementDao.findLast3ByNameAndUserDeviceId(name, userDeviceId);
	}

	@Override
	public Measurement findByEndDate(Timestamp timestamp) {
		return measurementDao.findByEndDate(timestamp);
	}

	@Override
	public void deleteAllByUserDeviceId(Integer userDeviceId) {
		measurementDao.deleteAllByUserDeviceId(userDeviceId);
	}

	@Override
	public Optional<Measurement> findByNameAndUserDeviceUserDeviceId(String scaleTypeMeasurement,
			Integer userDeviceId) {
		return measurementDao.findByNameAndUserDeviceUserDeviceId(scaleTypeMeasurement, userDeviceId);
	}

	@Override
	public Set<Measurement> findAllByUserDeviceUserDeviceIdAndFromXml(Integer userDeviceId, boolean fromXml) {
		return measurementDao.findAllByUserDeviceUserDeviceIdAndFromXmlAndEndDate(userDeviceId, fromXml);
	}

	@Override
	public Measurement findByUserDeviceIdAndNameAndEndDate(Integer userDeviceId, String string) {
		return measurementDao.findByUserDeviceIdAndNameAndEndDate(userDeviceId, string);
	}

	@Override
	public Set<Timestamp> findStartDateForMeasurementsBy() {
		return measurementDao.findStartDateForMeasurementsBy();
	}

	@Override
	public Optional<Measurement> findByStartDateAndUserDeviceUserDeviceIdAndName(Timestamp localDate,
			Integer userDeviceId, String string) {
		return measurementDao.findByStartDateAndUserDeviceUserDeviceIdAndName(localDate, userDeviceId, string);
	}

	@Override
	public Set<Measurement> findByStartDateBetween(Timestamp start, Timestamp end) {
		return measurementDao.findByStartDateBetween(start, end);
	}

	@Override
	public boolean checkIfMeasurementIsPresent(Integer userDeviceId, String string) {
		return measurementDao.findByNameAndUserDeviceUserDeviceId(string, userDeviceId).isPresent();
	}

	@Override
	public Optional<Measurement> findTopByOrderByStartDateDesc() {
		return measurementDao.findTopByOrderByStartDateDesc();
	}

	@Override
	public void deleteByUserDeviceUserDeviceIdAndMeasurementId(Integer userDeviceId, Integer measurementId) {
		measurementDao.deleteByUserDeviceUserDeviceIdAndMeasurementId(userDeviceId, measurementId);
	}

	@Override
	public Set<Measurement> findAllByUserDeviceUserDeviceIdAndStartDateBetween(Integer userDeviceId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return measurementDao.findAllByUserDeviceUserDeviceIdAndStartDateBetween(userDeviceId, timestampStartDate,
				timestampEndDate);
	}

	@Override
	public void saveAll(Set<Measurement> measurements) {
		measurementDao.saveAll(measurements);
	}

	@Override
	public void createMeasurement(Integer exerciseId, Measurement measurement, UserDevice userDevice,
			String nameOfMeasurement, String unitOfMeasurement) {
		measurement.setStartDate(new Timestamp(System.currentTimeMillis()));
		measurement.setEndDate(null);
		measurement.setName(nameOfMeasurement);
		measurement.setUnitOfMeasurement(unitOfMeasurement);
		measurement.setValue(exerciseDao.findById(exerciseId).get().getCaloriesBurned());
		measurement.setUserDevice(userDevice);
		measurement.setFromXml(false);
	}

	@Override
	public Measurement createMeasurementForHeightOrWeight(Float value, UserDevice userDevice, String name,
			String unitOfMeasurement) {
		Measurement measurementForHeightOrWeight = new Measurement();
		measurementForHeightOrWeight.setStartDate(new Timestamp(System.currentTimeMillis()));
		measurementForHeightOrWeight.setEndDate(null);
		measurementForHeightOrWeight.setFromXml(false);
		measurementForHeightOrWeight.setName(name);
		measurementForHeightOrWeight.setUnitOfMeasurement(unitOfMeasurement);
		measurementForHeightOrWeight.setValue(value);
		measurementForHeightOrWeight.setUserDevice(userDevice);
		return measurementForHeightOrWeight;
	}
}
