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

import com.web.dao.ExerciseDao;
import com.web.dao.RoleDao;
import com.web.model.Account;
import com.web.model.Exercise;
import com.web.model.UserPlan;
import com.web.service.AccountService;
import com.web.service.ExerciseService;

@Service("exerciseService")
public class ExerciseServiceImpl implements ExerciseService {

	@Autowired
	private ExerciseDao exerciseDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private AccountService accountService;

	@Override
	public void save(Exercise exercise) {
		exerciseDao.save(exercise);

	}

	@Override
	public Optional<Exercise> findById(Integer id) {
		return exerciseDao.findById(id);
	}

	@Override
	public void delete(Exercise exercise) {
		exerciseDao.delete(exercise);
	}

	@Override
	public void deleteById(Integer id) {
		exerciseDao.deleteById(id);
	}

	@Override
	public Set<Exercise> findAll() {
		return exerciseDao.findAll();
	}

	@Override
	public Set<Exercise> findAllNotPerfomerdExercisesForTrainingPlanId(Integer trainingPlanId) {
		return exerciseDao.findAllNotPerfomerdExercisesForTrainingPlanId(trainingPlanId);
	}

	@Override
	public Set<Exercise> findAllNotPerfomerdExercises() {
		return exerciseDao.findAllNotPerfomerdExercises();
	}

	@Override
	public Set<Exercise> findAllByTrainingPlanHelperPlanId(Integer helperPlanId) {
		return exerciseDao.findAllByTrainingPlanHelperPlanId(helperPlanId);
	}

	@Override
	public Set<Exercise> findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(Integer trainingPlanId,
			String string) {
		return exerciseDao.findAllByTrainingPlanHelperPlanIdAndTrainingPlanTypeOfPlan(trainingPlanId, string);
	}

	@Override
	public Optional<Exercise> findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(Integer accountId) {
		return exerciseDao.findTopByTrainingPlanHelperAccountIdOrderByCreateDateTimeDesc(accountId);
	}

	@Override
	public Map<Exercise, Long> sortExercises(Map<Exercise, Long> unsortMap, boolean order) {
		List<Entry<Exercise, Long>> list = new LinkedList<>(unsortMap.entrySet());
		list.sort((o1, o2) -> order
				? o1.getValue().compareTo(o2.getValue()) == 0 ? o1.getKey().compareTo(o2.getKey())
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? o2.getKey().compareTo(o1.getKey())
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
	}

	@Override
	public boolean checkExercise(Account account, String exerciseId) {
		if (accountService.checkId(exerciseId) && exerciseDao.findById(Integer.parseInt(exerciseId)).isPresent()) {
			Exercise exercise = exerciseDao.findById(Integer.parseInt(exerciseId)).get();
			if (account.getRoles().contains(roleDao.findByName("ROLE_TRAINER").get())) {
				if (exercise.getTrainingPlan().getHelper().getAccountId() == account.getAccountId()) {
					return true;
				}
			}
			if (account.getRoles().contains(roleDao.findByName("ROLE_USER").get())) {
				for (UserPlan userPlan : exercise.getTrainingPlan().getUserPlans()) {
					if (userPlan.getUser().getAccountId() == account.getAccountId()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
