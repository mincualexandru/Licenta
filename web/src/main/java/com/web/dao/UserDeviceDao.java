package com.web.dao;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.UserDevice;

@Repository("userDeviceDao")
public interface UserDeviceDao extends CrudRepository<UserDevice, Integer> {

	@Override
	Set<UserDevice> findAll();

	Set<UserDevice> findAllByBoughtAndUserAccountId(boolean b, Integer userId);

	UserDevice findByUserAccountIdAndDeviceDeviceId(Integer userId, Integer productId);

	@Transactional
	@Modifying
	@Query(value = "delete from users_devices where device_id = ?1 and user_id = ?2", nativeQuery = true)
	void deleteByDeviceIdAndUserId(Integer deviceId, Integer userId);

	Optional<UserDevice> findByDeviceNameAndUserAccountId(String string, Integer accountId);
}
