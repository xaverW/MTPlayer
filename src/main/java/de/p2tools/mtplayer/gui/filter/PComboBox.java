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

import java.util.ArrayList;

public class PComboBox extends ComboBox<FilmFilter> {

    FilmFilterList fList = new FilmFilterList();

    public PComboBox() {
        itemsProperty().bind(fList);

        addEventHandler(ComboBox.ON_SHOWING, event -> {
            System.out.println("Click on ");
            ProgData.getInstance().backwardFilterList.cleanBackForward();
            ProgData.getInstance().forwardFilterList.cleanBackForward();
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
            Button btnDel = new Button("");
            HBox hBox = new HBox();
            Label lblChannel = new Label();
            Label lblTheme = new Label();
            Label lblThemeExact = new Label();
            Label lblThemeTitle = new Label();
            Label lblTitle = new Label();

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
//                System.out.println("??? updateItem");
//                setVisibleRowCount(ProgData.getInstance().backwardFilterList.size());
                setVisibleRowCount(10);

                if (!empty && filmFilter != null) {
                    btnDel.setOnMousePressed(m -> {
                        System.out.println("DEL");
                        ProgData.getInstance().backwardFilterList.remove(filmFilter);
                    });

                    lblChannel.setVisible(!filmFilter.getChannel().isEmpty());
                    lblChannel.setManaged(lblChannel.isVisible());
                    lblChannel.setText(getSubString(filmFilter.getChannel()));

                    lblTheme.setVisible(!filmFilter.getTheme().isEmpty());
                    lblTheme.setManaged(lblTheme.isVisible());
                    lblTheme.setText(getSubString(filmFilter.getTheme()));

                    lblThemeExact.setVisible(!filmFilter.getExactTheme().isEmpty());
                    lblThemeExact.setManaged(lblThemeExact.isVisible());
                    lblThemeExact.setText(getSubString(filmFilter.getExactTheme()));
                    System.out.println("theme-title " + lblThemeTitle.getText());
                    System.out.println("theme-title " + filmFilter.getThemeTitle());

                    lblThemeTitle.setVisible(!filmFilter.getThemeTitle().isEmpty());
                    lblThemeTitle.setManaged(lblThemeTitle.isVisible());
                    lblThemeTitle.setText(getSubString(filmFilter.getThemeTitle()));
                    System.out.println("title " + lblTitle.getText());
                    System.out.println("title " + filmFilter.getTitle());

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
        if (s.length() > 25) {
            return s.substring(0, 25) + " ...";
        } else {
            return s;
        }
    }

//    private void init() {
////        ProgData.getInstance().backwardFilterList.addListener((u, o, n) -> {
////            System.out.println("liste");
////            addToList();
////        });
////        addToList();
//        itemsProperty().bind(fList);
//
////        itemsProperty().bind(ProgData.getInstance().backwardFilterList);
//    }

    private synchronized void addToList() {
        try {
            fList.clear();
            ArrayList<FilmFilter> filters = new ArrayList<>();
            ProgData.getInstance().backwardFilterList.forEach(f -> {
                fList.add(0, f);
            });
//            fList.setAll(filters);
        } catch (Exception ex) {
            System.out.println("================================");
            System.out.println(ex.getStackTrace());
        }
    }
}
