package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.Exercise;
import com.web.model.ExerciseDone;
import com.web.model.ExerciseFeedback;
import com.web.model.Measurement;
import com.web.model.TrainingPlan;
import com.web.model.Transaction;
import com.web.model.TypeMeasurement;
import com.web.model.UserDevice;
import com.web.model.UserTraining;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.MeasurementService;
import com.web.service.TrainingPlanService;
import com.web.service.TransactionService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.UserTrainingService;
import com.web.service.XmlParserService;
import com.web.utils.BandTypeMeasurement;
import com.web.utils.MeasurementObiective;
import com.web.utils.Product;
import com.web.utils.ScaleTypeMeasurement;

@Controller
public class UserController {

	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private TrainingPlanService trainingPlanService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private UserTrainingService userTrainingService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private XmlParserService xmlParsersService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

//	@Autowired
//	private RangeValuesService rangeValuesService;

	@GetMapping(path = { "/home" })
	public String home(Model model) {
		Account account = getAccountConnected();
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, account.getAccountId());
		Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
		boolean addBandToShoppingCart = false;
		boolean addScaleToShoppingCart = false;
		Set<Measurement> userMeasurements = new HashSet<>();
		for (UserDevice userDevice : userDevices) {
			if (userDevice.getDevice().getName().equals("Bratara") && userDevice.isBought() == false) {
				addBandToShoppingCart = true;
			}
			if (userDevice.getDevice().getName().equals("Cantar Inteligent") && userDevice.isBought() == false) {
				addScaleToShoppingCart = true;
			}
		}

		for (UserDevice userDevice : account.getUserDevices()) {
			if (userDevice.isBought()) {
				for (TypeMeasurement type : typeMeasurements) {
					userMeasurements.addAll(measurementService.findLast3ByNameAndUserDeviceId(type.getType(),
							userDevice.getUserDeviceId()));
				}
			}
		}

//		try {
//			xmlParsersService.readAllMeasurementsFromXmlFile(
//					userDeviceService.findAllByBoughtAndUserAccountId(true, account.getAccountId()));
//
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

