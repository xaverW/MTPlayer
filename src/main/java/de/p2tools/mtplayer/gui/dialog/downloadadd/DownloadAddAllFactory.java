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


package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.guitools.P2Color;

public class DownloadAddAllFactory {
    private DownloadAddAllFactory() {
    }

    public static void init(AddDownloadDto addDownloadDto) {
        if (ProgData.getInstance().setDataList.getSetDataListSaveAbo().size() == 1) {
            // wenns nur ein Set gibt, macht dann keinen Sinn
            addDownloadDto.textSet.setVisible(false);
            addDownloadDto.textSet.setManaged(false);
            addDownloadDto.cboSetData.setVisible(false);
            addDownloadDto.cboSetData.setManaged(false);
            addDownloadDto.chkSetAll.setVisible(false);
            addDownloadDto.chkSetAll.setManaged(false);
        }

        if (addDownloadDto.addDownloadData.length == 1) {
            // wenns nur einen Download gibt, macht dann keinen Sinn
            addDownloadDto.btnAll.setVisible(false);
            addDownloadDto.btnAll.setManaged(false);

            addDownloadDto.chkSetAll.setVisible(false);
            addDownloadDto.chkSetAll.setManaged(false);
            addDownloadDto.chkResolutionAll.setVisible(false);
            addDownloadDto.chkResolutionAll.setManaged(false);
            addDownloadDto.chkPathAll.setVisible(false);
            addDownloadDto.chkPathAll.setManaged(false);
            addDownloadDto.chkSubTitleAll.setVisible(false);
            addDownloadDto.chkSubTitleAll.setManaged(false);
            addDownloadDto.chkInfoAll.setVisible(false);
            addDownloadDto.chkInfoAll.setManaged(false);
            addDownloadDto.chkStartTimeAll.setVisible(false);
            addDownloadDto.chkStartTimeAll.setManaged(false);

        } else {
            addDownloadDto.chkSetAll.getStyleClass().add("checkBoxAll");
            addDownloadDto.chkResolutionAll.getStyleClass().add("checkBoxAll");
            addDownloadDto.chkPathAll.getStyleClass().add("checkBoxAll");
            addDownloadDto.chkSubTitleAll.getStyleClass().add("checkBoxAll");
            addDownloadDto.chkInfoAll.getStyleClass().add("checkBoxAll");
            addDownloadDto.chkStartTimeAll.getStyleClass().add("checkBoxAll");

            addDownloadDto.chkSetAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkSetAll.isSelected()) {
                    addDownloadDto.initSetDataDownload.makeSetDataChange();
                }
            });
            addDownloadDto.chkResolutionAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkResolutionAll.isSelected()) {
                    addDownloadDto.initResolutionButton.setRes();
                }
            });
            addDownloadDto.chkPathAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkPathAll.isSelected()) {
                    addDownloadDto.initPathName.pathChanged();
                }
            });
            addDownloadDto.chkStartTimeAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkStartTimeAll.isSelected()) {
                    addDownloadDto.initStartTimeDownload.setStartTime();
                }
            });
            addDownloadDto.chkInfoAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkInfoAll.isSelected()) {
                    addDownloadDto.initSubTitle.setInfoSubTitle();
                }
            });
            addDownloadDto.chkSubTitleAll.selectedProperty().addListener((observable, oldValue, newValue) -> {
                addCheckAllCss(addDownloadDto);
                if (addDownloadDto.chkSubTitleAll.isSelected()) {
                    addDownloadDto.initSubTitle.setInfoSubTitle();
                }
            });

            addDownloadDto.btnAll.setOnAction(a -> changeAll(addDownloadDto));
            addCheckAllCss(addDownloadDto);
        }
    }

    private static void changeAll(AddDownloadDto addDownloadDto) {
        boolean isNotSelected = !isAllSelected(addDownloadDto);

        if (ProgData.getInstance().setDataList.getSetDataListSaveAbo().size() > 1) {
            // nur dann wird er angezeigt
            addDownloadDto.chkSetAll.setSelected(isNotSelected);
        }
        addDownloadDto.chkResolutionAll.setSelected(isNotSelected);
        addDownloadDto.chkPathAll.setSelected(isNotSelected);
        addDownloadDto.chkSubTitleAll.setSelected(isNotSelected);
        addDownloadDto.chkInfoAll.setSelected(isNotSelected);
        addDownloadDto.chkStartTimeAll.setSelected(isNotSelected);

        addCheckAllCss(addDownloadDto);
    }

    private static void addCheckAllCss(AddDownloadDto addDownloadDto) {
        if (isAllSelected(addDownloadDto)) {
            final String c = P2Color.getCssColor(DownloadAddDialogFactory.getBlue());
            addDownloadDto.btnAll.setStyle("-fx-text-fill: " + c);

        } else {
            if (ProgConfig.SYSTEM_THEME_DARK.getValue()) {
                addDownloadDto.btnAll.setStyle("-fx-text-fill: white");
            } else {
                addDownloadDto.btnAll.setStyle("-fx-text-fill: black");
            }
        }
    }

    private static boolean isAllSelected(AddDownloadDto addDownloadDto) {
        return addDownloadDto.chkSetAll.isSelected() ||
                addDownloadDto.chkResolutionAll.isSelected() ||
                addDownloadDto.chkPathAll.isSelected() ||
                addDownloadDto.chkSubTitleAll.isSelected() ||
                addDownloadDto.chkInfoAll.isSelected() ||
                addDownloadDto.chkStartTimeAll.isSelected();
    }
}
