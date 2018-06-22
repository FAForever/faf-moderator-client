package com.faforever.moderatorclient.ui.main_window;

import com.faforever.moderatorclient.api.domain.VotingService;
import com.faforever.moderatorclient.mapstruct.VotingChoiceFX;
import com.faforever.moderatorclient.mapstruct.VotingQuestionFX;
import com.faforever.moderatorclient.mapstruct.VotingSubjectFX;
import com.faforever.moderatorclient.ui.UiService;
import com.faforever.moderatorclient.ui.ViewHelper;
import com.faforever.moderatorclient.ui.voting.VotingChoiceAddController;
import com.faforever.moderatorclient.ui.voting.VotingQuestionAddController;
import com.faforever.moderatorclient.ui.voting.VotingSubjectAddController;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Component
public class VotingController {
    private final VotingService votingService;
    private final SortedList<VotingSubjectFX> sortedSubjects;
    private final ObservableList<VotingSubjectFX> rawSubjects;
    private final FilteredList<VotingQuestionFX> filteredQuestions;
    private final SortedList<VotingQuestionFX> sortedQuestions;
    private final ObservableList<VotingQuestionFX> rawQuestions;
    private final SortedList<VotingChoiceFX> sortedChoices;
    private final FilteredList<VotingChoiceFX> filteredChoices;
    private final ObservableList<VotingChoiceFX> rawChoices;
    private final UiService uiService;
    public TableView<VotingSubjectFX> subjectTable;
    public TableView<VotingQuestionFX> questionTable;
    public TableView<VotingChoiceFX> choiceTable;
    public Button revealResultsButton;
    public Button deleteSubjectButton;
    public Button deleteQuestionButton;
    public Button addQuestionButton;
    public Button deleteChoiceButton;
    public Button addChoiceButton;

    public VotingController(VotingService votingService, UiService uiService) {
        this.votingService = votingService;
        this.uiService = uiService;

        rawSubjects = FXCollections.observableArrayList();
        sortedSubjects = new SortedList<>(rawSubjects);

        rawQuestions = FXCollections.observableArrayList();
        sortedQuestions = new SortedList<>(rawQuestions);
        filteredQuestions = new FilteredList<>(sortedQuestions);
        filteredQuestions.setPredicate(votingQuestionFX -> false);

        rawChoices = FXCollections.observableArrayList();
        sortedChoices = new SortedList<>(rawChoices);
        filteredChoices = new FilteredList<>(sortedChoices);
        filteredChoices.setPredicate(votingChoiceFX -> false);
    }

    @FXML
    public void initialize() {
        //Subjects
        sortedSubjects.comparatorProperty().bind(subjectTable.comparatorProperty());
        ViewHelper.buildSubjectTable(subjectTable, votingService, log, this::onRefreshSubjects);
        subjectTable.setItems(sortedSubjects);
        subjectTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                filteredQuestions.setPredicate(votingQuestionFX -> Objects.equals(votingQuestionFX.getVotingSubject(), newValue)));

        revealResultsButton.disableProperty()
                .bind(Bindings.createBooleanBinding(() -> {
                    VotingSubjectFX selectedItem = subjectTable.getSelectionModel().getSelectedItem();
                    return !(selectedItem != null && selectedItem.getEndOfVoteTime().isBefore(OffsetDateTime.now()) && !selectedItem.getRevealWinner());
                }, subjectTable.getSelectionModel().selectedItemProperty()));
        onRefreshSubjects();
        deleteSubjectButton.disableProperty().bind(subjectTable.getSelectionModel().selectedItemProperty().isNull());

