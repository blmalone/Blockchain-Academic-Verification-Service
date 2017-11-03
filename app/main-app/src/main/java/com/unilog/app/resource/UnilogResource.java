package com.unilog.app.resource;

import com.unilog.app.exception.UserExistsException;
import com.unilog.app.representation.RegistrationRequest;
import com.unilog.app.representation.Transcript;
import com.unilog.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class UnilogResource {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnilogResource.class);

    @GetMapping("/")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public String home(final Map<String, Object> model, final HttpSession session) {
        //checkSession(session);
        model.put("message", "Logged in Successfully"); //populating message part of model
        model.put("title", "Finish account activation");
        model.put("date", new Date());
        return "home"; //Display home.html
    }

    //GET - Returns the upload qualifications form.
    @GetMapping("/upload/qualification")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public String uploadQualificationForm(final Model model) {
        com.unilog.app.representation.Qualification qualification = new com.unilog.app.representation.Qualification();
        qualification.getTranscripts().add(new Transcript());
        qualification.getTranscripts().add(new Transcript()); //Allowing 2 transcripts to be added at a time.
        model.addAttribute("qualification", qualification);
        return "qualification";
    }

    //POST
    @PostMapping(value = "/upload/qualification", headers = "Accept=application/json")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String uploadQualifications(@ModelAttribute final com.unilog.app.representation.Qualification qualification,
                                       final HttpSession session, final Map<String, Object> model) throws UnsupportedEncodingException {
        checkSession(session);
        qualification.setIssuer((String) session.getAttribute("currentUser"));
        String result = userService.uploadQualification(qualification);
        if (result.equals("success")) {
            LOGGER.info("Adding qualifications");
            model.put("success", true);
            com.unilog.app.representation.Qualification emptyQualification = new com.unilog.app.representation.Qualification();
            emptyQualification.getTranscripts().add(new Transcript());
            emptyQualification.getTranscripts().add(new Transcript()); //Allowing 2 transcripts to be added at a time.
            model.put("qualification", emptyQualification);
        } else if (result.equals("qualificationError")) {
            model.put("qualificationError", true);
        } else {
            model.put("emailError", true);
        }
        return "qualification";
    }

    //GET - Returns the complete registration form.
    @GetMapping("/complete/registration")
    public String completeRegistrationForm(final Map<String, Object> model) {
        boolean isRegistrySet = false;
        if (model.size() != 0) {
            isRegistrySet = (boolean) model.get("registryCreated");
        }
        RegistrationRequest registrationRequest = userService.generateRegistrationRequest(isRegistrySet);
        model.put("registrationRequest", registrationRequest);
        return "registration";
    }

    //POST
    @PostMapping(value = "/complete/registration", headers = "Accept=application/json", params = "action=Submit")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String completeRegistration(@ModelAttribute final RegistrationRequest request, final Map<String, Object> model)
            throws UserExistsException, IOException, InterruptedException {
        LOGGER.info("Completing registration for transcript owner");
        if (userService.isRegistered(request.getRecipientEmailAddress())) {
            model.put("alreadyRegistered", true);
        } else if (userService.completeRegistration(request)) {
            LOGGER.info("Adding qualifications");
            model.put("success", true);
            RegistrationRequest registrationRequest = new RegistrationRequest();
            model.put("registrationRequest", registrationRequest);
        } else {
            model.put("error", true);
        }
        return "registration";
    }

    //POST
    @PostMapping(value = "/complete/registration", headers = "Accept=application/json", params = "action=Apply")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String applyForRegistration(@ModelAttribute final RegistrationRequest request, final Map<String, Object> model) throws UserExistsException, IOException, InterruptedException {
        LOGGER.info("Applying to register by potential institution");
        if (!userService.registrationApplication(request.getInstitutionEmailAddress(), request.getMessage())) {
            RegistrationRequest registrationRequest = (RegistrationRequest) model.get("registrationRequest");
            registrationRequest.setInstitutionEmailAddress("");
            model.put("registrationRequest", registrationRequest);
            model.put("invalidRegistrationEmail", true);
        } else {
            model.put("applicationSent", true);
        }
        return "registration";
    }

    //POST
    @PostMapping(value = "/complete/registration", headers = "Accept=application/json", params = "action=Activate")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String completeInstitutionRegistration(@ModelAttribute final RegistrationRequest request, final Map<String, Object> model) {
        LOGGER.info("Applying to register by potential institution");
        if (!userService.completeInstitutionRegistration(request)) {
            model.put("invalidEmail", true);
        } else {
            model.put("activated", true);
        }
        return "registration";
    }

    //GET
    @RequestMapping(value = "qualifications/{email}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public List<com.unilog.app.representation.Qualification> getAllQualifications(@PathVariable final String email) {
        LOGGER.info("Get all qualifications for {}", email);
        return userService.getQualifications(email);
    }

    private void checkSession(final HttpSession session) {
        if (null == session.getAttribute("currentUser")) {
            SecurityContextImpl spring_security_context = (SecurityContextImpl) session.getAttribute("SPRING_SECURITY_CONTEXT");
            User user = (User) spring_security_context.getAuthentication().getPrincipal();
            session.setAttribute("currentUser", user.getUsername());
        }
    }

}
