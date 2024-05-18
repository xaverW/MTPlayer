package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.TextFilter;
import de.p2tools.mtplayer.controller.filmfilter.TextFilterList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class PCboTextFilter extends HBox {

    private ComboBox<TextFilter> cbo = new ComboBox<>();
    private boolean itsMe = false;
    private final TextFilterList textFilterList;

    public PCboTextFilter() {
        this.textFilterList = ProgData.getInstance().textFilterList;
        cbo.setMaxWidth(Double.MAX_VALUE);
        cbo.setVisibleRowCount(10);
        getChildren().add(cbo);
        HBox.setHgrow(cbo, Priority.ALWAYS);

        PListener.addListener(new PListener(PListener.EVENT_FILTER_CHANGED, PCboTextFilter.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // dann sel löschen und evtl. neuen Filter hinzufügen
                addNewToList(ProgData.getInstance().filmFilterWorker.getActFilterSettings());
            }
        });

        cbo.valueProperty().addListener((u, o, n) -> {
            if (n != null) {
                FilmFilter actFilmFilter = ProgData.getInstance().filmFilterWorker.getActFilterSettings().getCopy();
                actFilmFilter.setChannel(n.getChannel());
                actFilmFilter.setExactTheme(n.getTheme());
                actFilmFilter.setTheme(n.getTheme());
                actFilmFilter.setThemeTitle(n.getThemeTitle());
                actFilmFilter.setTitle(n.getTitle());
                actFilmFilter.setSomewhere(n.getSomewhere());
                itsMe = true;
                ProgData.getInstance().filmFilterWorker.setActFilterSettings(actFilmFilter);
                itsMe = false;
            }
        });

        // die gespeicherten Filter eintragen
        cbo.setItems(textFilterList);

        cbo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TextFilter textFilter) {
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
                btnDel.setGraphic(ProgIcons.ICON_BUTTON_DEL_SW.getImageView());
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
                    btnDel.setOnMousePressed(m -> textFilterList.remove(filmFilter));

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

    private synchronized void addNewToList(FilmFilter addF) {
        if (itsMe) {
            // dann war es vom eigenen CBO und da sind die Filter ja schon alle drin
            return;
        }

        // einen neuen Filter einfügen
        cbo.getSelectionModel().clearSelection();

        TextFilter addFilter = new TextFilter(addF);
        if (addFilter.filterIsEmpty()) {
            // dann sind die eingeschalteten Textfilter leer, ist nix
            return;
        }

        TextFilter tf = textFilterList.stream().filter(addFilter::filterIsSame).findFirst().orElse(null);
        if (tf == null) {
            // dann ist er noch nicht / nicht mehr drin und kommt an Stelle 1
            while (textFilterList.size() > 15) {
                textFilterList.remove(textFilterList.size() - 1);
            }
            textFilterList.add(0, addFilter);
        }
    }
}
