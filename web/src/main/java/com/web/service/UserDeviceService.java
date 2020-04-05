package com.web.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.ui.Model;

import com.web.model.Account;
import com.web.model.UserDevice;

public interface UserDeviceService {
	void save(UserDevice userDevice);

	Optional<UserDevice> findById(Integer id);

	void delete(UserDevice userDevice);

	void deleteById(Integer id);

	Set<UserDevice> findAll();

	Set<UserDevice> findAllByBoughtAndUserAccountId(boolean b, Integer userId);

	UserDevice findByUserAccountIdAndDeviceDeviceId(Integer userId, Integer productId);

	void deleteByDeviceIdAndUserId(Integer deviceId, Integer userId);

	void getHeightAndWeight(Model model, UserDevice userDevice);

	Optional<UserDevice> findByDeviceNameAndUserAccountId(String string, Integer accountId);

	boolean checkIfDeviceIsPresent(Account account, String nameOfDevice);

	Optional<UserDevice> findTopByOrderByDateOfPurchaseDesc();
}
