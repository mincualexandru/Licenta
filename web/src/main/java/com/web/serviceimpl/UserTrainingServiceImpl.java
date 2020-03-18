package com.web.serviceimpl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.UserTrainingDao;
import com.web.model.UserTraining;
import com.web.service.UserTrainingService;

@Service("userTrainingService")
public class UserTrainingServiceImpl implements UserTrainingService {

	@Autowired
	private UserTrainingDao userTrainingDao;

	@Override
	public void save(UserTraining userTraining) {
		userTrainingDao.save(userTraining);
	}

	@Override
	public Optional<UserTraining> findById(Integer id) {
		return userTrainingDao.findById(id);
	}

	@Override
	public void delete(UserTraining userTraining) {
		userTrainingDao.delete(userTraining);
	}

	@Override
	public void deleteById(Integer id) {
		userTrainingDao.deleteById(id);
	}

	@Override
	public Set<UserTraining> findAll() {
		return userTrainingDao.findAll();
	}

	@Override
	public Set<UserTraining> findAllByBoughtAndUserAccountId(boolean b, Integer accountId) {
		return userTrainingDao.findAllByBoughtAndUserAccountId(b, accountId);
	}

	@Override
	public UserTraining findByUserAccountIdAndTrainingPlanTrainingPlanId(Integer userId, Integer productId) {
		return userTrainingDao.findByUserAccountIdAndTrainingPlanTrainingPlanId(userId, productId);
	}

	@Override
	public Set<UserTraining> findAllByUserAccountId(Integer accountId) {
		return userTrainingDao.findAllByUserAccountId(accountId);
	}
}
