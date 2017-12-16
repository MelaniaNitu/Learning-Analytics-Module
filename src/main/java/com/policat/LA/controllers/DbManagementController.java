package com.policat.LA.controllers;

import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import com.policat.LA.repositories.DomainRepository;
import com.policat.LA.repositories.QuizResultRepository;
import com.policat.LA.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/managedb")
public class DbManagementController{
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuizResultRepository quizResultRepository;

    @Autowired
    DomainRepository domainRepository;

    @ModelAttribute("users")
    public List<User> getUsers() {return (List<User>) userRepository.findAll();}

    @ModelAttribute("results")
    public QuizResult newResult(){return new QuizResult();}

    @RequestMapping(method = RequestMethod.GET)
    public String viewUsers(){
        return "manage_db_users";
    }

    @RequestMapping(value="/user/rename", method = RequestMethod.POST)
    public String saveChangedUsers(@Valid User user, BindingResult bindingResult){
        if(!bindingResult.hasErrors()){
            userRepository.save(user);
        }
        return "redirect:/managedb";
    }

/*
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String viewUserResults(@PathVariable Long id, Model model){
        User user = userRepository.findOne(id);
        List<>
    }
*/

}