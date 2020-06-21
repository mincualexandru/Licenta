package com.web.serviceimpl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.FoodDao;
import com.web.dao.RoleDao;
import com.web.model.Account;
import com.web.model.Food;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.FoodService;

@Service("foodService")
public class FoodServiceImpl implements FoodService {

	@Autowired
	private FoodDao foodDao;

	@Autowired
	private AccountService accountService;

	@Autowired
	private RoleDao roleDao;

	@Override
	public void save(Food food) {
		foodDao.save(food);
	}

	@Override
	public Optional<Food> findById(Integer id) {
		return foodDao.findById(id);
	}

	@Override
	public void delete(Food food) {
		foodDao.delete(food);

	}

	@Override
	public void deleteById(Integer id) {
		foodDao.deleteById(id);
	}

	@Override
	public Set<Food> findAll() {
		return foodDao.findAll();
	}

	@Override
	public Set<Food> findAllByDietPlanHelperPlanId(Integer helperPlanId) {
		return foodDao.findAllByDietPlanHelperPlanId(helperPlanId);
	}

	@Override
	public Set<Food> findAllNotEatenFoodsForDietPlanId(Integer dietPlanId) {
		return foodDao.findAllNotEatenFoodsForDietPlanId(dietPlanId);
	}

	@Override
	public Set<Food> findAllNotEatenFoods() {
		return foodDao.findAllNotEatenFoods();
	}

	@Override
	public Set<Food> findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(Integer dietPlanId, String string) {
		return foodDao.findAllByDietPlanHelperPlanIdAndDietPlanTypeOfPlan(dietPlanId, string);
	}

	@Override
	public Optional<Food> findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId) {
		return foodDao.findTopByDietPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId);
	}

	@Override
	public Map<Food, Long> sortFoods(Map<Food, Long> unsortMap, boolean order) {
		List<Entry<Food, Long>> list = new LinkedList<>(unsortMap.entrySet());
		list.sort((o1, o2) -> order
				? o1.getValue().compareTo(o2.getValue()) == 0 ? o1.getKey().compareTo(o2.getKey())
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? o2.getKey().compareTo(o1.getKey())
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
	}

	@Override
	public boolean checkFood(Account account, String foodId) {
		if (accountService.checkId(foodId) && foodDao.findById(Integer.parseInt(foodId)).isPresent()) {
			Food food = foodDao.findById(Integer.parseInt(foodId)).get();
			if (account.getRoles().contains(roleDao.findByName("ROLE_NUTRITIONIST").get())) {
				if (food.getDietPlan().getHelper().getAccountId() == account.getAccountId()) {
					return true;
				}
			}
			if (account.getRoles().contains(roleDao.findByName("ROLE_USER").get())) {
				for (UserPlan userPlan : food.getDietPlan().getUserPlans()) {
					if (userPlan.getUser().getAccountId() == account.getAccountId()) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
