package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public class PComboBox extends ComboBox<FilmFilter> {

    FilmFilterList fList = new FilmFilterList();

    public PComboBox() {
        itemsProperty().bind(fList);

        addEventHandler(ComboBox.ON_SHOWING, event -> {
            ProgData.getInstance().filmFilterWorker.getBackwardFilterList().cleanBackForward();
            ProgData.getInstance().filmFilterWorker.getForwardFilterList().cleanBackForward();
            addToList();
        });

        setConverter(new StringConverter<>() {
            @Override
            public String toString(FilmFilter person) {
                return "";
            }

            @Override
            public FilmFilter fromString(String string) {
                return null;
            }
        });

        setCellFactory(cell -> new ListCell<>() {
            final Button btnDel = new Button("");
            final HBox hBox = new HBox();
            final Label lblChannel = new Label();
            final Label lblTheme = new Label();
            final Label lblThemeExact = new Label();
            final Label lblThemeTitle = new Label();
            final Label lblTitle = new Label();

            {
                btnDel.setGraphic(ProgIcons.ICON_BUTTON_FILMFILTER_DEL.getImageView());
                btnDel.getStyleClass().add("buttonVerySmall");
                hBox.setPadding(new Insets(0));
                hBox.setSpacing(5);
                hBox.getChildren().addAll(btnDel, lblChannel, lblTheme, lblThemeExact, lblThemeTitle, lblTitle);
                lblChannel.getStyleClass().add("lblFilmFilter");
                lblTheme.getStyleClass().add("lblFilmFilter");
                lblThemeExact.getStyleClass().add("lblFilmFilter");
                lblThemeTitle.getStyleClass().add("lblFilmFilter");
                lblTitle.getStyleClass().add("lblFilmFilter");
            }


            @Override
            protected void updateItem(FilmFilter filmFilter, boolean empty) {
                super.updateItem(filmFilter, empty);
                setVisibleRowCount(10);

                if (!empty && filmFilter != null) {
                    btnDel.setOnMousePressed(m -> ProgData.getInstance().filmFilterWorker.getBackwardFilterList().remove(filmFilter));

                    lblChannel.setVisible(!filmFilter.getChannel().isEmpty());
                    lblChannel.setManaged(lblChannel.isVisible());
                    lblChannel.setText(getSubString(filmFilter.getChannel()));

                    lblTheme.setVisible(!filmFilter.getTheme().isEmpty());
                    lblTheme.setManaged(lblTheme.isVisible());
                    lblTheme.setText(getSubString(filmFilter.getTheme()));

                    lblThemeExact.setVisible(!filmFilter.getExactTheme().isEmpty());
                    lblThemeExact.setManaged(lblThemeExact.isVisible());
                    lblThemeExact.setText(getSubString(filmFilter.getExactTheme()));

                    lblThemeTitle.setVisible(!filmFilter.getThemeTitle().isEmpty());
                    lblThemeTitle.setManaged(lblThemeTitle.isVisible());
                    lblThemeTitle.setText(getSubString(filmFilter.getThemeTitle()));

                    lblTitle.setVisible(!filmFilter.getTitle().isEmpty());
                    lblTitle.setManaged(lblTitle.isVisible());
                    lblTitle.setText(getSubString(filmFilter.getTitle()));

                    if (filmFilter.getChannel().isEmpty() &&
                            filmFilter.getTheme().isEmpty() &&
                            filmFilter.getExactTheme().isEmpty() &&
                            filmFilter.getThemeTitle().isEmpty() &&
                            filmFilter.getTitle().isEmpty()) {

                        lblChannel.setVisible(true);
                        lblChannel.setManaged(lblChannel.isVisible());
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

    private String getSubString(String s) {
        if (s.length() > 20) {
            return s.substring(0, 20) + " ...";
        } else {
            return s;
        }
    }

    private synchronized void addToList() {
        fList.clear();
        ProgData.getInstance().filmFilterWorker.getBackwardFilterList().forEach(f -> {
            fList.add(0, f);
        });
    }
}