		List<Measurement> userMeasurementsSorted = userMeasurements.stream()
				.sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());

		model.addAttribute("addBandToShoppingCart", addBandToShoppingCart);
		model.addAttribute("addScaleToShoppingCart", addScaleToShoppingCart);
		model.addAttribute("user", account);
		model.addAttribute("typeMeasurements", typeMeasurements);
		model.addAttribute("userMeasurements", userMeasurementsSorted);
		return "home/home";

	}

	@PostMapping(path = { "/buy_scale" })
	public String buyScale(Model model, RedirectAttributes redirectAttributes) {
		Account account = getAccountConnected();
		Device device = deviceService.findOneDeviceRandomByName("Cantar Inteligent");
		if (device == null) {
			redirectAttributes.addFlashAttribute("soldOutForScales", "Stocul este epuizat pentru cantare");
			Device newDevice = new Device();
			newDevice.setCompany("Xiaomi");
			newDevice.setName("Cantar Inteligent");
			newDevice.setSerialNumber(generateRandomSerialNumber());
			newDevice.setPrice(20);
			Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
			typeMeasurements.removeIf(element -> !(element.getType().contains("Body")));
			newDevice.setTypeMeasurements(typeMeasurements);
			deviceService.save(newDevice);
			return "redirect:/home";
		} else {
			LOGGER.info("Nume " + device.getName() + " serial number " + device.getSerialNumber());
			model.addAttribute("device", device);
			model.addAttribute("account", account);
			model.addAttribute("typeMeasurements", device.getTypeMeasurements());
			return "home/buy_scale";
		}
	}

	@PostMapping(path = { "/buy_band" })
	public String buyBand(Model model, RedirectAttributes redirectAttributes) {

		Account account = getAccountConnected();
		Device device = deviceService.findOneDeviceRandomByName("Bratara");
		if (device == null) {
			redirectAttributes.addFlashAttribute("soldOutForBands", "Stocul este epuizat pentru bratari");
			Device newDevice = new Device();
			newDevice.setCompany("Xiaomi");
			newDevice.setName("Bratara");
			newDevice.setSerialNumber(generateRandomSerialNumber());
			newDevice.setPrice(20);
			Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
			typeMeasurements.removeIf(element -> element.getType().contains("Body"));
			newDevice.setTypeMeasurements(typeMeasurements);
			deviceService.save(newDevice);
			return "redirect:/home";
		} else {
			LOGGER.info("Nume " + device.getName() + " serial number " + device.getSerialNumber());
			model.addAttribute("device", device);
			model.addAttribute("account", account);
			model.addAttribute("typeMeasurements", device.getTypeMeasurements());
			return "home/buy_band";
		}
	}

	@PostMapping(path = { "/add_to_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer accountId,
			@RequestParam(required = false) Integer deviceId, @RequestParam(required = false) Integer trainingId,
			@RequestParam(required = false) Integer dietId) {
		Account account = accountService.findById(accountId).get();
		if (deviceId != null) {
			UserDevice userDevice = new UserDevice();
			Device device = deviceService.findById(deviceId).get();
			userDevice.setDevice(device);
			userDevice.setUser(account);
			userDevice.setBought(false);
			userDeviceService.save(userDevice);
		}
		if (trainingId != null) {
			UserTraining userTraining = new UserTraining();
			TrainingPlan trainingPlan = trainingPlanService.findById(trainingId).get();
			userTraining.setTrainingPlan(trainingPlan);
			userTraining.setUser(account);
			userTraining.setBought(false);
			userTrainingService.save(userTraining);
		}
		return "redirect:/shopping_cart";
	}

	@GetMapping(path = { "/shopping_cart" })
	public String shoppingCart(Model model) {
		Account user = getAccountConnected();
		Set<Product> products = new HashSet<>();
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
		Set<UserTraining> userTrainingPlans = userTrainingService.findAllByBoughtAndUserAccountId(false,
				user.getAccountId());
		Integer totalCostOfDevices = 0;
		for (UserDevice userDevice : userDevices) {
			totalCostOfDevices += userDevice.getDevice().getPrice();
			Product product = new Product();
			product.setProductId(userDevice.getDevice().getDeviceId());
			product.setCompanyName(userDevice.getDevice().getCompany());
			product.setProductName(userDevice.getDevice().getName());
			product.setPrice(userDevice.getDevice().getPrice());
			product.setType("device");
			products.add(product);
		}
		Integer totalCostOfTrainingPlans = 0;
		for (UserTraining userTraining : userTrainingPlans) {
			totalCostOfTrainingPlans += userTraining.getTrainingPlan().getPrice();
			Product product = new Product();
			product.setProductId(userTraining.getTrainingPlan().getTrainingPlanId());
			product.setProductName(userTraining.getTrainingPlan().getName());
			product.setPrice(userTraining.getTrainingPlan().getPrice());
			product.setForWho(userTraining.getTrainingPlan().getForWho());
			product.setType("trainingPlan");
			products.add(product);
		}
		model.addAttribute("products", products);
		model.addAttribute("totalSum", totalCostOfDevices + totalCostOfTrainingPlans);
		model.addAttribute("user", user);
		return "home/shopping_cart";
	}

	@PostMapping(path = { "/delete_product_from_shopping_cart" })
	public String deleteProductFromShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam Integer productId, @RequestParam String productName, RedirectAttributes redirectAttributes) {
		if (productName.equals("Bratara") || productName.equals("Cantar Inteligent")) {
			UserDevice userDeviceToDelete = userDeviceService.findByUserAccountIdAndDeviceDeviceId(userId, productId);
			userDeviceService.delete(userDeviceToDelete);
		} else {
			UserTraining userTrainingToDelete = userTrainingService
					.findByUserAccountIdAndTrainingPlanTrainingPlanId(userId, productId);
			userTrainingService.delete(userTrainingToDelete);
		}
		return "redirect:/shopping_cart";
	}

	@PostMapping(path = { "/buy_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam(required = false) Integer totalSum, RedirectAttributes redirectAttributes) {
		Account user = accountService.findById(userId).get();
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
		Set<UserTraining> userTrainingPlans = userTrainingService.findAllByBoughtAndUserAccountId(false,
				user.getAccountId());
		if (user.getTransaction().getAvailableBalance() >= totalSum) {
			boolean ok = false;
			if (!userDevices.isEmpty()) {
				ok = true;
				for (UserDevice userDevice : userDevices) {
					Integer devicePrice = userDevice.getDevice().getPrice();
					user.getTransaction()
							.setAvailableBalance(user.getTransaction().getAvailableBalance() - devicePrice);
					user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
					userDevice.setBought(true);
					userDeviceService.save(userDevice);
				}
			}
			if (!userTrainingPlans.isEmpty()) {
				for (UserTraining userTraining : userTrainingPlans) {
					Integer trainingPlanPrice = userTraining.getTrainingPlan().getPrice();
					user.getTransaction()
							.setAvailableBalance(user.getTransaction().getAvailableBalance() - trainingPlanPrice);
					if (!ok) {
						user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
						Account trainer = userTraining.getTrainingPlan().getTrainer();
						trainer.getTransaction()
								.setAvailableBalance(trainer.getTransaction().getAvailableBalance() + totalSum);
					}
					userTraining.setBought(true);
					userTrainingService.save(userTraining);
				}
			}
		} else {
			redirectAttributes.addFlashAttribute("insufficientBalance", "Fonduri insuficiente");
			return "redirect:/shopping_cart";
		}
		return "redirect:/home";
	}

	@GetMapping(path = { "/current_balance" })
	public String availableBalance(Model model) {
		Account user = getAccountConnected();
		model.addAttribute("transaction", user.getTransaction());
		return "home/current_balance";
	}

	@PostMapping(path = { "/modify_current_balance" })
	public String modifyCurrentBalance(Model model, @RequestParam Integer transactionId,
			@RequestParam Integer availableBalance, RedirectAttributes redirectAttributes) {
		Transaction transaction = transactionService.findById(transactionId).get();
		if (availableBalance > 0) {
			transaction.setAvailableBalance(availableBalance);
			transactionService.save(transaction);
			return "redirect:/current_balance";
		} else {
			redirectAttributes.addFlashAttribute("negativeValue", "Valorile negative nu sunt valide");
			return "redirect:/current_balance";
		}
	}

	@GetMapping(path = { "/choose_helper" })
	public String chooseHelper(Model model) {
		Account user = getAccountConnected();
		Set<Account> allTrainersOrNutritionists = accountService.findAllByRolesNameOrRolesName("ROLE_TRAINER",
				"ROLE_NUTRITIONIST");
		allTrainersOrNutritionists.removeIf(element -> !(element.isActive()));
		allTrainersOrNutritionists.removeIf(element -> user.getHelpers().contains(element));
		model.addAttribute("allTrainersOrNutritionists", allTrainersOrNutritionists);
		return "home/choose_helper";
	}

	@PostMapping(path = { "/choose_helper_save" })
	public String chooseHelperSave(Model model, @RequestParam Integer helperId) {
		Account user = getAccountConnected();
		user.getHelpers().add(accountService.findById(helperId).get());
		accountService.save(user);
		return "redirect:/choose_helper";
	}

	@GetMapping(path = { "/view_helpers" })
	public String viewHelpers(Model model) {
		Account user = getAccountConnected();
		model.addAttribute("trainersOrNutrituserionists", user.getHelpers());
		return "home/view_helpers";
	}

	@PostMapping(path = { "/trainer_training_plans" })
	public String viewTrainingsOrDiets(Model model, @RequestParam Integer trainerId) {
		Account user = getAccountConnected();
		model.addAttribute("trainingPlans", trainingPlanService.findAllByTrainingPlanNotAssociated(trainerId));
		model.addAttribute("user", user);
		return "home/trainer_training_plans";
	}

	@GetMapping(path = { "/view_training_plans" })
	public String viewTrainingPlans(Model model) {
		Account user = getAccountConnected();
		Set<UserTraining> userTrainings = userTrainingService.findAllByBoughtAndUserAccountId(true,
				user.getAccountId());
		model.addAttribute("userTrainings", userTrainings);
		model.addAttribute("user", user);
		return "home/view_training_plans";
	}

	@GetMapping(path = { "/exercises_done" })
	public String exercisesDone(Model model) {
		Account user = getAccountConnected();
		Set<ExerciseDone> exercisesDone = exerciseDoneService.findAllByUserAccountId(user.getAccountId());
		// ExerciseFeedback exerciseFeedback =
		// exerciseFeedbackService.findByUserAccountIdAndExerciseExerciseId();
		model.addAttribute("exercisesDone", exercisesDone);
		return "home/exercises_done";
	}

	@PostMapping(path = { "/perfom_this_exercise" })
	public String perfomThisExercise(Model model, @RequestParam Integer accountId, @RequestParam Integer exerciseId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		ExerciseDone exerciseDone = new ExerciseDone();
		exerciseDone.setExercise(exerciseService.findById(exerciseId).get());
		exerciseDone.setUser(accountService.findById(accountId).get());
		exerciseDoneService.save(exerciseDone);
		for (UserDevice userDevice : account.getUserDevices()) {
			if (userDevice.getDevice().getName().equals("Bratara")) {
				Measurement measurement = new Measurement();
				Timestamp timestampStartDate = new Timestamp(System.currentTimeMillis());
				measurement.setStartDate(timestampStartDate);
				measurement.setName(
						typeMeasurementService.findByType("HKQuantityTypeIdentifierActiveEnergyBurned").getType());
				measurement.setUnitOfMeasurement("kcal");
				measurement.setValue(exerciseService.findById(exerciseId).get().getCaloriesBurned());
				measurement.setUserDevice(userDevice);
				measurementService.save(measurement);
			}
		}
		redirectAttributes.addFlashAttribute("exerciseId", exerciseId);
		return "redirect:/review_for_exercise";
	}

	@GetMapping(path = { "/review_for_exercise" })
	public String reviewForExercise(@ModelAttribute("exerciseId") Integer exerciseId, Model model) {
		LOGGER.info("Id exercitiu " + exerciseId);
		model.addAttribute("exerciseId", exerciseId);
		return "home/review_for_exercise";
	}

	@PostMapping(path = { "/review_for_exercise_save" })
	public String reviewForExerciseSave(Model model, @RequestParam Integer exerciseId,
			@RequestParam Integer numberOfMinutes, @RequestParam String messageReview, @RequestParam Integer number) {
		Account user = getAccountConnected();
		Exercise exercise = exerciseService.findById(exerciseId).get();
		Measurement measurement = measurementService.findByEndDate(null);
		Timestamp startDate = measurement.getStartDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate.getTime());
		calendar.add(Calendar.MINUTE, numberOfMinutes);
		Timestamp endDate = new Timestamp(calendar.getTime().getTime());
		measurement.setEndDate(endDate);
		measurementService.save(measurement);
		ExerciseFeedback exerciseFeedback = new ExerciseFeedback();
		exerciseFeedback.setExercise(exercise);
		exerciseFeedback.setUser(user);
		exerciseFeedback.setMessage(messageReview);
		exerciseFeedback.setRating(number);
		exerciseFeedbackService.save(exerciseFeedback);
		return "redirect:/exercises_done";
	}

	@GetMapping(path = { "/view_devices" })
	public String viewDevices(Model model) {
		Account user = getAccountConnected();
		model.addAttribute("userDevices", user.getUserDevices());
		return "home/view_devices";
	}

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
			String activeEnergyBurned = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERACTIVEENERGYBURNED
					.getBandTypeMeasurement();
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
			String sleepAnalysis = BandTypeMeasurement.HKCATEGORYTYPEIDENTIFIERSLEEPANALYSIS.getBandTypeMeasurement();
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
			String heartRate = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERHEARTRATE.getBandTypeMeasurement();
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
			String stepCount = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERSTEPCOUNT.getBandTypeMeasurement();
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
			String bodyFatPercentage = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYFATPERCENTAGE
					.getScaleTypeMeasurement();
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
			String bodyMassIndex = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYMASSINDEX.getScaleTypeMeasurement();
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
			String bodyMass = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYMASS.getScaleTypeMeasurement();
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
			String leanBodyMass = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERLEANBODYMASS.getScaleTypeMeasurement();
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
				String activeEnergyBurned = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERACTIVEENERGYBURNED
						.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(activeEnergyBurned, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "sleepAnalysis":
				String sleepAnalysis = BandTypeMeasurement.HKCATEGORYTYPEIDENTIFIERSLEEPANALYSIS
						.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(sleepAnalysis, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "stepCount":
				String stepCount = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERSTEPCOUNT.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(stepCount, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "heartRate":
				String heartRate = BandTypeMeasurement.HKQUANTITYTYPEIDENTIFIERHEARTRATE.getBandTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(heartRate, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyFatPercentage":
				String bodyFatPercentage = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYFATPERCENTAGE
						.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyFatPercentage, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyMassIndex":
				String bodyMassIndex = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYMASSINDEX
						.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyMassIndex, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "bodyMass":
				String bodyMass = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERBODYMASS.getScaleTypeMeasurement();
				measurementsBetweenTimestamps = measurementService
						.findAllByNameAndUserDeviceUserDeviceIdAndStartDateBetween(bodyMass, userDeviceId,
								timestampStartDate, timestampEndDate);
				break;
			case "leanBodyMass":
				String leanBodyMass = ScaleTypeMeasurement.HKQUANTITYTYPEIDENTIFIERLEANBODYMASS
						.getScaleTypeMeasurement();
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

	private String generateRandomSerialNumber() {
		int leftLimit = 48;
		int rightLimit = 122;
		int targetStringLength = 10;
		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return generatedString;
	}

	private Account getAccountConnected() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		return user;
	}
}
