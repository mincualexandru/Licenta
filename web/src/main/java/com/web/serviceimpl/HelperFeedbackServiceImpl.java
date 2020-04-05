package com.web.serviceimpl;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.dao.HelperFeedbackDao;
import com.web.model.HelperFeedback;
import com.web.service.HelperFeedbackService;

@Service("helperFeedbackService")
public class HelperFeedbackServiceImpl implements HelperFeedbackService {

	@Autowired
	private HelperFeedbackDao helperFeedbackDao;

	@Override
	public void save(HelperFeedback helperFeedback) {
		helperFeedbackDao.save(helperFeedback);
	}

	@Override
	public Optional<HelperFeedback> findById(Integer id) {
		return helperFeedbackDao.findById(id);
	}

	@Override
	public void delete(HelperFeedback helperFeedback) {
		helperFeedbackDao.delete(helperFeedback);
	}

	@Override
	public void deleteById(Integer id) {
		helperFeedbackDao.deleteById(id);
	}

	@Override
	public Set<HelperFeedback> findAll() {
		return helperFeedbackDao.findAll();
	}

	@Override
	public Optional<HelperFeedback> findByLearnerAccountId(Integer accountId) {
		return helperFeedbackDao.findByLearnerAccountId(accountId);
	}

	@Override
	public Set<HelperFeedback> findAllByLearnerAccountId(Integer accountId) {
		return helperFeedbackDao.findAllByLearnerAccountId(accountId);
	}

	@Override
	public Optional<HelperFeedback> findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(Integer helperId,
			Timestamp startDate, Timestamp endDate) {
		return helperFeedbackDao.findFirstByHelperAccountIdAndDateOfFeedbackProviedBetween(helperId, startDate,
				endDate);
	}
}
