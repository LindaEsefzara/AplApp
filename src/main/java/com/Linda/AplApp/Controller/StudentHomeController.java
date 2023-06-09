package com.Linda.AplApp.Controller;

import com.Linda.AplApp.Entity.User;
import com.Linda.AplApp.Service.UserService;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
public class StudentHomeController {

    private UserService userService;

    @GetMapping("/student-home")
    public String studentHome(Model model) {
        // Här hämtar du studentens information från databasen eller någon annan datakälla
        String firstName = "";
        String lastName = "";
        String email = "";
        String password = "";
        Integer phoneNumber = 0;
        String gender = "";

        List<String> teacherMessages = Arrays.asList("Message 1", "Message 2", "Message 3");

        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        model.addAttribute("phoneNumber", phoneNumber);
        model.addAttribute("gender", gender);
        model.addAttribute("teacherMessages", teacherMessages);

        // Generera QR-koden baserat på studentens information
        generateQRCode(firstName, lastName, email);

        return "studentHome";
    }

    private void generateQRCode(String firstName, String lastName, String email) {
        String studentInfo = "First Name: " + firstName + "\nLast Name: " + lastName + "\nEmail: " + email;

        try {
            BufferedImage qrCodeImage = QRCodeGenerator.generateQRCodeImage(studentInfo);
            // Spara QR-koden eller gör något annat med den
            // Exempel: spara den som en bildfil, visa den på webbsidan, etc.
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute("user") User user) {
        try {
            // Retrieve the existing user entity from the database
            User existingUser = userService.getUserById(user.getId());

            // Update the relevant fields with the new values
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setGender(user.getGender());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            existingUser.setPassword(user.getPassword());
            // Update other fields as needed

            // Save the updated user entity
            userService.updateUser(existingUser);

            // Redirect the user to the appropriate page
            return "redirect:/student-home";
        } catch (Exception e) {
            // Handle any exceptions or errors
            e.printStackTrace();
            // Redirect the user to an error page or display an error message
            return "redirect:/error";
        }
    }
    // Övriga metoder och logik för studentens hemssida
    // ...
}
