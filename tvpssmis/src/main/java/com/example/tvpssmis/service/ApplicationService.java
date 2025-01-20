package com.example.tvpssmis.service;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.tvpssmis.entity.StudentApplication;
import com.example.tvpssmis.service.application.ApplicationDAO;

@Service
public class ApplicationService {
	
	private final ApplicationDAO applicationDAO;
    
	@Autowired
	public ApplicationService(ApplicationDAO applicationDAO) {
		this.applicationDAO = applicationDAO;
	}

	@Transactional
	public List<StudentApplication> getAllApplication(){
		return applicationDAO.findAll();
	}
	
	@Transactional
	public StudentApplication getById (int applicationId){
		return applicationDAO.findById(applicationId);
	}
	
	@Transactional
	public List<StudentApplication> getApplicationByUserId(int userId){
		return applicationDAO.findByUserId(userId);
	}
	
	@Transactional
	public void apply(StudentApplication application) {
		applicationDAO.save(application);
	}
	
	@Transactional
	public List<StudentApplication> getPendingApplication(){
		return applicationDAO.findByStatus("Pending");
	}
	
	@Transactional
	public void updateApplicationStatus(int applicationId, String Status) {
		StudentApplication application = applicationDAO.findById(applicationId);
		if (application != null) {
			application.setStatus(Status);
			applicationDAO.save(application);
		}
	}
	
	@Transactional
	public void deleteApplication(int applicationId) {
		StudentApplication application = applicationDAO.findById(applicationId);
		if (application != null) {
			applicationDAO.delete(application.getApplicationID());
		}
	}
	
	@Transactional
	public long getTotalApplicationCount() {
		return applicationDAO.countApplication();
	}
	
	@Transactional
	public long getAcceptedApplicationCount() {
		return applicationDAO.countAcceptedApplications();
	}
	
	@Transactional
	public long getRejectedApplicationCount() {
		return applicationDAO.countRejectedApplications();
	}
}
