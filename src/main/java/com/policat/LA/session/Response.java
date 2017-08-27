package com.policat.LA.session;

import com.policat.LA.entities.Option;
import com.policat.LA.entities.Question;

import java.util.HashSet;
import java.util.Set;

public class Response {
    private Question question;
    private Set<Option> selectedOptions = new HashSet<>();


    public Response(Question question, Set<Option> selectedOptions) {
        this.question = question;
        this.selectedOptions = selectedOptions;
    }

    public Question getQuestion() {
        return question;
    }

    public Set<Option> getSelectedOptions() {
        return selectedOptions;
    }

    public Boolean isCorrect() {
        Set<Option> correctOptions = this.question.getCorrectOptions();
        Set<Option> userOptions = this.selectedOptions;
        return correctOptions.equals(userOptions);
    }
}
