/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package example.payment.jbricks;

import javax.microedition.lcdui.Canvas;
import javax.microedition.midlet.MIDlet;


/**
 *
 * @created    August 16, 2005
 * @version
 */
public class Menu {
    private Main midlet;
    private Engine engine;
    private EngineState state;
    private AbstractMenu menu;
    private MainMenu mainMenu;
    private LevelMenu levelMenu;
    private BuyLifeMenu buyLifeMenu;
    private BuyLevelMenu buyLevelMenu;
    private SettingsMenu settingsMenu;
    private HistoryMenu historyMenu;
    private InfoMenu infoMenu;
    private boolean stepEnabled = true;

    /** Creates a new instance of Menu */
    public Menu() {
        mainMenu = new MainMenu();
        levelMenu = new LevelMenu();
        buyLifeMenu = new BuyLifeMenu();
        buyLevelMenu = new BuyLevelMenu();
        settingsMenu = new SettingsMenu();
        historyMenu = new HistoryMenu();
        infoMenu = new InfoMenu();
        menu = mainMenu;
        state = new EngineState();
    }

    public int getSelected() {
        return menu.getSelected();
    }

    public void keyPressed(int keycode) {
        menu.keyPressed(keycode);
    }

    public MenuItem[] getItems() {
        return menu.getItems();
    }

    public void setMIDlet(MIDlet midlet) {
        this.midlet = (Main)midlet;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
        engine.getState(state);
    }

    public void showResumeItem(boolean show) {
        mainMenu.showResumeItem(show);
    }

    public void resetSelected() {
        menu.resetSelected();
    }

    private abstract class AbstractMenu {
        protected MenuItem back = new MenuItem("Back", true);
        protected MenuItem[] items;
        private int selected = 0;

        public void keyPressed(int keycode) {
            if (stepEnabled) {
                if ((keycode == Canvas.UP) && (selected > 0)) {
                    setSelected(-1);

                    return;
                }

                if ((keycode == Canvas.DOWN) && (selected < (items.length - 1))) {
                    setSelected(1);

                    return;
                }
            }

            if (keycode == Canvas.FIRE) {
                executeCommand(selected);
            }
        }

        public MenuItem[] getItems() {
            return items;
        }

        public int getSelected() {
            return selected;
        }

        private void setSelected(int step) {
            if (step > 0) {
                selected++;
            } else {
                selected--;
            }

            if (!items[selected].isEnabled()) {
                setSelected(step);
            }
        }

        protected void resetSelected() {
            selected = 0;
        }

        protected abstract void executeCommand(int selected);
    }

    static class MenuItem {
        private String text;
        private boolean enabled;

        public MenuItem(String text, boolean enabled) {
            this.text = text;
            this.enabled = enabled;
        }

