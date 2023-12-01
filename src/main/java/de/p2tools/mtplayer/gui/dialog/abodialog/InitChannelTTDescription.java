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

import java.util.Arrays;

public class InitChannelTTDescription {
    private final AddAboDto addAboDto;

    public InitChannelTTDescription(AddAboDto addAboDto) {
        this.addAboDto = addAboDto;
        init();
    }

    private void init() {
        addAboDto.chkActive.setOnAction(a -> {
            if (!addAboDto.chkActive.isFocused()) {
                return;
            }
            setActive();
        });
        addAboDto.textAreaDescription.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.textAreaDescription.isFocused()) {
                return;
            }
            setDescription();
        });
        addAboDto.channelProperty.addListener((u, o, n) -> {
            setChannel();
        });
        addAboDto.textAreaTheme.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.textAreaTheme.isFocused()) {
                return;
            }
            setTheme();
        });
        addAboDto.textAreaThemeTitle.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.textAreaThemeTitle.isFocused()) {
                return;
            }
            setThemeTitle();
        });
        addAboDto.textAreaTitle.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.textAreaTitle.isFocused()) {
                return;
            }
            setTitle();
        });
        addAboDto.textAreaSomewhere.textProperty().addListener((u, o, n) -> {
            if (!addAboDto.textAreaSomewhere.isFocused()) {
                return;
            }
            setSomewhere();
        });
        addAboDto.chkThemeExact.setOnAction(a -> {
            if (!addAboDto.chkThemeExact.isFocused()) {
                return;
            }
            setThemeExact();
        });
    }

    public void makeAct() {
        // nach dem actFilm setzen, z.B. beim Wechsel
        addAboDto.chkActive.setSelected(addAboDto.getAct().abo.isActive());
        addAboDto.textAreaDescription.setText(addAboDto.getAct().abo.getDescription());
        addAboDto.channelProperty.setValue(addAboDto.getAct().abo.getChannel());
        addAboDto.textAreaTheme.setText(addAboDto.getAct().abo.getTheme());
        addAboDto.textAreaThemeTitle.setText(addAboDto.getAct().abo.getThemeTitle());
        addAboDto.textAreaTitle.setText(addAboDto.getAct().abo.getTitle());
        addAboDto.textAreaSomewhere.setText(addAboDto.getAct().abo.getSomewhere());
        addAboDto.chkThemeExact.setSelected(addAboDto.getAct().abo.isThemeExact());
        addAboDto.lblLastAbo.setText(addAboDto.getAct().abo.getDate().toString());
        addAboDto.lblGenDate.setText(addAboDto.getAct().abo.getGenDate().toString());
    }

    public void setActive() {
        if (addAboDto.chkActiveAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setActive(addAboDto.chkActive.isSelected());
            });
        } else {
            addAboDto.getAct().abo.setActive(addAboDto.chkActive.isSelected());
        }
    }

    public void setDescription() {
        if (addAboDto.chkDescriptionAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setDescription(addAboDto.textAreaDescription.getText());
            });
        } else {
            addAboDto.getAct().abo.setDescription(addAboDto.textAreaDescription.getText());
        }
    }

    public void setChannel() {
        if (addAboDto.chkChannelAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setChannel(addAboDto.channelProperty.getValueSafe());
            });
        } else {
            addAboDto.getAct().abo.setChannel(addAboDto.channelProperty.getValueSafe());
        }
    }

    public void setTheme() {
        if (addAboDto.chkThemeAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setTheme(addAboDto.textAreaTheme.getText());
            });
        } else {
            addAboDto.getAct().abo.setTheme(addAboDto.textAreaTheme.getText());
        }
    }

    public void setThemeTitle() {
        if (addAboDto.chkThemeTitleAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setThemeTitle(addAboDto.textAreaThemeTitle.getText());
            });
        } else {
            addAboDto.getAct().abo.setThemeTitle(addAboDto.textAreaThemeTitle.getText());
        }
    }

    public void setTitle() {
        if (addAboDto.chkTitleAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setTitle(addAboDto.textAreaTitle.getText());
            });
        } else {
            addAboDto.getAct().abo.setTitle(addAboDto.textAreaTitle.getText());
        }
    }

    public void setSomewhere() {
        if (addAboDto.chkSomewhereAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setSomewhere(addAboDto.textAreaSomewhere.getText());
            });
        } else {
            addAboDto.getAct().abo.setSomewhere(addAboDto.textAreaSomewhere.getText());
        }
    }

    public void setThemeExact() {
        if (addAboDto.chkThemeExactAll.isSelected()) {
            Arrays.stream(addAboDto.addAboData).forEach(addAboData -> {
                addAboData.abo.setThemeExact(addAboDto.chkThemeExact.isSelected());
            });
        } else {
            addAboDto.getAct().abo.setThemeExact(addAboDto.chkThemeExact.isSelected());
        }
    }
}
