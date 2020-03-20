package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.DeviceDao;
import com.web.model.Device;
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

}
