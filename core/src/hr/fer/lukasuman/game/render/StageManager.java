package hr.fer.lukasuman.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.LocalizationKeys;
import hr.fer.lukasuman.game.automata.AutomatonAction;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.level.Direction;
import hr.fer.lukasuman.game.level.blocks.*;
import hr.fer.lukasuman.game.screens.GameScreen;

import java.util.Set;

public class StageManager implements Disposable {
    private static final String TAG = StageManager.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private GameRenderer gameRenderer;
    private GameController gameController;
    private GameScreen gameScreen;
    private Skin skin;

    private Stage upperLeftStage;
    private Stage upperRightStage;
    private Stage lowerLeftStage;
    private Stage lowerRightStage;
    private Stage fullStage;

    private Viewport upperLeftViewport;
    private Viewport upperRightViewport;
    private Viewport lowerLeftViewport;
    private Viewport lowerRightViewport;
    private Viewport fullViewport;

    private Label scoreLabel;
    private SelectBox<AutomatonAction> actionSelectBox;
    private SelectBox<BlockLabel> transitionSelectBox;
    private TextButton newAutomatonButton;
    private TextButton saveAutomatonButton;
    private TextButton loadAutomatonButton;

    private SelectBox<BlockLabel> blockTypeSelectBox;
    private SelectBox<Direction> blockDirectionSelectBox;
    private TextButton newLevelButton;
    private TextButton saveLevelButton;
    private TextButton loadLevelButton;
    private Label fpsLabel;

    private ButtonGroup automatonButtonGroup;
    private TextButton selectionButton;
    private TextButton createStateButton;
    private TextButton deleteStateButton;
    private TextButton createTransitionButton;
    private TextButton deleteTransitionButton;
    private TextButton setStartStateButton;
//    private TextButton setGoalStateButton;

    private ButtonGroup levelButtonGroup;
    private TextButton selectBlockButton;
    private TextButton paintBlockButton;
    private TextButton startSimulationButton;
    private TextButton pauseSimulationButton;
    private Slider simulationSpeedSlider;

    private FileChooser fileChooser;
    private FileTypeFilter serTypeFilter;
    private FileTypeFilter pngTypeFilter;

    private Dialog confirmationDialog;
    private Label confirmationDialogLabel;
    private Table levelDimensionTable;
    private Table buttonTableYesNoCancel;
    private Table buttonTableOK;
    private TextField levelWidthTextField;
    private TextField levelHeightTextField;
    private CallbackFunction yesCallback;
    private CallbackFunction noCallback;
    private CallbackFunction finalCallback;

    private GamePreferences prefs;

    public StageManager(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.gameController = gameRenderer.getGameController();
        this.gameScreen = gameRenderer.getGameScreen();
        init();
    }

    private void init() {
        initFileChooser();

        upperLeftViewport = new FitViewport(Constants.VIEWPORT_GUI_WIDTH / 2.0f, Constants.VIEWPORT_GUI_HEIGHT / 2.0f);
        upperRightViewport = new FitViewport(Constants.VIEWPORT_GUI_WIDTH / 2.0f, Constants.VIEWPORT_GUI_HEIGHT / 2.0f);
        lowerLeftViewport = new FitViewport(Constants.VIEWPORT_GUI_WIDTH / 2.0f, Constants.VIEWPORT_GUI_HEIGHT / 2.0f);
        lowerRightViewport = new FitViewport(Constants.VIEWPORT_GUI_WIDTH / 2.0f, Constants.VIEWPORT_GUI_HEIGHT / 2.0f);
        fullViewport = new FitViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

        upperLeftStage = new Stage(upperLeftViewport);
        upperRightStage = new Stage(upperRightViewport);
        lowerLeftStage = new Stage(lowerLeftViewport);
        lowerRightStage = new Stage(lowerRightViewport);
        fullStage = new Stage(fullViewport);
        rebuildStage();

        initConfirmationDialog();
    }

