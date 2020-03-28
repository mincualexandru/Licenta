package com.web.serviceimpl;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.DeviceDao;
import com.web.model.Device;
import com.web.model.TypeMeasurement;
import com.web.service.DeviceService;

@Service("deviceService")
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	private DeviceDao deviceDao;

	@Override
	public void save(Device device) {
		deviceDao.save(device);
	}

	@Override
	public Optional<Device> findById(Integer id) {
		return deviceDao.findById(id);
	}

	@Override
	public void delete(Device device) {
		deviceDao.delete(device);
	}

	@Override
	public void deleteById(Integer id) {
		deviceDao.deleteById(id);
	}

	@Override
	public Set<Device> findAll() {
		return deviceDao.findAll();
	}

	@Override
	public Device findOneDeviceRandomByName(String string) {
		return deviceDao.findOneDeviceRandomByName(string);
	}

	@Override
	public Integer countByName(String string) {
		return deviceDao.countByName(string);
	}

	@Override
	public void deleteByDeviceId(Integer deviceId) {
		deviceDao.deleteByDeviceId(deviceId);
	}

	@Override
	public void createDevice(Device newDevice, Set<TypeMeasurement> typeMeasurements, String nameOfDevice,
			Integer price) {
		newDevice.setCompany("Xiaomi");
		newDevice.setName(nameOfDevice);
		newDevice.setSerialNumber(generateRandomSerialNumber());
		newDevice.setPrice(price);
		newDevice.setTypeMeasurements(typeMeasurements);
	}

	@Override
	public String generateRandomSerialNumber() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;

	}

	@Override
	public Optional<Device> findByName(String string) {
		return deviceDao.findByName(string);
	}

}
