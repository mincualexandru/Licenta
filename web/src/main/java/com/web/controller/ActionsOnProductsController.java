package com.web.controller;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
import com.web.model.HelperPlan;
import com.web.model.Transaction;
import com.web.model.TypeMeasurement;
import com.web.model.UserDevice;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.DeviceService;
import com.web.service.HelperPlanService;
import com.web.service.MeasurementService;
import com.web.service.TransactionService;
import com.web.service.TypeMeasurementService;
import com.web.service.UserDeviceService;
import com.web.service.UserPlanService;
import com.web.utils.Product;

@Controller
public class ActionsOnProductsController {

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
	private MeasurementService measurementService;

	@Autowired
	private TypeMeasurementService typeMeasurementService;

	@GetMapping(path = { "/wallet" })
	public String loadFundsAccount(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			model.addAttribute("transaction", user.getTransaction());
			return "home/wallet";
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
				deviceService.createDevice(newDevice, typeMeasurements, "Cantar Inteligent", 250);
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

	@GetMapping(path = { "/choose_device" })
	public String chooseDevice(Model model) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			boolean bandAlreadyBought = false;
			boolean scaleAlreadyBought = false;
			for (UserDevice userDevice : account.getUserDevices()) {
				if (userDevice.getDevice().getName().equals("Bratara")) {
					bandAlreadyBought = true;

				} else if (userDevice.getDevice().getName().equals("Cantar Inteligent")) {
					scaleAlreadyBought = true;
				}
			}
			Set<TypeMeasurement> typeMeasurementsForBand = typeMeasurementService.findAll();
			Set<TypeMeasurement> typeMeasurementsForScale = typeMeasurementService.findAll();
			typeMeasurementsForBand.removeIf(element -> element.getType().contains("Body"));
			typeMeasurementsForScale.removeIf(element -> !element.getType().contains("Body"));
			Device band = new Device("Banda", "Xiaomi", "ef32rg53", 100);
			band.setTypeMeasurements(typeMeasurementsForBand);
			Device scale = new Device("Cantar Inteligent", "Xiaomi", "3412fgfd3", 250);
			scale.setTypeMeasurements(typeMeasurementsForScale);
			model.addAttribute("bandAlreadyBought", bandAlreadyBought);
			model.addAttribute("scaleAlreadyBought", scaleAlreadyBought);
			model.addAttribute("account", account);
			model.addAttribute("band", band);
			model.addAttribute("scale", scale);
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
				Device device = deviceService.findById(deviceId).get();
				userDeviceService.addDeviceToUser(account, device);
			}
			if (trainingId != null) {
				HelperPlan trainingPlan = helperPlanService.findByHelperPlanIdAndTypeOfPlan(trainingId, "Antrenament")
						.get();
				userPlanService.createUserPlan(account, trainingPlan);
			}
			if (dietId != null) {
				HelperPlan dietPlan = helperPlanService.findByHelperPlanIdAndTypeOfPlan(dietId, "Dieta").get();
				userPlanService.createUserPlan(account, dietPlan);
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
				totalCostOfDevices += userDevice.getDevice().getPrice();
				String image = null;
				if (userDevice.getDevice().getName().equals("Fit Buddy")) {
					image = "4e56fc0b-28d1-4bd4-98c6-697e5a8e4721_rw_1200.gif";
				} else if (userDevice.getDevice().getName().equals("Bratara")) {
					image = "band.jpg";
				} else if (userDevice.getDevice().getName().equals("Cantar Inteligent")) {
					image = "scale.jpg";
				}
				products.add(new Product(userDevice.getDevice().getDeviceId(), userDevice.getDevice().getName(),
						userDevice.getDevice().getPrice(), userDevice.getDevice().getCompany(), null, "Dispozitiv",
						null, null, image));
			}
			Integer totalCostOfPlans = 0;
			for (UserPlan userPlan : userPlans) {
				totalCostOfPlans += userPlan.getHelperPlan().getPrice();
				products.add(new Product(userPlan.getHelperPlan().getHelperPlanId(), userPlan.getHelperPlan().getName(),
						userPlan.getHelperPlan().getPrice(), null, userPlan.getHelperPlan().getForWho(),
						userPlan.getHelperPlan().getTypeOfPlan(), null, userPlan.getHelperPlan().getDescription()));
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
					typeMeasurementService.deleteAllByDeviceId(deviceToDelete.getDeviceId());
				}
				deviceService.deleteByDeviceId(deviceToDelete.getDeviceId());
			} else {
				UserPlan userPlanToDelete = userPlanService.findByUserAccountIdAndHelperPlanHelperPlanId(userId,
						productId);
				userPlanService.deleteByUserPlanId(userPlanToDelete.getUserPlanId());
			}
			redirectAttributes.addFlashAttribute("succesDeleteProduct", true);
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
				boolean thereArePurchasedDevices = false;
				if (!userDevices.isEmpty()) {
					thereArePurchasedDevices = true;
					for (UserDevice userDevice : userDevices) {
						Integer devicePrice = userDevice.getDevice().getPrice();
						user.getTransaction()
								.setAvailableBalance(user.getTransaction().getAvailableBalance() - devicePrice);
						user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
						userDevice.setBought(true);
//						if (userDevice.getDevice().getName().equals("Bratara")) {
//							Set<Measurement> measurements = measurementService
//									.findAllByUserDeviceUserDeviceIdAndFromXml(null, false);
//							measurements.forEach(element -> System.out
//									.println(element.getName() + " " + element.getMeasurementId()));
//							measurements.forEach(measurement -> {
//								measurement.setUserDevice(userDevice);
//								measurementService.save(measurement);
//							});
//					}
						userDevice.setDateOfPurchase(new Timestamp(System.currentTimeMillis()));
						userDeviceService.save(userDevice);
					}
				}
				if (!userPlans.isEmpty()) {
					for (UserPlan userPlan : userPlans) {
						Integer userPlanPrice = userPlan.getHelperPlan().getPrice();
						user.getTransaction()
								.setAvailableBalance(user.getTransaction().getAvailableBalance() - userPlanPrice);
						if (!thereArePurchasedDevices) {
							user.getTransaction().setPayments(user.getTransaction().getPayments() - totalSum);
							Account trainer = userPlan.getHelperPlan().getHelper();
							trainer.getTransaction()
									.setAvailableBalance(trainer.getTransaction().getAvailableBalance() + totalSum);
						}
						userPlan.setBought(true);
						userPlan.setDateOfPurchase(new Timestamp(System.currentTimeMillis()));
						userPlanService.save(userPlan);
					}
				}
			} else {
				redirectAttributes.addFlashAttribute("insufficientBalance", true);
				return "redirect:/shopping_cart";
			}
			redirectAttributes.addFlashAttribute("message", "from_buy_shopping_cart");
			return "redirect:/success";
		} else

		{
			return "redirect:/home";
		}
	}

	@PostMapping(path = { "/modify_current_balance" })
	public String modifyCurrentBalance(Model model, @RequestParam Integer transactionId,
			@RequestParam Integer moneyAdded, RedirectAttributes redirectAttributes) {
		Account account = accountService.getAccountConnected();
		if (account.isActive()) {
			Transaction transaction = transactionService.findById(transactionId).get();
			UserDevice fitBuddyDevice = userDeviceService
					.findByDeviceNameAndUserAccountId("Fit Buddy", account.getAccountId()).get();
			if (moneyAdded > 0) {
				transaction.setAvailableBalance(transaction.getAvailableBalance() + moneyAdded);
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
				return "redirect:/wallet";
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

	@GetMapping(path = { "/choose_plan" })
	public String viewTrainingsOrDiets(Model model) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			Set<Account> helpers = user.getHelpers();
			Set<HelperPlan> plans = new HashSet<>();
			helpers.forEach(element -> plans.addAll(helperPlanService
					.findAllPlansByHelperIdAndUserIdNotAssociated(element.getAccountId(), user.getAccountId())));
			if (user.getGender().getGender().equals("Barbat")) {
				plans.removeIf(element -> element.getForWho().getGender().equals("Femeie"));
			} else if (user.getGender().getGender().equals("Femeie")) {
				plans.removeIf(element -> element.getForWho().getGender().equals("Barbat"));
			}
			model.addAttribute("user", user);
			model.addAttribute("plans", plans);
			return "home/choose_plan";
		} else {
			return "redirect:/home";
		}
	}
}
