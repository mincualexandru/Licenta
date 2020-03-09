package com.web.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.web.model.Account;
import com.web.model.Device;
import com.web.model.ExerciseDone;
import com.web.model.TrainingPlan;
import com.web.model.Transaction;
import com.web.model.UserDevice;
import com.web.model.UserTraining;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.ExerciseDoneService;
import com.web.service.ExerciseService;
import com.web.service.TrainingPlanService;
import com.web.service.TransactionService;
import com.web.service.UserDeviceService;
import com.web.service.UserTrainingService;
import com.web.utils.Product;

@Controller
public class UserController {

	private Logger logger = Logger.getLogger(AuthenticationController.class);

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

	@GetMapping(path = { "/home" })
	public String home(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Set<UserDevice> userDevices = userDeviceService.findAllByBoughtAndUserAccountId(false, account.getAccountId());
		boolean addBandToShoppingCart = false;
		boolean addScaleToShoppingCart = false;
		for (UserDevice userDevice : userDevices) {
			if (userDevice.getDevice().getName().equals("Bratara") && userDevice.isBought() == false) {
				addBandToShoppingCart = true;
			}
			if (userDevice.getDevice().getName().equals("Cantar Inteligent") && userDevice.isBought() == false) {
				addScaleToShoppingCart = true;
			}
		}
		model.addAttribute("addBandToShoppingCart", addBandToShoppingCart);
		model.addAttribute("addScaleToShoppingCart", addScaleToShoppingCart);
		model.addAttribute("user", account);
		return "home/home";
	}

	@PostMapping(path = { "/buy_scale" })
	public String buyScale(Model model, RedirectAttributes redirectAttributes) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Device device = deviceService.findOneDeviceRandomByName("Cantar Inteligent");
		if (device == null) {
			redirectAttributes.addFlashAttribute("soldOutForScales", "Stocul este epuizat pentru cantare");
			return "redirect:/home";
		} else {
			logger.info("Nume " + device.getName() + " serial number " + device.getSerialNumber());
			model.addAttribute("device", device);
			model.addAttribute("account", account);
			return "home/buy_scale";
		}
	}

	@PostMapping(path = { "/buy_band" })
	public String buyBand(Model model, RedirectAttributes redirectAttributes) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = accountService.findByUsername(auth.getName());
		Device device = deviceService.findOneDeviceRandomByName("Bratara");
		if (device == null) {
			redirectAttributes.addFlashAttribute("soldOutForBands", "Stocul este epuizat pentru bratari");
			return "redirect:/home";
		} else {
			logger.info("Nume " + device.getName() + " serial number " + device.getSerialNumber());
			model.addAttribute("device", device);
			model.addAttribute("account", account);
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
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
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		Set<Account> allTrainersOrNutritionists = accountService.findAllByRolesNameOrRolesName("ROLE_TRAINER",
				"ROLE_NUTRITIONIST");
		allTrainersOrNutritionists.removeIf(element -> !(element.isActive()));
		allTrainersOrNutritionists.removeIf(element -> user.getHelpers().contains(element));
		model.addAttribute("allTrainersOrNutritionists", allTrainersOrNutritionists);
		return "home/choose_helper";
	}

	@PostMapping(path = { "/choose_helper_save" })
	public String chooseHelperSave(Model model, @RequestParam Integer helperId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		user.getHelpers().add(accountService.findById(helperId).get());
		accountService.save(user);
		return "redirect:/choose_helper";
	}

	@GetMapping(path = { "/view_helpers" })
	public String viewHelpers(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		model.addAttribute("trainersOrNutrituserionists", user.getHelpers());
		return "home/view_helpers";
	}

	@PostMapping(path = { "/trainer_training_plans" })
	public String viewTrainingsOrDiets(Model model, @RequestParam Integer trainerId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		model.addAttribute("trainingPlans", trainingPlanService.findAllByTrainingPlanNotAssociated(trainerId));
		model.addAttribute("user", user);
		return "home/trainer_training_plans";
	}

	@GetMapping(path = { "/view_training_plans" })
	public String viewTrainingPlans(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		Set<UserTraining> userTrainings = userTrainingService.findAllByBoughtAndUserAccountId(true,
				user.getAccountId());
		model.addAttribute("userTrainings", userTrainings);
		model.addAttribute("user", user);
		return "home/view_training_plans";
	}

	@GetMapping(path = { "/exercises_done" })
	public String exercisesDone(Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account user = accountService.findByUsername(auth.getName());
		Set<ExerciseDone> exercisesDone = exerciseDoneService.findAllByUserAccountId(user.getAccountId());
		model.addAttribute("exercisesDone", exercisesDone);
		return "home/exercises_done";
	}

	@PostMapping(path = { "/perfom_this_exercise" })
	public String perfomThisExercise(Model model, @RequestParam Integer accountId, @RequestParam Integer exerciseId) {
		ExerciseDone exerciseDone = new ExerciseDone();
		exerciseDone.setExercise(exerciseService.findById(exerciseId).get());
		exerciseDone.setUser(accountService.findById(accountId).get());
		exerciseDoneService.save(exerciseDone);
		return "redirect:/exercises_done";
	}

}
