package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.TextFilter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class FilmFilterComboBox extends HBox {

    private ComboBox<TextFilter> cbo = new ComboBox<>();
    private final ObservableList<TextFilter> fList = FXCollections.observableArrayList();

    public FilmFilterComboBox() {
        cbo.setMaxWidth(Double.MAX_VALUE);
        cbo.setVisibleRowCount(10);
        getChildren().add(cbo);
        HBox.setHgrow(cbo, Priority.ALWAYS);

        PListener.addListener(new PListener(PListener.EVENT_FILTER_CHANGED, FilmFilterComboBox.class.getSimpleName()) {
            @Override
            public void ping() {
                addToList();
            }
        });

        // die gespeicherten Filter eintragen
        ProgData.getInstance().filmFilterWorker.getForwardFilterList().forEach(this::addToList);
        ProgData.getInstance().filmFilterWorker.getBackwardFilterList().forEach(this::addToList);
        cbo.setItems(fList);

        cbo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TextFilter person) {
                return "";
            }

            @Override
            public TextFilter fromString(String string) {
                return null;
            }
        });

        cbo.setCellFactory(cell -> new ListCell<>() {
            final Button btnDel = new Button("");
            final HBox hBox = new HBox();
            final Label lblChannel = new Label();
            final Label lblTheme = new Label();
            final Label lblThemeTitle = new Label();
            final Label lblTitle = new Label();
            final Label lblSomewhere = new Label();

            {
                btnDel.setGraphic(ProgIcons.ICON_BUTTON_FILMFILTER_DEL.getImageView());
                btnDel.getStyleClass().add("buttonVerySmall");
                hBox.setPadding(new Insets(0));
                hBox.setSpacing(5);
                hBox.getChildren().addAll(btnDel, lblChannel, lblTheme, lblThemeTitle, lblTitle, lblSomewhere);
                lblChannel.getStyleClass().add("lblFilmFilter");
                lblTheme.getStyleClass().add("lblFilmFilter");
                lblThemeTitle.getStyleClass().add("lblFilmFilter");
                lblTitle.getStyleClass().add("lblFilmFilter");
                lblSomewhere.getStyleClass().add("lblFilmFilter");
            }


            @Override
            protected void updateItem(TextFilter filmFilter, boolean empty) {
                super.updateItem(filmFilter, empty);
                cbo.setVisibleRowCount(8);
                cbo.setVisibleRowCount(10);

                if (!empty && filmFilter != null) {
                    btnDel.setOnMousePressed(m -> fList.remove(filmFilter));

                    if (!filmFilter.filterIsEmpty()) {
                        lblChannel.setText(getSubString(filmFilter.getChannel()));
                        lblTheme.setText(getSubString(filmFilter.getTheme()));
                        lblThemeTitle.setText(getSubString(filmFilter.getThemeTitle()));
                        lblTitle.setText(getSubString(filmFilter.getTitle()));
                        lblSomewhere.setText(getSubString(filmFilter.getSomewhere()));

                    } else {
                        // darf eigentlich nicht vorkommen
                        lblChannel.setText("<==>");
                    }

                    setGraphic(hBox);
                } else {
                    // Nothing to display here
                    setGraphic(null);
                }
            }
        });
    }

    public ComboBox<TextFilter> getCbo() {
        return cbo;
    }

    private String getSubString(String s) {
        if (s.length() > 20) {
            return s.substring(0, 20) + " ...";
        } else {
            return s;
        }
    }

    private synchronized void addToList() {
        addToList(ProgData.getInstance().filmFilterWorker.getActFilterSettings());
    }

    private synchronized void addToList(FilmFilter addF) {
        // einen neuen Filter einfügen
        TextFilter addFilter = new TextFilter(addF);
        if (addFilter.filterIsEmpty()) {
            // dann sind die eingeschalteten Textfilter leer, ist nix
            return;
        }

        TextFilter cboF = cbo.getSelectionModel().getSelectedItem();
        if (cboF != null) {
            if (addFilter.filterIsSame(cboF)) {
                // dann ist der gleiche schon vorne
                return;
            }
        }

        if (fList.stream().filter(addFilter::filterIsSame).findFirst().isEmpty()) {
            // dann ist er noch nicht drin
            cbo.getSelectionModel().clearSelection();
            while (fList.size() > 15) {
                fList.remove(fList.size() - 1);
            }
            fList.add(0, addFilter);
        }
    }
}
