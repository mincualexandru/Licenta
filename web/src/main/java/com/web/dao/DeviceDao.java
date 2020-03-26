package com.web.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.web.model.Device;

@Repository("deviceDao")
public interface DeviceDao extends CrudRepository<Device, Integer> {
	@Override
	Set<Device> findAll();

	@Query(value = "select * from devices where name = ?1 and device_id not in (select ud.device_id FROM users_devices ud) order by rand() limit 1;", nativeQuery = true)
	Device findOneDeviceRandomByName(String string);

	Integer countByName(String string);

	@Transactional
	@Modifying
	@Query(value = "delete from devices where device_id = ?1", nativeQuery = true)
	void deleteByDeviceId(Integer deviceId);
}
