package hr.fer.lukasuman.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileTypeFilter;
import com.kotcrab.vis.ui.widget.file.SingleFileChooserListener;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.*;
import hr.fer.lukasuman.game.screens.GameScreen;

import java.util.Map;
import java.util.Set;

public class GameRenderer implements Disposable {

    private static final String TAG = GameRenderer.class.getName();

    private GameController gameController;
    private GameScreen gameScreen;

    private OrthographicCamera fullCamera;
    private OrthographicCamera leftCamera;
    private OrthographicCamera rightCamera;

    private ScreenViewport leftViewport;
    private ScreenViewport rightViewport;

    private Texture stateTexture;
    private Texture selectedStateTexture;
    private Texture runningStateTexture;
    private SpriteBatch batch;
    private Sprite playerSprite;
    private ShapeRenderer transitionRenderer;
    private GlyphLayout glyphLayout;

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

    private Skin skin;

    private Label scoreLabel;
    private SelectBox<AutomatonAction> actionSelectBox;
    private SelectBox<String> transitionSelectBox;
    private TextButton newAutomatonButton;
    private TextButton saveAutomatonButton;
    private TextButton loadAutomatonButton;

    private SelectBox<String> blockTypeSelectBox;
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
    private TextButton setGoalStateButton;

    private ButtonGroup levelButtonGroup;
    private TextButton selectBlockButton;
    private TextButton paintBlockButton;
    private TextButton startSimulationButton;
    private TextButton pauseSimulationButton;
    private Slider simulationSpeedSlider;

    private FileChooser fileChooser;
    private FileTypeFilter serTypeFilter;
    private FileTypeFilter pngTypeFilter;

    private Dialog automatonDialog;
    private Dialog levelDialog;

    private GamePreferences prefs;

    public GameRenderer (GameController gameController, GameScreen gameScreen) {
        this.gameController = gameController;
        this.gameScreen = gameScreen;
        init();
    }

