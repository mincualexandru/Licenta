package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.UserDeviceDao;
import com.web.model.UserDevice;

@Service("userDeviceService")
public class UserDeviceServiceImpl implements UserDeviceService {

	@Autowired
	private UserDeviceDao userDeviceDao;

	@Override
	public void save(UserDevice userDevice) {
		userDeviceDao.save(userDevice);
	}

	@Override
	public Optional<UserDevice> findById(Integer id) {
		return userDeviceDao.findById(id);
	}

	@Override
	public void delete(UserDevice userDevice) {
		userDeviceDao.delete(userDevice);
	}

	@Override
	public void deleteById(Integer id) {
		userDeviceDao.deleteById(id);
	}

	@Override
	public Set<UserDevice> findAll() {
		return userDeviceDao.findAll();
	}

	@Override
	public Set<UserDevice> findAllByBoughtAndUserAccountId(boolean b, Integer userId) {
		return userDeviceDao.findAllByBoughtAndUserAccountId(b, userId);
	}
}
