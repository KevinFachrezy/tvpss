<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<title>Student Application | TVPSS MIS</title>
<!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Font Awesome for icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
    <style>
        <%@ include file="/WEB-INF/views/css/sidebars.css" %>
    </style>
</head>

<body> 
    <div class="d-flex" style="min-height: 100vh">
		<!-- Sidebar Include -->
		<%@ include file="/WEB-INF/views/fragments/sidebar.jsp"%>
	    <div class="container-fluid p-4">
			<h1 class="mb-4">Application Form</h1>
			<div class="row justify-content-center">
				<div class="col-md-12">
	    			<form id ="Application" action="${pageContext.request.contextPath}/application/submitApplication" method="post">
	    			<input type="hidden" name="programId" value="${programId}" />
	    			<input type="hidden" name="userId" value="${userId}" />
	        			<div class="form-section">
	        				<div class="row">
								<br><br><br>
								<div class= "col-md-6-3">
			        				<label class="form-label" for="programId">Skills:</label>
						        	<input type="text" class="form-control" id="skills" name="skills" placeholder= "Enter skills" required/>
								</div>
								<br><br><br>
								<div class= "col-md-6-3">
			        				<label class="form-label" for="programId">Interest:</label>
						        	<input type="text" class="form-control" id="interests" name="interests" placeholder= "Enter Interests" required/>
								</div>
				        	</div>
						</div>
						<br>
						<div class="text-center">
							<button type="submit" class="btn btn-primary btn-lg">Apply</button>
						</div>
				   </form>
				 </div>
			</div>
		</div>
	</div>
</body>
</html>
