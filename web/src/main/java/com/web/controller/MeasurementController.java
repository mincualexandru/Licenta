package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Measurement;
import com.web.model.TypeMeasurement;
import com.web.model.UserDevice;
import com.web.service.MeasurementService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.utils.BandTypeMeasurement;
import com.web.utils.MeasurementObiective;
import com.web.utils.ScaleTypeMeasurement;

@Controller
public class MeasurementController {

	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@PostMapping(path = { "/view_measurements_for_device" })
	public String viewMeasurementsForDevice(Model model, @RequestParam Integer userDeviceId) {
		UserDevice userDevice = userDeviceService.findById(userDeviceId).get();
		List<Measurement> measurements = measurementService
				.findAllByUserDeviceUserDeviceId(userDevice.getUserDeviceId());
		model.addAttribute("measurements", measurements);
		model.addAttribute("userDevice", userDevice);
		return "home/view_measurements_for_device";
	}

	@GetMapping(path = { "/view_charts_for_device/{chartOption}/{userDeviceId}" })
	public String viewChartsForDevice(@ModelAttribute("betweenTimestamp") String betweenTimestamp, Model model,
			@PathVariable Integer userDeviceId, @PathVariable String chartOption) {
		@SuppressWarnings("unchecked")
		Set<Measurement> measurementsBetweenTimestamps = (Set<Measurement>) model.asMap()
				.get("measurementsBetweenTimestamps");
		UserDevice userDevice = userDeviceService.findById(userDeviceId).get();
		TypeMeasurement typeMeasurement = new TypeMeasurement();
		Set<Measurement> chosenMeasurements = new HashSet<>();
		Set<MeasurementObiective> goalMeasurements = new HashSet<>();
		Map<String, Float> chartMap = new TreeMap<>();
		Float sum = 0f;
		String previousValue = null;
		Float maximValue = 0f;
		Float minimValue = 0f;
		switch (chartOption) {
		case "activeEnergyBurned":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String activeEnergyBurned = BandTypeMeasurement.ENERGYBURNED.getBandTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(activeEnergyBurned);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(activeEnergyBurned,
						userDevice.getUserDeviceId());
				for (Measurement measurement : measurementsBetweenTimestamps) {
					LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
					LocalDate localDate = startDate.toLocalDate();
					Integer hour = startDate.getHour();
					Integer nextHour = hour + 1;
					String value = hour + "-" + nextHour + " " + localDate;
					if (!value.equals(previousValue)) {
						sum = 0f;
						previousValue = value;
					}
					sum += measurement.getValue();
					chartMap.put(localDate + " " + String.format("%02d", hour), sum);
				}
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(activeEnergyBurned,
						userDevice.getUserDeviceId());
				for (Measurement measurement : chosenMeasurements) {
					LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
					LocalDate localDate = startDate.toLocalDate();
					Integer hour = startDate.getHour();
					Integer nextHour = hour + 1;
					String value = hour + "-" + nextHour + " " + localDate;
					LOGGER.info(value);
					if (!value.equals(previousValue)) {
						sum = 0f;
						previousValue = value;
					}
					sum += measurement.getValue();
					chartMap.put(localDate + " " + String.format("%02d", hour), sum);
				}
			}
			break;
		case "sleepAnalysis":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String sleepAnalysis = BandTypeMeasurement.SLEEPANALYSIS.getBandTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(sleepAnalysis);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				for (Measurement measurement : measurementsBetweenTimestamps) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime startDateTime = measurement.getStartDate().toLocalDateTime();
					LocalDate startDate = startDateTime.toLocalDate();
					String formatDate = startDate.format(formatter);
					if (!formatDate.equals(previousValue)) {
						sum = 0f;
						previousValue = formatDate;
					}
					sum += measurement.getValue();
					chartMap.put(formatDate, sum);
				}
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(sleepAnalysis,
						userDevice.getUserDeviceId());
				for (Measurement measurement : chosenMeasurements) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDateTime startDateTime = measurement.getStartDate().toLocalDateTime();
					LocalDate startDate = startDateTime.toLocalDate();
					String formatDate = startDate.format(formatter);
					if (!formatDate.equals(previousValue)) {
						sum = 0f;
						previousValue = formatDate;
					}
					sum += measurement.getValue();
					chartMap.put(formatDate, sum);
				}
			}
			break;
		case "heartRate":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String heartRate = BandTypeMeasurement.HEARTRATE.getBandTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(heartRate);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				measurementService.buildMap(chartMap, measurementsBetweenTimestamps);
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(heartRate,
						userDevice.getUserDeviceId());
				measurementService.buildMap(chartMap, chosenMeasurements);
			}
			break;
		case "stepCount":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String stepCount = BandTypeMeasurement.STEPCOUNT.getBandTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(stepCount);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(stepCount,
						userDevice.getUserDeviceId());
				for (Measurement measurement : measurementsBetweenTimestamps) {
					LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
					LocalDate localDate = startDate.toLocalDate();
					Integer hour = startDate.getHour();
					Integer nextHour = hour + 1;
					String value = hour + "-" + nextHour + " " + localDate;
					if (!value.equals(previousValue)) {
						sum = 0f;
						previousValue = value;
					}
					sum += measurement.getValue();
					chartMap.put(localDate + " " + String.format("%02d", hour), sum);
				}
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(stepCount,
						userDevice.getUserDeviceId());
				for (Measurement measurement : chosenMeasurements) {
					LocalDateTime startDate = measurement.getStartDate().toLocalDateTime();
					LocalDate localDate = startDate.toLocalDate();
					Integer hour = startDate.getHour();
					Integer nextHour = hour + 1;
					String value = hour + "-" + nextHour + " " + localDate;
					if (!value.equals(previousValue)) {
						sum = 0f;
						previousValue = value;
					}
					sum += measurement.getValue();
					chartMap.put(localDate + " " + String.format("%02d", hour), sum);
				}
			}
			break;
		case "bodyFatPercentage":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String bodyFatPercentage = ScaleTypeMeasurement.FATPERCENTAGE.getScaleTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(bodyFatPercentage);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				measurementService.buildMap(chartMap, measurementsBetweenTimestamps);
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(bodyFatPercentage,
						userDevice.getUserDeviceId());
				measurementService.buildMap(chartMap, chosenMeasurements);
			}
			break;
		case "bodyMassIndex":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String bodyMassIndex = ScaleTypeMeasurement.MASSINDEX.getScaleTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(bodyMassIndex);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				measurementService.buildMap(chartMap, measurementsBetweenTimestamps);
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(bodyMassIndex,
						userDevice.getUserDeviceId());
				measurementService.buildMap(chartMap, chosenMeasurements);
			}
			break;
		case "bodyMass":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String bodyMass = ScaleTypeMeasurement.MASS.getScaleTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(bodyMass);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				measurementService.buildMap(chartMap, measurementsBetweenTimestamps);
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(bodyMass,
						userDevice.getUserDeviceId());
				measurementService.buildMap(chartMap, chosenMeasurements);
			}
			break;
		case "leanBodyMass":
			LOGGER.info("Optiunea aleasa este " + chartOption);
			String leanBodyMass = ScaleTypeMeasurement.LEANMASS.getScaleTypeMeasurement();
			typeMeasurement = typeMeasurementService.findByType(leanBodyMass);
			if (betweenTimestamp.equals("notSelectedTimestamp")) {
				measurementService.buildMap(chartMap, measurementsBetweenTimestamps);
			} else {
				chosenMeasurements = measurementService.findAllByNameAndUserDeviceUserDeviceId(leanBodyMass,
						userDevice.getUserDeviceId());
				measurementService.buildMap(chartMap, chosenMeasurements);
			}
			break;
		default:
			break;
		}
		for (Map.Entry<String, Float> entry : chartMap.entrySet()) {
			String key = entry.getKey();
			Float value = entry.getValue();
			if (typeMeasurement.getGoalMin() < value && typeMeasurement.getGoalMax() > value
					&& goalMeasurements.size() < 3) {
				goalMeasurements.add(new MeasurementObiective(typeMeasurement.getType(), key, value));
			}
			String maximumDate = chartMap.keySet().stream().max(String::compareTo).get();
			String minimumDate = chartMap.keySet().stream().min(String::compareTo).get();
			boolean datesAreEquals = false;
			if (minimumDate.equals(maximumDate)) {
				datesAreEquals = true;
			}
			minimValue = Collections.min(chartMap.values());
			maximValue = Collections.max(chartMap.values());
			model.addAttribute("minimumDate", minimumDate);
			model.addAttribute("maximumDate", maximumDate);
			model.addAttribute("datesAreEquals", datesAreEquals);
			model.addAttribute("minimValue", minimValue);
			model.addAttribute("maximValue", maximValue);
		}
		model.addAttribute("chartMap", chartMap);
		model.addAttribute("chosenMeasurements", chosenMeasurements);
		model.addAttribute("measurementsBetweenTimestamps", measurementsBetweenTimestamps);
		model.addAttribute("goalMeasurements", goalMeasurements);
		model.addAttribute("chartOption", chartOption);
		model.addAttribute("userDeviceId", userDeviceId);

		return "home/view_charts_for_device";
	}

	@PostMapping(path = { "/view_charts_between_dates" })
	public String viewChartsBetweenDates(Model model, @RequestParam String chartOption,
			@RequestParam Integer userDeviceId, @RequestParam String startDateInput, @RequestParam String endDateInput,
			RedirectAttributes redirectAttributes) {
		try {
			Set<Measurement> measurementsBetweenTimestamps = new HashSet<>();
			LocalDate startDate = LocalDate.parse(startDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate endDate = LocalDate.parse(endDateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			Timestamp timestampStartDate = Timestamp.valueOf(startDate.atStartOfDay());
			Timestamp timestampEndDate = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());
			switch (chartOption) {
			case "activeEnergyBurned":
				String activeEnergyBurned = BandTypeMeasurement.ENERGYBURNED.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(activeEnergyBurned, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "sleepAnalysis":
				String sleepAnalysis = BandTypeMeasurement.SLEEPANALYSIS.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(sleepAnalysis, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "stepCount":
				String stepCount = BandTypeMeasurement.STEPCOUNT.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(stepCount, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "heartRate":
				String heartRate = BandTypeMeasurement.HEARTRATE.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(heartRate, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyFatPercentage":
				String bodyFatPercentage = ScaleTypeMeasurement.FATPERCENTAGE.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyFatPercentage, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyMassIndex":
				String bodyMassIndex = ScaleTypeMeasurement.MASSINDEX.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyMassIndex, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyMass":
				String bodyMass = ScaleTypeMeasurement.MASS.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyMass, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "leanBodyMass":
				String leanBodyMass = ScaleTypeMeasurement.LEANMASS.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(leanBodyMass, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			default:
				break;
			}
			redirectAttributes.addFlashAttribute("measurementsBetweenTimestamps", measurementsBetweenTimestamps);
			redirectAttributes.addFlashAttribute("betweenTimestamp", "notSelectedTimestamp");

		} catch (Exception e) {

		}
		return "redirect:/view_charts_for_device/" + chartOption + "/" + userDeviceId;
	}

}
