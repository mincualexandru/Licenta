package com.web.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.Exercise;
import com.web.model.ExerciseDone;
import com.web.model.Food;
import com.web.model.FoodEaten;
import com.web.model.HelperPlan;
import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountInformationService;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.FoodEatenService;
import com.web.service.FoodFeedbackService;
import com.web.service.FoodService;
import com.web.service.HelperPlanService;
import com.web.service.MeasurementService;
import com.web.service.RoleService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;

@Controller
public class AdminController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private AccountInformationService accountInformationService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserPlanService userPlanService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private FoodEatenService foodEatenService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private HelperPlanService helperPlanService;

	@Autowired
	private FoodService foodService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private FoodFeedbackService foodFeedbackService;

	@Autowired
	private DeviceService deviceService;

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@GetMapping(path = { "/admin" })
	public String admin(Model model) {
		Integer totalProfit = 0;
		Integer totalPayments = 0;
		String previousValue = null;
		int number = 0;
		Map<String, Integer> chartNumberOfAccountsByDay = new TreeMap<>();
		Map<String, Integer> chartNumberOfMeasurementsByDay = new TreeMap<>();
		Map<String, Integer> chartNumberOfUserPlansByDay = new TreeMap<>();
		Account admin = accountService.getAccountConnected();
		List<Account> accounts = accountService.findAll().stream()
				.sorted((e1, e2) -> e1.getDateOfCreation().compareTo(e2.getDateOfCreation()))
				.collect(Collectors.toList());
		accounts.removeIf(element -> element.getUsername() == admin.getUsername());
		for (Account account : accounts) {
			String value = account.getDateOfCreation().toLocalDateTime().toLocalDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			chartNumberOfAccountsByDay.put(value, number);
		}
		List<Measurement> measurements = measurementService.findAll().stream()
				.sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).collect(Collectors.toList());
		for (Measurement measurement : measurements) {
			String value = measurement.getStartDate().toLocalDateTime().toLocalDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			chartNumberOfMeasurementsByDay.put(value, number);
		}
		List<UserPlan> userPlans = userPlanService.findAll().stream()
				.sorted((e1, e2) -> e1.getDateOfPurchase().compareTo(e2.getDateOfPurchase()))
				.collect(Collectors.toList());
		for (UserPlan plan : userPlans) {
			String value = plan.getDateOfPurchase().toLocalDateTime().toLocalDate()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			totalProfit += plan.getHelperPlan().getPrice() * 70 / 100;
			totalPayments += plan.getHelperPlan().getPrice() - plan.getHelperPlan().getPrice() * 70 / 100;
			chartNumberOfUserPlansByDay.put(value, number);
		}
		for (UserDevice userDevice : userDeviceService.findAll()) {
			totalProfit += userDevice.getDevice().getPrice();
		}
		Map<HelperPlan, Long> theMostBoughtPlans = userPlans.stream().map(element -> element.getHelperPlan())
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		Map<HelperPlan, Long> sortedMapDesc = sortByValue(theMostBoughtPlans, false);
		model.addAttribute("exerciseFeedbacks", exerciseFeedbackService.findAll());
		model.addAttribute("foodFeedbacks", foodFeedbackService.findAll());
		model.addAttribute("totalProfit", totalProfit);
		model.addAttribute("totalPayments", totalPayments);
		model.addAttribute("account", admin);
		model.addAttribute("accounts", accounts);
		model.addAttribute("chartNumberOfAccountsByDay", chartNumberOfAccountsByDay);
		model.addAttribute("measurements", measurements);
		model.addAttribute("chartNumberOfMeasurementsByDay", chartNumberOfMeasurementsByDay);
		model.addAttribute("userPlans", userPlans);
		model.addAttribute("chartNumberOfUserPlansByDay", chartNumberOfUserPlansByDay);
		model.addAttribute("theMostBoughtPlans", sortedMapDesc);
		return "admin/admin";
	}

	private static Map<HelperPlan, Long> sortByValue(Map<HelperPlan, Long> unsortMap, final boolean order) {
		List<Entry<HelperPlan, Long>> list = new LinkedList<>(unsortMap.entrySet());
		list.sort((o1, o2) -> order
				? o1.getValue().compareTo(o2.getValue()) == 0 ? o1.getKey().compareTo(o2.getKey())
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? o2.getKey().compareTo(o1.getKey())
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));

	}

	@GetMapping(path = { "/validations-accounts" })
	public String validationsAccounts(Model model) {
		Set<Account> inactiveAccounts = accountService.findAllByActive(false);
		inactiveAccounts.removeIf(element -> element.getAccountInformation() == null);
		model.addAttribute("inactiveAccounts", inactiveAccounts);
		return "admin/validations-accounts";
	}

	@PostMapping(path = { "/validate-account" })
	public String validateAccount(Model model, @RequestParam Integer accountId) {
		model.addAttribute("account", accountService.findById(accountId).get());
		model.addAttribute("accountInformation", accountInformationService.findByAccountAccountId(accountId));
		return "admin/validate-account";
	}

	@PostMapping(path = { "/validate-account-save" })
	public String validateAccountSave(@RequestParam Integer accountId, Model model) {
		Account account = accountService.findById(accountId).get();
		LOGGER.info("Arata id " + account.getAccountId());
		account.setActive(true);
		accountService.save(account);
		return "redirect:/validations-accounts";
	}

	@GetMapping(path = { "/accounts" })
	public String getAllUsers(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		LOGGER.info("Numele contului curent este " + account.getUsername());
		Set<Account> accounts = accountService.findAll();
		accounts.removeIf(element -> element.getUsername() == account.getUsername());
		Set<Account> users = new HashSet<>();
		for (Account account1 : accounts) {
			if (account1.getRoles().contains(roleService.findByName("ROLE_USER").get())) {
				users.add(account1);
			}
		}
		accounts.removeAll(users);
		model.addAttribute("users", users);
		model.addAttribute("helpers", accounts);
		return "admin/accounts";
	}

	@PostMapping(path = { "/view_account_details" })
	public String viewAccountDetails(Model model, @RequestParam Integer accountId) {
		model.addAttribute("account", accountService.findById(accountId).get());
		model.addAttribute("accountInformation", accountInformationService.findByAccountAccountId(accountId));
		return "admin/view_account_details";
	}

	@PostMapping(path = { "/create-user-save" })
	public String createUserSave(Model model, @ModelAttribute("user") Account user) {
		accountService.save(user);
		return "redirect:/accounts";
	}

	@PostMapping(path = { "/delete-user" })
	public String deleteUser(Model model, @RequestParam Integer userId) {
		Account user = accountService.findById(userId).get();
		accountService.delete(user);
		return "redirect:/accounts";
	}

	@PostMapping(path = { "/update-user" })
	public String modifyUser(Model model, @RequestParam Integer userId, @RequestParam String name) {
		Account user = accountService.findById(userId).get();
		user.setUsername(name);
		accountService.save(user);
		return "redirect:/accounts";
	}

	@PostMapping(path = { "/user_activity" })
	public String userActivity(Model model, @RequestParam Integer accountId) {
		Account account = accountService.findById(accountId).get();
		Integer number = null;
		String previousValue = null;
		Set<UserPlan> userPlans = userPlanService.findAllByBoughtAndUserAccountId(true, account.getAccountId());
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(true, account.getAccountId());
		Set<Measurement> measurements = new HashSet<>();
		userDevices.forEach(userDevice -> measurements.addAll(userDevice.getMeasurements()));
		List<Measurement> userMeasurementsSorted = measurements.stream()
				.sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())).collect(Collectors.toList());
		Set<ExerciseDone> exercisesDone = exerciseDoneService.findAllByUserAccountId(account.getAccountId());
		Set<FoodEaten> foodsEaten = foodEatenService.findAllByUserAccountId(account.getAccountId());
		Map<String, Integer> chartExercisesDone = new TreeMap<>();
		Map<String, Integer> chartFoodEaten = new TreeMap<>();
		Map<String, Integer> chartMeasurements = new TreeMap<>();
		for (ExerciseDone exerciseDone : exercisesDone) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime startDateTime = exerciseDone.getDateOfExecution().toLocalDateTime();
			LocalDate startDate = startDateTime.toLocalDate();
			String formatDate = startDate.format(formatter);
			String value = formatDate;
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			chartExercisesDone.put(value, number);
		}
		for (FoodEaten foodEaten : foodsEaten) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime startDateTime = foodEaten.getDateOfExecution().toLocalDateTime();
			LocalDate startDate = startDateTime.toLocalDate();
			String formatDate = startDate.format(formatter);
			String value = formatDate;
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			chartFoodEaten.put(value, number);
		}
		for (Measurement measurement : userMeasurementsSorted) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDateTime startDateTime = measurement.getStartDate().toLocalDateTime();
			LocalDate startDate = startDateTime.toLocalDate();
			String formatDate = startDate.format(formatter);
			String value = formatDate;
			if (!value.equals(previousValue)) {
				number = 0;
				previousValue = value;
			}
			number++;
			chartMeasurements.put(value, number);
		}
		boolean passedTenDaysExerciseDone = false;
		boolean passedTenDaysFoodEaten = false;
		LocalDateTime dateTimeNow = LocalDateTime.now();
		if (exerciseDoneService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			ExerciseDone exerciseDone = exerciseDoneService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId)
					.get();
			Duration duration = Duration.between(exerciseDone.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				System.out.println("passedTenDaysExerciseDone");
				passedTenDaysExerciseDone = true;
			}
		}
		if (foodEatenService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).isPresent()) {
			FoodEaten foodEaten = foodEatenService.findTopByUserAccountIdOrderByDateOfExecutionDesc(accountId).get();
			Duration duration = Duration.between(foodEaten.getDateOfExecution().toLocalDateTime(), dateTimeNow);
			if (duration.toDays() > 10) {
				System.out.println("passedTenDaysFoodEaten");
				passedTenDaysFoodEaten = true;
			}
		}
		model.addAttribute("passedTenDaysFoodEaten", passedTenDaysFoodEaten);
		model.addAttribute("passedTenDaysExerciseDone", passedTenDaysExerciseDone);
		model.addAttribute("chartMeasurements", chartMeasurements);
		model.addAttribute("chartExercisesDone", chartExercisesDone);
		model.addAttribute("chartFoodEaten", chartFoodEaten);
		model.addAttribute("devices", userDevices);
		model.addAttribute("plans", userPlans);
		model.addAttribute("account", account);
		return "admin/user_activity";
	}

	@PostMapping(path = { "/helper_activity" })
	public String helperActivity(Model model, @RequestParam Integer accountId) {
		Account account = accountService.findById(accountId).get();
		System.out.println(account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get()));
		Set<HelperPlan> helperPlans = account.getPlans();
		Set<Exercise> exercises = new HashSet<>();
		Set<Food> foods = new HashSet<>();
		Set<Account> learners = new HashSet<>();
		helperPlans.forEach(element -> foods.addAll(element.getFoods()));
		helperPlans.forEach(element -> exercises.addAll(element.getExercises()));
		List<Exercise> exercisesSorted = exercises.stream()
				.sorted((e1, e2) -> e1.getCreateDateTime().compareTo(e2.getCreateDateTime()))
				.collect(Collectors.toList());
		List<Food> foodsSorted = foods.stream()
				.sorted((e1, e2) -> e1.getCreateDateTime().compareTo(e2.getCreateDateTime()))
				.collect(Collectors.toList());
		accountService.findAllLearnersByHelperId(account.getAccountId())
				.forEach(element -> learners.add(accountService.findById(element).get()));
		Integer number = null;
		String previousValue = null;
		Map<String, Integer> chart = new TreeMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (Exercise exerciseDone : exercisesSorted) {
			String formatDate = exerciseDone.getCreateDateTime().toLocalDateTime().toLocalDate().format(formatter);
			if (!formatDate.equals(previousValue)) {
				number = 0;
				previousValue = formatDate;
			}
			number++;
			chart.put(formatDate, number);
		}
		for (Food exerciseDone : foodsSorted) {
			String formatDate = exerciseDone.getCreateDateTime().toLocalDateTime().toLocalDate().format(formatter);
			if (!formatDate.equals(previousValue)) {
				number = 0;
				previousValue = formatDate;
			}
			number++;
			chart.put(formatDate, number);
		}
