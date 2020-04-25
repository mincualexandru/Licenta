package com.web.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.web.model.Account;
import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.service.AccountService;
import com.web.service.UserDeviceService;
import com.web.utils.BandTypeMeasurement;
import com.web.utils.ScaleTypeMeasurement;

@Controller
public class DifferencesBetweenMeasurementsController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDeviceService userDeviceService;

	@GetMapping(path = { "/differences_for_calories" })
	public String home(Model model, HttpServletResponse response) {
		Account account = accountService.getAccountConnected();
		Float sumOfMeasurementValues = 0f;
		String previousValueForDate = null;
		Set<Measurement> commonMeasurements = new HashSet<>();
		Map<String, Float> caloriesBurnedValues = new TreeMap<>();
		Map<String, Float> caloriesAccumulatedValues = new TreeMap<>();
		List<String> caloriesDates = new ArrayList<>();
		List<String> caloriesCommonDates = new ArrayList<>();
		String activeEnergyBurned = BandTypeMeasurement.ENERGYBURNED.getBandTypeMeasurement();
		String activeEnergyAccumulated = "HKQuantityTypeIdentifierActiveEnergyAccumulated";
		String bodyMass = ScaleTypeMeasurement.MASS.getScaleTypeMeasurement();
		String height = ScaleTypeMeasurement.HEIGHT.getScaleTypeMeasurement();
		if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
			UserDevice fitBuddyDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			fitBuddyDevice.getMeasurements().removeIf(element -> element.getName().equals(bodyMass));
			fitBuddyDevice.getMeasurements().removeIf(element -> element.getName().equals(height));
			commonMeasurements.addAll(fitBuddyDevice.getMeasurements());
		}
		if (userDeviceService.checkIfDeviceIsPresent(account, "Bratara")) {
			UserDevice bandDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId()).get();
			commonMeasurements.addAll(bandDevice.getMeasurements());
		}
		List<Measurement> commonMeasurementsSorted = commonMeasurements.stream()
				.sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).collect(Collectors.toList());
		for (Measurement measurement : commonMeasurementsSorted) {
			if (measurement.getName().equals(activeEnergyBurned)) {
				String formatDate = formatDate(measurement);
				if (!formatDate.equals(previousValueForDate)) {
					sumOfMeasurementValues = 0f;
					previousValueForDate = formatDate;
				}
				sumOfMeasurementValues += measurement.getValue();
				caloriesBurnedValues.put(formatDate, sumOfMeasurementValues);
			}
		}
		for (Measurement measurement : commonMeasurementsSorted) {
			if (measurement.getName().equals(activeEnergyAccumulated)) {
				String formatDate = formatDate(measurement);
				if (!formatDate.equals(previousValueForDate)) {
					sumOfMeasurementValues = 0f;
					previousValueForDate = formatDate;
				}
				sumOfMeasurementValues += measurement.getValue();
				caloriesAccumulatedValues.put(formatDate, sumOfMeasurementValues);
			}
		}
		caloriesCommonDates = justCaloriesCommonDates(caloriesBurnedValues, caloriesAccumulatedValues, caloriesDates);
		caloriesCommonDates = caloriesCommonDates.stream().distinct().sorted().collect(Collectors.toList());
		model.addAttribute("dates", caloriesCommonDates);
		model.addAttribute("caloriesBurned", caloriesBurnedValues);
		model.addAttribute("caloriesAccumulated", caloriesAccumulatedValues);
		return "home/differences_for_calories";

	}

	private List<String> justCaloriesCommonDates(Map<String, Float> caloriesBurnedValues,
			Map<String, Float> caloriesAccumulatedValues, List<String> caloriesDates) {
		List<String> caloriesCommonDates;
		caloriesDates.addAll(caloriesAccumulatedValues.keySet());
		caloriesDates.addAll(caloriesBurnedValues.keySet());
		caloriesCommonDates = caloriesDates.stream()
				.filter(integer -> caloriesDates.indexOf(integer) != caloriesDates.lastIndexOf(integer))
				.collect(Collectors.toList());
		caloriesDates.removeAll(caloriesCommonDates);
		caloriesAccumulatedValues.keySet().removeAll(caloriesDates);
		caloriesBurnedValues.keySet().removeAll(caloriesDates);
		return caloriesCommonDates;
	}

	private String formatDate(Measurement measurement) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime startDateTime = measurement.getStartDate().toLocalDateTime();
		LocalDate startDate = startDateTime.toLocalDate();
		String formatDate = startDate.format(formatter);
		return formatDate;
	}

}
