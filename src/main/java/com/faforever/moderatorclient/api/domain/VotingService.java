package com.faforever.moderatorclient.api.domain;

import com.faforever.commons.api.dto.VotingChoice;
import com.faforever.commons.api.dto.VotingQuestion;
import com.faforever.commons.api.dto.VotingSubject;
import com.faforever.commons.api.elide.ElideNavigator;
import com.faforever.moderatorclient.api.FafApiCommunicationService;
import com.faforever.moderatorclient.mapstruct.VotingChoiceMapper;
import com.faforever.moderatorclient.mapstruct.VotingQuestionMapper;
import com.faforever.moderatorclient.mapstruct.VotingSubjectMapper;
import com.faforever.moderatorclient.ui.domain.VotingChoiceFX;
import com.faforever.moderatorclient.ui.domain.VotingQuestionFX;
import com.faforever.moderatorclient.ui.domain.VotingSubjectFX;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class VotingService {
    private final FafApiCommunicationService fafApi;
    private final VotingSubjectMapper subjectMapper;
    private final VotingQuestionMapper questionMapper;
    private final VotingChoiceMapper votingChoiceMapper;

    //region subjects
    public List<VotingSubject> getAllSubjectsFromApi() {
        log.debug("Retrieving all subjects");
        List<VotingSubject> result = fafApi.getAll(VotingSubject.class, ElideNavigator.of(VotingSubject.class).collection());
        log.trace("found {} subjects", result.size());
        return result;
    }


    public CompletableFuture<List<VotingSubjectFX>> getAllSubjects() {
        return CompletableFuture.supplyAsync(() -> subjectMapper.mapToFx(getAllSubjectsFromApi()));
    }

    public void updateSubject(VotingSubjectFX rowValue) {
        update(subjectMapper.map(rowValue));
    }

    public void deleteSubject(VotingSubjectFX subjectFX) {
        deleteSubject(subjectMapper.map(subjectFX));
    }

    public VotingSubject update(VotingSubject votingSubject) {
        //Both are immutable
        votingSubject.setBeginOfVoteTime(null);
        votingSubject.setEndOfVoteTime(null);
        log.debug("Patching subject of id: ", votingSubject.getId());
        return fafApi.patch(ElideNavigator.of(VotingSubject.class).id(votingSubject.getId()), votingSubject);
    }

    private void deleteSubject(VotingSubject votingSubject) {
        log.debug("Deleting subject: {}", votingSubject);
        fafApi.delete(ElideNavigator.of(VotingSubject.class).id(votingSubject.getId()));
    }

    public VotingSubject create(VotingSubjectFX votingSubjectFX) {
        return create(subjectMapper.map(votingSubjectFX));
    }

    public VotingSubject create(VotingSubject votingSubject) {
        log.debug("Adding subject: {}", votingSubject);
        return fafApi.post(ElideNavigator.of(VotingSubject.class).collection(), votingSubject);
    }
    //endregion

    //region questions
    public List<VotingQuestion> getAllQuestionsFromApi() {
        log.debug("Retrieving all questions");
        List<VotingQuestion> result = fafApi.getAll(VotingQuestion.class, ElideNavigator.of(VotingQuestion.class)
                .collection()
                .addInclude("winners")
                .addInclude("votingChoices")
                .addInclude("votingSubject"));
        log.trace("found {} questions", result.size());
        return result;
    }


    public CompletableFuture<List<VotingQuestionFX>> getAllQuestions() {
        return CompletableFuture.supplyAsync(() -> questionMapper.mapToFx(getAllQuestionsFromApi()));
    }

    public void updateQuestion(VotingQuestionFX rowValue) {
        update(questionMapper.map(rowValue));
    }

    public void deleteQuestion(VotingQuestionFX questionFX) {
        deleteQuestion(questionMapper.map(questionFX));
    }

    public VotingQuestion update(VotingQuestion votingQuestion) {
        log.debug("Patching Question of id: ", votingQuestion.getId());
        return fafApi.patch(ElideNavigator.of(VotingQuestion.class).id(votingQuestion.getId()), votingQuestion);
    }

    private void deleteQuestion(VotingQuestion votingQuestion) {
        log.debug("Deleting question: {}", votingQuestion);
        fafApi.delete(ElideNavigator.of(VotingQuestion.class).id(votingQuestion.getId()));
    }

    public VotingQuestion create(VotingQuestionFX votingQuestionFX) {
        return create(questionMapper.map(votingQuestionFX));
    }

    public VotingQuestion create(VotingQuestion votingQuestion) {
        log.debug("Adding question: {}", votingQuestion);
        return fafApi.post(ElideNavigator.of(VotingQuestion.class).collection(), votingQuestion);
    }
    //endregion

    //region choices
    public List<VotingChoice> getAllChoicesFromApi() {
        log.debug("Retrieving all choices");
        List<VotingChoice> result = fafApi.getAll(VotingChoice.class, ElideNavigator.of(VotingChoice.class)
                .collection()
                .addInclude("votingQuestion"));
        log.trace("found {} choices", result.size());
        return result;
    }


    public CompletableFuture<List<VotingChoiceFX>> getAllChoices() {
        return CompletableFuture.supplyAsync(() -> votingChoiceMapper.mapToFX(getAllChoicesFromApi()));
    }

    public void updateChoice(VotingChoiceFX rowValue) {
        update(votingChoiceMapper.map(rowValue));
    }

    public void deleteChoice(VotingChoiceFX choiceFX) {
        deleteChoice(votingChoiceMapper.map(choiceFX));
    }

    public VotingChoice update(VotingChoice votingChoice) {
        log.debug("Patching Choice of id: ", votingChoice.getId());
        return fafApi.patch(ElideNavigator.of(VotingChoice.class).id(votingChoice.getId()), votingChoice);
    }

    private void deleteChoice(VotingChoice votingChoice) {
        log.debug("Deleting choice: {}", votingChoice);
        fafApi.delete(ElideNavigator.of(VotingChoice.class).id(votingChoice.getId()));
    }

    public VotingChoice create(VotingChoiceFX votingChoiceFX) {
        return create(votingChoiceMapper.map(votingChoiceFX));
    }

    public VotingChoice create(VotingChoice votingChoice) {
        log.debug("Adding choice: {}", votingChoice);
        return fafApi.post(ElideNavigator.of(VotingChoice.class).collection(), votingChoice);
    }
    //endregion
}