    private void rebuildStage () {
        skin = Assets.getInstance().getAssetManager().get(Constants.SKIN_LIBGDX_UI);
        prefs = GamePreferences.getInstance();

        Table upperLeftTable = new Table();
        upperLeftTable.setFillParent(true);
        upperLeftTable.setDebug(prefs.debug);
        Table upperRightTable = new Table();
        upperRightTable.setFillParent(true);
        upperRightTable.setDebug(prefs.debug);

        //TODO fix widget sizes within stages

        float upperRatio = Constants.UPPER_BORDER_RATIO * 2.0f;
        Value upperWidthValue = Value.percentWidth(1.0f, upperLeftTable);
        Value upperHeightValue = Value.percentHeight(upperRatio / (1.0f + upperRatio), upperLeftTable);

        upperLeftTable.add(rebuildAutomataNorth()).expand().top().size(upperWidthValue, upperHeightValue);
        upperLeftStage.addActor(upperLeftTable);
        upperRightTable.add(rebuildLevelNorth()).expand().top().size(upperWidthValue, upperHeightValue);
        upperRightStage.addActor(upperRightTable);

        Table lowerLeftTable = new Table();
        lowerLeftTable.setFillParent(true);
        lowerLeftTable.setDebug(prefs.debug);
        Table lowerRightTable = new Table();
        lowerRightTable.setFillParent(true);
        lowerRightTable.setDebug(prefs.debug);

        float lowerRatio = Constants.LOWER_BORDER_RATIO * 2.0f;
        Value lowerWidthValue = Value.percentWidth(1.0f, lowerLeftTable);
        Value lowerHeightValue = Value.percentHeight(lowerRatio / (1.0f + lowerRatio), lowerLeftTable);

        lowerLeftTable.add(rebuildAutomataSouth()).expand().bottom().size(lowerWidthValue, lowerHeightValue);
        lowerLeftStage.addActor(lowerLeftTable);
        lowerRightTable.add(rebuildLevelSouth()).expand().bottom().size(lowerWidthValue, lowerHeightValue);
        lowerRightStage.addActor(lowerRightTable);
    }

