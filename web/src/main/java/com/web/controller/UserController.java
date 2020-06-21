package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.Exercise;
import com.web.model.ExerciseDone;
import com.web.model.Food;
import com.web.model.FoodEaten;
import com.web.model.Measurement;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseService;
import com.web.service.FoodEatenService;
import com.web.service.FoodService;
import com.web.service.MeasurementService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;

@Controller
public class UserController {

	private final Logger LOGGER = Logger.getLogger(this.getClass());

	@Autowired
	private AccountService accountService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private UserDeviceService userDeviceService;

	@Autowired
	private UserPlanService userPlanService;

	@Autowired
	private ExerciseDoneService exerciseDoneService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private MeasurementService measurementService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@Autowired
	private FoodEatenService foodEatenService;

	@Autowired
	private FoodService foodService;

	@GetMapping(path = { "/home" })
	public String home(Model model, HttpServletRequest request) {
		Account account = accountService.getAccountConnected();
		LocalDateTime dateTimeNow = LocalDateTime.now();
		LocalDate date = LocalDate.now();
		LocalDateTime startDateTime = date.atStartOfDay();
		LocalDateTime endDateTime = date.atStartOfDay().plusDays(1).minusSeconds(1);
		Timestamp timestampStartDate = Timestamp.valueOf(startDateTime);
		Timestamp timestampEndDate = Timestamp.valueOf(endDateTime);
		if (account.isActive() == false) {
			boolean passedTenDaysExerciseDone = false;
			boolean passedTenDaysFoodEaten = false;
			passedTenDaysExerciseDone = exerciseDoneService.checkIfTenDaysExerciseHavePassed(passedTenDaysExerciseDone,
					dateTimeNow, account.getAccountId());
			passedTenDaysFoodEaten = foodEatenService.checkIfTenDaysFoodHavePassed(passedTenDaysFoodEaten, dateTimeNow,
					account.getAccountId());
			model.addAttribute("passedTenDaysFoodEaten", passedTenDaysFoodEaten);
			model.addAttribute("passedTenDaysExerciseDone", passedTenDaysExerciseDone);
		} else {

			if (account.getTransaction().getAvailableBalance() == 0) {
				return "redirect:/wallet";
			}

			if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
				UserDevice fitBuddyDevice = userDeviceService
						.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
				if (!measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
						"HKQuantityTypeIdentifierHeight")
						|| !measurementService.checkIfMeasurementIsPresent(fitBuddyDevice.getUserDeviceId(),
								"HKQuantityTypeIdentifierBodyMass")) {
					return "redirect:/set_height_and_weight";
				}
			}

			// masuratorile cu caloriile arse sa fie adaugate cumva la numarul de calorii
			// arse din interfata
			Set<ExerciseDone> exercisesDoneToday = exerciseDoneService.findAllByUserAccountIdAndDateOfExecutionBetween(
					account.getAccountId(), timestampStartDate, timestampEndDate);
			Set<FoodEaten> foodsEatenToday = foodEatenService.findAllByUserAccountIdAndDateOfExecutionBetween(
					account.getAccountId(), timestampStartDate, timestampEndDate);
			Set<Measurement> measurementsToday = new HashSet<>();
			account.getUserDevices()
					.forEach(element -> measurementsToday
							.addAll(measurementService.findAllByUserDeviceUserDeviceIdAndStartDateBetween(
									element.getUserDeviceId(), timestampStartDate, timestampEndDate)));

			Map<Exercise, Long> favoriteExercises = account.getExerciseDone().stream()
					.map(element -> element.getExercise())
					.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
			Map<Exercise, Long> favoriteExercisesDesc = exerciseService.sortExercises(favoriteExercises, false);

			Map<Food, Long> favoriteFoods = account.getFoodEaten().stream().map(element -> element.getFood())
					.collect(Collectors.groupingBy(e -> e, Collectors.counting()));
			Map<Food, Long> favoriteFoodsDesc = foodService.sortFoods(favoriteFoods, false);