        public String getText() {
            return text;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private class MainMenu extends AbstractMenu {
        protected static final int ITEM_RESUME = 0;
        protected static final int ITEM_START = 1;
        protected static final int ITEM_BUY_LIFE = 2;
        protected static final int ITEM_BUY_LEVEL = 3;
        protected static final int ITEM_HISTORY = 4;
        protected static final int ITEM_SETTINGS = 5;
        protected static final int ITEM_INFO = 6;
        protected static final int ITEM_EXIT = 7;
        protected MenuItem[] mainItems1 =
            {
                new MenuItem("Start Game", true), new MenuItem("Buy Life", true),
                new MenuItem("Buy Level", true), new MenuItem("Payment History", true),
                new MenuItem("Settings", true), new MenuItem("Info", true),
                new MenuItem("Exit", true),
            };
        protected MenuItem[] mainItems2 =
            {
                new MenuItem("Resume Game", true), new MenuItem("Start Game", true),
                new MenuItem("Buy Life", true), new MenuItem("Buy Level", true),
                new MenuItem("Payment History", true), new MenuItem("Settings", true),
                new MenuItem("Info", true), new MenuItem("Exit", true),
            };

        public MainMenu() {
            items = mainItems1;
        }

        protected void executeCommand(int selected) {
            if ((items == mainItems1)) {
                selected++;
            }

            switch (selected) {
            case ITEM_START:
                levelMenu.setItems(engine.getAvailableLevels());
                menu = levelMenu;

                break;

            case ITEM_BUY_LIFE:
                buyLifeMenu.getItems()[BuyLifeMenu.ITEM_3_LIVES].setEnabled(engine.canBuyLives(3));
                menu = buyLifeMenu;
                resetSelected();

                break;

            case ITEM_BUY_LEVEL:
                buyLevelMenu.getItems()[BuyLevelMenu.ITEM_3_LEVELS].setEnabled(engine.canBuyLevels(
                        3));
                menu = buyLevelMenu;
                resetSelected();

                break;

            case ITEM_HISTORY:
                stepEnabled = false;
                historyMenu.setItems(midlet.getHistory());
                menu = historyMenu;

                break;

            case ITEM_SETTINGS:
                menu = settingsMenu;

                break;

            case ITEM_INFO:
                stepEnabled = false;
                infoMenu.getItems()[InfoMenu.ITEM_LEVELS].setText("Available levels: " +
                    engine.getAvailableLevels());
                infoMenu.getItems()[InfoMenu.ITEM_LIVES].setText("Available lives: " +
                    engine.getAvailableLives());
                menu = infoMenu;

                break;

            case ITEM_EXIT:
                engine.stop();
                midlet.destroyApp(true);

                break;

            case ITEM_RESUME:
                engine.setState(Engine.PLAY);
                engine.resumeGame();
                midlet.showCommandExit(true);
            }
        }

        protected void showResumeItem(boolean show) {
            if (show) {
                items = mainItems2;
            } else {
                items = mainItems1;
            }
        }
    }

    private class LevelMenu extends AbstractMenu {
        protected MenuItem[] levelItems;
        protected int levels = 0;

        protected void executeCommand(int selected) {
            if (selected < levels) {
                engine.startGame(selected);
                midlet.showCommandExit(true);
                mainMenu.showResumeItem(true);
            }

            menu = mainMenu;
            resetSelected();
        }

        public void setItems(int levels) {
            this.levels = levels;
            levelItems = new MenuItem[levels + 1];

            for (int i = 0; i < levels; i++) {
                levelItems[i] = new MenuItem("Level " + (i + 1), true);
            }

            levelItems[levels] = back;
            items = levelItems;
        }
    }

    private class BuyLifeMenu extends AbstractMenu {
        protected static final int ITEM_1_LIFE = 0;
        protected static final int ITEM_3_LIVES = 1;
        private MenuItem[] lifeItems =
            {
                new MenuItem("Buy single life", true),
                new MenuItem("Buy 3 lives for reduced price", true), back
            };

        public BuyLifeMenu() {
            items = lifeItems;
        }

        protected void executeCommand(int selected) {
            switch (selected) {
            case ITEM_1_LIFE:
                midlet.buyLife(1);

                break;

            case ITEM_3_LIVES:
                midlet.buyLife(3);

                break;
            }

            resetSelected();

            mainMenu.mainItems2[MainMenu.ITEM_BUY_LIFE].setEnabled(engine.canBuyLives(1));

            mainMenu.mainItems1[MainMenu.ITEM_BUY_LIFE - 1].setEnabled(engine.canBuyLives(1));

            menu = mainMenu;
        }
    }

    private class BuyLevelMenu extends AbstractMenu {
        protected static final int ITEM_1_LEVEL = 0;
        protected static final int ITEM_3_LEVELS = 1;
        private MenuItem[] levelItems =
            {
                new MenuItem("Buy single level", true),
                new MenuItem("Buy 3 levels for reduced price", true), back
            };

        public BuyLevelMenu() {
            items = levelItems;
        }

        protected void executeCommand(int selected) {
            switch (selected) {
            case ITEM_1_LEVEL:
                midlet.buyLevel(1);

                break;

            case ITEM_3_LEVELS:
                midlet.buyLevel(3);

                break;
            }

            resetSelected();

            mainMenu.mainItems2[MainMenu.ITEM_BUY_LEVEL].setEnabled(engine.canBuyLevels(1));

            mainMenu.mainItems1[MainMenu.ITEM_BUY_LEVEL - 1].setEnabled(engine.canBuyLevels(1));

            menu = mainMenu;
        }
    }

    private class SettingsMenu extends AbstractMenu {
        protected static final int ITEM_TRAN_LISTENER = 0;
        protected static final int ITEM_BACK = 1;
        private MenuItem[] tranListenerItem =
            {
                new MenuItem("Transaction Listener Enabled", true),
                new MenuItem("Transaction Listener Disabled", true)
            };
        public MenuItem[] settingsItems = { tranListenerItem[0], back };
        private boolean transactionListenerNull = false;

        public SettingsMenu() {
            items = settingsItems;
        }

        protected void executeCommand(int selected) {
            switch (selected) {
            case ITEM_TRAN_LISTENER:
                transactionListenerNull = !transactionListenerNull;
                midlet.setTransactionListenerNull(transactionListenerNull);
                items[0] = (transactionListenerNull) ? tranListenerItem[1] : tranListenerItem[0];

                break;

            case ITEM_BACK:
                resetSelected();
                menu = mainMenu;

                break;
            }
        }
    }

    private class HistoryMenu extends AbstractMenu {
        private MenuItem[] historyItems;

        protected void executeCommand(int selected) {
            stepEnabled = true;
            menu = mainMenu;
            resetSelected();
        }

        public void setItems(String[] record) {
            if ((record == null) || (record.length == 0)) {
                historyItems = new MenuItem[1];
            } else {
                historyItems = new MenuItem[record.length + 1];

                for (int i = 1; i <= record.length; i++) {
                    historyItems[i] = new MenuItem(record[i - 1], true);
                }
            }

            historyItems[0] = back;
            items = historyItems;
        }
    }

    private class InfoMenu extends AbstractMenu {
        protected static final int ITEM_LIVES = 1;
        protected static final int ITEM_LEVELS = 2;
        private MenuItem[] infoItems =
            {
                back, new MenuItem("Available lives: 0", true),
                new MenuItem("Available levels: 0", true),
            };

        public InfoMenu() {
            items = infoItems;
        }

        protected void executeCommand(int selected) {
            stepEnabled = true;
            menu = mainMenu;
            resetSelected();
        }
    }
}