    private Table rebuildAutomataNorth() {
        Table automataNorth = new Table();
        automataNorth.setDebug(prefs.debug);
        scoreLabel = new Label("", skin, Constants.DEFAULT_FONT_NAME, Color.WHITE);
        automataNorth.add(scoreLabel).expandX();

        actionSelectBox = new SelectBox<>(skin);
        actionSelectBox.setItems(AutomatonAction.MOVE_FORWARD, AutomatonAction.ROTATE_LEFT, AutomatonAction.ROTATE_RIGHT);
        actionSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
                if (gameController.isSimulationRunning()) return;
                AutomatonState selectedState = gameController.getSelectedState();
                if (selectedState != null) {
                    selectedState.setAction(actionSelectBox.getSelected());
                }
            }
        });
        automataNorth.add(actionSelectBox).expandX();

        transitionSelectBox = new SelectBox<>(skin);
        transitionSelectBox.setItems(BlockLabel.values());
        transitionSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
            }
        });
        automataNorth.add(transitionSelectBox).expandX();

        newAutomatonButton = new TextButton(getBundle().get(LocalizationKeys.NEW_AUTOMATON), skin);
        newAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if (gameController.isSimulationRunning()) return;
                if (gameController.getAutomataController().getCurrentAutomaton().isChangesPending()) {
                    showConfirmationDialog(StageManager.this::saveAutomatonClicked,
                            StageManager.this::newAutomatonClicked, getBundle().get(LocalizationKeys.AUTOMATON_CONFIRM_MESSAGE));
                } else {
                    newAutomatonClicked();
                }
            }
        });
        automataNorth.add(newAutomatonButton).expandX();

        saveAutomatonButton = new TextButton(getBundle().get(LocalizationKeys.SAVE_AUTOMATON), skin);
        saveAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if (gameController.isSimulationRunning()) return;
                saveAutomatonClicked();
            }
        });
        automataNorth.add(saveAutomatonButton).expandX();

        loadAutomatonButton = new TextButton(getBundle().get(LocalizationKeys.LOAD_AUTOMATON), skin);
        loadAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if (gameController.isSimulationRunning()) return;
                if (gameController.getAutomataController().getCurrentAutomaton().isChangesPending()) {
                    showConfirmationDialog(StageManager.this::saveAutomatonClicked,
                            StageManager.this::loadAutomatonClicked,
                            getBundle().get(LocalizationKeys.AUTOMATON_CONFIRM_MESSAGE));
                } else {
                    loadAutomatonClicked();
                }
            }
        });
        automataNorth.add(loadAutomatonButton).expandX();

        //TODO add buttons for next/previous automaton

        return automataNorth;
    }

    private void newAutomatonClicked() {
        gameController.getAutomataController().addNewAutomaton();
    }

    private void saveAutomatonClicked() {
        if (gameController.getAutomataController().getCurrentAutomaton().getNumberOfState() == 0) {
            return;
        }
        gameController.setFileProcessor(gameController.getAutomataController()::saveAutomaton);
        fileChooser.setMode(FileChooser.Mode.SAVE);
        fileChooser.setFileTypeFilter(serTypeFilter);
        showFileChooser();
    }

    private void loadAutomatonClicked() {
        gameController.setFileProcessor(gameController.getAutomataController()::loadAutomaton);
        fileChooser.setMode(FileChooser.Mode.OPEN);
        fileChooser.setFileTypeFilter(serTypeFilter);
        showFileChooser();
    }

    private Table rebuildLevelNorth() {
        Table levelNorth = new Table();
        levelNorth.setDebug(prefs.debug);

        blockTypeSelectBox = new SelectBox<>(skin);
        BlockLabel[] array = {BlockLabel.EMPTY, BlockLabel.WALL, BlockLabel.START, BlockLabel.GOAL};
        blockTypeSelectBox.setItems(array);
        blockTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
            }
        });
        levelNorth.add(blockTypeSelectBox).expandX();

        blockDirectionSelectBox = new SelectBox<>(skin);
        blockDirectionSelectBox.setItems(Direction.values());
        blockDirectionSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
            }
        });
        levelNorth.add(blockDirectionSelectBox).expandX();

        if (gameRenderer.isCustomPlay()) {
            newLevelButton = new TextButton(getBundle().get(LocalizationKeys.NEW_LEVEL), skin);
            newLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (gameController.isSimulationRunning()) return;
                    if (gameController.getLevelController().getCurrentLevel().isChangesPending()) {
                        showConfirmationDialog(StageManager.this::saveLevelClicked,
                                StageManager.this::newLevelClicked, getBundle().get(LocalizationKeys.LEVEL_CONFIRM_MESSAGE));
                    } else {
                        newLevelClicked();
                    }
                }
            });
            levelNorth.add(newLevelButton).expandX();

            saveLevelButton = new TextButton(getBundle().get(LocalizationKeys.SAVE_LEVEL), skin);
            saveLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (gameController.isSimulationRunning()) return;
                    saveLevelClicked();
                }
            });
            levelNorth.add(saveLevelButton).expandX();

            loadLevelButton = new TextButton(getBundle().get(LocalizationKeys.LOAD_LEVEL), skin);
            loadLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (gameController.isSimulationRunning()) return;
                    if (gameController.getLevelController().getCurrentLevel().isChangesPending()) {
                        showConfirmationDialog(StageManager.this::saveLevelClicked,
                                StageManager.this::loadLevelClicked, getBundle().get(LocalizationKeys.LEVEL_CONFIRM_MESSAGE));
                    } else {
                        loadLevelClicked();
                    }
                }
            });
            levelNorth.add(loadLevelButton).expandX();
        }

        //TODO add buttons for next/previous levels

        fpsLabel = new Label("", skin);
        levelNorth.add(fpsLabel).right();

        return levelNorth;
    }

    private void newLevelClicked() {
        levelDimensionTable.setVisible(true);
        showConfirmationDialog(StageManager.this::createNewLevel, null,
                getBundle().get(LocalizationKeys.NEW_LEVEL_CONFIRM_MESSAGE),
                () -> levelDimensionTable.setVisible(false));
    }

    private void createNewLevel() {
        try {
            int width = Integer.parseInt(levelWidthTextField.getText().trim());
            int height = Integer.parseInt(levelHeightTextField.getText().trim());
            gameController.getLevelController().createNewLevel(width, height);
        } catch (NumberFormatException exc) {
            Gdx.app.debug(TAG, "invalid dimensions "
                    + levelWidthTextField.getText() + " " + levelHeightTextField.getText());
            return;
        }
    }

    private void saveLevelClicked() {
        gameController.setFileProcessor(gameController.getLevelController()::saveLevel);
        fileChooser.setMode(FileChooser.Mode.SAVE);
        fileChooser.setFileTypeFilter(pngTypeFilter);
        showFileChooser();
    }

    private void loadLevelClicked() {
        gameController.setFileProcessor(gameController.getLevelController()::loadLevel);
        fileChooser.setMode(FileChooser.Mode.OPEN);
        fileChooser.setFileTypeFilter(pngTypeFilter);
        showFileChooser();
    }

    private Table rebuildAutomataSouth() {
        Table automataSouth = new Table();
        automataSouth.setDebug(prefs.debug);

        automatonButtonGroup = new ButtonGroup();
        selectionButton = new TextButton(getBundle().get(LocalizationKeys.SELECT_STATE), skin, "toggle");
        automatonButtonGroup.add(selectionButton);
        automataSouth.add(selectionButton).expandX();

        createStateButton = new TextButton(getBundle().get(LocalizationKeys.ADD_STATE), skin, "toggle");
        automatonButtonGroup.add(createStateButton);
        automataSouth.add(createStateButton).expandX();

        deleteStateButton = new TextButton(getBundle().get(LocalizationKeys.DELETE_STATE), skin, "toggle");
        automatonButtonGroup.add(deleteStateButton);
        automataSouth.add(deleteStateButton).expandX();

        createTransitionButton = new TextButton(getBundle().get(LocalizationKeys.CREATE_TRANSITION), skin, "toggle");
        automatonButtonGroup.add(createTransitionButton);
        automataSouth.add(createTransitionButton).expandX();

        deleteTransitionButton = new TextButton(getBundle().get(LocalizationKeys.DELETE_TRANSITION), skin, "toggle");
        automatonButtonGroup.add(deleteTransitionButton);
        automataSouth.add(deleteTransitionButton).expandX();

        setStartStateButton = new TextButton(getBundle().get(LocalizationKeys.SET_START), skin, "toggle");
        automatonButtonGroup.add(setStartStateButton);
        automataSouth.add(setStartStateButton).expandX();

//        setGoalStateButton = new TextButton("set\ngoal", skin, "toggle");
//        automatonButtonGroup.add(setGoalStateButton);
//        automataSouth.add(setGoalStateButton).expandX();

        return automataSouth;
    }

    private Table rebuildLevelSouth() {
        Table levelSouth = new Table();
        levelSouth.setDebug(prefs.debug);

        Table simulationTable = new Table();
        simulationTable.setDebug(prefs.debug);
        levelSouth.add(simulationTable).expandX();

        startSimulationButton = new TextButton(getBundle().get(LocalizationKeys.START_SIM_BTN_TEXT), skin);
        startSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.toggleSimulationStarted();
            }
        });
        simulationTable.add(startSimulationButton).pad(3.0f).expandX();

        pauseSimulationButton = new TextButton(getBundle().get(LocalizationKeys.PAUSE_SIM_BTN_TEXT), skin);
        pauseSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.toggleSimulationPaused();
            }
        });
        simulationTable.add(pauseSimulationButton).pad(3.0f).expandX();

        simulationSpeedSlider = new Slider(1.0f, 10.0f, 1.0f, false, skin);
        simulationSpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setSimulationSpeed(((Slider)actor).getValue());
            }
        });
        simulationTable.add(simulationSpeedSlider).expandX();

        Table editTable = new Table();
        editTable.setDebug(prefs.debug);
        levelSouth.add(editTable).expandX();

        levelButtonGroup = new ButtonGroup();

        selectBlockButton = new TextButton(getBundle().get(LocalizationKeys.SELECT_BLOCK), skin, "toggle");
        levelButtonGroup.add(selectBlockButton);
        editTable.add(selectBlockButton).expandX();

        if (gameRenderer.isCustomPlay()) {
            paintBlockButton = new TextButton(getBundle().get(LocalizationKeys.PAINT_BLOCK), skin, "toggle");
            levelButtonGroup.add(paintBlockButton);
            editTable.add(paintBlockButton).expandX();
        }

        return levelSouth;
    }

    private void initFileChooser() {
        VisUI.load();
        FileChooser.setDefaultPrefsName("hr.fer.lukasuman.game.filechooser");
        fileChooser = new FileChooser(FileChooser.Mode.OPEN);
        fileChooser.setSelectionMode(FileChooser.SelectionMode.FILES_AND_DIRECTORIES);

        serTypeFilter = new FileTypeFilter(false); //allow "All Types" mode where all files are shown
        serTypeFilter.addRule("Java serializable files (*.ser)", "ser");

        pngTypeFilter = new FileTypeFilter(false);
        pngTypeFilter.addRule("PNG level files (*.png)", "png");

        fileChooser.setCenterOnAdd(true);
        fileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void canceled() {
                gameController.setFileProcessor(null);
                Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
            }

            @Override
            public void selected (Array<FileHandle> file) {
                gameController.getFileProcessor().processFile(file.first());
                gameController.setFileProcessor(null);
                Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
            }
        });
    }

    public void showFileChooser() {
        fullStage.addActor(fileChooser.fadeIn());
        Gdx.input.setInputProcessor(fullStage);
    }

    private void initConfirmationDialog() {
        //TODO fix dialog size/position when resizing the screen
        confirmationDialog = new Dialog(getBundle().get(LocalizationKeys.CONFIRM), skin) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * Constants.DIALOG_WIDTH_FACTOR;
            }

            @Override
            public float getPrefHeight() {
                return Gdx.graphics.getWidth() * Constants.DIALOG_HEIGHT_FACTOR;
            }
        };
        confirmationDialog.setModal(true);
        confirmationDialog.setMovable(false);
        confirmationDialog.setResizable(false);

        Table contentTable = confirmationDialog.getContentTable();
        Value fullWidthValue = Value.percentWidth(1.0f, confirmationDialog);

        confirmationDialogLabel = new Label("", skin);
        confirmationDialogLabel.setWrap(true);
        confirmationDialogLabel.setAlignment(Align.center);
        contentTable.add(confirmationDialogLabel).width(fullWidthValue);
        contentTable.row();
        levelDimensionTable = new Table();
        levelDimensionTable.setVisible(false);
        contentTable.add(levelDimensionTable).width(fullWidthValue);

        levelDimensionTable.add(new Label(getBundle().get(LocalizationKeys.LEVEL_WIDTH) + ": ", skin));
        levelWidthTextField = new TextField("", skin);
        levelWidthTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        levelWidthTextField.setMaxLength(3);
        levelDimensionTable.add(levelWidthTextField);
        levelDimensionTable.add(new Label(getBundle().get(LocalizationKeys.BLOCKS), skin));
        levelDimensionTable.row();
        levelDimensionTable.add(new Label(getBundle().get(LocalizationKeys.LEVEL_HEIGHT) + ": ", skin));
        levelHeightTextField = new TextField("", skin);
        levelHeightTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        levelHeightTextField.setMaxLength(3);
        levelDimensionTable.add(levelHeightTextField);
        levelDimensionTable.add(new Label(getBundle().get(LocalizationKeys.BLOCKS), skin));

        Stack buttonStack = new Stack();
        confirmationDialog.getButtonTable().add(buttonStack);

        buttonTableYesNoCancel = new Table();
        buttonStack.add(buttonTableYesNoCancel);

        TextButton yesButton = new TextButton(getBundle().get(LocalizationKeys.YES), skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                confirmationDialogClick(yesCallback);
            }
        });
        buttonTableYesNoCancel.add(yesButton).expandX();

        TextButton noButton = new TextButton(getBundle().get(LocalizationKeys.NO), skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                confirmationDialogClick(noCallback);
            }
        });
        buttonTableYesNoCancel.add(noButton).expandX();

        TextButton cancelButton = new TextButton(getBundle().get(LocalizationKeys.CANCEL), skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                confirmationDialogClick(null);
            }
        });
        buttonTableYesNoCancel.add(cancelButton).expandX();

        buttonTableOK = new Table();
        buttonTableOK.setVisible(false);
        buttonStack.add(buttonTableOK);

        TextButton okButton = new TextButton(getBundle().get(LocalizationKeys.OK), skin);
        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmationDialog.remove();
                buttonTableOK.setVisible(false);
                buttonTableYesNoCancel.setVisible(true);
                Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
            }
        });
        buttonTableOK.add(okButton).center();
    }

    private void confirmationDialogClick(CallbackFunction callback) {
        confirmationDialog.remove();
        Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
        if (finalCallback != null) {
            finalCallback.executeCallback();
            finalCallback = null;
        }
        if (callback != null) {
            callback.executeCallback();
        }
    }

    public void showConfirmationDialog(CallbackFunction yesCallback, CallbackFunction noCallback, String message) {
        showConfirmationDialog(yesCallback, noCallback, message, null);
    }

    public void showConfirmationDialog(CallbackFunction yesCallback, CallbackFunction noCallback,
                                       String message, CallbackFunction finalCallback) {
        this.yesCallback = yesCallback;
        this.noCallback = noCallback;
        if (this.finalCallback != null) {
            finalCallback.executeCallback();
        }
        this.finalCallback = finalCallback;
        message = message == null ? "" : message;
        confirmationDialogLabel.setText(message);
        confirmationDialog.show(fullStage);
        Gdx.input.setInputProcessor(fullStage);
    }

    public void showInformation(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        confirmationDialogLabel.setText(message);
        buttonTableYesNoCancel.setVisible(false);
        buttonTableOK.setVisible(true);
        confirmationDialog.show(fullStage);
        Gdx.input.setInputProcessor(fullStage);
    }

    public void renderStages() {
        upperLeftViewport.apply();
        updateScore();
        upperLeftStage.act();
        upperLeftStage.draw();

        upperRightViewport.apply();
        if (GamePreferences.getInstance().showFpsCounter) {
            updateFpsCounter();
            fpsLabel.setVisible(true);
        } else {
            fpsLabel.setVisible(false);
        }
        upperRightStage.act();
        upperRightStage.draw();

        lowerLeftViewport.apply();
        lowerLeftStage.act();
        lowerLeftStage.draw();

        lowerRightViewport.apply();
        lowerRightStage.act();
        lowerRightStage.draw();

        fullViewport.apply();
        fullStage.act();
        fullStage.draw();
    }

    public void resize(int width, int height, int measure, int upperHeight, int lowerHeight, int paddingX, int paddingY) {
        fullViewport.update(width, height, true);
        upperLeftViewport.update(width / 2, (int)(measure / 2.0f + upperHeight), true);
        upperRightViewport.update(width / 2, (int)(measure / 2.0f + upperHeight), true);
        lowerLeftViewport.update(width / 2, (int)(measure / 2.0f + lowerHeight), true);
        lowerRightViewport.update(width / 2, (int)(measure / 2.0f + lowerHeight), true);

        fullViewport.setScreenPosition(0, 0);
        upperLeftViewport.setScreenPosition(paddingX, height / 2);
        upperRightViewport.setScreenPosition(width / 2 + paddingX, height / 2);
        lowerLeftViewport.setScreenPosition(paddingX, paddingY);
        lowerRightViewport.setScreenPosition(width / 2 + paddingX, paddingY);
    }

    @Override
    public void dispose() {
        upperLeftStage.dispose();
        upperRightStage.dispose();
        lowerLeftStage.dispose();
        lowerRightStage.dispose();
        fullStage.dispose();
    }

    private void updateScore() {
        scoreLabel.setText(getBundle().get(LocalizationKeys.STATES) + ": " + gameController.getAutomataController().getCurrentAutomaton().getStates().size());
    }

    private void updateFpsCounter() {
        int fps = Gdx.graphics.getFramesPerSecond();
        if (fps >= 45) {
            fpsLabel.setColor(0, 1, 0, 1);
        } else if (fps >= 30) {
            fpsLabel.setColor(1, 1, 0, 1);
        } else {
            fpsLabel.setColor(1, 0, 0, 1);
        }
        fpsLabel.setText("FPS: " + fps);
    }

    public Stage getUpperLeftStage() {
        return upperLeftStage;
    }

    public Stage getUpperRightStage() {
        return upperRightStage;
    }

    public Stage getLowerLeftStage() {
        return lowerLeftStage;
    }

    public Stage getLowerRightStage() {
        return lowerRightStage;
    }

    public Stage getFullStage() {
        return fullStage;
    }

    public SelectBox<AutomatonAction> getActionSelectBox() {
        return actionSelectBox;
    }

    public SelectBox<BlockLabel> getTransitionSelectBox() {
        return transitionSelectBox;
    }

    public SelectBox<BlockLabel> getBlockTypeSelectBox() {
        return blockTypeSelectBox;
    }

    public SelectBox<Direction> getBlockDirectionSelectBox() {
        return blockDirectionSelectBox;
    }

    public ButtonGroup getAutomatonButtonGroup() {
        return automatonButtonGroup;
    }

    public TextButton getSelectionButton() {
        return selectionButton;
    }

    public TextButton getCreateStateButton() {
        return createStateButton;
    }

    public TextButton getDeleteStateButton() {
        return deleteStateButton;
    }

    public TextButton getCreateTransitionButton() {
        return createTransitionButton;
    }

    public TextButton getDeleteTransitionButton() {
        return deleteTransitionButton;
    }

    public TextButton getSetStartStateButton() {
        return setStartStateButton;
    }

    public ButtonGroup getLevelButtonGroup() {
        return levelButtonGroup;
    }

    public TextButton getSelectBlockButton() {
        return selectBlockButton;
    }

    public TextButton getPaintBlockButton() {
        return paintBlockButton;
    }

    public TextButton getStartSimulationButton() {
        return startSimulationButton;
    }

    public TextButton getPauseSimulationButton() {
        return pauseSimulationButton;
    }

    public Slider getSimulationSpeedSlider() {
        return simulationSpeedSlider;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }
}
