package com.unilog.app.resource;

import com.unilog.app.representation.AdminWindow;
import com.unilog.app.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Controller
public class AdminResource {

    @Autowired
    private AdminService adminService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);

    @GetMapping("/admin/portal")
    @Secured("ROLE_ADMIN")
    public String adminPortalForm(final Model model) {
        model.addAttribute("adminWindow", adminService.generateAdminWindow());
        return "adminPortal";
    }

    @PostMapping(value = "/admin/portal", params = "action=Publish")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @Secured("ROLE_ADMIN")
    public String adminPortalPublishRegistry(final Map<String, Object> model) {
        String result = adminService.publishRegistry();
        if (result.equals("error")) {
            model.put("registryError", true);
        }
        model.put("adminWindow", adminService.generateAdminWindow());
        return "adminPortal";
    }

    @PostMapping(value = "/admin/portal", params = "action=Complete")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public String adminPortalPublishOneUser(@ModelAttribute final AdminWindow request, final Map<String, Object> model) {
        try {
            boolean result = adminService.completeAccount(request.getSelectedID());
            if (result) {
                model.put("successOneUser", true);
            } else {
                model.put("publishError", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.put("publishError", true);
        }
        model.put("adminWindow", adminService.generateAdminWindow());
        return "adminPortal";
    }

    @PostMapping(value = "/admin/portal", params = "action=Complete All")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @Secured("ROLE_ADMIN")
    public String adminPortalPublishAllUsers(final Map<String, Object> model) {
        try {
            String result = adminService.completeAllAccounts();
            if (result.equals("allSuccess")) {
                model.put(result, true);
            } else if (result.equals("partialSuccess")) {
                model.put(result, true);
            } else { //zeroSuccess
                model.put(result, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.put("publishError", true);
        }
        model.put("adminWindow", adminService.generateAdminWindow());
        return "adminPortal";
    }

    @PostMapping(value = "/admin/portal", params = "action=Register")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    @Secured("ROLE_ADMIN")
    public String adminPortalRegisterUser(@ModelAttribute final AdminWindow request, final Map<String, Object> model) {
        if (!adminService.registerAccount(request)) {
            model.put("registrationError", true);
        } else {
            model.put("registrationSuccess", true);
        }
        model.put("adminWindow", adminService.generateAdminWindow());
        return "adminPortal";
    }
}
