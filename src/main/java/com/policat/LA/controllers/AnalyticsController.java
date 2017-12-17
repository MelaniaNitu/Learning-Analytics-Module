package com.policat.LA.controllers;

import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import com.policat.LA.repositories.UserRepository;
import com.policat.LA.session.AuthedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired
    UserRepository userRepository;

    @ModelAttribute("users")
    public List<User> getUsers() {return (List<User>) userRepository.findAll();}

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public String viewUsers(){
        return "users";
    }

    @RequestMapping(value = "descriptive", method = RequestMethod.GET)
    public String viewDescriptive(Model model){
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        List<QuizResult> quizResults = user.getQuizResults();
        quizResults.sort(Comparator.comparing(QuizResult::getDate));
        model.addAttribute(quizResults);
        return "descriptive";
    }

    @RequestMapping(value = "diagnostic", method = RequestMethod.GET)
    public String viewDiagnostic(){
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        List<QuizResult> quizResults = user.getQuizResults();
        quizResults.sort(Comparator.comparing(QuizResult::getScore));

        //am sortat rezultatele in functie de scor, tb afisat DOAR cel mai mic scor + toate de sub mediana

        return "diagnostic";
    }

    @RequestMapping(value = "predictive", method = RequestMethod.GET)
    public String viewPredictive(){
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        List<QuizResult> quizResults = user.getQuizResults();

        // 1. compar Nivel User (avg(Nota Initiala,Nota Finala)) cu nota test hartie
        //2. afisez toate notele sub mediana
        //3. Grafic cu trendul

        return "predictive";
    }

    @RequestMapping(value = "prescriptive", method = RequestMethod.GET)
    public String viewPrescriptive(){
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        List<QuizResult> quizResults = user.getQuizResults();

       //verific unde a avut cel mai mic scor(domeniu+label) => recomand tutorial in domeniu/label-ul respectiv

        return "prescriptive";
    }

    @RequestMapping(value = "summary", method = RequestMethod.GET)
    public String viewSummary(){
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        return "summary";
    }

    @RequestMapping(value = "summary/{id}", method = RequestMethod.GET)
    public String viewSummary(@PathVariable Long id){
        User user = userRepository.findOne(id);
        return "summary";
    }
}