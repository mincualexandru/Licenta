package com.web.controller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
		Account account = getAccountConnected();
		Set<TypeMeasurement> typeMeasurements = typeMeasurementService.findAll();
		Set<Measurement> userMeasurements = new HashSet<>();
		boolean bandAlreadyBought = false;
		boolean scaleAlreadyBought = false;
		for (UserDevice userDevice : account.getUserDevices()) {
			if (userDevice.isBought()) {
				for (TypeMeasurement type : typeMeasurements) {
					userMeasurements.addAll(measurementService.findLast3ByNameAndUserDeviceId(type.getType(),
							userDevice.getUserDeviceId()));
				}
				if (userDevice.getDevice().getName().equals("Bratara")) {
					bandAlreadyBought = true;
				}
				if (userDevice.getDevice().getName().equals("Cantar Inteligent")) {
					scaleAlreadyBought = true;
				}
			}
		}

		List<Measurement> userMeasurementsSorted = userMeasurements.stream()
				.sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());

		model.addAttribute("bandAlreadyBought", bandAlreadyBought);
		model.addAttribute("scaleAlreadyBought", scaleAlreadyBought);
		model.addAttribute("user", account);
		model.addAttribute("typeMeasurements", typeMeasurements);
		model.addAttribute("userMeasurements", userMeasurementsSorted);
		return "home/home";

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

	// DE MODIFICAT SI PENTRU DIETE

	@GetMapping(path = { "/shopping_cart" })
	public String shoppingCart(Model model) {
		Account user = getAccountConnected();
		Set<Product> products = new HashSet<>();
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
		Set<UserPlan> userPlans = userPlanService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
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
		Integer totalCostOfDietPlans = 0;
		for (UserPlan userPlan : userPlans) {
			totalCostOfDietPlans += userPlan.getHelperPlan().getPrice();
			Product product = new Product();
			product.setProductId(userPlan.getHelperPlan().getHelperPlanId());
			product.setProductName(userPlan.getHelperPlan().getName());
			product.setPrice(userPlan.getHelperPlan().getPrice());
			product.setForWho(userPlan.getHelperPlan().getForWho());
			product.setType("trainingPlan");
			products.add(product);
		}
		model.addAttribute("products", products);
		model.addAttribute("totalSum", totalCostOfDevices + totalCostOfDietPlans);
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

	// DE MODIFICAT SI PENTRU DIETE

	@PostMapping(path = { "/buy_shopping_cart" })
	public String addToShoppingCart(Model model, @RequestParam Integer userId,
			@RequestParam(required = false) Integer totalSum, RedirectAttributes redirectAttributes) {
		Account user = accountService.findById(userId).get();
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
		Set<UserPlan> userTrainingPlans = userPlanService.findAllByBoughtAndUserAccountId(false, user.getAccountId());
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
			if (!userTrainingPlans.isEmpty()) {
				for (UserPlan userTraining : userTrainingPlans) {
					Integer trainingPlanPrice = userTraining.getHelperPlan().getPrice();
					user.getTransaction()
							.setAvailableBalance(user.getTransaction().getAvailableBalance() - trainingPlanPrice);
					if (!ok) {
						user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
						Account trainer = userTraining.getHelperPlan().getHelper();
						trainer.getTransaction()
								.setAvailableBalance(trainer.getTransaction().getAvailableBalance() + totalSum);
					}
					userTraining.setBought(true);
					userPlanService.save(userTraining);
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
		Set<HelperPlan> trainingPlans = helperPlanService.findAllTrainingPlansByHelperPlanNotAssociated(trainerId,
				user.getAccountId());
		if (user.getGender().getGender().equals("Barbat")) {
			trainingPlans.removeIf(element -> element.getForWho().getGender().equals("Femeie"));
		} else if (user.getGender().getGender().equals("Femeie")) {
			trainingPlans.removeIf(element -> element.getForWho().getGender().equals("Barbat"));
		}
		trainingPlans.removeIf(element -> element.getExercises().size() == 0);
		model.addAttribute("trainingPlans", trainingPlans);
		model.addAttribute("user", user);
		return "home/trainer_training_plans";
	}

	@PostMapping(path = { "/nutritionist_diet_plans" })
	public String nutritionistDietPlans(Model model, @RequestParam Integer nutritionistId) {
		Account user = getAccountConnected();
		Set<HelperPlan> dietPlans = helperPlanService.findAllDietPlansByHelperPlanNotAssociated(nutritionistId,
				user.getAccountId());
		if (user.getGender().getGender().equals("Barbat")) {
			dietPlans.removeIf(element -> element.getForWho().getGender().equals("Femeie"));
		} else if (user.getGender().getGender().equals("Femeie")) {
			dietPlans.removeIf(element -> element.getForWho().getGender().equals("Barbat"));
		}
		dietPlans.removeIf(element -> element.getExercises().size() == 0);
		model.addAttribute("dietPlans", dietPlans);
		model.addAttribute("user", user);
		return "home/nutritionist_diet_plans";
	}

	@GetMapping(path = { "/view_training_plans" })
	public String viewTrainingPlans(Model model) {
		Account user = getAccountConnected();
		Set<UserPlan> userTrainings = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
				user.getAccountId(), "Antrenament");
		model.addAttribute("userTrainings", userTrainings);
		model.addAttribute("user", user);
		return "home/view_training_plans";
	}

	@GetMapping(path = { "/view_diet_plans" })
	public String viewDietPlans(Model model) {
		Account user = getAccountConnected();
		Set<UserPlan> userDiets = userPlanService.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(true,
				user.getAccountId(), "Dieta");
		model.addAttribute("userDiets", userDiets);
		model.addAttribute("user", user);
		return "home/view_diet_plans";
	}

	@GetMapping(path = { "/exercises_done" })
	public String exercisesDone(Model model) {
		Account user = getAccountConnected();
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
		if (account.getUserDevices().size() == 0) {
			Measurement measurement = new Measurement();
			Timestamp timestampStartDate = new Timestamp(System.currentTimeMillis());
			measurement.setStartDate(timestampStartDate);
			measurement
					.setName(typeMeasurementService.findByType("HKQuantityTypeIdentifierActiveEnergyBurned").getType());
			measurement.setUnitOfMeasurement("kcal");
			measurement.setValue(exerciseService.findById(exerciseId).get().getCaloriesBurned());
			measurement.setUserDevice(null);
			measurement.setFromXml(false);
			measurementService.save(measurement);
		} else {
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
					measurement.setFromXml(false);
					measurementService.save(measurement);
				}
			}
		}
		redirectAttributes.addFlashAttribute("exerciseId", exerciseId);
		return "redirect:/offers_feedback";
	}

	@GetMapping(path = { "/view_devices" })
	public String viewDevices(Model model) {
		Account user = getAccountConnected();
		model.addAttribute("userDevices", user.getUserDevices());
		return "home/view_devices";
	}

	@PostMapping(path = { "/eat_this_food" })
	public String eatThisFood(Model model, @RequestParam Integer accountId, @RequestParam Integer foodId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		FoodEaten foodEaten = new FoodEaten();
		foodEaten.setFood(foodService.findById(foodId).get());
		foodEaten.setUser(accountService.findById(accountId).get());
		foodEatenService.save(foodEaten);
		redirectAttributes.addFlashAttribute("foodId", foodId);
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
		Account user = getAccountConnected();
		if (exerciseId != null) {
			Exercise exercise = exerciseService.findById(exerciseId).get();
			ExerciseFeedback exerciseFeedback = new ExerciseFeedback();
			exerciseFeedback.setExercise(exercise);
			exerciseFeedback.setUser(user);
			exerciseFeedback.setMessage(messageReview);
			exerciseFeedback.setRating(number);
			Measurement measurement = measurementService.findByEndDate(null);
			Timestamp startDate = measurement.getStartDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(startDate.getTime());
			calendar.add(Calendar.MINUTE, numberOfMinutes);
			Timestamp endDate = new Timestamp(calendar.getTime().getTime());
			measurement.setEndDate(endDate);
			measurementService.save(measurement);
			exerciseFeedbackService.save(exerciseFeedback);
			return "redirect:/exercises_done";
		} else {
			LOGGER.info("Id este " + foodId);
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
		Account user = getAccountConnected();
		Set<FoodEaten> foodEaten = foodEatenService.findAllByUserAccountId(user.getAccountId());
		model.addAttribute("foodEaten", foodEaten);
		return "home/foods_eaten";
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
