package com.web.controller;

import java.time.Duration;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.web.service.FoodEatenService;
import com.web.service.FoodFeedbackService;
import com.web.service.HelperPlanService;
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
	private HelperPlanService helperPlanService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private FoodFeedbackService foodFeedbackService;

	@Autowired
	private DeviceService deviceService;

	private Logger LOGGER = Logger.getLogger(AuthenticationController.class);

	@GetMapping(path = { "/admin" })
	public String admin(Model model) {
		Integer totalIncome = 0;
		Integer totalPayments = 0;
		Account admin = accountService.getAccountConnected();
		Set<Account> accounts = accountService.findAll();
		Set<UserPlan> userPlans = userPlanService.findAll();
		Set<Device> devices = deviceService.findAll();
		accounts.removeIf(element -> element.getUsername() == admin.getUsername());
		for (UserPlan plan : userPlans) {
			totalIncome += plan.getHelperPlan().getPrice() * 70 / 100;
			totalPayments += plan.getHelperPlan().getPrice() - plan.getHelperPlan().getPrice() * 70 / 100;
		}
		for (UserDevice userDevice : userDeviceService.findAll()) {
			totalIncome += userDevice.getDevice().getPrice();
		}
		Map<HelperPlan, Long> theMostBoughtPlans = userPlans.stream().map(element -> element.getHelperPlan())
				.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
		Map<HelperPlan, Long> sortedMapDesc = sortByValue(theMostBoughtPlans, false);
		model.addAttribute("totalIncome", totalIncome);
		model.addAttribute("totalPayments", totalPayments);
		model.addAttribute("account", admin);
		model.addAttribute("accounts", accounts);
		model.addAttribute("userPlans", userPlans);
		model.addAttribute("theMostBoughtPlans", sortedMapDesc);
		model.addAttribute("devices", devices);
		model.addAttribute("admin", admin);
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

	@PostMapping(path = { "/validate_account_save" })
	public String validateAccountSave(@RequestParam Integer accountId, Model model) {
		Account account = accountService.findById(accountId).get();
		LOGGER.info("Arata id " + account.getAccountId());
		account.setActive(true);
		accountService.save(account);
		return "redirect:/accounts";
	}

	@GetMapping(path = { "/accounts" })
	public String getAllUsers(Model model) {
		Account account = accountService.getAccountConnected();
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
		Map<String, Integer> chartMeasurements = new TreeMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		for (Measurement measurement : userMeasurementsSorted) {
			String startDate = measurement.getStartDate().toLocalDateTime().toLocalDate().format(formatter);
			if (!startDate.equals(previousValue)) {
				number = 0;
				previousValue = startDate;
			}
			number++;
			chartMeasurements.put(startDate, number);
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
		model.addAttribute("devices", userDevices);
		model.addAttribute("plans", userPlans);
		model.addAttribute("account", account);
		return "admin/user_activity";
	}

	@PostMapping(path = { "/helper_activity" })
	public String helperActivity(Model model, @RequestParam Integer accountId) {
		Account account = accountService.findById(accountId).get();
		Set<HelperPlan> helperPlans = account.getPlans();
		Set<Exercise> exercises = new HashSet<>();
		Set<Food> foods = new HashSet<>();
		Set<Account> learners = new HashSet<>();

		helperPlans.forEach(element -> foods.addAll(element.getFoods()));
		helperPlans.forEach(element -> exercises.addAll(element.getExercises()));

		accountService.findAllLearnersByHelperId(account.getAccountId())
				.forEach(element -> learners.add(accountService.findById(element).get()));

		List<Exercise> exercisesSorted = exercises.stream()
				.sorted((e1, e2) -> e1.getCreateDateTime().compareTo(e2.getCreateDateTime()))
				.collect(Collectors.toList());

		List<Food> foodsSorted = foods.stream()
				.sorted((e1, e2) -> e1.getCreateDateTime().compareTo(e2.getCreateDateTime()))
				.collect(Collectors.toList());

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

		for (Food foodEaten : foodsSorted) {
			String formatDate = foodEaten.getCreateDateTime().toLocalDateTime().toLocalDate().format(formatter);
			if (!formatDate.equals(previousValue)) {
				number = 0;
				previousValue = formatDate;
			}
			number++;
			chart.put(formatDate, number);
		}

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

	@PostMapping(path = { "/view_content_of_all_accounts" })
	public String viewDevices(Model model, @RequestParam String contentOption) {
		switch (contentOption) {
		case "devices":
			model.addAttribute("devices", deviceService.findAll());
			break;
		case "plans":
			model.addAttribute("plans", helperPlanService.findAll());
			break;
		case "feedbacksExercises":
			model.addAttribute("exerciseFeedbacks", exerciseFeedbackService.findAll());
			break;
		case "feedbacksFoods":
			model.addAttribute("foodFeedbacks", foodFeedbackService.findAll());
			break;
		default:
			model.addAttribute("noOption", true);
			break;
		}
		return "admin/view_content_of_all_accounts";
	}
}