//		boolean passedTenDaysExercise = false;
//		boolean passedTenDaysFood = false;
//		LocalDateTime dateTimeNow = LocalDateTime.now();
//		boolean isNutritionist = false;
//		boolean isTrainer = false;
//		if (account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
//			if (foodService.findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId).isPresent()) {
//				Food food = foodService.findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId).get();
//				Duration duration = Duration.between(food.getCreateDateTime().toLocalDateTime(), dateTimeNow);
//				if (duration.toDays() > 10) {
//					System.out.println("passedTenDaysFood");
//					passedTenDaysFood = true;
//				}
//			}
//			isNutritionist = true;
//		} else if (account.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
//			if (exerciseService.findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId).isPresent()) {
//				Exercise exercise = exerciseService
//						.findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId).get();
//				Duration duration = Duration.between(exercise.getCreateDateTime().toLocalDateTime(), dateTimeNow);
//				if (duration.toDays() > 10) {
//					System.out.println("passedTenDaysExercise");
//					passedTenDaysExercise = true;
//				}
//			}
//			isTrainer = true;
//		}
//		model.addAttribute("isTrainer", isTrainer);
//		model.addAttribute("isNutritionist", isNutritionist);
//		model.addAttribute("passedTenDaysFood", passedTenDaysFood);
//		model.addAttribute("passedTenDaysExercise", passedTenDaysExercise);
		model.addAttribute("account", account);
		model.addAttribute("learners", learners);
		model.addAttribute("plans", helperPlans);
		model.addAttribute("chart", chart);
		return "admin/helper_activity";
	}

	@PostMapping(path = { "/disable_account" })
	public String modifyUser(Model model, @RequestParam Integer accountId) {
		Account account = accountService.findById(accountId).get();
		account.setActive(false);
		accountService.save(account);
		return "redirect:/accounts";
	}

	@GetMapping(path = { "/view_devices" })
	public String viewDevices(Model model) {
		Set<Device> devices = deviceService.findAll();
		model.addAttribute("devices", devices);
		return "admin/view_devices";
	}

	@GetMapping(path = { "/view_measurements" })
	public String viewMeasurements(Model model) {
		Set<Measurement> measurements = measurementService.findAll();
		model.addAttribute("measurements", measurements);
		return "admin/view_measurements";
	}

	@GetMapping(path = { "/view_plans" })
	public String viewPlans(Model model) {
		Set<HelperPlan> plans = helperPlanService.findAll();
		model.addAttribute("plans", plans);
		return "admin/view_plans";
	}

	@GetMapping(path = { "/view_exercises" })
	public String viewExercises(Model model) {
		Set<Exercise> exercises = exerciseService.findAll();
		model.addAttribute("exercises", exercises);
		return "admin/view_exercises";
	}

	@GetMapping(path = { "/view_foods" })
	public String viewFoods(Model model) {
		Set<Food> foods = foodService.findAll();
		model.addAttribute("foods", foods);
		return "admin/view_foods";
	}
}
