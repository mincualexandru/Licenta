package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.web.dao.MeasurementDao;
import com.web.dao.UserDeviceDao;
import com.web.model.Account;
import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.service.UserDeviceService;
import com.web.utils.ScaleTypeMeasurement;

@Service("userDeviceService")
public class UserDeviceServiceImpl implements UserDeviceService {

	@Autowired
	private UserDeviceDao userDeviceDao;

	@Autowired
	private MeasurementDao measurementDao;

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

	@Override
	public UserDevice findByUserAccountIdAndDeviceDeviceId(Integer userId, Integer productId) {
		return userDeviceDao.findByUserAccountIdAndDeviceDeviceId(userId, productId);
	}

	@Override
	public void deleteByDeviceIdAndUserId(Integer deviceId, Integer userId) {
		userDeviceDao.deleteByDeviceIdAndUserId(deviceId, userId);
	}

	@Override
	public void getHeightAndWeight(Model model, UserDevice userDevice) {
		Float height;
		Float weight;
		if (userDevice.getDevice().getName().equals("Cantar Inteligent")
				|| userDevice.getDevice().getName().equals("Fit Buddy")) {
			Optional<Measurement> heightMeasurement = measurementDao.findByNameAndUserDeviceUserDeviceId(
					ScaleTypeMeasurement.HEIGHT.getScaleTypeMeasurement(), userDevice.getUserDeviceId());
			Optional<Measurement> weightMeasurement = measurementDao
					.findAllByNameAndUserDeviceUserDeviceId(ScaleTypeMeasurement.MASS.getScaleTypeMeasurement(),
							userDevice.getUserDeviceId())
					.stream().reduce((prev, next) -> next);
			if (heightMeasurement.isPresent() && weightMeasurement.isPresent()) {
				height = heightMeasurement.get().getValue();
				model.addAttribute("height", Math.round(height));
				weight = weightMeasurement.get().getValue();
				model.addAttribute("weight", Math.round(weight));
			}
		}
	}

	@Override
	public Optional<UserDevice> findByDeviceNameAndUserAccountId(String string, Integer accountId) {
		return userDeviceDao.findByDeviceNameAndUserAccountId(string, accountId);
	}

	@Override
	public boolean checkIfDeviceIsPresent(Account account, String nameOfDevice) {
		return userDeviceDao.findByDeviceNameAndUserAccountId(nameOfDevice, account.getAccountId()).isPresent();
	}

	@Override
	public Optional<UserDevice> findTopByOrderByDateOfPurchaseDesc() {
		return userDeviceDao.findTopByOrderByDateOfPurchaseDesc();
	}
}
