package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.moderatorclient.ui.ViewHelper;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * Originally created by eckig: https://gist.github.com/eckig/30abf0d7d51b7756c2e7
 * <p>
 * Creates a TableCell that contains a TextArea similar to a TextFieldTableCell.
 *
 * @param <S>
 * @param <T>
 */
@SuppressWarnings("WeakerAccess")
public class TextAreaTableCell<S, T> extends TableCell<S, T> {

    private Integer inputLimit;
    private TextArea textArea;
    private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");

    public TextAreaTableCell() {
        this(null, null);
    }

    public TextAreaTableCell(StringConverter<T> converter, Integer inputLimit) {
        this.getStyleClass().add("text-area-table-cell");
        setConverter(converter);
        this.inputLimit = inputLimit;
    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
        return forTableColumn(new DefaultStringConverter(), null);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
        return forTableColumn(converter, null);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter, Integer inputLimit) {
        return list -> new TextAreaTableCell<>(converter, inputLimit);
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter) {
        return converter == null ? cell.getItem() == null ? "" : cell.getItem()
                .toString() : converter.toString(cell.getItem());
    }

    private static <T> TextArea createTextArea(final Cell<T> cell, final StringConverter<T> converter, Integer inputLimit) {
        TextArea textArea = new TextArea(getItemText(cell, converter));
        textArea.setWrapText(true);
        textArea.editableProperty().bind(cell.editableProperty());

        textArea.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cell.cancelEdit();
                t.consume();
            } else if (t.getCode() == KeyCode.ENTER && t.isShiftDown()) {
                t.consume();
                textArea.insertText(textArea.getCaretPosition(), "\n");
            } else if (t.getCode() == KeyCode.ENTER) {
                if (converter == null) {
                    throw new IllegalStateException(
                            "Attempting to convert text input into Object, but provided "
                                    + "StringConverter is null. Be sure to set a StringConverter "
                                    + "in your cell factory.");
                }
                if (inputLimit != null && textArea.getText().length() > inputLimit) {
                    ViewHelper.errorDialog("This text box has an input limit", String.format("You can not enter more than '%d' characters here.", inputLimit));
                    return;
                }
                cell.commitEdit(converter.fromString(textArea.getText()));
                t.consume();
            }
        });
        textArea.prefRowCountProperty().bind(Bindings.size(textArea.getParagraphs()));
        return textArea;
    }

    private static <T> void cancelEdit(Cell<T> cell, final StringConverter<T> converter) {
        Platform.runLater(() -> {
            cell.setText(getItemText(cell, converter));
            cell.setGraphic(null);
        });
    }

    private void startEdit(final Cell<T> cell, final StringConverter<T> converter) {
        textArea.setText(getItemText(cell, converter));

        cell.setText(null);
        cell.setGraphic(textArea);

        textArea.selectAll();
        textArea.requestFocus();
    }

    private void updateItem(final Cell<T> cell, final StringConverter<T> converter) {

        if (cell.isEmpty()) {
            cell.setText(null);
            cell.setGraphic(null);
            cell.setTooltip(null);

        } else {
            if (cell.isEditing()) {
                if (textArea != null) {
                    textArea.setText(getItemText(cell, converter));
                }
                cell.setText(null);
                cell.setGraphic(textArea);
                cell.setTooltip(null);
            } else {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(null);

                //Add text as tooltip so that player can read text without editing it.
                Tooltip tooltip = new Tooltip(getItemText(cell, converter));
                tooltip.setWrapText(true);
                tooltip.prefWidthProperty().bind(cell.widthProperty());
                cell.setTooltip(tooltip);
            }
        }
    }

    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }

    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
        }

        super.startEdit();

        if (isEditing()) {
            if (textArea == null) {
                textArea = createTextArea(this, getConverter(), inputLimit);
            }

            startEdit(this, getConverter());
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        cancelEdit(this, getConverter());
    }

    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        updateItem(this, getConverter());
    }

}