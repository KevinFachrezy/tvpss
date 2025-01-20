package com.example.tvpssmis.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.tvpssmis.entity.StudentApplication;
import com.example.tvpssmis.entity.User;
import com.example.tvpssmis.service.application.ApplicationDAO;
import com.example.tvpssmis.service.ProgramService;


@Controller
@RequestMapping("/application")
public class ApplicationDashboardController {
    
    @Autowired
    private ApplicationDAO applicationDAO;
    
    @Autowired
    private ProgramService programService;
    
    
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
        model.addAttribute("user", loggedInUser);
        
        if (!"Student".equalsIgnoreCase(loggedInUser.getRole().getRoleName())) {
            model.addAttribute("error", "Access denied. This dashboard is only for students.");
            return "error";
        }
        
        int schoolId = loggedInUser.getSchool().getSchoolId();
    	
        int totalApplication = applicationDAO.findAll().size();
        int acceptedApplications = applicationDAO.countRejectedApplications();
        int rejectedApplications = applicationDAO.countAcceptedApplications();
        
        //Fetch overview data of each application
        List<StudentApplication> applications = applicationDAO.findAll();
;        List<ApplicationOverview> applicationOverview = new ArrayList<>();
        for(StudentApplication application : applications) {
            int numByStatus = applicationDAO.findByStatus(application.getStatus()).size();
            
//            applicationOverview.add(new ApplicationOverview(application.getApplicationID(), application.set));
        }
        
        
        
        model.addAttribute("totalApplications", totalApplication);
        model.addAttribute("acceptedApplications", acceptedApplications);
        model.addAttribute("rejectedApplications", rejectedApplications);
//        model.addAttribute("programs", 
//        model.addAttribute("applicationOverview", applicationOverview);
        
        
        return "studentApplication/dashboard";
    }
    
    public static class ApplicationOverview {
        private int applicationId;
        private int programId;
        private Date applyDate;
        
		public ApplicationOverview(int applicationId, int programId, Date applyDate) {
			super();
			this.applicationId = applicationId;
			this.programId = programId;
			this.applyDate = applyDate;
		}

		public int getApplicationId() {
			return applicationId;
		}

		public int getProgramId() {
			return programId;
		}

		public Date getApplyDate() {
			return applyDate;
		}
        
        
    }
}
