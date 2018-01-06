package com.policat.LA.controllers;

import com.policat.LA.dtos.DataPointBarDTO;
import com.policat.LA.dtos.DataPointLineDTO;
import com.policat.LA.entities.QuestionResponse;
import com.policat.LA.entities.QuestionTag;
import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import com.policat.LA.repositories.QuestionResponseRepository;
import com.policat.LA.repositories.QuizResultRepository;
import com.policat.LA.repositories.UserRepository;
import com.policat.LA.services.PdfGeneratorService;
import com.policat.LA.session.AuthedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    QuizResultRepository quizResultRepository;
    @Autowired
    QuestionResponseRepository questionResponseRepository;
    @Autowired
    PdfGeneratorService pdfGeneratorService;


    @ModelAttribute("users")
    public List<User> getUsers() {
        return (List<User>) userRepository.findAll();
    }

    @ModelAttribute("user")
    public User getCurrentUser() {
        AuthedUser auth = (AuthedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = auth.getUser();
        return user;
    }

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public String viewUsers(){
        return "users";
    }


    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String saveScore(@Valid User userScore, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            User dbUser = userRepository.findOne(userScore.getId());
            dbUser.setScoreTp(userScore.getScoreTp());
            dbUser.setScorePaper(userScore.getScorePaper());
            userRepository.save(dbUser);
        }
        return "users";
    }

    @RequestMapping(value = "/users/{id}/delete", method = RequestMethod.GET)
    public String deleteUser(@PathVariable Long id) {
        userRepository.delete(id);
        return "redirect:/analytics/users";
    }


    @RequestMapping(value = "descriptive", method = RequestMethod.GET)
    public String viewDescriptive(Model model){
        calcDescriptive(model, getCurrentUser());
        return "descriptive";
    }

    private void calcDescriptive(Model model, User user){
        List<QuizResult> quizResults = quizResultRepository.findByUser(user);
        quizResults.sort(Comparator.comparing(QuizResult::getDate).reversed());

        double average = quizResults.stream().mapToInt(q -> q.getScore()).average().getAsDouble();

        Supplier<Stream<QuizResult>> globalQuizResultStream = () -> StreamSupport.stream(quizResultRepository.findAll().spliterator(), false);
        int globalMax = globalQuizResultStream.get().mapToInt(q -> q.getScore()).max().getAsInt();
        int globalMin = globalQuizResultStream.get().mapToInt(q -> q.getScore()).min().getAsInt();
        double globalMedian = (globalMax + globalMin) / (double) 2;

        String userLevelComparison;
        if(average > globalMedian) {
            userLevelComparison = "Above average";
        } else if (average < globalMedian) {
            userLevelComparison = "Under average";
        } else {
            userLevelComparison = "Average";
        }

        model.addAttribute("quizResults", quizResults);
        model.addAttribute("userLevel", average);
        model.addAttribute("userLevelComparison", userLevelComparison);
    }

    @RequestMapping(value = "diagnostic", method = RequestMethod.GET)
    public String viewDiagnostic(Model model){
       calcDiagnostic(model, getCurrentUser());
       return "diagnostic";
    }

    private void calcDiagnostic(Model model, User user){
        List<QuizResult> quizResults = quizResultRepository.findByUser(user);

        int maxScore = quizResults.stream().mapToInt(q -> q.getScore()).max().orElse(100);
        int minScore = quizResults.stream().mapToInt(q -> q.getScore()).min().orElse(0);
        double median = (maxScore + minScore) / (double) 2;

        List<QuizResult> underMedian = quizResults.stream().filter(q -> q.getScore() < median).collect(Collectors.toList());

        model.addAttribute("minScore", minScore);
        model.addAttribute("underMedian", underMedian);
    }

    @RequestMapping(value = "predictive", method = RequestMethod.GET)
    public String viewPredictive(Model model){
        calcPredictive(model, getCurrentUser());
        return "predictive";
    }


    private void calcPredictive(Model model, User user){
        List<QuizResult> quizResults = quizResultRepository.findByUser(user);
        quizResults.sort(Comparator.comparing(QuizResult::getDate));

        AtomicInteger xPos = new AtomicInteger(1);
        List<DataPointLineDTO> graphDataPoints = quizResults.stream().map(q -> new DataPointLineDTO(xPos.getAndIncrement(), q.getScore())).collect(Collectors.toList());
        int nrScores = graphDataPoints.size();

        //trendline formula: http://classroom.synonym.com/calculate-trendline-2709.html
        //slope
        double a = nrScores * graphDataPoints.stream().mapToDouble(dp -> dp.getX() * dp.getY()).sum();
        double b = graphDataPoints.stream().mapToDouble(dp -> dp.getX()).sum() * graphDataPoints.stream().mapToDouble(dp -> dp.getY()).sum();
        double c = nrScores * graphDataPoints.stream().mapToDouble(dp -> Math.pow(dp.getX(), 2)).sum();
        double d = Math.pow(graphDataPoints.stream().mapToDouble(dp -> dp.getX()).sum(), 2);
        double slope = (a - b) / (c - d);

        //y-intercept
        double e = graphDataPoints.stream().mapToDouble(dp -> dp.getY()).sum();
        double f = slope * graphDataPoints.stream().mapToDouble(dp -> dp.getX()).sum();
        double yIntercept = (e - f) / (double) nrScores;

        //set dash line for the last real point (since they affect the next value)
        graphDataPoints.get(graphDataPoints.size() - 1).setLineDashType("dash");

        //predict next values using trendline
        List<DataPointLineDTO> predictedDataPoints = IntStream.range(1, xPos.get() + 3).mapToObj(x -> new DataPointLineDTO(x, Math.round(slope * x + yIntercept), "dash")).collect(Collectors.toList());
        //graphDataPoints.addAll(predictedDataPoints);

        model.addAttribute("graphDataPoints", graphDataPoints);
        model.addAttribute("predictedDataPoints", predictedDataPoints);
    }

    @RequestMapping(value = "prescriptive", method = RequestMethod.GET)
    public String viewPrescriptive(Model model){
        calcPrescriptive(model, getCurrentUser());
        return "prescriptive";
    }

    private void calcPrescriptive(Model model, User user){
        List<QuestionResponse> questionResponses = questionResponseRepository.findByUser(user);

        Map<QuestionTag, Integer> tagScores = questionResponses.stream().collect(Collectors.groupingBy(qr -> qr.getQuestion().getQuestionTag(), Collectors.summingInt(qr -> qr.isCorrect() ? 1 : 0)));
        QuestionTag weakestTag = Collections.min(tagScores.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();

        model.addAttribute("weakestTag", weakestTag);
    }

    @RequestMapping(value = "summary", method = RequestMethod.GET)
    public String viewSummary(Model model){
        User currentUser = getCurrentUser();
        return viewSummary(currentUser.getId(), model);
    }

    @RequestMapping(value = "summary/{id}", method = RequestMethod.GET)
    public String viewSummary(@PathVariable Long id, Model model){
        User user = userRepository.findOne(id);
        calcDescriptive(model, user);
        calcDiagnostic(model, user);
        calcPredictive(model, user);
        calcPrescriptive(model, user);
        return "summary";
    }

    @RequestMapping(value = "summary/{id}/export", method = RequestMethod.GET)
    public void exportSummary(@PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response){
        User user = userRepository.findOne(id);

        calcDescriptive(model, user);
        calcDiagnostic(model, user);
        calcPredictive(model, user);
        calcPrescriptive(model, user);

        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss") ;

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\""+user.getUsername()+"_"+dateFormat.format(date)+".pdf\"");
        try {
            pdfGeneratorService.createPdf("summary",model.asMap(), request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "learnogram", method = RequestMethod.GET)
    public String viewLernogram(Model model){
        List<DataPointBarDTO> tpDataPoints = new ArrayList<>();
        List<DataPointBarDTO> paperDataPoints = new ArrayList<>();
        List<DataPointBarDTO> catDataPoints = new ArrayList<>();

        List<User> users = getUsers();
        for(User user : users) {
            BigDecimal scoreTp = user.getScoreTp();
            BigDecimal scorePaper = user.getScorePaper();
            tpDataPoints.add(new DataPointBarDTO(user.getUsername(), scoreTp != null ? scoreTp : new BigDecimal(0)));
            paperDataPoints.add(new DataPointBarDTO(user.getUsername(), scorePaper != null ? scorePaper : new BigDecimal(0)));

            List<QuizResult> quizResults = quizResultRepository.findByUser(user);
            double userCatAverage = quizResults.stream().mapToInt(q -> q.getScore()).average().orElse(0);
            catDataPoints.add(new DataPointBarDTO(user.getUsername(), new BigDecimal(userCatAverage).divide(new BigDecimal(10))));
        }

        model.addAttribute("tpDataPoints", tpDataPoints);
        model.addAttribute("paperDataPoints", paperDataPoints);
        model.addAttribute("catDataPoints", catDataPoints);
        return "learnogram";
    }
}