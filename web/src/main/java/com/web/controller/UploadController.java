package com.web.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.serviceimpl.StorageServiceImpl;

@RestController
public class UploadController {

	@Autowired
	private StorageServiceImpl storageService;

//	@Autowired
//	private XmlParserService xmlParsersService;

	List<String> files = new ArrayList<String>();
	private Path rootLocation = null;

	@PostMapping("/post")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file,
			@RequestParam("userName") String userName) {
		String message = "";
		try {
			Path currentPath = Paths.get(".");
			Path absolutePath = currentPath.toAbsolutePath();
			rootLocation = Paths.get(absolutePath + "/src/main/resources/static/uploads/" + userName);
			if (Files.notExists(rootLocation)) {
				storageService.init(rootLocation);
			}
			storageService.store(file, rootLocation);
			StorageServiceImpl.extractFolder(
					absolutePath + "/src/main/resources/static/uploads/" + userName + "/" + file.getOriginalFilename());
			files.add(file.getOriginalFilename());

//			xmlParsersService.readAllMeasurementsFromXmlFile();

			message = "You successfully uploaded " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.OK).body(message);
		} catch (Exception e) {
			message = "FAIL to upload " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
		}
	}
}