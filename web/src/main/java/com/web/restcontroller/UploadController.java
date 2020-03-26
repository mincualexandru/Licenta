package com.web.restcontroller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.TypeMeasurement;
import com.web.model.UserDevice;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.XmlParserService;
import com.web.serviceimpl.StorageServiceImpl;

@RestController
public class UploadController {

	@Autowired
	private StorageServiceImpl storageService;

	@Autowired
	private XmlParserService xmlParsersService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	List<String> files = new ArrayList<String>();
	private Path rootLocation = null;

	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("userName") String userName) {
		String message = "";
		try {
			Account account = accountService.findByUsername(userName);
			if (file.getOriginalFilename().equals("export.zip")) {
				Path currentPath = Paths.get(".");
				Path absolutePath = currentPath.toAbsolutePath();
				rootLocation = Paths.get(absolutePath + "/src/main/resources/static/uploads/" + userName);
				if (Files.notExists(rootLocation)) {
					storageService.init(rootLocation);
				}
				if (Files.exists(rootLocation.resolve(file.getOriginalFilename()))) {
					storageService.delete(rootLocation.resolve(file.getOriginalFilename()),
							rootLocation.resolve("export"), account.getUserDevices());
				}
				storageService.store(file, rootLocation);
				StorageServiceImpl.extractFolder(absolutePath + "/src/main/resources/static/uploads/" + userName + "/"
						+ file.getOriginalFilename());
				files.add(file.getOriginalFilename());

				try {
					xmlParsersService.readAllMeasurementsFromXmlFile(
							userDeviceService.findAllByBoughtAndUserAccountId(true, account.getAccountId()), userName);

				} catch (ParseException e) {
					e.printStackTrace();
				}

				message = "Felicitari";
				return ResponseEntity.status(HttpStatus.OK).body(message);
			} else {
				message = "Fisierul pe care il incarci trebuie sa aiba denumirea 'export.zip'";
				return ResponseEntity.status(HttpStatus.OK).body(message);
			}
		} catch (Exception e) {
			message = "A aparut o eroare ! Fisierul nu a fost incarcat ! ";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}

	@PostMapping("/devices_from_user")
	public Set<Device> loginResource(@RequestBody String userName) {
		String message = "";
		try {
			Account account = accountService.findByUsername(userName);
			Set<Device> devicesFromUser = new HashSet<>();
			for (UserDevice userDevice : account.getUserDevices()) {
				devicesFromUser.add(userDevice.getDevice());
			}
			message = "You logged in successfully  " + userName + "!";
			ResponseEntity.status(HttpStatus.OK).body(message);
			return devicesFromUser;
		} catch (Exception e) {
			message = "FAIL " + userName + "!";
			ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
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
		Integer numberOfMeasurements = null;
		UserDevice userDevice = userDeviceService.findByUserAccountIdAndDeviceDeviceId(accountId, deviceId);
		numberOfMeasurements = userDevice.getMeasurements().size();
		return ResponseEntity.ok().body(numberOfMeasurements);
	}

	@GetMapping("/get_types_of_measurements")
	public ResponseEntity<List<String>> getTypesOfMeasurements() {
		List<String> typeMeasurements = new ArrayList<>();
		for (TypeMeasurement typeMeasure : typeMeasurementService.findAll()) {
			typeMeasurements.add(typeMeasure.getType());
		}
		return ResponseEntity.ok().body(typeMeasurements);
	}
}