package com.Linda.AplApp.Controller;

import com.Linda.AplApp.Configuration.FormValidationUtil;
import com.Linda.AplApp.Entity.RequestResponse;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Service.UserService;
import jakarta.persistence.Table;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.LinkOption;


@Controller
@Table(name = "Students")
@RequestMapping(value = "/student")
public class StudentController {

    private UserService userService;

    @RequestMapping(value="/students/edit", method = RequestMethod.GET)
    public ModelAndView editStudent(@RequestParam(value = "k") String key){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Edit student");
        Authentication authy = SecurityContextHolder.getContext().getAuthentication();
        if (authy != null){
            LoggerFactory.getLogger(LinkOption.class).info("GET STUDENTS DATA :: " + userService.getUsersByRoleNotLoggedIn("STUDENT", authy.getName()));
            modelAndView.addObject("students", userService.getUsersByRoleNotLoggedIn("STUDENT", authy.getName()));
        } else {
            LoggerFactory.getLogger(LoggerFactory.class).error("AUTH ERROR :: Auth is null");
        }
        User user = userService.getUserById(Long.valueOf(key));
        modelAndView.addObject("edit_data", user);
        modelAndView.setViewName("teacher/edit_student");
        return modelAndView;
    }
    @RequestMapping(value = "/update_user", method = RequestMethod.POST)
    public String updateUser(@RequestParam(value = "Förnamn") String userFirstName, @RequestParam(value = "Efternamn") String userLastName, @RequestParam(value = "email") String userEmail, @RequestParam(value = "Telefonnummer") int userPhoneNumber, @RequestParam(value = "Kön") String userGender, @RequestParam("t") String userType, @RequestParam("i") String id, RedirectAttributes redirectAttributes){
        FormValidationUtil formValidationUtil = new FormValidationUtil();
        boolean suc = false;
        if (!formValidationUtil.isEmptyInput(userFirstName)){
            if (!formValidationUtil.isEmptyInput(userLastName)){
                if (!formValidationUtil.isEmptyInput(userLastName)){
                    User user = User.builder().firstName(userFirstName).lastName(userLastName).email(userEmail).id(Long.valueOf(id)).build();
                    RequestResponse requestResponse = userService.updateUserDetails(user);
                    if (requestResponse.getResponseCode() == 0){
                        redirectAttributes.addFlashAttribute("success", true);
                        suc = true;
                        redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-check-circle\"></i>&nbsp;User details successfully updated");
                    } else {
                        redirectAttributes.addFlashAttribute("success", false);
                        redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;" + requestResponse.getMessage());
                        redirectAttributes.addFlashAttribute("f", userFirstName);
                        redirectAttributes.addFlashAttribute("l", userLastName);
                        redirectAttributes.addFlashAttribute("e", userEmail);
                        redirectAttributes.addFlashAttribute("p", userPhoneNumber);
                        redirectAttributes.addFlashAttribute("g", userGender);
                    }
                } else {
                    redirectAttributes.addFlashAttribute("success", false);
                    redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; Email address is required");
                    redirectAttributes.addFlashAttribute("f", userFirstName);
                    redirectAttributes.addFlashAttribute("l", userLastName);
                    redirectAttributes.addFlashAttribute("e", userEmail);
                    redirectAttributes.addFlashAttribute("p", userPhoneNumber);
                    redirectAttributes.addFlashAttribute("g", userGender);
                }
            } else {
                redirectAttributes.addFlashAttribute("success", false);
                redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; Last name is required");
                redirectAttributes.addFlashAttribute("f", userFirstName);
                redirectAttributes.addFlashAttribute("l", userLastName);
                redirectAttributes.addFlashAttribute("e", userEmail);
                redirectAttributes.addFlashAttribute("p", userPhoneNumber);
                redirectAttributes.addFlashAttribute("g", userGender);
            }
        } else {
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; First name is required");
            redirectAttributes.addFlashAttribute("f", userFirstName);
            redirectAttributes.addFlashAttribute("l", userLastName);
            redirectAttributes.addFlashAttribute("e", userEmail);
            redirectAttributes.addFlashAttribute("p", userPhoneNumber);
            redirectAttributes.addFlashAttribute("g", userGender);
        }
        switch (userType) {
            case "STUDENT":
                if (suc){
                    return "redirect:/teacher/students";
                } else {
                    return "redirect:/teacher/students/edit?k=" + id + "#edit";
                }
            case "TEACHER":
                if (suc){
                    return "redirect:/teacher/teachers";
                } else {
                    return "redirect:/teacher/teachers/edit?k=" + id + "#edit";
                }
            default:
                return "redirect:/logout";
        }
    }

    @RequestMapping(value = "/new_student", method = RequestMethod.POST)
    public String newStudent(@RequestParam("Förnamn") String studentFirstName, @RequestParam("Efternamn") String studentLastName, @RequestParam("email") String studentEmail, @RequestParam("Telefonnummer") int studentPhoneNumber, @RequestParam("Kön") String studentGender, RedirectAttributes redirectAttributes){
        FormValidationUtil formValidationUtil = new FormValidationUtil();
        if (!formValidationUtil.isEmptyInput(studentFirstName)){
            if (!formValidationUtil.isEmptyInput(studentLastName)){
                if (!formValidationUtil.isEmptyInput(studentEmail)){
                    studentFirstName = studentFirstName.trim();
                    studentLastName = studentLastName.trim();
                    studentEmail = studentEmail.trim();
                    studentGender = studentGender.trim();
                    User student = User.builder().firstName(studentFirstName.toLowerCase()).lastName(studentLastName.toLowerCase()).email(studentEmail).password(studentEmail).role("STUDENT").build();
                    LoggerFactory.getLogger(LoggerFactory.class).info("USER INPUT :: NEW STUDENT DATA --- " + student.toString());
                    RequestResponse requestResponse = userService.saveUser(student);
                    LoggerFactory.getLogger(LoggerFactory.class).info("NEW STUDENT :: RESP --- " + requestResponse.toString());
                    if (requestResponse.getResponseCode() == 0){
                        redirectAttributes.addFlashAttribute("success", true);
                        redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-check-circle\"></i>&nbsp;Student successfully added. Student's first time login password is <b>" + studentEmail + "</b>");
                    } else {
                        redirectAttributes.addFlashAttribute("success", false);
                        redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;" + requestResponse.getMessage());
                        redirectAttributes.addFlashAttribute("Förnamn", studentFirstName);
                        redirectAttributes.addFlashAttribute("Efternamn", studentLastName);
                        redirectAttributes.addFlashAttribute("email", studentEmail);
                        redirectAttributes.addFlashAttribute("phoneNumber", studentPhoneNumber);
                        redirectAttributes.addFlashAttribute("gender", studentGender);
                    }
                } else {
                    redirectAttributes.addFlashAttribute("success", false);
                    redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; Students email address is required");
                    redirectAttributes.addFlashAttribute("Förnamn", studentFirstName);
                    redirectAttributes.addFlashAttribute("Efternamn", studentLastName);
                    redirectAttributes.addFlashAttribute("email", studentEmail);
                    redirectAttributes.addFlashAttribute("phoneNumber", studentPhoneNumber);
                    redirectAttributes.addFlashAttribute("gender", studentGender);
                }
            } else {
                redirectAttributes.addFlashAttribute("success", false);
                redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; Students last name is required");
                redirectAttributes.addFlashAttribute("Förnamn", studentFirstName);
                redirectAttributes.addFlashAttribute("Efternamn", studentLastName);
                redirectAttributes.addFlashAttribute("email", studentEmail);
                redirectAttributes.addFlashAttribute("phoneNumber", studentPhoneNumber);
                redirectAttributes.addFlashAttribute("gender", studentGender);
            }
        } else {
            redirectAttributes.addFlashAttribute("success", false);
            redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp; Students first name is required");
            redirectAttributes.addFlashAttribute("Förnamn", studentFirstName);
            redirectAttributes.addFlashAttribute("Efternamn", studentLastName);
            redirectAttributes.addFlashAttribute("email", studentEmail);
            redirectAttributes.addFlashAttribute("phoneNumber", studentPhoneNumber);
            redirectAttributes.addFlashAttribute("gender", studentGender);
        }
        return "redirect:/teacher/students";
    }

}