package com.Linda.AplApp.Controller;

import com.Linda.AplApp.Configuration.FormValidationUtil;
import com.Linda.AplApp.Entity.RequestResponse;
import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Service.UserService;
import jakarta.persistence.Table;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.LinkOption;
import java.util.List;

@Controller
@Table(name = "Teachers")
@RequestMapping(value = "/teacher")
public class TeacherController {

    @Autowired
    UserService userService;

    @RequestMapping(value="/home", method = RequestMethod.GET)
    public ModelAndView home(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Home");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            modelAndView.addObject("user_email", auth.getName());
            List<User> users = userService.findUserByEmail(auth.getName());
            modelAndView.setViewName("teacher/home");
            if (users.size() == 1){
                modelAndView.addObject("full_name", users.get(0).getFirstName() + " " + users.get(0).getLastName());
            }
            modelAndView.addObject("teachers_count", userService.getUsersCount("TEACHER"));
            modelAndView.addObject("students_count", userService.getUsersCount("STUDENT"));
        } else {
            LoggerFactory.getLogger(LoggerFactory.class).error("AUTH ERROR :: Auth is null");
        }
        return modelAndView;
    }

    @RequestMapping(value="/teachers", method = RequestMethod.GET)
    public ModelAndView teachers(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Teachers");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            List<User> teachers = userService.getUsersByRoleNotLoggedIn("TEACHER", auth.getName());
            LoggerFactory.getLogger(LinkOption.class).info("GET TEACHERS DATA :: " + teachers.toString());
            modelAndView.addObject("teachers", teachers);
        } else {
            LoggerFactory.getLogger(LoggerFactory.class).error("AUTH ERROR :: Auth is null");
        }
        modelAndView.setViewName("teacher/teachers");
        return modelAndView;
    }

    @RequestMapping(value="/students", method = RequestMethod.GET)
    public ModelAndView students(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Students");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            List<User> students = userService.getUsersByRoleNotLoggedIn("STUDENT", auth.getName());
            LoggerFactory.getLogger(LinkOption.class).info("GET STUDENTS DATA :: " + students.toString());
            modelAndView.addObject("students", students);
        } else {
            LoggerFactory.getLogger(LoggerFactory.class).error("AUTH ERROR :: Auth is null");
        }
        modelAndView.setViewName("teacher/students");
        return modelAndView;
    }

    @RequestMapping(value="/teachers/edit", method = RequestMethod.GET)
    public ModelAndView editTeacher(@RequestParam(value = "k") String key){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("title", "Edit teacher");
        Authentication authy = SecurityContextHolder.getContext().getAuthentication();
        if (authy != null){
            LoggerFactory.getLogger(LinkOption.class).info("GET TEACHER DATA :: " + userService.getUsersByRoleNotLoggedIn("TEACHER", authy.getName()));
            modelAndView.addObject("teachers", userService.getUsersByRoleNotLoggedIn("TEACHER", authy.getName()));
        } else {
            LoggerFactory.getLogger(LoggerFactory.class).error("AUTH ERROR :: Auth is null");
        }
        User user = userService.getUserById(Long.valueOf(key));
        modelAndView.addObject("edit_data", user);
        modelAndView.setViewName("teacher/edit_teacher");
        return modelAndView;
    }

    @RequestMapping(value = "/susact_user", method = RequestMethod.GET)
    public String activateAccount(@RequestParam(value = "i") String id, @RequestParam(value = "t") String type, @RequestParam("ac") String action, RedirectAttributes redirectAttributes){
        User user = User.builder().id(Long.valueOf(id)).build();
        if (action.equals("0")){
            user.setActive(0);
            RequestResponse requestResponse = userService.updateUserStatus(user);
            if (requestResponse.getResponseCode() == 0){
                redirectAttributes.addFlashAttribute("success", true);
                redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-check-circle\"></i>&nbsp;User account successfully suspended");
            } else {
                redirectAttributes.addFlashAttribute("success", false);
                redirectAttributes.addFlashAttribute("message", requestResponse.getMessage());
            }
        } else {
            user.setActive(1);
            RequestResponse requestResponse = userService.updateUserStatus(user);
            if (requestResponse.getResponseCode() == 0){
                redirectAttributes.addFlashAttribute("success", true);
                redirectAttributes.addFlashAttribute("message", "<i class=\"fa fa-check-circle\"></i>&nbsp;User account successfully reactivated");
            } else {
                redirectAttributes.addFlashAttribute("success", false);
                redirectAttributes.addFlashAttribute("message", requestResponse.getMessage());
            }
        }
        switch (type) {
            case "0":
                return "redirect:/teacher/students";
            case "1":
                return "redirect:/teacher/teachers";
            default:
                return "redirect:/logout";
        }
    }

    @RequestMapping(value = "/new_teacher", method = RequestMethod.POST)
    public String newTeacher(@RequestParam("tfname") String teacherFirstName, @RequestParam("tlname") String teacherLastName, @RequestParam("temail") String teacherEmail, RedirectAttributes redirectAtt){
        FormValidationUtil formValidationUtil = new FormValidationUtil();
        if (!formValidationUtil.isEmptyInput(teacherFirstName)) {
            if (!formValidationUtil.isEmptyInput(teacherLastName)) {
                if (!formValidationUtil.isEmptyInput(teacherEmail)) {
                    teacherFirstName = teacherFirstName.trim();
                    teacherLastName = teacherLastName.trim();
                    teacherEmail = teacherEmail.trim();
                    User student = User.builder().firstName(teacherFirstName.toLowerCase()).lastName(teacherLastName.toLowerCase()).email(teacherEmail).password(teacherEmail).role("TEACHER").build();
                    LoggerFactory.getLogger(LoggerFactory.class).info("USER INPUT :: NEW TEACHER DATA --- " + student.toString());
                    RequestResponse requestResponse = userService.saveUser(student);
                    LoggerFactory.getLogger(LoggerFactory.class).info("NEW TEACHER :: RESP --- " + requestResponse.toString());
                    if (requestResponse.getResponseCode() == 0){
                        redirectAtt.addFlashAttribute("success", true);
                        redirectAtt.addFlashAttribute("message", "<i class=\"fa fa-check-circle\"></i>&nbsp;Teacher successfully added. Teacher's first time login password is <b>" + teacherEmail + "</b>");
                    } else {
                        redirectAtt.addFlashAttribute("success", false);
                        redirectAtt.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;" + requestResponse.getMessage());
                        redirectAtt.addFlashAttribute("tfname", teacherFirstName);
                        redirectAtt.addFlashAttribute("tlname", teacherLastName);
                        redirectAtt.addFlashAttribute("temail", teacherEmail);
                    }
                } else {
                    redirectAtt.addFlashAttribute("success", false);
                    redirectAtt.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;Teacher's email address is required");
                    redirectAtt.addFlashAttribute("tfname", teacherFirstName);
                    redirectAtt.addFlashAttribute("tlname", teacherLastName);
                    redirectAtt.addFlashAttribute("temail", teacherEmail);
                }
            } else {
                redirectAtt.addFlashAttribute("success", false);
                redirectAtt.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;Teacher's last name is required");
                redirectAtt.addFlashAttribute("tfname", teacherFirstName);
                redirectAtt.addFlashAttribute("tlname", teacherLastName);
                redirectAtt.addFlashAttribute("temail", teacherEmail);
            }
        } else {
            redirectAtt.addFlashAttribute("success", false);
            redirectAtt.addFlashAttribute("message", "<i class=\"fa fa-times-circle\"></i>&nbsp;Teacher's first name is required");
            redirectAtt.addFlashAttribute("tfname", teacherFirstName);
            redirectAtt.addFlashAttribute("tlname", teacherLastName);
            redirectAtt.addFlashAttribute("temail", teacherEmail);
        }
        return "redirect:/teacher/teachers";
    }
}