/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.cs4370.models.FollowableUser;
import uga.menik.cs4370.models.User;
import uga.menik.cs4370.services.PeopleService;
import uga.menik.cs4370.services.UserService;
import uga.menik.cs4370.services.FollowService;
import javax.sql.DataSource;

/**
 * Handles /people URL and its sub URL paths.
 */
@Controller
@RequestMapping("/people")
public class PeopleController {

    // Inject UserService and PeopleService instances.
    // See LoginController.java to see how to do this.
    // Hint: Add a constructor with @Autowired annotation.
    private final UserService userService;
    private final PeopleService peopleService;
    private final DataSource dataSource;
    private final FollowService followService; 

    @Autowired
    public PeopleController(PeopleService peopleService, UserService userService, DataSource dataSource, FollowService followService) {
        this.peopleService = peopleService;
        this.userService = userService;
        this.dataSource = dataSource;
        this.followService = followService; 
    }

    /**
     * Serves the /people web page.
     * 
     * Note that this accepts a URL parameter called error.
     * The value to this parameter can be shown to the user as an error message.
     * See notes in HashtagSearchController.java regarding URL parameters.
     */
    @GetMapping
    public ModelAndView webpage(@RequestParam(name = "error", required = false) String error) {
        // See notes on ModelAndView in BookmarksController.java.
        ModelAndView mv = new ModelAndView("people_page");

        // Following line populates sample data.
        // You should replace it with actual data from the database.
        // Use the PeopleService instance to find followable users.
        // Use UserService to access logged in userId to exclude.
        User loggedInUser = userService.getLoggedInUser();

        String userID = loggedInUser.getUserId();

        List<FollowableUser> followableUsers = peopleService.getFollowableUsers(userID);
        mv.addObject("users", followableUsers);

        // If an error occured, you can set the following property with the
        // error message to show the error message to the user.
        // An error message can be optionally specified with a url query parameter too.
        String errorMessage = error;
        mv.addObject("errorMessage", errorMessage);

        // Enable the following line if you want to show no content message.
        // Do that if your content list is empty.
        // mv.addObject("isNoContent", true);
        
        return mv;
    }

    /**
     * This function handles user follow and unfollow.
     * Note the URL has parameters defined as variables ie: {userId} and {isFollow}.
     * Follow and unfollow is handled by submitting a get type form to this URL 
     * by specifing the userId and the isFollow variables.
     * Learn more here: https://www.w3schools.com/tags/att_form_method.asp
     * An example URL that is handled by this function looks like below:
     * http://localhost:8081/people/1/follow/false
     * The above URL assigns 1 to userId and false to isFollow.
     */
    @GetMapping("{userId}/follow/{isFollow}")
    public String followUnfollowUser(
            @PathVariable("userId") String userId,
            @PathVariable("isFollow") Boolean isFollow) {
        System.out.println("User is attempting to follow/unfollow a user:");
        System.out.println("\tuserId: " + userId);
        System.out.println("\tisFollow: " + isFollow);

        try {
            User currentUser = userService.getLoggedInUser();
        

            if (currentUser.getUserId().equals(userId)) {
                return redirectWithError("You cannot follow yourself.");
            }

            boolean success = followService.followUnfollowUser(
                currentUser.getUserId(), 
                userId, 
                isFollow
            );

            if (success) {
                return "redirect:/people";
            }
        } catch (IllegalArgumentException e) {
            return redirectWithError(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error in followUnfollowUser: " + e.getMessage());
            e.printStackTrace();
        }

        return redirectWithError("Failed to " + (isFollow ? "follow" : "unfollow") + 
                " the user. Please try again.");
    }

    /**
     * Helper method to create a redirect URL with an encoded error message.
     *
     * @param message The error message to encode
     * @return The redirect URL string with encoded error message
     */
    private String redirectWithError(String message) {
        return "redirect:/people?error=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}
