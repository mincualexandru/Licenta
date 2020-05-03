package com.web.serviceimpl;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.HelperPlanDao;
import com.web.model.HelperPlan;
import com.web.service.HelperPlanService;

@Service("helperPlanService")
public class HelperPlanServiceImpl implements HelperPlanService {

	@Autowired
	private HelperPlanDao helperPlanDao;

	@Override
	public void save(HelperPlan helperPlan) {
		helperPlanDao.save(helperPlan);
	}

	@Override
	public Optional<HelperPlan> findById(Integer id) {
		return helperPlanDao.findById(id);
	}

	@Override
	public void delete(HelperPlan helperPlan) {
		helperPlanDao.delete(helperPlan);
	}

	@Override
	public void deleteById(Integer id) {
		helperPlanDao.deleteById(id);
	}

	@Override
	public Set<HelperPlan> findAll() {
		return helperPlanDao.findAll();
	}

	@Override
	public Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId) {
		return helperPlanDao.findAllTrainingPlansByHelperPlanNotAssociated(helperId);
	}

	@Override
	public Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId, Integer userId) {
		return helperPlanDao.findAllDietPlansByHelperPlanNotAssociated(helperId, userId);
	}

	@Override
	public Optional<HelperPlan> findByHelperPlanIdAndTypeOfPlan(int parseInt, String string) {
		return helperPlanDao.findByHelperPlanIdAndTypeOfPlan(parseInt, string);
	}

	@Override
	public Set<HelperPlan> findAllTrainingPlansByHelperPlanNotAssociated(Integer helperId, Integer userId) {
		return helperPlanDao.findAllTrainingPlansByHelperPlanNotAssociated(helperId, userId);
	}

	@Override
	public Set<HelperPlan> findAllDietPlansByHelperPlanNotAssociated(Integer helperId) {
		return helperPlanDao.findAllDietPlansByHelperPlanNotAssociated(helperId);
	}

	@Override
	public Set<HelperPlan> findAllPlansByHelperIdAndUserIdNotAssociated(Integer helperId, Integer userId) {
		return helperPlanDao.findAllPlansByHelperIdAndUserIdNotAssociated(helperId, userId);
	}

	@Override
	public Set<HelperPlan> findAllByHelperAccountIdAndDateOfCreationBetween(Integer accountId,
			Timestamp timestampStartDate, Timestamp timestampEndDate) {
		return helperPlanDao.findAllByHelperAccountIdAndDateOfCreationBetween(accountId, timestampStartDate,
				timestampEndDate);
	}

	@Override
	public Optional<HelperPlan> findByHelperPlanIdAndTypeOfPlanAndHelperAccountId(int parseInt, String string,
			Integer accountId) {
		return helperPlanDao.findByHelperPlanIdAndTypeOfPlanAndHelperAccountId(parseInt, string, accountId);
	}

}
