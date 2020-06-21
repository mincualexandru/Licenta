package com.web.restcontroller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.model.Account;
import com.web.service.AccountService;
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

	List<String> files = new ArrayList<String>();

	private Path rootLocation = null;

	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("userName") String userName) {
		String message = "";
		try {
			if (accountService.findByUsername(userName).isPresent()) {
				Account account = accountService.findByUsername(userName).get();
				if (file.getOriginalFilename().equals("export.zip")) {
					Path absolutePath = Paths.get(".").toAbsolutePath();
					rootLocation = Paths.get(absolutePath + "/src/main/resources/static/uploads/" + userName);
					if (Files.notExists(rootLocation)) {
						storageService.init(rootLocation);
					}
					if (Files.exists(rootLocation.resolve(file.getOriginalFilename()))) {
						storageService.delete(rootLocation.resolve(file.getOriginalFilename()),
								rootLocation.resolve("export"), account.getUserDevices());
					}
					storageService.store(file, rootLocation);
					StorageServiceImpl.extractFolder(absolutePath + "/src/main/resources/static/uploads/" + userName
							+ "/" + file.getOriginalFilename());
					files.add(file.getOriginalFilename());

					try {
						xmlParsersService.readAllMeasurementsFromXmlFile(
								userDeviceService.findAllByBoughtAndUserAccountId(true, account.getAccountId()),
								userName);

					} catch (ParseException e) {
						e.printStackTrace();
					}
					message = "Felicitari";
					return ResponseEntity.status(HttpStatus.OK).body(message);
				} else {
					message = "Fisierul pe care il incarci trebuie sa aiba denumirea 'export.zip'";
					return ResponseEntity.status(HttpStatus.OK).body(message);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			message = "A aparut o eroare ! Fisierul nu a fost incarcat ! ";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}
}