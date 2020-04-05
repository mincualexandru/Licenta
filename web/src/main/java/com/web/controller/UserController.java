package com.web.controller;

import java.sql.Timestamp;
import java.time.Duration;
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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.Exercise;
import com.web.model.ExerciseDone;
import com.web.model.ExerciseFeedback;
import com.web.model.Food;
import com.web.model.FoodEaten;
import com.web.model.FoodFeedback;
import com.web.model.HelperFeedback;
import com.web.model.HelperPlan;
import com.web.model.Measurement;
import com.web.model.Transaction;
import com.web.model.TypeMeasurement;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.FoodEatenService;
import com.web.service.FoodFeedbackService;
import com.web.service.FoodService;
import com.web.service.HelperFeedbackService;
import com.web.service.HelperPlanService;
import com.web.service.MeasurementService;
import com.web.service.TransactionService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;
import com.web.utils.BandTypeMeasurement;
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
	private HelperPlanService helperPlanService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private UserPlanService userPlanService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private HelperFeedbackService helperFeedbackService;

	@Autowired
	private FoodEatenService foodEatenService;

	@Autowired
	private FoodService foodService;

	@Autowired
	private FoodFeedbackService foodFeedbackService;

	@GetMapping(path = { "/home" })
	public String home(Model model, HttpServletResponse response) {
		Account account = accountService.getAccountConnected();

		if (account.isActive() == false) {
			boolean passedTenDaysExerciseDone = false;
			boolean passedTenDaysFoodEaten = false;
			LocalDateTime dateTimeNow = LocalDateTime.now();
			passedTenDaysExerciseDone = checkIfTenDaysExerciseHavePassed(passedTenDaysExerciseDone, dateTimeNow,
					account.getAccountId());
			passedTenDaysFoodEaten = checkIfTenDaysFoodHavePassed(passedTenDaysFoodEaten, dateTimeNow,
					account.getAccountId());
			model.addAttribute("passedTenDaysFoodEaten", passedTenDaysFoodEaten);
			model.addAttribute("passedTenDaysExerciseDone", passedTenDaysExerciseDone);
		} else {

			Set<Measurement> allTypes = measurementService.findAll();
			Set<Measurement> sleepTypeMeasurements = new HashSet<>();
			Float sumOfMeasurementValues = 0f;
			String previousValueForDate = null;
			Set<Measurement> commonMeasurements = new HashSet<>();
			Map<String, Float> caloriesBurnedValues = new TreeMap<>();
			Map<String, Float> caloriesAccumulatedValues = new TreeMap<>();
			Map<String, Float> sleepAnalysisInBed = new TreeMap<>();
			Map<String, Float> sleepAnalysisAsSleep = new TreeMap<>();
			Map<String, Float> sleepAnalysisAwake = new TreeMap<>();
			List<String> caloriesDates = new ArrayList<>();
			Set<String> caloriesCommonDates = new HashSet<>();
			List<String> sleepDates = new ArrayList<>();
			Set<String> sleepCommonDates = new HashSet<>();
			String activeEnergyBurned = BandTypeMeasurement.ENERGYBURNED.getBandTypeMeasurement();
			String activeEnergyAccumulated = "HKQuantityTypeIdentifierActiveEnergyAccumulated";
			String bodyMass = ScaleTypeMeasurement.MASS.getScaleTypeMeasurement();
			String height = ScaleTypeMeasurement.HEIGHT.getScaleTypeMeasurement();

			if (account.getTransaction().getAvailableBalance() == 0) {
				return "redirect:/load_funds_account";
			}

			if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")
					&& !userDeviceService.checkIfDeviceIsPresent(account, "Cantar Inteligent")) {
				UserDevice fitBuddyDevice = userDeviceService
						.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
				if (!measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
						"HKQuantityTypeIdentifierHeight")
						|| !measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
								"HKQuantityTypeIdentifierBodyMass")) {
					return "redirect:/set_height_and_weight";
				}
			}
			sleepTypeMeasurements.addAll(measurementService.findAllByName("HKCategoryValueSleepAnalysisAsleep"));
			sleepTypeMeasurements.addAll(measurementService.findAllByName("HKCategoryValueSleepAnalysisAwake"));
			sleepTypeMeasurements.addAll(measurementService.findAllByName("HKCategoryValueSleepAnalysisInBed"));
			sleepTypeMeasurements.addAll(measurementService.findAllByName(activeEnergyBurned));
			allTypes.removeAll(sleepTypeMeasurements);
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
				bandDevice.getMeasurements().removeAll(allTypes);
				commonMeasurements.addAll(bandDevice.getMeasurements());
			}
			List<Measurement> commonMeasurementsSorted = commonMeasurements.stream()
					.sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).collect(Collectors.toList());
			for (Measurement measurement : commonMeasurementsSorted) {
				String formatDate = formatDate(measurement);
				if (!formatDate.equals(previousValueForDate)) {
					sumOfMeasurementValues = 0f;
					previousValueForDate = formatDate;
				}
				sumOfMeasurementValues += measurement.getValue();
				if (measurement.getName().equals(activeEnergyBurned)) {
					caloriesBurnedValues.put(formatDate, sumOfMeasurementValues);
				} else if (measurement.getName().equals(activeEnergyAccumulated)) {
					caloriesAccumulatedValues.put(formatDate, sumOfMeasurementValues);
				} else if (measurement.getName().equals("HKCategoryValueSleepAnalysisInBed")) {
					sleepAnalysisInBed.put(formatDate, sumOfMeasurementValues);
				} else if (measurement.getName().equals("HKCategoryValueSleepAnalysisAwake")) {
					sleepAnalysisAwake.put(formatDate, sumOfMeasurementValues);
				} else if (measurement.getName().equals("HKCategoryValueSleepAnalysisAsleep")) {
					sleepAnalysisAsSleep.put(formatDate, sumOfMeasurementValues);
				}
			}
			sleepCommonDates = justSleepCommonDates(sleepAnalysisInBed, sleepAnalysisAsSleep, sleepAnalysisAwake,
					sleepDates);
			caloriesCommonDates = justCaloriesCommonDates(caloriesBurnedValues, caloriesAccumulatedValues,
					caloriesDates);

			Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
			Set<Measurement> userMeasurements = new HashSet<>();
			for (UserDevice userDevice : account.getUserDevices()) {
				if (userDevice.isBought()) {
					for (TypeMeasurement type : typeMeasurements) {
						userMeasurements.addAll(measurementService.findLast3ByNameAndUserDeviceId(type.getType(),
								userDevice.getUserDeviceId()));
					}
				}
			}
			List<Measurement> userMeasurementsSorted = userMeasurements.stream()
					.sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());

			model.addAttribute("typeMeasurements", typeMeasurements);
			model.addAttribute("userMeasurements", userMeasurementsSorted);
			model.addAttribute("sleepDates", sleepCommonDates);
			model.addAttribute("dates", caloriesCommonDates);
			model.addAttribute("caloriesBurned", caloriesBurnedValues);
			model.addAttribute("caloriesAccumulated", caloriesAccumulatedValues);
			model.addAttribute("sleepAnalysisAsSleep", sleepAnalysisAsSleep);
			model.addAttribute("sleepAnalysisAwake", sleepAnalysisAwake);
			model.addAttribute("sleepAnalysisInBed", sleepAnalysisInBed);
		}
		model.addAttribute("user", account);
		return "home/home";
	}

	private boolean checkIfTenDaysFoodHavePassed(boolean passedTenDaysFoodEaten, LocalDateTime dateTimeNow,
			Integer accountId) {
		if (foodEatenService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			FoodEaten foodEaten = foodEatenService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).get();
			Duration duration = Duration.between(foodEaten.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				System.out.println("passedTenDaysFoodEaten");
				passedTenDaysFoodEaten = true;
			}
		}
		return passedTenDaysFoodEaten;
	}

	private boolean checkIfTenDaysExerciseHavePassed(boolean passedTenDaysExerciseDone, LocalDateTime dateTimeNow,
			Integer accountId) {
		if (exerciseDoneService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			ExerciseDone exerciseDone = exerciseDoneService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId)
					.get();
			Duration duration = Duration.between(exerciseDone.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				System.out.println("passedTenDaysExerciseDone");
				passedTenDaysExerciseDone = true;
			}
		}
		return passedTenDaysExerciseDone;
	}

	@GetMapping(path = { "/action_activate_account" })
	public String actionActivateAccount(Model model) {
		Account account = accountService.getAccountConnected();
		boolean passedTenDaysExerciseDone = false;
		boolean passedTenDaysFoodEaten = false;
		LocalDateTime dateTimeNow = LocalDateTime.now();
		if (!account.isActive()) {
			passedTenDaysExerciseDone = checkIfTenDaysExerciseHavePassed(passedTenDaysExerciseDone, dateTimeNow,
					account.getAccountId());
			passedTenDaysFoodEaten = checkIfTenDaysFoodHavePassed(passedTenDaysFoodEaten, dateTimeNow,
					account.getAccountId());
			if (passedTenDaysExerciseDone && passedTenDaysFoodEaten) {
				account.getUserPlans().removeIf(element -> !element.isBought());
				model.addAttribute("user", account);
				return "home/action_activate_account";
			} else {
				return "redirect:/home";
			}
		} else {
			return "redirect:/home";
		}
	}

	private Set<String> justCaloriesCommonDates(Map<String, Float> caloriesBurnedValues,
			Map<String, Float> caloriesAccumulatedValues, List<String> caloriesDates) {
		Set<String> caloriesCommonDates;
		caloriesDates.addAll(caloriesAccumulatedValues.keySet());
		caloriesDates.addAll(caloriesBurnedValues.keySet());
		caloriesCommonDates = caloriesDates.stream()
				.filter(integer -> caloriesDates.indexOf(integer) != caloriesDates.lastIndexOf(integer))
				.collect(Collectors.toSet());
		caloriesDates.removeAll(caloriesCommonDates);
		caloriesAccumulatedValues.keySet().removeAll(caloriesDates);
		caloriesBurnedValues.keySet().removeAll(caloriesDates);
		return caloriesCommonDates;
	}

	private Set<String> justSleepCommonDates(Map<String, Float> sleepAnalysisInBed,
			Map<String, Float> sleepAnalysisAsSleep, Map<String, Float> sleepAnalysisAwake, List<String> sleepDates) {
		Set<String> sleepCommonDates;
		sleepDates.addAll(sleepAnalysisInBed.keySet());
		sleepDates.addAll(sleepAnalysisAwake.keySet());
		sleepDates.addAll(sleepAnalysisAsSleep.keySet());
		sleepCommonDates = sleepDates.stream()
				.filter(integer -> sleepDates.indexOf(integer) != sleepDates.lastIndexOf(integer))
				.collect(Collectors.toSet());
		sleepDates.removeAll(sleepCommonDates);
		sleepAnalysisInBed.keySet().removeAll(sleepDates);
		sleepAnalysisAwake.keySet().removeAll(sleepDates);
		sleepAnalysisAsSleep.keySet().removeAll(sleepDates);
		return sleepCommonDates;
	}

	private String formatDate(Measurement measurement) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime startDateTime = measurement.getStartDate().toLocalDateTime();
		LocalDate startDate = startDateTime.toLocalDate();
		String formatDate = startDate.format(formatter);
		return formatDate;
	}

	@GetMapping(path = { "/set_height_and_weight" })
	public String setHeightAndWeight(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			UserDevice userDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			if ((measurementService
					.findByNameAndUserDeviceUserDeviceId("HKQuantityTypeIdentifierHeight", userDevice.getUserDeviceId())
					.isPresent())
					&& (measurementService.findByNameAndUserDeviceUserDeviceId("HKQuantityTypeIdentifierBodyMass",
							userDevice.getUserDeviceId()).isPresent())) {
				return "redirect:/home";
			}
			boolean heightIsNotCalculated = true;
			boolean weightIsNotCalculated = true;
			if ((measurementService
					.findByNameAndUserDeviceUserDeviceId("HKQuantityTypeIdentifierHeight", userDevice.getUserDeviceId())
					.isPresent())) {
				heightIsNotCalculated = false;
			} else if ((measurementService.findByNameAndUserDeviceUserDeviceId("HKQuantityTypeIdentifierBodyMass",
					userDevice.getUserDeviceId()).isPresent())) {
				weightIsNotCalculated = false;
			}
			model.addAttribute("heightIsNotCalculated", heightIsNotCalculated);
			model.addAttribute("weightIsNotCalculated", weightIsNotCalculated);
			return "home/set_height_and_weight";
		} else {
			return "redirect:/home";
		}

	}

	@PostMapping(path = { "/set_height_and_weight_save" })
	public String setHeightAndWeightSave(Model model, RedirectAttributes redirectAttributes,
			@RequestParam(required = false) Float weight, @RequestParam(required = false) Float height) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			UserDevice userDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			if (!(weight == null)) {
				Measurement measurementForWeight = new Measurement();
				measurementForWeight.setStartDate(new Timestamp(System.currentTimeMillis()));
				measurementForWeight.setEndDate(null);
				measurementForWeight.setFromXml(false);
				measurementForWeight.setName("HKQuantityTypeIdentifierBodyMass");
				measurementForWeight.setUnitOfMeasurement("kg");
				measurementForWeight.setValue(weight);
				measurementForWeight.setUserDevice(userDevice);
				measurementService.save(measurementForWeight);
			}
			if (!(height == null)) {
				Measurement measurementForHeight = new Measurement();
				measurementForHeight.setStartDate(new Timestamp(System.currentTimeMillis()));
				measurementForHeight.setEndDate(null);
				measurementForHeight.setFromXml(false);
				measurementForHeight.setName("HKQuantityTypeIdentifierHeight");
				measurementForHeight.setUnitOfMeasurement("cm");
				measurementForHeight.setValue(height);
				measurementForHeight.setUserDevice(userDevice);
				measurementService.save(measurementForHeight);
			}
			redirectAttributes.addFlashAttribute("message", "from_set_height_and_weight");
			return "redirect:/success";
		} else {
			return "redirect:/home";
		}

	}

	@GetMapping(path = { "/current_balance" })
	public String availableBalance(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			model.addAttribute("transaction", user.getTransaction());
			return "home/current_balance";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/load_funds_account" })
	public String loadFundsAccount(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			model.addAttribute("transaction", user.getTransaction());
			return "home/load_funds_account";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/add_device_to_shopping_cart" })
	public String buyScale(Model model, RedirectAttributes redirectAttributes, @RequestParam String deviceName) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
			Device newDevice = new Device();
			UserDevice userDevice = new UserDevice();
			if (deviceName.equals("Bratara")) {
				typeMeasurements.removeIf(element -> element.getType().contains("Body"));
				deviceService.createDevice(newDevice, typeMeasurements, "Bratara", 100);
			} else if (deviceName.equals("Cantar Inteligent")) {
				typeMeasurements.removeIf(element -> !(element.getType().contains("Body")));
				deviceService.createDevice(newDevice, typeMeasurements, "Cantar Inteligent", 150);
			}
			if (newDevice.getPrice() > account.getTransaction().getAvailableBalance()) {
				redirectAttributes.addFlashAttribute("insufficientBalance",
						"Nu ai fonduri suficiente pentru achizitionarea acestui dispozitiv");
				return "redirect:/load_funds_account";
			}
			deviceService.save(newDevice);
			userDevice.setDevice(newDevice);
			userDevice.setUser(account);
			userDevice.setBought(false);
			userDeviceService.save(userDevice);
			return "redirect:/shopping_cart";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/view_feedbacks_from_helper" })
	public String viewFeedbacks(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Set<HelperFeedback> feedbacks = helperFeedbackService.findAllByLearnerAccountId(account.getAccountId());
			LOGGER.info(feedbacks.size());
			model.addAttribute("feedbacks", feedbacks);
			return "home/view_feedbacks_from_helper";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/choose_device" })
	public String chooseDevice(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			boolean bandAlreadyBought = false;
			boolean scaleAlreadyBought = false;
			for (UserDevice userDevice : account.getUserDevices()) {
				if (userDevice.getDevice().getName().equals("Bratara") && userDevice.isBought()) {
					bandAlreadyBought = true;

				} else if (userDevice.getDevice().getName().equals("Cantar Inteligent") && userDevice.isBought()) {
					scaleAlreadyBought = true;
				}
			}
			model.addAttribute("bandAlreadyBought", bandAlreadyBought);
			model.addAttribute("scaleAlreadyBought", scaleAlreadyBought);
			model.addAttribute("account", account);
			return "home/choose_device";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/add_to_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer accountId,
			@RequestParam(required = false) Integer deviceId, @RequestParam(required = false) Integer trainingId,
			@RequestParam(required = false) Integer dietId) {
		Account account = accountService.findById(accountId).get();
		if (account.isActive()) {
			if (deviceId != null) {
				UserDevice userDevice = new UserDevice();
				Device device = deviceService.findById(deviceId).get();
				userDevice.setDevice(device);
				userDevice.setUser(account);
				userDevice.setBought(false);
				userDeviceService.save(userDevice);
			}
			if (trainingId != null) {
				UserPlan userTraining = new UserPlan();
				HelperPlan trainingPlan = helperPlanService.findByHelperPlanIdAndTypeOfPlan(trainingId, "Antrenament")
						.get();
				userTraining.setHelperPlan(trainingPlan);
				userTraining.setUser(account);
				userTraining.setBought(false);
				userPlanService.save(userTraining);
			}
			if (dietId != null) {
				UserPlan userDiet = new UserPlan();
				HelperPlan dietPlan = helperPlanService.findByHelperPlanIdAndTypeOfPlan(dietId, "Dieta").get();
				userDiet.setHelperPlan(dietPlan);
				userDiet.setUser(account);
				userDiet.setBought(false);
				userPlanService.save(userDiet);
			}
			return "redirect:/shopping_cart";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/shopping_cart" })
	public String shoppingCart(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<Product> products = new HashSet<>();
			Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
			Set<UserPlan> userPlans = userPlanService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
			Integer totalCostOfDevices = 0;
			for (UserDevice userDevice : userDevices) {
				Product product = new Product();
				totalCostOfDevices += userDevice.getDevice().getPrice();
				product.setProductId(userDevice.getDevice().getDeviceId());
				product.setCompanyName(userDevice.getDevice().getCompany());
				product.setProductName(userDevice.getDevice().getName());
				product.setPrice(userDevice.getDevice().getPrice());
				product.setType("device");
				products.add(product);
			}
			Integer totalCostOfPlans = 0;
			for (UserPlan userPlan : userPlans) {
				Product product = new Product();
				totalCostOfPlans += userPlan.getHelperPlan().getPrice();
				product.setProductName(userPlan.getHelperPlan().getName());
				product.setPrice(userPlan.getHelperPlan().getPrice());
				product.setForWho(userPlan.getHelperPlan().getForWho());
				product.setType(userPlan.getHelperPlan().getTypeOfPlan());
				products.add(product);
			}
			model.addAttribute("products", products);
			model.addAttribute("totalSum", totalCostOfDevices + totalCostOfPlans);
			model.addAttribute("user", user);
			return "home/shopping_cart";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/delete_product_from_shopping_cart" })
	public String deleteProductFromShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam Integer productId, @RequestParam String productName, RedirectAttributes redirectAttributes) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (productName.equals("Bratara") || productName.equals("Cantar Inteligent")) {
				UserDevice userDeviceToDelete = userDeviceService.findByUserAccountIdAndDeviceDeviceId(userId,
						productId);
				Device deviceToDelete = userDeviceToDelete.getDevice();
				userDeviceService.deleteByDeviceIdAndUserId(deviceToDelete.getDeviceId(), userId);
				if (deviceToDelete.getTypeMeasurements() != null) {
					for (TypeMeasurement typeMeasurementForDevice : deviceToDelete.getTypeMeasurements()) {
						typeMeasurementService.deleteByDeviceIdAndTypeMeasurementId(deviceToDelete.getDeviceId(),
								typeMeasurementForDevice.getTypeMeasurementId());
					}
					deviceService.deleteByDeviceId(deviceToDelete.getDeviceId());
				}

			} else {
				UserPlan userPlanToDelete = userPlanService.findByUserAccountIdAndHelperPlanHelperPlanId(userId,
						productId);
				userPlanService.deleteByUserPlanId(userPlanToDelete.getUserPlanId());
			}
			return "redirect:/shopping_cart";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/buy_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam(required = false) Integer totalSum, RedirectAttributes redirectAttributes) {
		Account user = accountService.findById(userId).get();
		if (user.isActive()) {
			Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
			Set<UserPlan> userPlans = userPlanService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
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
						if (userDevice.getDevice().getName().equals("Bratara")) {
							Set<Measurement> measurements = measurementService
									.findAllByUserDeviceUserDeviceIdAndFromXml(null, false);
							for (Measurement measurement : measurements) {
								measurement.setUserDevice(userDevice);
								measurementService.save(measurement);
							}
						}
						userDeviceService.save(userDevice);
					}
				}
				if (!userPlans.isEmpty()) {
					for (UserPlan userPlan : userPlans) {
						Integer userPlanPrice = userPlan.getHelperPlan().getPrice();
						user.getTransaction()
								.setAvailableBalance(user.getTransaction().getAvailableBalance() - userPlanPrice);
						if (!ok) {
							user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
							Account trainer = userPlan.getHelperPlan().getHelper();
							trainer.getTransaction()
									.setAvailableBalance(trainer.getTransaction().getAvailableBalance() + totalSum);
						}
						userPlan.setBought(true);
						userPlanService.save(userPlan);
					}
				}
			} else {
				redirectAttributes.addFlashAttribute("insufficientBalance", "Fonduri insuficiente");
				return "redirect:/shopping_cart";
			}
			redirectAttributes.addFlashAttribute("message", "from_buy_shopping_cart");
			return "redirect:/success";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/modify_current_balance" })
	public String modifyCurrentBalance(Model model, @RequestParam Integer transactionId,
			@RequestParam Integer availableBalance, RedirectAttributes redirectAttributes) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Transaction transaction = transactionService.findById(transactionId).get();
			UserDevice fitBuddyDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			if (availableBalance > 0) {
				transaction.setAvailableBalance(availableBalance);
				transactionService.save(transaction);
				if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy ")
						&& !userDeviceService.checkIfDeviceIsPresent(account, "Cantar Inteligent")
						&& !measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
								"HKQuantityTypeIdentifierHeight")
						|| !measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
								"HKQuantityTypeIdentifierBodyMass")) {
					redirectAttributes.addFlashAttribute("message", "height_or_weight_is_not_set");
					return "redirect:/success";
				} else {
					redirectAttributes.addFlashAttribute("message", "from_modify_current_balance");
					return "redirect:/success";
				}
			} else {
				redirectAttributes.addFlashAttribute("negativeValue", "Valorile negative nu sunt valide");
				return "redirect:/load_funds_account";
			}
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/choose_helper" })
	public String chooseHelper(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<Account> allTrainersOrNutritionists = accountService.findAllByRolesNameOrRolesName("ROLE_TRAINER",
					"ROLE_NUTRITIONIST");
			allTrainersOrNutritionists.removeIf(element -> !(element.isActive()));
			allTrainersOrNutritionists.removeIf(element -> user.getHelpers().contains(element));
			model.addAttribute("allTrainersOrNutritionists", allTrainersOrNutritionists);
			return "home/choose_helper";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/choose_helper_save" })
	public String chooseHelperSave(Model model, @RequestParam Integer helperId, RedirectAttributes redirectAttributes) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			user.getHelpers().add(accountService.findById(helperId).get());
			accountService.save(user);
			redirectAttributes.addFlashAttribute("message", "from_choose_helper");
			return "redirect:/success";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/view_helpers" })
	public String viewHelpers(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			model.addAttribute("trainersOrNutrituserionists", user.getHelpers());
			return "home/view_helpers";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/proposals_from_helpers" })
	public String viewTrainingsOrDiets(Model model, @RequestParam(required = false) Integer trainerId,
			@RequestParam(required = false) Integer nutritionistId) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<HelperPlan> plans = new HashSet<>();
			if (trainerId != null) {
				plans = helperPlanService.findAllTrainingPlansByHelperPlanNotAssociated(trainerId, user.getAccountId());
				if (user.getGender().getGender().equals("Barbat")) {
					plans.removeIf(element -> element.getForWho().getGender().equals("Femeie"));
				} else if (user.getGender().getGender().equals("Femeie")) {
					plans.removeIf(element -> element.getForWho().getGender().equals("Barbat"));
				}
				plans.removeIf(element -> element.getExercises().size() == 0);
			} else if (nutritionistId != null) {
				plans = helperPlanService.findAllDietPlansByHelperPlanNotAssociated(nutritionistId,
						user.getAccountId());
				if (user.getGender().getGender().equals("Barbat")) {
					plans.removeIf(element -> element.getForWho().getGender().equals("Femeie"));
				} else if (user.getGender().getGender().equals("Femeie")) {
					plans.removeIf(element -> element.getForWho().getGender().equals("Barbat"));
				}
				plans.removeIf(element -> element.getFoods().size() == 0);
			}
			model.addAttribute("trainerId", trainerId);
			model.addAttribute("nutritionistId", nutritionistId);
			model.addAttribute("plans", plans);
			model.addAttribute("user", user);
			return "home/proposals_from_helpers";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/view_training_plans" })
	public String viewTrainingPlans(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<UserPlan> userTrainings = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
					user.getAccountId(), "Antrenament");
			model.addAttribute("userTrainings", userTrainings);
			model.addAttribute("user", user);
			return "home/view_training_plans";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/view_diet_plans" })
	public String viewDietPlans(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<UserPlan> userDiets = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
					user.getAccountId(), "Dieta");
			model.addAttribute("userDiets", userDiets);
			model.addAttribute("user", user);
			return "home/view_diet_plans";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/exercises_done" })
	public String exercisesDone(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<ExerciseDone> exercisesDone = exerciseDoneService.findAllByUserAccountId(user.getAccountId());
			model.addAttribute("exercisesDone", exercisesDone);
			return "home/exercises_done";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/perfom_this_exercise" })
	public String perfomThisExercise(Model model, @RequestParam Integer accountId, @RequestParam Integer exerciseId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		if (account.isActive()) {
			ExerciseDone exerciseDone = new ExerciseDone();
			exerciseDone.setExercise(exerciseService.findById(exerciseId).get());
			exerciseDone.setUser(accountService.findById(accountId).get());
			exerciseDoneService.save(exerciseDone);
			UserDevice userDevice;
			Measurement measurement = new Measurement();
			if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
				userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId())
						.get();
				createMeasurement(exerciseId, measurement, userDevice, "HKQuantityTypeIdentifierActiveEnergyBurned",
						"kcal");
				measurementService.save(measurement);
			} else if (userDeviceService.checkIfDeviceIsPresent(account, "Bratara")) {
				userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId())
						.get();
				createMeasurement(exerciseId, measurement, userDevice, "HKQuantityTypeIdentifierActiveEnergyBurned",
						"kcal");
				measurementService.save(measurement);
			}
			if (!account.isActive()) {
				account.setActive(true);
				accountService.save(account);
			}
			redirectAttributes.addFlashAttribute("exerciseId", exerciseId);
			redirectAttributes.addFlashAttribute("success", true);
			return "redirect:/offers_feedback";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/view_user_devices" })
	public String viewDevices(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			model.addAttribute("userDevices", user.getUserDevices());
			model.addAttribute("user", user);
			return "home/view_user_devices";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/eat_this_food" })
	public String eatThisFood(Model model, @RequestParam Integer accountId, @RequestParam Integer foodId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		if (account.isActive()) {
			Food food = foodService.findById(foodId).get();
			FoodEaten foodEaten = new FoodEaten();
			foodEaten.setFood(food);
			foodEaten.setUser(account);
			foodEatenService.save(foodEaten);
			if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
				UserDevice userDefaultDevice = userDeviceService
						.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
				Measurement measurement = new Measurement();
				Timestamp timestampStartDate = new Timestamp(System.currentTimeMillis());
				measurement.setStartDate(timestampStartDate);
				measurement.setName("HKQuantityTypeIdentifierActiveEnergyAccumulated");
				measurement.setUnitOfMeasurement("kcal");
				measurement.setValue(food.getCalories());
				measurement.setUserDevice(userDefaultDevice);
				measurement.setEndDate(null);
				measurement.setFromXml(false);
				measurementService.save(measurement);
				redirectAttributes.addFlashAttribute("foodId", foodId);
				redirectAttributes.addFlashAttribute("success", true);
			}
			return "redirect:/offers_feedback_food";
		} else {
			return "redirect:/home";
		}
	}

	@ModelAttribute("foodId")
	public Integer getFoodId() {
		return new Integer(-1);
	}

	@ModelAttribute("exerciseId")
	public Integer getExerciseId() {
		return new Integer(-1);
	}

	@ModelAttribute("success")
	public boolean getSucces() {
		return false;
	}

	@GetMapping(path = { "/offers_feedback_food" })
	public String reviewForFood(@ModelAttribute("foodId") Integer foodId, @ModelAttribute("success") boolean success,
			Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (foodId != -1) {
				model.addAttribute("foodId", foodId);
				model.addAttribute("success", success);
			} else if (foodId == -1) {
				model.addAttribute("error", true);
			}
			return "home/offers_feedback_food";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/offers_feedback" })
	public String reviewForExercise(@ModelAttribute("exerciseId") Integer exerciseId,
			@ModelAttribute("success") boolean success, Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (exerciseId != -1) {
				model.addAttribute("exerciseId", exerciseId);
				model.addAttribute("success", success);
			} else if (exerciseId == -1) {
				model.addAttribute("error", true);
			}
			return "home/offers_feedback";
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/offers_feedback_save" })
	public String reviewForExerciseSave(Model model, @RequestParam(required = false) Integer foodId,
			@RequestParam(required = false) Integer exerciseId, @RequestParam(required = false) String messageReview,
			@RequestParam(required = false) Integer number) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			if (exerciseId != null) {
				Exercise exercise = exerciseService.findById(exerciseId).get();
				ExerciseFeedback exerciseFeedback = new ExerciseFeedback();
				exerciseFeedback.setExercise(exercise);
				exerciseFeedback.setUser(user);
				exerciseFeedback.setMessage(messageReview);
				exerciseFeedback.setRating(number);
				exerciseFeedbackService.save(exerciseFeedback);
				return "redirect:/exercises_done";
			} else if (foodId != null) {
				Food food = foodService.findById(foodId).get();
				FoodFeedback foodFeedback = new FoodFeedback();
				foodFeedback.setFood(food);
				foodFeedback.setMessage(messageReview);
				foodFeedback.setRating(number);
				foodFeedback.setUser(user);
				foodFeedbackService.save(foodFeedback);
				return "redirect:/foods_eaten";
			} else {
				return "redirect:/error";
			}
		} else {
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/delete_device" })
	public String deleteDevice(Model model, @RequestParam Integer deviceId) {
		Device device = deviceService.findById(deviceId).get();
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (device.getTypeMeasurements() != null) {
				device.getTypeMeasurements().forEach(element -> typeMeasurementService
						.deleteByDeviceIdAndTypeMeasurementId(deviceId, element.getTypeMeasurementId()));
			}
			UserDevice userDevice = userDeviceService
					.findByDeviceNameAndUserAccountId(device.getName(), account.getAccountId()).get();
			if (userDevice.getMeasurements() != null) {
				userDevice.getMeasurements()
						.forEach(element -> measurementService.deleteByUserDeviceUserDeviceIdAndMeasurementId(
								userDevice.getUserDeviceId(), element.getMeasurementId()));
			}
			userDeviceService.deleteByDeviceIdAndUserId(deviceId, account.getAccountId());
			deviceService.deleteByDeviceId(deviceId);
			return "redirect:/view_user_devices";
		} else {
			return "redirect:/home";
		}
	}

	@GetMapping(path = { "/foods_eaten" })
	public String foodsEaten(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<FoodEaten> foodEaten = foodEatenService.findAllByUserAccountId(user.getAccountId());
			model.addAttribute("foodEaten", foodEaten);
			return "home/foods_eaten";
		} else {
			return "redirect:/home";
		}
	}

	private void createMeasurement(Integer exerciseId, Measurement measurement, UserDevice userDevice,
			String nameOfMeasurement, String unitOfMeasurement) {
		Timestamp timestampStartDate = new Timestamp(System.currentTimeMillis());
		measurement.setStartDate(timestampStartDate);
		measurement.setEndDate(null);
		measurement.setName(nameOfMeasurement);
		measurement.setUnitOfMeasurement(unitOfMeasurement);
		measurement.setValue(exerciseService.findById(exerciseId).get().getCaloriesBurned());
		measurement.setUserDevice(userDevice);
		measurement.setFromXml(false);
	}
}
