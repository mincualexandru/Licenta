package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.UserPlanDao;
import com.web.model.UserPlan;
import com.web.service.UserPlanService;

@Service("userPlanService")
public class UserPlanServiceImpl implements UserPlanService {

	@Autowired
	private UserPlanDao userPlanDao;

	@Override
	public void save(UserPlan userTraining) {
		userPlanDao.save(userTraining);
	}

	@Override
	public Optional<UserPlan> findById(Integer id) {
		return userPlanDao.findById(id);
	}

	@Override
	public void delete(UserPlan userTraining) {
		userPlanDao.delete(userTraining);
	}

	@Override
	public void deleteById(Integer id) {
		userPlanDao.deleteById(id);
	}

	@Override
	public Set<UserPlan> findAll() {
		return userPlanDao.findAll();
	}

	@Override
	public Set<UserPlan> findAllByUserAccountId(Integer accountId) {
		return userPlanDao.findAllByUserAccountId(accountId);
	}

	@Override
	public UserPlan findByUserAccountIdAndHelperPlanHelperPlanId(Integer userId, Integer helperPlanId) {
		return userPlanDao.findByUserAccountIdAndHelperPlanHelperPlanId(userId, helperPlanId);
	}

	@Override
	public void deleteByUserPlanId(Integer userPlanId) {
		userPlanDao.deleteByUserPlanId(userPlanId);
	}

	@Override
	public Set<UserPlan> findAllByHelperPlanTypeOfPlan(String string) {
		return userPlanDao.findAllByHelperPlanTypeOfPlan(string);
	}

	@Override
	public Set<UserPlan> findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(boolean b, Integer accountId,
			String string) {
		return userPlanDao.findAllByBoughtAndUserAccountIdAndHelperPlanTypeOfPlan(b, accountId, string);
	}

	@Override
	public Set<UserPlan> findAllByBoughtAndUserAccountId(boolean b, Integer accountId) {
		return userPlanDao.findAllByBoughtAndUserAccountId(b, accountId);
	}

	@Override
	public Optional<UserPlan> findTopByOrderByDateOfPurchaseDesc() {
		return userPlanDao.findTopByOrderByDateOfPurchaseDesc();
	}
}
