package com.web.serviceimpl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.web.model.UserDevice;
import com.web.service.MeasurementService;

@Service("storageService")
public class StorageServiceImpl {

	Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private MeasurementService measurementService;

	public void store(MultipartFile file, Path rootLocation2) {
		try {
			Files.copy(file.getInputStream(), rootLocation2.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			throw new RuntimeException("FAIL!");
		}
	}

	static public void extractFolder(String zipFile) throws ZipException, IOException {
		int BUFFER = 2048;
		File file = new File(zipFile);
		ZipFile zip = new ZipFile(file);
		String newPath = zipFile.substring(0, zipFile.length() - 4);
		new File(newPath).mkdir();
		Enumeration zipFileEntries = zip.entries();
		while (zipFileEntries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
			String currentEntry = entry.getName();
			File destFile = new File(newPath, currentEntry);
			File destinationParent = destFile.getParentFile();
			destinationParent.mkdirs();
			if (!entry.isDirectory()) {
				BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
				int currentByte;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(destFile);
				BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
				while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, currentByte);
				}
				dest.flush();
				dest.close();
				is.close();
			}
			if (currentEntry.endsWith(".zip")) {
				extractFolder(destFile.getAbsolutePath());
			}
		}
		zip.close();
	}

	public void init(Path rootLocation2) {
		try {
			Files.createDirectories(rootLocation2);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize storage!");
		}
	}

	public void delete(Path resolve, Path resolve2, Set<UserDevice> userDevices) {
		try {
			if (Files.exists(resolve2)) {
				Files.walk(resolve2).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
			Files.deleteIfExists(resolve);
			for (UserDevice userDevice : userDevices) {
				measurementService.deleteAllByUserDeviceId(userDevice.getUserDeviceId());
			}
		} catch (NoSuchFileException e) {
			System.out.println("No such file/directory exists");
		} catch (DirectoryNotEmptyException e) {
			System.out.println("Directory is not empty.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
