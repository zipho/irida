package ca.corefacility.bioinformatics.irida.web.controller;

import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.UserResource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author josh
 */
@Controller
@RequestMapping(value = "/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final CRUDService<String, User> userCRUD;

    @Autowired
    public UsersController(CRUDService<String, User> userCRUD) {
        this.userCRUD = userCRUD;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String showUsersPage(Model model) {
        return "users/index";
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable int id, Model model) {
        model.addAttribute("userId", id);
        return "users/user";
    }

    @RequestMapping(produces = "application/json")
    public List<UserResource> getJsonAllUsers() {
        logger.info("JSON /users called");
        List<User> users = userCRUD.list();
        List<UserResource> resources = new ArrayList<>(users.size());
        logger.info("The size is: ", users.size());
        for (User u : users) {
            UserResource resource = new UserResource(u);
            Link link = linkTo(UsersController.class).slash(u.getId()).withSelfRel();
            resource.add(link);
            resources.add(resource);
        }
        return resources;
    }
}
