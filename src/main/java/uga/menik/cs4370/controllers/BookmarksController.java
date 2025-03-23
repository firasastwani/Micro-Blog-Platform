/**
Copyright (c) 2024 Sami Menik, PhD. All rights reserved.

This is a project developed by Dr. Menik to give the students an opportunity to apply database concepts learned in the class in a real world project. Permission is granted to host a running version of this software and to use images or videos of this work solely for the purpose of demonstrating the work to potential employers. Any form of reproduction, distribution, or transmission of the software's source code, in part or whole, without the prior written consent of the copyright owner, is strictly prohibited.
*/
package uga.menik.cs4370.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import uga.menik.cs4370.models.Post;
import uga.menik.cs4370.services.PostService;
import uga.menik.cs4370.services.UserService;

/**
 * Handles /bookmarks and its sub URLs.
 * No other URLs at this point.
 * 
 * Learn more about @Controller here: 
 * https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
 */
@Controller
@RequestMapping("/bookmarks")
public class BookmarksController {

    private final PostService postService;
    private final UserService userService;

    @Autowired
    public BookmarksController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * /bookmarks URL itself is handled by this.
     */
    @GetMapping
    public ModelAndView webpage() {
        // posts_page is a mustache template from src/main/resources/templates.
        // ModelAndView class enables initializing one and populating placeholders
        // in the template using Java objects assigned to named properties.
        ModelAndView mv = new ModelAndView("posts_page");

        try {
            // Get current user's ID
            String currentUserId = userService.getLoggedInUser().getUserId();
            
            // Get bookmarked posts for current user
            List<Post> posts = postService.getBookmarkedPosts(currentUserId);
            
            mv.addObject("posts", posts);
            
            if (posts.isEmpty()) {
                mv.addObject("isNoContent", true);
            }
            
        } catch (Exception e) {
            System.err.println("Error in bookmarks controller: " + e.getMessage());
            e.printStackTrace();
            mv.addObject("errorMessage", "Failed to load bookmarks. Please try again.");
        }

        return mv;
    }
    
}