			model.addAttribute("favoriteExercisesDesc", favoriteExercisesDesc);
			model.addAttribute("favoriteFoodsDesc", favoriteFoodsDesc);
			model.addAttribute("exercisesDoneToday", exercisesDoneToday);
			model.addAttribute("foodsEatenToday", foodsEatenToday);
			model.addAttribute("measurementsToday", measurementsToday);
			model.addAttribute("serverName", request.getServerName());
		}
		model.addAttribute("user", account);
		return "home/home";
	}

	@GetMapping(path = { "/action_activate_account" })
	public String actionActivateAccount(Model model) {
		Account account = accountService.getAccountConnected();
		boolean passedTenDaysExerciseDone = false;
		boolean passedTenDaysFoodEaten = false;
		LocalDateTime dateTimeNow = LocalDateTime.now();
		if (!account.isActive()) {
			passedTenDaysExerciseDone = exerciseDoneService.checkIfTenDaysExerciseHavePassed(passedTenDaysExerciseDone,
					dateTimeNow, account.getAccountId());
			passedTenDaysFoodEaten = foodEatenService.checkIfTenDaysFoodHavePassed(passedTenDaysFoodEaten, dateTimeNow,
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
			if (!(weight == null) && weight > 0) {
				measurementService.save(measurementService.createMeasurementForHeightOrWeight(weight, userDevice,
						"HKQuantityTypeIdentifierBodyMass", "kg"));
			}
			if (!(height == null) && height > 0) {
				measurementService.save(measurementService.createMeasurementForHeightOrWeight(height, userDevice,
						"HKQuantityTypeIdentifierHeight", "cm"));
			}
			if (!(height == null) && height < 0 && !(weight == null) && weight < 0) {
				redirectAttributes.addFlashAttribute("negativeValueForHeight", true);
				redirectAttributes.addFlashAttribute("negativeValueForWeight", true);
				return "redirect:/set_height_and_weight";
			} else if (!(height == null) && height < 0) {
				redirectAttributes.addFlashAttribute("negativeValueForHeight", true);
				return "redirect:/set_height_and_weight";
			} else if (!(weight == null) && weight < 0) {
				redirectAttributes.addFlashAttribute("negativeValueForWeight", true);
				return "redirect:/set_height_and_weight";
			}
			redirectAttributes.addFlashAttribute("message", "from_set_height_and_weight");
			return "redirect:/success";
		} else {
			return "redirect:/home";
		}

	}

	@GetMapping(path = { "/view_user_plans" })
	public String viewDietPlans(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<UserPlan> userPlans = userPlanService.findAllByBoughtAndUserAccountId(true, user.getAccountId());
			model.addAttribute("plans", userPlans);
			model.addAttribute("user", user);
			return "home/view_user_plans";
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
		model.addAttribute("exercise", exerciseService.findById(exerciseId).get());
		model.addAttribute("account", accountService.findById(accountId).get());
		return "home/perfom_this_exercise";
	}

	@PostMapping(path = { "/perfom_this_exercise_save" })
	public String perfomThisExerciseSave(Model model, @RequestParam Integer accountId, @RequestParam Integer exerciseId,
			RedirectAttributes redirectAttributes) {
		Account account = accountService.findById(accountId).get();
		exerciseDoneService.addExerciseDone(accountId, exerciseId);
		UserDevice userDevice = new UserDevice();
		Measurement measurement = new Measurement();
		if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
			userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			measurementService.createMeasurement(exerciseId, measurement, userDevice,
					"HKQuantityTypeIdentifierActiveEnergyBurned", "kcal");
			measurementService.save(measurement);
		} else if (userDeviceService.checkIfDeviceIsPresent(account, "Bratara")) {
			userDevice = userDeviceService.findByDeviceNameAndUserAccountId("Bratara", account.getAccountId()).get();
			measurementService.createMeasurement(exerciseId, measurement, userDevice,
					"HKQuantityTypeIdentifierActiveEnergyBurned", "kcal");
			measurementService.save(measurement);
		}
		if (!account.isActive()) {
			account.setActive(true);
			accountService.save(account);
		}
		redirectAttributes.addFlashAttribute("exerciseId", exerciseId);
		redirectAttributes.addFlashAttribute("success", true);
		return "redirect:/offers_feedback";
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
		Food food = foodService.findById(foodId).get();
		foodEatenService.addFoodToEat(account, food);
		if (userDeviceService.checkIfDeviceIsPresent(account, "Fit Buddy")) {
			UserDevice userDefaultDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			Measurement measurement = measurementService.createMeasurementForHeightOrWeight(food.getCalories(),
					userDefaultDevice, "HKQuantityTypeIdentifierActiveEnergyAccumulated", "kcal");
			measurementService.save(measurement);
			redirectAttributes.addFlashAttribute("foodId", foodId);
			redirectAttributes.addFlashAttribute("success", true);
		}
		if (!account.isActive()) {
			account.setActive(true);
			accountService.save(account);
		}
		return "redirect:/offers_feedback_food";
	}

	@PostMapping(path = { "/delete_device" })
	public String deleteDevice(Model model, @RequestParam Integer deviceId) {
		Device device = deviceService.findById(deviceId).get();
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			if (device.getTypeMeasurements() != null) {
				typeMeasurementService.deleteAllByDeviceId(deviceId);
			}
			UserDevice userDevice = userDeviceService
					.findByDeviceNameAndUserAccountId(device.getName(), account.getAccountId()).get();
			if (userDevice.getMeasurements() != null) {
				measurementService.deleteAllByUserDeviceId(userDevice.getUserDeviceId());
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
}
