package com.web.controller;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
import com.web.model.Exercise;
import com.web.model.ExerciseFeedback;
import com.web.model.Food;
import com.web.model.FoodFeedback;
import com.web.model.HelperFeedback;
import com.web.service.AccountService;
import com.web.service.ExerciseFeedbackService;
import com.web.service.ExerciseService;
import com.web.service.FoodFeedbackService;
import com.web.service.FoodService;
import com.web.service.HelperFeedbackService;
import com.web.service.RoleService;
import com.web.utils.Qualifying;

@Controller
public class FeedbackController {

	private Logger LOGGER = Logger.getLogger(CommonController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private HelperFeedbackService helperFeedbackService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private ExerciseService exerciseService;

	@Autowired
	private ExerciseFeedbackService exerciseFeedbackService;

	@Autowired
	private FoodFeedbackService foodFeedbackService;

	@Autowired
	private FoodService foodService;

	@GetMapping(path = { "/view_feedbacks" })
	public String viewFeedbacks(Model model) {
		Account account = accountService.getAccountConnected();
		boolean roleNutritionist = false;
		if (account.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			roleNutritionist = true;
		}
		if (account.isActive()) {
			Set<Integer> learnersIds = accountService.findAllLearnersByHelperId(account.getAccountId());
			Set<ExerciseFeedback> exerciseFeedbacks = new HashSet<>();
			Set<FoodFeedback> foodFeedbacks = new HashSet<>();
			account.getRoles().forEach(role -> {
				learnersIds.forEach(integer -> {
					if (role.getName().equals("ROLE_TRAINER")) {
						exerciseFeedbacks.addAll(exerciseFeedbackService.findAllByUserAccountId(integer));
						model.addAttribute("exerciseFeedbacks", exerciseFeedbacks);
					} else if (role.getName().equals("ROLE_NUTRITIONIST")) {
						foodFeedbacks.addAll(foodFeedbackService.findAllByUserAccountId(integer));
						model.addAttribute("foodFeedbacks", foodFeedbacks);
					}
				});
			});
			if (learnersIds.size() == 0) {
				if (exerciseFeedbacks.size() == 0) {
					model.addAttribute("emptyList", true);
				}
			}
			model.addAttribute("account", account);
			return "common/view_feedbacks";
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}

	}

	@PostMapping(path = { "/helper_offers_feedback" })
	public String trainerOffersFeedback(Model model, @RequestParam Integer learnerId,
			RedirectAttributes redirectAttributes) {
		Account helper = accountService.getAccountConnected();
		boolean roleNutritionist = false;
		if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			roleNutritionist = true;
		}
		if (helper.isActive()) {
			LocalDate date = LocalDate.now();
			LocalDateTime startDateTime = date.atStartOfDay();
			LocalDateTime endDateTime = date.atStartOfDay().plusDays(1).minusSeconds(1);
			Timestamp timestampStartDate = Timestamp.valueOf(startDateTime);
			Timestamp timestampEndDate = Timestamp.valueOf(endDateTime);
			if (helperFeedbackService.findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(helper.getAccountId(),
					timestampStartDate, timestampEndDate).isPresent()) {
				redirectAttributes.addFlashAttribute("feedbackWasProvided", true);
				if (helper.getRoles().contains(roleService.findByName("ROLE_TRAINER").get())) {
					return "redirect:/view_progress/" + learnerId;
				} else if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
					return "redirect:/view_progress_nutritionist/" + learnerId;
				}

			}
			model.addAttribute("account", helper);
			model.addAttribute("learnerId", learnerId);
			model.addAttribute("qualifyings", Qualifying.values());
			return "common/helper_offers_feedback";
		} else if (roleNutritionist) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}
	}

	@PostMapping(path = { "/helper_offers_feedback_save" })
	public String trainerOffersFeedbackSave(Model model, @RequestParam Integer learnerId, @RequestParam String reason,
			@RequestParam Qualifying qualifying) {
		Account helper = accountService.getAccountConnected();
		if (helper.isActive()) {
			helperFeedbackService.helperCreateFeedbackForLearner(helper, learnerId, reason, qualifying);
			return "redirect:/view_learners";
		} else if (helper.getRoles().contains(roleService.findByName("ROLE_NUTRITIONIST").get())) {
			return "redirect:/nutritionist";
		} else {
			return "redirect:/trainer";
		}
	}

	@GetMapping(path = { "/view_feedbacks_from_helper" })
	public String viewFeedbacksFromHelper(Model model) {
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
	public String offerFeedbackForFood(@ModelAttribute("foodId") Integer foodId,
			@ModelAttribute("success") boolean success, Model model) {
		if (foodId != -1) {
			model.addAttribute("foodId", foodId);
			model.addAttribute("success", success);
		} else if (foodId == -1) {
			model.addAttribute("error", true);
		}
		return "home/offers_feedback_food";
	}

	@GetMapping(path = { "/offers_feedback" })
	public String offerFeedbackForExercise(@ModelAttribute("exerciseId") Integer exerciseId,
			@ModelAttribute("success") boolean success, Model model) {
		if (exerciseId != -1) {
			model.addAttribute("exerciseId", exerciseId);
			model.addAttribute("success", success);
		} else if (exerciseId == -1) {
			model.addAttribute("error", true);
		}
		return "home/offers_feedback";
	}

	@PostMapping(path = { "/offers_feedback_save" })
	public String offerFeedback(Model model, @RequestParam(required = false) Integer foodId,
			@RequestParam(required = false) Integer exerciseId, @RequestParam(required = false) String messageReview,
			@RequestParam(required = false) Integer number) {
		Account user = accountService.getAccountConnected();
		if (user.isActive()) {
			if (exerciseId != null) {
				Exercise exercise = exerciseService.findById(exerciseId).get();
				exerciseFeedbackService.createFeedbackForExercise(messageReview, number, user, exercise);
				return "redirect:/exercises_done";
			} else if (foodId != null) {
				Food food = foodService.findById(foodId).get();
				foodFeedbackService.createFeedbackForFood(messageReview, number, user, food);
				return "redirect:/foods_eaten";
			} else {
				return "redirect:/error";
			}
		} else {
			return "redirect:/home";
		}
	}

}
