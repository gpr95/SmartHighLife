package pl.bsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bsp.entities.Resource;
import pl.bsp.model.ParentalControlPolicy;
import pl.bsp.model.User;
import pl.bsp.services.ParentalControlPolicyService;
import pl.bsp.services.UserServiceImpl;

import java.util.List;

/**
 * Created by Kamil on 2017-05-22.
 */
@RestController
public class ParentalControlController {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    ParentalControlPolicyService policyService;

    @RequestMapping(value="/parentalPolicies/{username}", method = RequestMethod.GET, produces =
            { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE } )
    public ResponseEntity<List<ParentalControlPolicy>> resources(@PathVariable("username") String username) {
        User resourcesOwner = userService.findByUsername(username);
        List<pl.bsp.model.ParentalControlPolicy> policies = resourcesOwner.getPolicies();
        if(policies == null || policies.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<>(policies, HttpStatus.OK);
    }

    @RequestMapping(value="/parentalPolicy",method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE })
    public ResponseEntity<ParentalControlPolicy> addResource(@RequestBody ParentalControlPolicy policy) {
        if(policyService.add(policy))
            return new ResponseEntity<>(policy, HttpStatus.OK);
        else
            return new ResponseEntity<>(policy, HttpStatus.BAD_REQUEST);
    }
}
