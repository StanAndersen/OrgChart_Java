package com.systemsinmotion.orgchart.web.controller;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.systemsinmotion.orgchart.entity.Department;
import com.systemsinmotion.orgchart.entity.Employee;
import com.systemsinmotion.orgchart.entity.JobTitle;
import com.systemsinmotion.orgchart.service.DepartmentService;
import com.systemsinmotion.orgchart.service.EmployeeService;
import com.systemsinmotion.orgchart.service.JobTitleService;
import com.systemsinmotion.orgchart.web.View;

@Controller
public class DefaultController {

	private static final String REDIRECT = "redirect:/app/";

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory
			.getLogger(DefaultController.class);

	@Autowired
	EmployeeService employeeService;

	@Autowired
	DepartmentService departmentService;

	@Autowired
	JobTitleService jobTitleService;
	
//************************* GET Methods****************************************
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public String doGet() {
		return View.HOME;
	}
	
	@RequestMapping(value = "emps", method = RequestMethod.GET)
	public String doEmployees_GET(String string, String string2,
			String string3, String string4, Model model) {
		addAllActiveEmployeesToModel(model);
		addAllActiveJobTitlesToModel(model);
		addAllActiveDepartmentsToModel(model);
		return View.EMPLOYEES;
	}

	@RequestMapping(value = "depts", method = RequestMethod.GET)
	public String doDepartments_GET(Model model) {
		addAllActiveDepartmentsToModel(model);
		return View.DEPARTMENTS;
	}

	@RequestMapping(value = "titles", method = RequestMethod.GET)
	public String doJobTitles_GET(Model model) {
		addAllActiveJobTitlesToModel(model);
		return View.JOB_TITLES;
	}
	
//************************* POST Methods****************************************
	@RequestMapping(value = "empl", method = RequestMethod.POST)
	public String doEmployees_POST(Employee employee, Model model) {
		this.employeeService.storeEmployee(employee);
		addAllActiveEmployeesToModel(model);
		return REDIRECT + View.EMPLOYEES;
	}

	@RequestMapping(value = "depart", method = RequestMethod.POST)
	public String doDepartments_POST(Department department, Model model) {
		this.departmentService.saveDepartment(department);
		addAllActiveDepartmentsToModel(model);
		return REDIRECT + View.DEPARTMENTS;
	}
	
	@RequestMapping(value = "title", method = RequestMethod.POST)
	public String doJobTitles_POST(JobTitle jobTitle, Model model) {
		this.jobTitleService.saveJobTitle(jobTitle);
		addAllActiveJobTitlesToModel(model);
		return REDIRECT + View.JOB_TITLES;
	}
	
//************************* PUT Methods****************************************
	// Code Spike put
	@RequestMapping(value = "depart", method = RequestMethod.PUT)
	public String doDepartments_PUT(Department department, Model model) {
		this.departmentService.saveDepartment(department);
		addAllActiveDepartmentsToModel(model);
		return REDIRECT + View.DEPARTMENTS;
	}	
	
	@RequestMapping(value = "title", method = RequestMethod.PUT)
	public String doJobTitles_PUT(JobTitle jobTitle, Model model) {
		this.jobTitleService.saveJobTitle(jobTitle);
		addAllActiveJobTitlesToModel(model);
		return REDIRECT + View.JOB_TITLES;
	}
	
//************************* DELETE Methods****************************************
	// Code Spike delete
	@RequestMapping(value = "delete/dept/{id}", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> doDepartments_Delete(@PathVariable("id") Integer id) {
		Department department = departmentService.findDepartmentByID(id);
		
		this.departmentService.removeDepartment(department);
		
		String removeMsg = "Department " + department.getName() + " succesfully removed.";
		return new ResponseEntity<String>(removeMsg, HttpStatus.ACCEPTED);
	}
	
	@RequestMapping(value = "delete/title/{id}", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> doJobTitle_Delete(@PathVariable("id") Integer id) {
		JobTitle jobTitle = jobTitleService.findbyID(id);
		
		this.jobTitleService.removeJobTitle(jobTitle);
		
		String removeMsg = "Job Title " + jobTitle.getName() + " succesfully removed.";
		return new ResponseEntity<String>(removeMsg, HttpStatus.ACCEPTED);
	}
	
//************************* AJAX Methods****************************************
	
	// Code Spike AJAX
	@RequestMapping(value = "dept/{id}", method = RequestMethod.GET)
	public @ResponseBody String getDeptAjax(@PathVariable("id") Integer id) {
		if (id == null) {
			return "";
		}
		Department dept = departmentService.findDepartmentByID(id);
		return dept.toString();
	}

	@RequestMapping(value = "title/{id}", method = RequestMethod.GET)
	public @ResponseBody String getJobAjax(@PathVariable("id") Integer id) {
		if (id == null) {
			return "";
		}
		JobTitle jobtitle = jobTitleService.findbyID(id);
		return jobtitle.toString();
	}
	
	// Code Spike exception handler
	
	@ExceptionHandler(ConstraintViolationException.class) 
	public RedirectView test() {
		RedirectView redirectView = new RedirectView(View.HOME);
		return redirectView;
	}
	
	// Code Spike constraint violation example
	@RequestMapping(value = "depts2/{name}", method = RequestMethod.GET)
	public String testFail(@PathVariable("name") String name) {
		if (name.equals("hi")) {
			
			throw new ConstraintViolationException("You failed", new SQLException("failed"), "name");
		}
		
		return View.DEPARTMENTS;
	}

	private void addAllActiveEmployeesToModel(Model model) {
		List<Employee> employees = employeeService.findAllActiveEmployees();
		model.addAttribute("emps", employees);
	}
	
	private void addAllActiveDepartmentsToModel(Model model) {
		List<Department> departments = departmentService.findAllActiveDepartments();
		model.addAttribute("depts", departments);
	}
	
	private void addAllActiveJobTitlesToModel(Model model) {
		List<JobTitle> jobTitles = jobTitleService.findAllActiveJobTitles();
		model.addAttribute("titles", jobTitles);
	}
	
	public void setDepartmentService(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}
}
