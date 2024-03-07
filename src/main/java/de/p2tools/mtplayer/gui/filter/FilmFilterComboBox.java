package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.PListener;
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
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class FilmFilterComboBox extends HBox {

    private ComboBox<FilmFilter> cbo = new ComboBox<>();
    private final FilmFilterList fList = new FilmFilterList();

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
        ProgData.getInstance().filmFilterWorker.getBackwardFilterList().forEach(f -> fList.add(0, f));
        cbo.itemsProperty().bind(fList);

        cbo.addEventHandler(ComboBox.ON_SHOWING, event -> {
            ProgData.getInstance().filmFilterWorker.getBackwardFilterList().cleanBackForward();
            ProgData.getInstance().filmFilterWorker.getForwardFilterList().cleanBackForward();
        });

        cbo.setConverter(new StringConverter<>() {
            @Override
            public String toString(FilmFilter person) {
                return "";
            }

            @Override
            public FilmFilter fromString(String string) {
                return null;
            }
        });

        cbo.setCellFactory(cell -> new ListCell<>() {
            final Button btnDel = new Button("");
            final HBox hBox = new HBox();
            final Label lblChannel = new Label();
            final Label lblTheme = new Label();
            final Label lblThemeExact = new Label();
            final Label lblThemeTitle = new Label();
            final Label lblTitle = new Label();
            final Label lblSomewhere = new Label();

            {
                btnDel.setGraphic(ProgIcons.ICON_BUTTON_FILMFILTER_DEL.getImageView());
                btnDel.getStyleClass().add("buttonVerySmall");
                hBox.setPadding(new Insets(0));
                hBox.setSpacing(5);
                hBox.getChildren().addAll(btnDel, lblChannel, lblTheme, lblThemeExact, lblThemeTitle, lblTitle, lblSomewhere);
                lblChannel.getStyleClass().add("lblFilmFilter");
                lblTheme.getStyleClass().add("lblFilmFilter");
                lblThemeExact.getStyleClass().add("lblFilmFilter");
                lblThemeTitle.getStyleClass().add("lblFilmFilter");
                lblTitle.getStyleClass().add("lblFilmFilter");
                lblSomewhere.getStyleClass().add("lblFilmFilter");
            }


            @Override
            protected void updateItem(FilmFilter filmFilter, boolean empty) {
                super.updateItem(filmFilter, empty);
                cbo.setVisibleRowCount(8);
                cbo.setVisibleRowCount(10);

                if (!empty && filmFilter != null) {
                    btnDel.setOnMousePressed(m -> fList.remove(filmFilter));

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

                    lblSomewhere.setVisible(!filmFilter.getSomewhere().isEmpty());
                    lblSomewhere.setManaged(lblSomewhere.isVisible());
                    lblSomewhere.setText(getSubString(filmFilter.getSomewhere()));

                    if (filmFilter.getChannel().isEmpty() &&
                            filmFilter.getTheme().isEmpty() &&
                            filmFilter.getExactTheme().isEmpty() &&
                            filmFilter.getThemeTitle().isEmpty() &&
                            filmFilter.getTitle().isEmpty() &&
                            filmFilter.getSomewhere().isEmpty()) {

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

    public ComboBox<FilmFilter> getCbo() {
        return cbo;
    }

    private String getSubString(String s) {
        if (s.length() > 20) {
            return s.substring(0, 20) + " ...";
        } else {
            return s;
        }
    }

//    private synchronized void addToList__() {
//        final FilmFilterList fList = new FilmFilterList();
//        FilmFilterList list = ProgData.getInstance().filmFilterWorker.getBackwardFilterList();
//        if (list.size() > 1) {
//            // der letzte Filter ist der aktuelle
//            for (int i = 0; i < list.size() - 1; ++i) {
//                fList.add(0, list.get(i));
//            }
//        }
//        cbo.getItems().setAll(fList);
//    }

    private synchronized void addToList() {
        final FilmFilter actFilter = ProgData.getInstance().filmFilterWorker.getActFilterSettings().getCopy();
        if (actFilter.getChannel().isEmpty() &&
                actFilter.getTheme().isEmpty() &&
                actFilter.getThemeTitle().isEmpty() &&
                actFilter.getTitle().isEmpty() &&
                actFilter.getSomewhere().isEmpty()) {
            // dann sind die Textfilter leer, ist nix
            return;
        }

        FilmFilter cboF = getCbo().getSelectionModel().getSelectedItem();
        if (cboF != null) {
            if (actFilter.getChannel().equals(cboF.getChannel()) &&
                    actFilter.getTheme().equals(cboF.getTheme()) &&
                    actFilter.getThemeTitle().equals(cboF.getThemeTitle()) &&
                    actFilter.getTitle().equals(cboF.getTitle()) &&
                    actFilter.getSomewhere().equals(cboF.getSomewhere())) {
                // dann ist der gleiche schon vorne
                return;
            }
        }

        if (fList.stream().anyMatch(f ->
                f.getChannel().equals(actFilter.getChannel()) &&
                        f.getTheme().equals(actFilter.getTheme()) &&
                        f.getThemeTitle().equals(actFilter.getThemeTitle()) &&
                        f.getTitle().equals(actFilter.getTitle()) &&
                        f.getSomewhere().equals(actFilter.getSomewhere()))) {
            // dann ist schon drin
            return;
        }

        if (fList.stream().filter(f ->
                f.getChannel().equals(actFilter.getChannel()) &&
                        f.getTheme().equals(actFilter.getTheme()) &&
                        f.getThemeTitle().equals(actFilter.getThemeTitle()) &&
                        f.getTitle().equals(actFilter.getTitle()) &&
                        f.getSomewhere().equals(actFilter.getSomewhere())).findFirst().isEmpty()) {

            while (fList.size() > 10) {
                fList.remove(fList.size() - 1);
            }
            cbo.getSelectionModel().clearSelection();
            fList.add(0, actFilter);
        }
    }
}