        //Questions
        questionTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                filteredChoices.setPredicate(votingChoiceFX -> Objects.equals(votingChoiceFX.getVotingQuestion(), newValue)));
        addQuestionButton.disableProperty().bind(subjectTable.getSelectionModel().selectedItemProperty().isNull());
        deleteQuestionButton.disableProperty().bind(questionTable.getSelectionModel().selectedItemProperty().isNull());
        sortedQuestions.comparatorProperty().bind(questionTable.comparatorProperty());
        ViewHelper.buildQuestionTable(questionTable, votingService, log, this::onRefreshQuestions);
        questionTable.setItems(filteredQuestions);
        onRefreshQuestions();

        //Choices
        addChoiceButton.disableProperty().bind(questionTable.getSelectionModel().selectedItemProperty().isNull());
        deleteChoiceButton.disableProperty().bind(choiceTable.getSelectionModel().selectedItemProperty().isNull());
        sortedChoices.comparatorProperty().bind(choiceTable.comparatorProperty());
        ViewHelper.buildChoiceTable(choiceTable, votingService, log, this::onRefreshChoices);
        choiceTable.setItems(filteredChoices);
        onRefreshChoices();
    }

    //region subjects
    public void onRefreshSubjects() {
        votingService.getAllSubjects().thenAccept(rawSubjects::setAll)
                .exceptionally(throwable -> {
                    log.error("Error loading subjects", throwable);
                    return null;
                });
    }

    public void deleteSubject() {
        ObservableList<VotingSubjectFX> selectedItems = subjectTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        selectedItems.forEach(votingService::deleteSubject);
        onRefreshSubjects();
    }

    public void addSubject() {
        VotingSubjectAddController votingSubjectAddController = uiService.loadFxml("ui/voting/voting_subject_add.fxml");
        votingSubjectAddController.setOnSave(this::onRefreshSubjects);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Add new subject");
        newCategoryDialog.setScene(new Scene(votingSubjectAddController.getRoot()));
        newCategoryDialog.showAndWait();
    }

    public void revealWinner() {
        VotingSubjectFX selectedItem = subjectTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        selectedItem.setRevealWinner(true);
        votingService.updateSubject(selectedItem);
        onRefreshSubjects();
        onRefreshChoices();
        onRefreshQuestions();
    }
    //endregion

    //region questions
    public void onRefreshQuestions() {
        votingService.getAllQuestions().thenAccept(rawQuestions::setAll)
                .exceptionally(throwable -> {
                    log.error("Error loading questions", throwable);
                    return null;
                });
    }

    public void deleteQuestion() {
        ObservableList<VotingQuestionFX> selectedItems = questionTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        selectedItems.forEach(votingService::deleteQuestion);
        onRefreshQuestions();
    }

    public void addQuestion() {
        VotingQuestionAddController votingQuestionAddController = uiService.loadFxml("ui/voting/voting_question_add.fxml");
        VotingSubjectFX selectedItem = subjectTable.getSelectionModel().getSelectedItem();
        String id = selectedItem.getId();
        votingQuestionAddController.setVotingSubjectId(id);
        votingQuestionAddController.setOnSave(this::onRefreshQuestions);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Add new question");
        newCategoryDialog.setScene(new Scene(votingQuestionAddController.getRoot()));
        newCategoryDialog.showAndWait();
    }
    //endregion

    //region choices
    public void onRefreshChoices() {
        votingService.getAllChoices().thenAccept(rawChoices::setAll)
                .exceptionally(throwable -> {
                    log.error("Error loading choices", throwable);
                    return null;
                });
    }

    public void deleteChoice() {
        ObservableList<VotingChoiceFX> selectedItems = choiceTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty()) {
            return;
        }
        selectedItems.forEach(votingService::deleteChoice);
        onRefreshChoices();
    }

    public void addChoice() {
        VotingChoiceAddController votingChoiceAddController = uiService.loadFxml("ui/voting/voting_choice_add.fxml");
        votingChoiceAddController.setOnSave(this::onRefreshChoices);
        VotingQuestionFX selectedItem = questionTable.getSelectionModel().getSelectedItem();
        String id = selectedItem.getId();
        votingChoiceAddController.setVotingQuestionId(id);
        Stage newCategoryDialog = new Stage();
        newCategoryDialog.setTitle("Add new choice");
        newCategoryDialog.setScene(new Scene(votingChoiceAddController.getRoot()));
        newCategoryDialog.showAndWait();
    }
    //endregion
}
