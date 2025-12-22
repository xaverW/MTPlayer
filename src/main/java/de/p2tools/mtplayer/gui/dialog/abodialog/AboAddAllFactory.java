/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */


package de.p2tools.mtplayer.gui.dialog.abodialog;

import de.p2tools.mtplayer.controller.config.ProgData;

public class AboAddAllFactory {
    private AboAddAllFactory() {
    }

    public static void init(AddAboDto addAboDto) {
        if (ProgData.getInstance().setDataList.getSetDataListAbo().size() == 1) {
            // wenns nur ein Set gibt, macht dann keinen Sinn
            addAboDto.textSet.setVisible(false);
            addAboDto.textSet.setManaged(false);
            addAboDto.cboSetData.setVisible(false);
            addAboDto.cboSetData.setManaged(false);
            addAboDto.chkSetAll.setVisible(false);
            addAboDto.chkSetAll.setManaged(false);
        }

        if (addAboDto.addAboData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            addAboDto.btnAll.setVisible(false);
            addAboDto.btnAll.setManaged(false);

            addAboDto.chkActiveAll.setVisible(false);
            addAboDto.chkActiveAll.setManaged(false);
            addAboDto.chkSourceAll.setVisible(false);
            addAboDto.chkSourceAll.setManaged(false);
            addAboDto.chkDescriptionAll.setVisible(false);
            addAboDto.chkDescriptionAll.setManaged(false);
            addAboDto.chkResolutionAll.setVisible(false);
            addAboDto.chkResolutionAll.setManaged(false);
            addAboDto.chkChannelAll.setVisible(false);
            addAboDto.chkChannelAll.setManaged(false);
            addAboDto.chkThemeAll.setVisible(false);
            addAboDto.chkThemeAll.setManaged(false);
            addAboDto.chkThemeExactAll.setVisible(false);
            addAboDto.chkThemeExactAll.setManaged(false);
            addAboDto.chkThemeTitleAll.setVisible(false);
            addAboDto.chkThemeTitleAll.setManaged(false);
            addAboDto.chkTitleAll.setVisible(false);
            addAboDto.chkTitleAll.setManaged(false);
            addAboDto.chkSomewhereAll.setVisible(false);
            addAboDto.chkSomewhereAll.setManaged(false);
            addAboDto.chkTimeRangeAll.setVisible(false);
            addAboDto.chkTimeRangeAll.setManaged(false);
            addAboDto.chkDurationAll.setVisible(false);
            addAboDto.chkDurationAll.setManaged(false);
            addAboDto.chkStartTimeAll.setVisible(false);
            addAboDto.chkStartTimeAll.setManaged(false);
            addAboDto.chkDestAboDirAll.setVisible(false);
            addAboDto.chkDestAboDirAll.setManaged(false);
            addAboDto.chkDestAboFileNameAll.setVisible(false);
            addAboDto.chkDestAboFileNameAll.setManaged(false);
            addAboDto.chkSetAll.setVisible(false);
            addAboDto.chkSetAll.setManaged(false);

        } else {
            addAboDto.chkActiveAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkSourceAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkDescriptionAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkResolutionAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkChannelAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkThemeAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkThemeExactAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkThemeTitleAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkTitleAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkSomewhereAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkTimeRangeAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkDurationAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkStartTimeAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkDestAboDirAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkDestAboFileNameAll.getStyleClass().add("checkBoxAll");
            addAboDto.chkSetAll.getStyleClass().add("checkBoxAll");

            addAboDto.chkActiveAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkActiveAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setActive();
                }
            });
            addAboDto.chkSourceAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkSourceAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setSource();
                }
            });
            addAboDto.chkDescriptionAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkDescriptionAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setDescription();
                }
            });
            addAboDto.chkResolutionAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkResolutionAll.isSelected()) {
                    addAboDto.initResolution.setRes();
                }
            });
            addAboDto.chkChannelAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkChannelAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setChannel();
                }
            });
            addAboDto.chkThemeAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkThemeAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setTheme();
                }
            });
            addAboDto.chkThemeExactAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkThemeExactAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setThemeExact();
                }
            });
            addAboDto.chkThemeTitleAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkThemeTitleAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setThemeTitle();
                }
            });
            addAboDto.chkTitleAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkTitleAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setTitle();
                }
            });
            addAboDto.chkSomewhereAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkSomewhereAll.isSelected()) {
                    addAboDto.initChannelTTDescription.setSomewhere();
                }
            });
            addAboDto.chkTimeRangeAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkTimeRangeAll.isSelected()) {
                    addAboDto.initTimeRangeAndDuration.setTimeRange();
                }
            });
            addAboDto.chkDurationAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkDurationAll.isSelected()) {
                    addAboDto.initTimeRangeAndDuration.setDurationMin();
                    addAboDto.initTimeRangeAndDuration.setDurationMax();
                }
            });
            addAboDto.chkStartTimeAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkStartTimeAll.isSelected()) {
                    addAboDto.initStartTime.setStartTimePick();
                }
            });
            addAboDto.chkDestAboDirAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkDestAboDirAll.isSelected()) {
                    addAboDto.initDestination.setPathToAbo();
                }
            });
            addAboDto.chkDestAboFileNameAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkDestAboFileNameAll.isSelected()) {
                    addAboDto.initDestination.setFileNameToAbo();
                }
            });
            addAboDto.chkSetAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addAboDto);
                if (addAboDto.chkSetAll.isSelected()) {
                    addAboDto.initSetData.makeSetDataChange();
                }
            });

            addAboDto.btnAll.setOnAction(a -> changeAll(addAboDto));
            addCheckAllCss(addAboDto);
        }
    }

    private static void changeAll(AddAboDto addAboDto) {
        boolean isNotSelected = !isAllSelected(addAboDto);

        if (ProgData.getInstance().setDataList.getSetDataListAbo().size() > 1) {
            // nur dann wird er angezeigt
            addAboDto.chkSetAll.setSelected(isNotSelected);
        }
        addAboDto.chkActiveAll.setSelected(isNotSelected);
        addAboDto.chkSourceAll.setSelected(isNotSelected);
        addAboDto.chkDescriptionAll.setSelected(isNotSelected);
        addAboDto.chkResolutionAll.setSelected(isNotSelected);
        addAboDto.chkChannelAll.setSelected(isNotSelected);
        addAboDto.chkThemeAll.setSelected(isNotSelected);
        addAboDto.chkThemeExactAll.setSelected(isNotSelected);
        addAboDto.chkThemeTitleAll.setSelected(isNotSelected);
        addAboDto.chkTitleAll.setSelected(isNotSelected);
        addAboDto.chkSomewhereAll.setSelected(isNotSelected);
        addAboDto.chkTimeRangeAll.setSelected(isNotSelected);
        addAboDto.chkDurationAll.setSelected(isNotSelected);
        addAboDto.chkStartTimeAll.setSelected(isNotSelected);
        addAboDto.chkDestAboDirAll.setSelected(isNotSelected);
        addAboDto.chkDestAboFileNameAll.setSelected(isNotSelected);
        addCheckAllCss(addAboDto);
    }

    private static void addCheckAllCss(AddAboDto addAboDto) {
        if (isAllSelected(addAboDto)) {
//            final String c = P2Color.getCssColor(DownloadAddDialogFactory.getBlue());
            addAboDto.btnAll.setStyle("-fx-text-fill: -pBackgroundSelTextColor");

        } else {
            addAboDto.btnAll.setStyle("-fx-text-fill: -pBackgroundTextColor");
//            if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
//                addAboDto.btnAll.setStyle("-fx-text-fill: white");
//            } else {
//                addAboDto.btnAll.setStyle("-fx-text-fill: black");
//            }
        }
    }

    private static boolean isAllSelected(AddAboDto addAboDto) {
        return addAboDto.chkActiveAll.isSelected() ||
                addAboDto.chkSourceAll.isSelected() ||
                addAboDto.chkDescriptionAll.isSelected() ||
                addAboDto.chkResolutionAll.isSelected() ||
                addAboDto.chkChannelAll.isSelected() ||
                addAboDto.chkThemeAll.isSelected() ||
                addAboDto.chkThemeExactAll.isSelected() ||
                addAboDto.chkThemeTitleAll.isSelected() ||
                addAboDto.chkTitleAll.isSelected() ||
                addAboDto.chkSomewhereAll.isSelected() ||
                addAboDto.chkTimeRangeAll.isSelected() ||
                addAboDto.chkDurationAll.isSelected() ||
                addAboDto.chkStartTimeAll.isSelected() ||
                addAboDto.chkDestAboDirAll.isSelected() ||
                addAboDto.chkDestAboFileNameAll.isSelected() ||
                addAboDto.chkSetAll.isSelected();
    }
}
