package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.filter.TextFilter;
import de.p2tools.mtplayer.controller.filter.TextFilterList;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class PCboLastFilmTextFilter extends HBox {

    private ComboBox<TextFilter> cbo = new ComboBox<>();
    private boolean itsMe = false;
    private final TextFilterList textFilterList;
    boolean audio;

    public PCboLastFilmTextFilter(boolean audio) {
        this.audio = audio;
        this.textFilterList = audio ? ProgData.getInstance().textFilterListAudio : ProgData.getInstance().textFilterListFilm;
        cbo.setMaxWidth(Double.MAX_VALUE);
        cbo.setVisibleRowCount(10);
        getChildren().add(cbo);
        HBox.setHgrow(cbo, Priority.ALWAYS);

        if (audio) {
            ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_AUDIO_CHANGED) {
                @Override
                public void pingGui() {
                    // dann sel löschen und evtl. neuen Filter hinzufügen
                    addNewToList(ProgData.getInstance().filterWorkerAudio.getActFilterSettings());
                }
            });

        } else {
            ProgData.getInstance().pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_FILM_CHANGED) {
                @Override
                public void pingGui() {
                    // dann sel löschen und evtl. neuen Filter hinzufügen
                    addNewToList(ProgData.getInstance().filterWorkerFilm.getActFilterSettings());
                }
            });
        }
        cbo.valueProperty().addListener((u, o, n) -> {
            if (n != null) {
                if (audio) {
                    FilmFilter actFilmFilter = ProgData.getInstance().filterWorkerAudio.getActFilterSettings().getCopy();
                    actFilmFilter.setChannel(n.getChannel());
                    actFilmFilter.setExactTheme(n.getTheme());
                    actFilmFilter.setTheme(n.getTheme());
                    actFilmFilter.setThemeTitle(n.getThemeTitle());
                    actFilmFilter.setTitle(n.getTitle());
                    actFilmFilter.setSomewhere(n.getSomewhere());
                    itsMe = true;
                    ProgData.getInstance().filterWorkerAudio.setActFilterSettings(actFilmFilter);
                    itsMe = false;

                } else {
                    FilmFilter actFilmFilter = ProgData.getInstance().filterWorkerFilm.getActFilterSettings().getCopy();
                    actFilmFilter.setChannel(n.getChannel());
                    actFilmFilter.setExactTheme(n.getTheme());
                    actFilmFilter.setTheme(n.getTheme());
                    actFilmFilter.setThemeTitle(n.getThemeTitle());
                    actFilmFilter.setTitle(n.getTitle());
                    actFilmFilter.setSomewhere(n.getSomewhere());
                    itsMe = true;
                    ProgData.getInstance().filterWorkerFilm.setActFilterSettings(actFilmFilter);
                    itsMe = false;
                }
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
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> setCellFact());
        setCellFact();
    }

    private void setCellFact() {
        cbo.setCellFactory(cell -> new ListCell<>() {
            final Button btnDel = new Button("");
            final HBox hBox = new HBox();
            final Label lblChannel = new Label();
            final Label lblTheme = new Label();
            final Label lblThemeTitle = new Label();
            final Label lblTitle = new Label();
            final Label lblSomewhere = new Label();

            {
                btnDel.setGraphic(PIconFactory.PICON.BTN_CLEAR_CBO.getFontIcon());
                btnDel.getStyleClass().add("buttonVerySmall");
                btnDel.setMaxSize(16, 16);
                btnDel.setMinSize(16, 16);
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
