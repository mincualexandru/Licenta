package com.web.restcontroller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.UserDevice;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;

@RestController
public class DeviceController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@PostMapping("/devices_from_user")
	public Set<Device> loginResource(@RequestBody String userName) {
		try {
			if (accountService.findByUsername(userName).isPresent()) {
				Account account = accountService.findByUsername(userName).get();
				Set<Device> devicesFromUser = new HashSet<>();
				for (UserDevice userDevice : account.getUserDevices()) {
					if (userDevice.isBought()) {
						devicesFromUser.add(userDevice.getDevice());
					}
				}
				return devicesFromUser;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@GetMapping("/get_device/{deviceId}")
	public ResponseEntity<Device> findBookById(@PathVariable("deviceId") Integer deviceId) {
		Device device = deviceService.findById(deviceId).get();
		return ResponseEntity.ok().body(device);
	}

	@GetMapping("/get_number_of_measurements/{accountId}/{deviceId}")
	public ResponseEntity<Integer> numberOfMeasurementsOfUserDevice(@PathVariable("accountId") Integer accountId,
			@PathVariable("deviceId") Integer deviceId) {
		UserDevice userDevice = userDeviceService.findByUserAccountIdAndDeviceDeviceId(accountId, deviceId);
		return ResponseEntity.ok().body(userDevice.getMeasurements().size());
	}

	@GetMapping("/get_types_of_measurements")
	public ResponseEntity<List<String>> getTypesOfMeasurements() {
		List<String> typeMeasurements = new ArrayList<>();
		typeMeasurementService.findAll().forEach(typeMeasure -> typeMeasurements.add(typeMeasure.getType()));
		return ResponseEntity.ok().body(typeMeasurements);
	}
}