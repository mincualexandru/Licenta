package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.TypeMeasurementDao;
import com.web.model.TypeMeasurement;
import com.web.service.TypeMeasurementService;

@Service("typeMeasurementService")
public class TypeMeasurementServiceImpl implements TypeMeasurementService {

	@Autowired
	private TypeMeasurementDao typeMeasurementDao;

	@Override
	public void save(TypeMeasurement typeMeasurement) {
		typeMeasurementDao.save(typeMeasurement);
	}

	@Override
	public Optional<TypeMeasurement> findById(Integer id) {
		return typeMeasurementDao.findById(id);
	}

	@Override
	public void delete(TypeMeasurement typeMeasurement) {
		typeMeasurementDao.delete(typeMeasurement);
	}

	@Override
	public void deleteById(Integer id) {
		typeMeasurementDao.deleteById(id);
	}

	@Override
	public Set<TypeMeasurement> findAll() {
		return typeMeasurementDao.findAll();
	}

	@Override
	public TypeMeasurement findByType(String chartOption) {
		return typeMeasurementDao.findByType(chartOption);
	}

}
