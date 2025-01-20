package com.example.tvpssmis.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tvpssmis.entity.Program;
import com.example.tvpssmis.entity.StudentApplication;
import com.example.tvpssmis.entity.User;
import com.example.tvpssmis.repository.UserRepository;
import com.example.tvpssmis.service.ProgramService;
import com.example.tvpssmis.service.ApplicationService;
import com.example.tvpssmis.service.application.ApplicationDAO;

@Controller
@RequestMapping("/application")
public class ApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    
    private final ApplicationDAO applicationDAO;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;
    private final ProgramService programService;
    
    

    public ApplicationController(ApplicationDAO applicationDAO, UserRepository userRepository,
			ApplicationService applicationService, ProgramService programService) {
		super();
		this.applicationDAO = applicationDAO;
		this.userRepository = userRepository;
		this.applicationService = applicationService;
		this.programService = programService;
	}
    
    @GetMapping
    public String getApplication(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
        int roleId = loggedInUser.getRole().getRoleId();
        List<StudentApplication> applications;
        boolean hasApplicationForUser = false;
        
        if (roleId == 4 || roleId == 5) {
        	applications = applicationService.getAllApplication();
        	hasApplicationForUser = true;
        	System.out.println("This stage pass");
        } else {
        	applications = applicationService.getApplicationByUserId(loggedInUser.getUserId());
        	hasApplicationForUser = applications.stream()
        	.anyMatch(application -> application.getUser().getUserId() == loggedInUser.getUserId());
        }
        
        System.out.println("This stage pass");
        model.addAttribute("application", applications);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("hasApplicationForUser", hasApplicationForUser);
        
        return "application/applications";
    }
    
    @GetMapping("/submitApplication")
    public String showApplicationForm(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("user");
    	if (loggedInUser == null || loggedInUser.getRole().getRoleId() != 3) { // Only Students
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
    	
    	int schoolId = loggedInUser.getSchool().getSchoolId();
    	
    	List<Program> programs = programService.getProgramsBySchoolId(schoolId);
    	model.addAttribute("programs",programs);
    	model.addAttribute("user", loggedInUser);
    	
    	return "application/studentApplicationForm";
    }
    
    @PostMapping("/submitApplication")
    public String submitApplication(HttpServletRequest request, HttpSession session, Model model) {
    	User loggedInUser = (User) session.getAttribute("user");
    	if (loggedInUser == null || loggedInUser.getRole().getRoleId() != 3) { // Only Students
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
    	
    	StudentApplication application = new StudentApplication();
    	application.setInterest(request.getParameter("interests"));
    	application.setSkills(request.getParameter("skills"));
    	application.setStatus("Pending");
    	application.setApplyDate(new Date());
    	
//    	int programId = Integer.parseInt(request.getParameter("programId"));
    	Program program = programService.getProgramById(1);
    	if (program == null) {
            model.addAttribute("error", "Selected program not found.");
            return "error";
        }
    	application.setProgramID(program);
    	application.setUser(loggedInUser.getUserId());
    	applicationService.apply(application);
    	
    	return "redirect:/applications";
    }

//	@PostMapping("/submitApplication")
//    public String submitApplication(@RequestParam("programId") int programId,
//                                     @RequestParam("skills") String skills,
//                                     @RequestParam("interests") String interests,
//                                     HttpSession session) {
//        Integer userId = (Integer) session.getAttribute("userId"); // Retrieve userId from session
//        User user = userRepository.findById(userId).orElse(null);
//        
//        logger.info("Received application submission: userId={}, programId={}, skills={}, interests={}", userId, programId, skills, interests);
//
//        StudentApplication application = new StudentApplication();
//        
//        Program program = programService.getProgramById(programId);
//        
//        if (user != null && program != null) {
//            application.setUser(user);
//            application.setProgramID(program);
//            application.setSkills(skills);
//            application.setInterest(interests);
//            application.setApplyDate(new Date());
//
//            applicationDAO.save(application);
//            logger.info("Application saved successfully for userId={}", userId);
//            return "redirect:/application/applications";
//        } else {
//            logger.error("User or Program not found: userId={}, programId={}", userId, programId);
//            return "redirect:/application/error";
//        }
//    }
    
    @GetMapping("/approvals")
    public String showApprovalPage(Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("user");

    	if (loggedInUser == null || (loggedInUser.getRole().getRoleId() != 4 && loggedInUser.getRole().getRoleId() != 5)) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
    	List<StudentApplication> pendingApplications = applicationService.getPendingApplication();
    	model.addAttribute("application", pendingApplications);
    	model.addAttribute("user", loggedInUser);
    	
    	return "application/approvals";
        
    }
    
    @PostMapping("/approve")
    public String approveApplication(HttpServletRequest request, HttpSession session, Model model) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null || (loggedInUser.getRole().getRoleId() != 4 && loggedInUser.getRole().getRoleId() != 5)) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
        int applicationId = Integer.parseInt(request.getParameter("applicationId"));
        String action = request.getParameter("action");
        
        if ("approve".equals(action)) {
        	applicationService.updateApplicationStatus(applicationId, "Approved");
        } else if ("reject".equals(action)) {
        	applicationService.updateApplicationStatus(applicationId, "Rejected");
        }
        return "redirect:/application/approvals";
    }
    
    @PostMapping("/updateStatus")
    public String updateStudioStatus(HttpServletRequest request, HttpSession session, Model model) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null || (loggedInUser.getRole().getRoleId() != 4 && loggedInUser.getRole().getRoleId() != 5)) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        int applicationId = Integer.parseInt(request.getParameter("applicationId"));
        String status = request.getParameter("status");
        
        applicationService.updateApplicationStatus(applicationId, status);
        
        return "redirect:/application";
    }
    
    @PostMapping("/delete")
    public String deleteApplication(HttpServletRequest request, HttpSession session, Model model) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null || (loggedInUser.getRole().getRoleId() != 4 && loggedInUser.getRole().getRoleId() != 5)) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
        int applicationId = Integer.parseInt(request.getParameter("applicationId"));
        
        StudentApplication application = applicationService.getById(applicationId);
        if (application == null) {
        	model.addAttribute("error", "Application not found.");
            return "error";
        }
        
        if("Rejected".equals(application.getStatus()) || "Pending".equals(application.getStatus())) {
        	applicationService.deleteApplication(applicationId);
        } else {
        	model.addAttribute("error", "Only Rejected or Pending applications can be deleted");
        	return "error";
        }
        return "redirect:/application";
    }
    
    @GetMapping("/{userId}/details")
    public String getApplicationDetails(@PathVariable int userId, Model model, HttpSession session) {
    	User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            model.addAttribute("error", "Unauthorized access.");
            return "error";
        }
        
        StudentApplication application = applicationService.getById(userId);
        if (application == null) {
        	model.addAttribute("error", "Application not found.");
            return "error";
        }
        
        Program program = application.getProgramID();
        
        List<StudentApplication> applicationList = new ArrayList<>();
        if(!"Student".equalsIgnoreCase(loggedInUser.getRole().getRoleName())) {
        	applicationList = applicationService.getApplicationByUserId(userId);
        }
        
        model.addAttribute("application", application);
        model.addAttribute("applicationList", applicationList);
        model.addAttribute("program", program);
        model.addAttribute("userRole", loggedInUser.getRole().getRoleName());
        
        return "studentApplication/application";
    }

    // @GetMapping("/dashboard")
    // public String getApplications(Model model, HttpSession session) {
    //     Integer userId = (Integer) session.getAttribute("userId"); // Retrieve userId from session
    //     List<StudentApplication> applications = applicationDAO.findByUserId(userId); // Fetch applications for the user
        
    //     model.addAttribute("applications", applications);
    //     return "studentApplication/dashboard"; // Return the dashboard view
    // }
}
