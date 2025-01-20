package com.example.tvpssmis.service.application;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.tvpssmis.entity.StudentApplication;


import java.util.List;

@Repository
public class ApplicationDAO {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Transactional
    public StudentApplication findById(int id) {
    	Session currentSession = sessionFactory.getCurrentSession();
    	return currentSession.get(StudentApplication.class, id);
    }

    @Transactional
    public void save(StudentApplication application) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.saveOrUpdate(application);
    }

    @Transactional
    public void detach(StudentApplication application) {
    	Session currentSession = sessionFactory.getCurrentSession();
    	currentSession.evict(application);
    }
    
    @Transactional
    public void update(int id, StudentApplication application) {
    	Session currentSession = sessionFactory.getCurrentSession();
    	StudentApplication existingApplication = currentSession.get(StudentApplication.class, id);
    	
    	if(existingApplication !=null) {
    		existingApplication.setInterest(application.getInterest());
    		existingApplication.setSkills(application.getSkills());
    		existingApplication.setStatus(application.getStatus());
    		existingApplication.setApplyDate(application.getApplyDate());
    		
    		currentSession.merge(existingApplication);
    	}
    }
    
    @Transactional
    public void delete(int id) {
    	Session currentSession = sessionFactory.getCurrentSession();
    	StudentApplication application = currentSession.byId(StudentApplication.class).load(id);
    	if(application != null) {
    		currentSession.delete(application);
    	}
    }
    
    @Transactional
    public List<StudentApplication> findAll(){
    	Session currentSession = sessionFactory.getCurrentSession();
    	return currentSession.createQuery("from StudentApplication", StudentApplication.class).getResultList();
    }
    
    @Transactional
    public List<StudentApplication> findByProgramId(int programId){
    	Session currentSession = sessionFactory.getCurrentSession();
    	return currentSession.createQuery("From StudentApplication where program.id = :programId)", StudentApplication.class)
    			.setParameter("programId", programId).getResultList();
    }
    
    @Transactional
    public long countApplication() {
    	Session currentSession = sessionFactory.getCurrentSession();
    	return (long) currentSession.createQuery("select count(e) from StudentApplication e").uniqueResult();
    }
    
    @Transactional
    public int countAcceptedApplications() {
        Session session = sessionFactory.openSession();
        Long count = (Long) session.createQuery("SELECT COUNT(*) FROM StudentApplication WHERE status = 'Accepted'").uniqueResult();
        session.close();
        return count != null ? count.intValue() : 0;
    }
    
    @Transactional
    public int countRejectedApplications() {
        Session session = sessionFactory.openSession();
        Long count = (Long) session.createQuery("SELECT COUNT(*) FROM StudentApplication WHERE status = 'Rejected'").uniqueResult();
        session.close();
        return count != null ? count.intValue() : 0;
    }
    
    @Transactional
    public List<StudentApplication> findByStatus(String status) {
        Session currentSession = sessionFactory.openSession();
        return currentSession.createQuery("from StudentApplication where status = :status", StudentApplication.class)
        		.setParameter("status", status)
        		.getResultList();
    }
    
    @Transactional
    public List<StudentApplication> findByUserId(int userId){
    	Session currentSession = sessionFactory.openSession();
    	return currentSession.createQuery("from StudentApplication where userId = :userId", StudentApplication.class)
    			.setParameter("userId", userId).getResultList();
    }
    
}


