package com.web.controller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.web.utils.Product;

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
	public String home(Model model) {
		Account account = accountService.getAccountConnected();

		if (account.getTransaction().getAvailableBalance() == 0) {
			return "redirect:/load_funds_account";
		}

		if ((userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).isPresent()
				|| userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId()).isPresent())
				&& !(userDeviceService.findByDeviceNameAndUserAccountId("Cantar Inteligent", account.getAccountId())
						.isPresent())
				&& (!(measurementService.findByName("HKQuantityTypeIdentifierHeight").isPresent())
						|| !(measurementService.findByName("HKQuantityTypeIdentifierBodyMass").isPresent()))) {
			return "redirect:/set_height_and_weight";
		}

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
		model.addAttribute("user", account);
		model.addAttribute("typeMeasurements", typeMeasurements);
		model.addAttribute("userMeasurements", userMeasurementsSorted);
		return "home/home";

	}

	@GetMapping(path = { "/set_height_and_weight" })
	public String setHeightAndWeight(Model model) {
		boolean heightIsNotCalculated = true;
		boolean weightIsNotCalculated = true;
		if ((measurementService.findByName("HKQuantityTypeIdentifierHeight").isPresent())) {
			heightIsNotCalculated = false;
		} else if ((measurementService.findByName("HKQuantityTypeIdentifierBodyMass").isPresent())) {
			weightIsNotCalculated = false;
		}
		model.addAttribute("heightIsNotCalculated", heightIsNotCalculated);
		model.addAttribute("weightIsNotCalculated", weightIsNotCalculated);
		return "home/set_height_and_weight";
	}

	@PostMapping(path = { "/set_height_and_weight_save" })
	public String setHeightAndWeightSave(Model model, @RequestParam(required = false) Float weight,
			@RequestParam(required = false) Float height) {
		Account account = accountService.getAccountConnected();
		UserDevice userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId())
				.get();
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
		return "redirect:/home";
	}

	@GetMapping(path = { "/current_balance" })
	public String availableBalance(Model model) {
		Account user = accountService.getAccountConnected();
		model.addAttribute("transaction", user.getTransaction());
		return "home/current_balance";
	}

	@GetMapping(path = { "/load_funds_account" })
	public String loadFundsAccount(Model model) {
		Account user = accountService.getAccountConnected();
		model.addAttribute("transaction", user.getTransaction());
		return "home/load_funds_account";
	}

	@PostMapping(path = { "/add_device_to_shopping_cart" })
	public String buyScale(Model model, RedirectAttributes redirectAttributes, @RequestParam String deviceName) {
		Account account = accountService.getAccountConnected();
		Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
		Device newDevice = new Device();
		UserDevice userDevice = new UserDevice();
		if (deviceName.equals("Fit Buddy")) {
			typeMeasurements.removeAll(typeMeasurements);
			typeMeasurements = typeMeasurementService.findTypeMeasurementsForFitBuddy();
			deviceService.createDevice(newDevice, typeMeasurements, "Fit Buddy", 0);
			deviceService.save(newDevice);
			userDevice.setDevice(newDevice);
			userDevice.setUser(account);
			userDevice.setBought(true);
			userDeviceService.save(userDevice);
			return "redirect:/home";
		} else {
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
		}
	}

	@GetMapping(path = { "/view_feedbacks_from_helper" })
	public String viewFeedbacks(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<HelperFeedback> feedbacks = helperFeedbackService.findAllByLearnerAccountId(account.getAccountId());
		LOGGER.info(feedbacks.size());
		model.addAttribute("feedbacks", feedbacks);
		return "home/view_feedbacks_from_helper";
	}

	@GetMapping(path = { "/default_device" })
	public String defaultDevice(Model model) {
		Account account = accountService.getAccountConnected();
		model.addAttribute("account", account);
		return "home/default_device";
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
	}

	@GetMapping(path = { "/shopping_cart" })
	public String shoppingCart(Model model) {
		Account user = accountService.getAccountConnected();
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
	}

	@PostMapping(path = { "/delete_product_from_shopping_cart" })
	public String deleteProductFromShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam Integer productId, @RequestParam String productName, RedirectAttributes redirectAttributes) {
		if (productName.equals("Bratara") || productName.equals("Cantar Inteligent")) {
			UserDevice userDeviceToDelete = userDeviceService.findByUserAccountIdAndDeviceDeviceId(userId, productId);
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
			UserPlan userPlanToDelete = userPlanService.findByUserAccountIdAndHelperPlanHelperPlanId(userId, productId);
			userPlanService.deleteByUserPlanId(userPlanToDelete.getUserPlanId());
		}
		return "redirect:/shopping_cart";
	}

	@PostMapping(path = { "/buy_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam(required = false) Integer totalSum, RedirectAttributes redirectAttributes) {
		Account user = accountService.findById(userId).get();
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
		return "redirect:/home";
	}

	@PostMapping(path = { "/modify_current_balance" })
	public String modifyCurrentBalance(Model model, @RequestParam Integer transactionId,
			@RequestParam Integer availableBalance, RedirectAttributes redirectAttributes) {
		Account account = accountService.getAccountConnected();
		Transaction transaction = transactionService.findById(transactionId).get();
		if (availableBalance > 0) {
			transaction.setAvailableBalance(availableBalance);
			transactionService.save(transaction);
			if ((userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).isPresent()
					|| userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId())
							.isPresent())
					&& !(userDeviceService.findByDeviceNameAndUserAccountId("Cantar Inteligent", account.getAccountId())
							.isPresent())
					&& (!(measurementService.findByName("HKQuantityTypeIdentifierHeight").isPresent())
							|| !(measurementService.findByName("HKQuantityTypeIdentifierBodyMass").isPresent()))) {
				return "redirect:/set_height_and_weight";
			} else {
				return "redirect:/current_balance";
			}
		} else {
			if ((userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).isPresent()
					|| userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId())
							.isPresent())
					&& !(userDeviceService.findByDeviceNameAndUserAccountId("Cantar Inteligent", account.getAccountId())
							.isPresent())
					&& (!(measurementService.findByName("HKQuantityTypeIdentifierHeight").isPresent())
							|| !(measurementService.findByName("HKQuantityTypeIdentifierBodyMass").isPresent()))) {
				redirectAttributes.addFlashAttribute("negativeValue", "Valorile negative nu sunt valide");
				return "redirect:/load_funds_account";
			} else {
				redirectAttributes.addFlashAttribute("negativeValue", "Valorile negative nu sunt valide");
				return "redirect:/current_balance";
			}
		}
	}

	@GetMapping(path = { "/choose_helper" })
	public String chooseHelper(Model model) {
		Account user = accountService.getAccountConnected();
		Set<Account> allTrainersOrNutritionists = accountService.findAllByRolesNameOrRolesName("ROLE_TRAINER",
				"ROLE_NUTRITIONIST");
		allTrainersOrNutritionists.removeIf(element -> !(element.isActive()));
		allTrainersOrNutritionists.removeIf(element -> user.getHelpers().contains(element));
		model.addAttribute("allTrainersOrNutritionists", allTrainersOrNutritionists);
		return "home/choose_helper";
	}

	@PostMapping(path = { "/choose_helper_save" })
	public String chooseHelperSave(Model model, @RequestParam Integer helperId) {
		Account user = accountService.getAccountConnected();
		user.getHelpers().add(accountService.findById(helperId).get());
		accountService.save(user);
		return "redirect:/choose_helper";
	}

	@GetMapping(path = { "/view_helpers" })
	public String viewHelpers(Model model) {
		Account user = accountService.getAccountConnected();
		model.addAttribute("trainersOrNutrituserionists", user.getHelpers());
		return "home/view_helpers";
	}

	@PostMapping(path = { "/proposals_from_helpers" })
	public String viewTrainingsOrDiets(Model model, @RequestParam(required = false) Integer trainerId,
			@RequestParam(required = false) Integer nutritionistId) {
		Account user = accountService.getAccountConnected();
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
			plans = helperPlanService.findAllDietPlansByHelperPlanNotAssociated(nutritionistId, user.getAccountId());
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
	}

	@GetMapping(path = { "/view_training_plans" })
	public String viewTrainingPlans(Model model) {
		Account user = accountService.getAccountConnected();
		Set<UserPlan> userTrainings = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
				user.getAccountId(), "Antrenament");
		model.addAttribute("userTrainings", userTrainings);
		model.addAttribute("user", user);
		return "home/view_training_plans";
	}

	@GetMapping(path = { "/view_diet_plans" })
	public String viewDietPlans(Model model) {
		Account user = accountService.getAccountConnected();
		Set<UserPlan> userDiets = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
				user.getAccountId(), "Dieta");
		model.addAttribute("userDiets", userDiets);
		model.addAttribute("user", user);
		return "home/view_diet_plans";
	}

	@GetMapping(path = { "/exercises_done" })
	public String exercisesDone(Model model) {
		Account user = accountService.getAccountConnected();
		Set<ExerciseDone> exercisesDone = exerciseDoneService.findAllByUserAccountId(user.getAccountId());
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
		UserDevice userDevice;
		if (userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId()).isPresent()) {
			Measurement measurement = new Measurement();
			userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId()).get();
			createMeasurement(exerciseId, measurement, userDevice, "HKQuantityTypeIdentifierActiveEnergyBurned",
					"kcal");
			measurementService.save(measurement);
		} else if (userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId())
				.isPresent()) {
			Measurement measurement = new Measurement();
			userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			createMeasurement(exerciseId, measurement, userDevice, "HKQuantityTypeIdentifierActiveEnergyBurned",
					"kcal");
			measurementService.save(measurement);
		}
		redirectAttributes.addFlashAttribute("exerciseId", exerciseId);
		return "redirect:/offers_feedback";
	}

	@GetMapping(path = { "/view_devices" })
	public String viewDevices(Model model) {
		Account user = accountService.getAccountConnected();
		model.addAttribute("userDevices", user.getUserDevices());
		return "home/view_devices";
	}

	@PostMapping(path = { "/eat_this_food" })
	public String eatThisFood(Model model, @RequestParam Integer accountId, @RequestParam Integer foodId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		Food food = foodService.findById(foodId).get();
		FoodEaten foodEaten = new FoodEaten();
		foodEaten.setFood(food);
		foodEaten.setUser(account);
		foodEatenService.save(foodEaten);
		if (userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).isPresent()) {
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
		}
		return "redirect:/offers_feedback_food";
	}

	@GetMapping(path = { "/offers_feedback_food" })
	public String reviewForFood(@ModelAttribute("foodId") Integer foodId, Model model) {
		model.addAttribute("foodId", foodId);
		return "home/offers_feedback_food";
	}

	@GetMapping(path = { "/offers_feedback" })
	public String reviewForExercise(@ModelAttribute("exerciseId") Integer exerciseId, Model model) {
		model.addAttribute("exerciseId", exerciseId);
		return "home/offers_feedback";
	}

	@PostMapping(path = { "/offers_feedback_save" })
	public String reviewForExerciseSave(Model model, @RequestParam(required = false) Integer foodId,
			@RequestParam(required = false) Integer exerciseId, @RequestParam(required = false) Integer numberOfMinutes,
			@RequestParam(required = false) String messageReview, @RequestParam(required = false) Integer number) {
		Account user = accountService.getAccountConnected();
		UserDevice userDevice = null;
		if (exerciseId != null) {
			Exercise exercise = exerciseService.findById(exerciseId).get();
			ExerciseFeedback exerciseFeedback = new ExerciseFeedback();
			exerciseFeedback.setExercise(exercise);
			exerciseFeedback.setUser(user);
			exerciseFeedback.setMessage(messageReview);
			exerciseFeedback.setRating(number);
			exerciseFeedbackService.save(exerciseFeedback);
			if (userDeviceService.findByDeviceNameAndUserAccountId("Bratara", user.getAccountId()).isPresent()) {
				userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Bratara", user.getAccountId()).get();
				LOGGER.info("Se face feedback pentru Bratara");
			} else if (userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", user.getAccountId())
					.isPresent()) {
				userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", user.getAccountId()).get();
				LOGGER.info("Se face feedback pentru buddy");
			}
			Measurement measurement = measurementService.findByUserDeviceIdAndNameAndEndDate(
					userDevice.getUserDeviceId(), "HKQuantityTypeIdentifierActiveEnergyBurned");
			editMeasurement(numberOfMinutes, measurement);
			return "redirect:/exercises_done";
		} else {
			Food food = foodService.findById(foodId).get();
			FoodFeedback foodFeedback = new FoodFeedback();
			foodFeedback.setFood(food);
			foodFeedback.setMessage(messageReview);
			foodFeedback.setRating(number);
			foodFeedback.setUser(user);
			foodFeedbackService.save(foodFeedback);
			return "redirect:/foods_eaten";
		}
	}

	@GetMapping(path = { "/foods_eaten" })
	public String foodsEaten(Model model) {
		Account user = accountService.getAccountConnected();
		Set<FoodEaten> foodEaten = foodEatenService.findAllByUserAccountId(user.getAccountId());
		model.addAttribute("foodEaten", foodEaten);
		return "home/foods_eaten";
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

	private void editMeasurement(Integer numberOfMinutes, Measurement measurement) {
		Timestamp startDate = measurement.getStartDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startDate.getTime());
		calendar.add(Calendar.MINUTE, numberOfMinutes);
		Timestamp endDate = new Timestamp(calendar.getTime().getTime());
		measurement.setEndDate(endDate);
		measurementService.save(measurement);
	}
}
