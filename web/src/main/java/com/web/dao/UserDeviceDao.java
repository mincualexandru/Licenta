package com.web.dao;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.web.model.UserDevice;

@Repository("userDeviceDao")
public interface UserDeviceDao extends CrudRepository<UserDevice, Integer> {

	@Override
	Set<UserDevice> findAll();

	Set<UserDevice> findAllByBoughtAndUserAccountId(boolean b, Integer userId);

	UserDevice findByUserAccountIdAndDeviceDeviceId(Integer userId, Integer productId);
}