    private void init () {
        initCameras();

        batch = new SpriteBatch();
        stateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_STATE_TEXTURE);
        selectedStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_SELECTED_STATE_TEXTURE);
        runningStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_RUNNING_STATE_TEXTURE);
        playerSprite = new Sprite((Texture) Assets.getInstance().getAssetManager().get(Constants.PLAYER_TEXTURE));
        transitionRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();

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

        initAutomatonDialog();
        initLevelDialog();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initCameras() {
        fullCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        leftCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        rightCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        leftViewport = new ScreenViewport(leftCamera);
        rightViewport = new ScreenViewport(rightCamera);

        gameController.setFullCamera(fullCamera);
        gameController.setAutomataCamera(leftCamera);
        gameController.setLevelCamera(rightCamera);

        fullCamera.position.set(0, 0, 0);
        fullCamera.update();
        leftCamera.position.set(0, 0, 0);
        leftCamera.update();
        rightCamera.position.set(0, 0, 0);
        rightCamera.update();

//        Gdx.app.debug(TAG, "left camera center at " + leftCamera.unproject(new Vector3(0.0f, 0.0f, 0.0f)));
//        Gdx.app.debug(TAG, "right camera center at " + rightCamera.unproject(new Vector3(0.0f, 0.0f, 0.0f)));
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

        float lowerRatio = Constants.UPPER_BORDER_RATIO * 2.0f;
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
        scoreLabel = new Label("0", skin, Constants.DEFAULT_FONT_NAME, Color.WHITE);
        automataNorth.add(scoreLabel).expandX();

        actionSelectBox = new SelectBox<>(skin);
        actionSelectBox.setItems(AutomatonAction.MOVE_FORWARD, AutomatonAction.ROTATE_LEFT, AutomatonAction.ROTATE_RIGHT);
        actionSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
                AutomatonState selectedState = gameController.getSelectedState();
                if (selectedState != null) {
                    selectedState.setAction(actionSelectBox.getSelected());
                }
            }
        });
        automataNorth.add(actionSelectBox).expandX();

        transitionSelectBox = new SelectBox<String>(skin);
        transitionSelectBox.setItems(EmptyBlock.LABEL, WallBlock.LABEL, StartBlock.LABEL, GoalBlock.LABEL);
        transitionSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
                AutomatonTransition selectedTransition = gameController.getSelectedTransition();
                if (selectedTransition != null) {
                    selectedTransition.setLabel(transitionSelectBox.getSelected());
                }
            }
        });
        automataNorth.add(transitionSelectBox).expandX();

        newAutomatonButton = new TextButton("new\nautomaton", skin);
        newAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {

            }
        });
        automataNorth.add(newAutomatonButton).expandX();

        saveAutomatonButton = new TextButton("save\nautomaton", skin);
        saveAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setFileProcessor(gameController.getAutomataController()::saveAutomaton);
                fileChooser.setMode(FileChooser.Mode.SAVE);
                fileChooser.setFileTypeFilter(serTypeFilter);
                showFileChooser();
            }
        });
        automataNorth.add(saveAutomatonButton).expandX();

        loadAutomatonButton = new TextButton("load\nautomaton", skin);
        loadAutomatonButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setFileProcessor(gameController.getAutomataController()::loadAutomaton);
                fileChooser.setMode(FileChooser.Mode.OPEN);
                fileChooser.setFileTypeFilter(serTypeFilter);
                showFileChooser();
            }
        });
        automataNorth.add(loadAutomatonButton).expandX();

        return automataNorth;
    }

    private Table rebuildLevelNorth() {
        Table levelNorth = new Table();
        levelNorth.setDebug(prefs.debug);

        blockTypeSelectBox = new SelectBox<>(skin);
        Set<String> blockTypes = BlockFactory.getBlockTypes();
        blockTypeSelectBox.setItems(blockTypes.toArray(new String[blockTypes.size()]));
        blockTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameController.setIgnoreNextClick(true);
            }
        });
        levelNorth.add(blockTypeSelectBox).expandX();

        saveLevelButton = new TextButton("save level", skin);
        saveLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setFileProcessor(gameController.getLevelController()::saveLevel);
                fileChooser.setMode(FileChooser.Mode.SAVE);
                fileChooser.setFileTypeFilter(pngTypeFilter);
                showFileChooser();
            }
        });
        levelNorth.add(saveLevelButton).expandX();

        loadLevelButton = new TextButton("load level", skin);
        loadLevelButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setFileProcessor(gameController.getLevelController()::loadLevel);
                fileChooser.setMode(FileChooser.Mode.OPEN);
                fileChooser.setFileTypeFilter(pngTypeFilter);
                showFileChooser();
            }
        });
        levelNorth.add(loadLevelButton).expandX();

        fpsLabel = new Label("FPS: 60", skin);
        levelNorth.add(fpsLabel).right();

        return levelNorth;
    }

    private Table rebuildAutomataSouth() {
        Table automataSouth = new Table();
        automataSouth.setDebug(prefs.debug);

        automatonButtonGroup = new ButtonGroup();
        selectionButton = new TextButton("select\nstate", skin, "toggle");
        automatonButtonGroup.add(selectionButton);
        automataSouth.add(selectionButton).expandX();

        createStateButton = new TextButton("add\nstate", skin, "toggle");
        automatonButtonGroup.add(createStateButton);
        automataSouth.add(createStateButton).expandX();

        deleteStateButton = new TextButton("delete\nstate", skin, "toggle");
        automatonButtonGroup.add(deleteStateButton);
        automataSouth.add(deleteStateButton).expandX();

        createTransitionButton = new TextButton("create\ntransition", skin, "toggle");
        automatonButtonGroup.add(createTransitionButton);
        automataSouth.add(createTransitionButton).expandX();

        deleteTransitionButton = new TextButton("delete\ntransition", skin, "toggle");
        automatonButtonGroup.add(deleteTransitionButton);
        automataSouth.add(deleteTransitionButton).expandX();

        setStartStateButton = new TextButton("set\nstart", skin, "toggle");
        automatonButtonGroup.add(setStartStateButton);
        automataSouth.add(setStartStateButton).expandX();

        setGoalStateButton = new TextButton("set\ngoal", skin, "toggle");
        automatonButtonGroup.add(setGoalStateButton);
        automataSouth.add(setGoalStateButton).expandX();

        return automataSouth;
    }

    private Table rebuildLevelSouth() {
        Table levelSouth = new Table();
        levelSouth.setDebug(prefs.debug);

        Table simulationTable = new Table();
        simulationTable.setDebug(prefs.debug);
        levelSouth.add(simulationTable).uniformX();

        startSimulationButton = new TextButton(Constants.START_SIM_BTN_TEXT, skin);
        startSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.startSimulation();
            }
        });
        simulationTable.add(startSimulationButton).expandX();

        pauseSimulationButton = new TextButton(Constants.PAUSE_SIM_BTN_TEXT, skin);
        pauseSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.pauseSimulation();
            }
        });
        simulationTable.add(pauseSimulationButton).expandX();

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
        levelSouth.add(editTable).uniformX();

        levelButtonGroup = new ButtonGroup();

        selectBlockButton = new TextButton("select\nblock", skin, "toggle");
        levelButtonGroup.add(selectBlockButton);
        editTable.add(selectBlockButton).expandX();

        paintBlockButton = new TextButton("paint\nblock", skin, "toggle");
        levelButtonGroup.add(paintBlockButton);
        editTable.add(paintBlockButton).expandX();

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
        fileChooser.setListener(new SingleFileChooserListener() {
            @Override
            protected void selected(FileHandle file) {
                gameController.getFileProcessor().processFile(file);
                gameController.setFileProcessor(null);
                Gdx.input.setInputProcessor(gameScreen.getInputProcessor());
            }
        });
    }

    public void showFileChooser() {
        fullStage.addActor(fileChooser.fadeIn());
        Gdx.input.setInputProcessor(fullStage);
    }

    private void saveAutomaton() {
        gameController.setFileProcessor(gameController.getAutomataController()::saveAutomaton);
        fileChooser.setMode(FileChooser.Mode.SAVE);
        fileChooser.setFileTypeFilter(serTypeFilter);
        showFileChooser();
    }

    private void initAutomatonDialog() {
        automatonDialog = new Dialog("Automaton not saved!", skin) {
            @Override
            public float getPrefWidth() {
                return Gdx.graphics.getWidth() * Constants.DIALOG_WIDTH_FACTOR;
            }

            @Override
            public float getPrefHeight() {
                 return Gdx.graphics.getWidth() * Constants.DIALOG_HEIGHT_FACTOR;
            }
        };

        automatonDialog.setModal(true);
        automatonDialog.setMovable(false);
        automatonDialog.setResizable(false);

        Label label = new Label("Would you like to save the current automaton?", skin);
        automatonDialog.getContentTable().add(label);

        Table buttonTable = new Table();

        TextButton yesButton = new TextButton("Yes", skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                automatonDialog.remove();
                saveAutomaton();
            }
        });
        buttonTable.add(yesButton).expandX();

        TextButton noButton = new TextButton("No", skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                automatonDialog.remove();
            }
        });
        buttonTable.add(noButton).expandX();

        TextButton cancelButton = new TextButton("Cancel", skin);
        //TODO automaton dialog
    }

    private void initLevelDialog() {
        //TODO level dialog
    }

    public void render () {
        renderAutomata(batch);
        renderLevel(batch);
        renderGUI();
    }

    private void renderAutomata(SpriteBatch batch) {
        leftViewport.apply();
        batch.setProjectionMatrix(leftCamera.combined);
        renderTransitionLines();
        DrawableAutomaton automaton = gameController.getAutomataController().getCurrentAutomaton();
        batch.begin();
        for (Map.Entry<AutomatonState, Sprite> entry : automaton.getStateSprites().entrySet()) {
            AutomatonState state = entry.getKey();
            Sprite sprite = entry.getValue();
            if (state.equals(automaton.getCurrentState())) {
                sprite.setTexture(runningStateTexture);
            } else if (state.equals(gameController.getSelectedState())){
                sprite.setTexture(selectedStateTexture);
            }
            sprite.draw(batch);
            sprite.setTexture(stateTexture);

            BitmapFont font = Assets.getInstance().getFonts().defaultSmall;
            String text = state.getAction().toString();
            glyphLayout.setText(font, text);
            font.draw(batch, text, state.getX() - glyphLayout.width / 2.0f, state.getY() + glyphLayout.height / 2.0f);
        }
        batch.end();
    }

    private void renderLevel(SpriteBatch batch) {
        rightViewport.apply();
        batch.setProjectionMatrix(rightCamera.combined);
        Level level = gameController.getLevelController().getCurrentLevel();
        level.updateSprites(rightCamera);
        AbstractBlock[][] blocks = level.getBlocks();
        batch.begin();
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                blocks[x][y].render(batch);
            }
        }
        playerSprite.setSize(level.getBlockSize(), level.getBlockSize());
        float halfWidth = playerSprite.getWidth() / 2.0f;
        float halfHeight = playerSprite.getHeight() / 2.0f;
        playerSprite.setOrigin(halfWidth, halfHeight);
        Vector2 playerPos = level.calcPos(level.getCurrentPosition().x, level.getCurrentPosition().y);
        playerSprite.setPosition(playerPos.x - halfWidth, playerPos.y - halfHeight);
        playerSprite.setRotation(-90.0f * level.getCurrentDirection().getDegrees());
        playerSprite.draw(batch);
        batch.end();
    }

    private void renderTransitionLines() {
        Gdx.gl.glLineWidth(Constants.TRANSITIONS_LINE_WIDTH);

        transitionRenderer.setProjectionMatrix(leftCamera.combined);
        transitionRenderer.begin(ShapeRenderer.ShapeType.Line);
        transitionRenderer.setColor(Constants.TRANSITION_COLOR);

        for (AutomatonTransition transition : gameController.getAutomataController().getCurrentAutomaton().getTransitionSet()) {
            if (transition.equals(gameController.getSelectedTransition())) {
                transitionRenderer.setColor(Constants.SELECTED_TRANSITION_COLOR);
                transition.draw(transitionRenderer);
                transitionRenderer.setColor(Constants.TRANSITION_COLOR);
            }
            transition.draw(transitionRenderer);
        }

        transitionRenderer.end();
        Gdx.gl.glLineWidth(1);

        BitmapFont font = Assets.getInstance().getFonts().defaultSmall;
        batch.begin();
        for (AutomatonTransition transition : gameController.getAutomataController().getCurrentAutomaton().getTransitionSet()) {
            String text = transition.getLabel();
            glyphLayout.setText(font, text);
            font.draw(batch, text, transition.getMiddlePoint().x - glyphLayout.width / 2.0f,
                    transition.getMiddlePoint().y + glyphLayout.height / 2.0f);
        }
        batch.end();
    }

    private void renderGUI() {
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

    private void updateScore() {
        scoreLabel.setText("states: " + gameController.getAutomataController().getCurrentAutomaton().getStates().size());
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

    public void resize (int width, int height) {
        int viewportHeight = (int)(height / (1.0f + Constants.UPPER_BORDER_RATIO + Constants.LOWER_BORDER_RATIO));
        int upperHeight = (int)(height - viewportHeight * (1.0f + Constants.LOWER_BORDER_RATIO));
        int lowerHeight = (int)(height - viewportHeight * (1.0f + Constants.UPPER_BORDER_RATIO));
        int measure = (int)Math.min(width / 2.0f, viewportHeight);
        int paddingX = ((int)(width / 2.0f) - measure) / 2;
        int paddingY = (viewportHeight - measure) / 2;

        leftViewport.update(measure, measure);
        leftViewport.setScreenPosition(paddingX, lowerHeight + paddingY);
        rightViewport.update(measure, measure);
        rightViewport.setScreenPosition(width / 2 + paddingX, lowerHeight + paddingY);

        leftCamera.zoom = Constants.VIEWPORT_WIDTH / measure;

        upperLeftViewport.update(width / 2, height / 2, true);
        upperRightViewport.update(width / 2, height / 2, true);
        lowerLeftViewport.update(width / 2, height / 2, true);
        lowerRightViewport.update(width / 2, height / 2, true);

        upperLeftViewport.setScreenPosition(0, height / 2);
        upperRightViewport.setScreenPosition(width / 2, height / 2);
        lowerLeftViewport.setScreenPosition(0, 0);
        lowerRightViewport.setScreenPosition(width / 2, 0);

        gameController.getAutomataController().getCurrentAutomaton().recalculateTransitions();

//        Gdx.app.debug(TAG, "right camera (" + rightCamera.viewportWidth + ", " + rightCamera.viewportHeight + ")");
    }

    @Override
    public void dispose () {
        batch.dispose();
        VisUI.dispose();
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

    public SelectBox<String> getTransitionSelectBox() {
        return transitionSelectBox;
    }

    public SelectBox<String> getBlockTypeSelectBox() {
        return blockTypeSelectBox;
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

    public ScreenViewport getLeftViewport() {
        return leftViewport;
    }

    public ScreenViewport getRightViewport() {
        return rightViewport;
    }

    public FileChooser getFileChooser() {
        return fileChooser;
    }
}
