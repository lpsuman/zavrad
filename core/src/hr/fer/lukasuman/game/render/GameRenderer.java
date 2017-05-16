package hr.fer.lukasuman.game.render;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.*;

import java.util.Map;

public class GameRenderer implements Disposable {

    private static final String TAG = GameRenderer.class.getName();

    private GameController gameController;

    private OrthographicCamera fullCamera;
    private OrthographicCamera leftCamera;
    private OrthographicCamera rightCamera;
    private OrthographicCamera cameraGUI;

    private ScreenViewport leftViewport;
    private ScreenViewport rightViewport;
    private ScreenViewport viewportGUI;

    private Texture stateTexture;
    private Texture selectedStateTexture;
    private Texture runningStateTexture;
    private SpriteBatch batch;
    private Sprite playerSprite;
    private ShapeRenderer transitionRenderer;
    private GlyphLayout glyphLayout;

    private Stage stage;
    private Skin skin;
    private Label scoreLabel;
    private SelectBox<AutomatonAction> actionSelectBox;
    private SelectBox<String> transitionSelectBox;
    private Label fpsLabel;
    private ButtonGroup buttonGroup;
    private TextButton selectionButton;
    private TextButton createStateButton;
    private TextButton deleteStateButton;
    private TextButton createTransitionButton;
    private TextButton deleteTransitionButton;
    private TextButton startSimulationButton;
    private TextButton pauseSimulationButton;
    private Slider simulationSpeedSlider;

    public GameRenderer (GameController gameController) {
        this.gameController = gameController;
        init();
    }

    private void init () {
        fullCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        leftCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        rightCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);

        leftViewport = new ScreenViewport(leftCamera);
        rightViewport = new ScreenViewport(rightCamera);
        viewportGUI = new ScreenViewport(cameraGUI);

        batch = new SpriteBatch();

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

        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(false); // flip y-axis
        cameraGUI.update();

        stateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_STATE_TEXTURE);
        selectedStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_SELECTED_STATE_TEXTURE);
        runningStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_RUNNING_STATE_TEXTURE);
        playerSprite = new Sprite((Texture) Assets.getInstance().getAssetManager().get(Constants.PLAYER_TEXTURE));
        transitionRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();

        stage = new Stage(viewportGUI);
        rebuildStage();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void rebuildStage () {
        skin = Assets.getInstance().getAssetManager().get(Constants.SKIN_LIBGDX_UI);

        Table table = new Table();
        table.setFillParent(true);


        table.add(rebuildAutomataNorth()).uniform().left();
        table.add(rebuildLevelNorth()).uniform().right();

        table.row();
        Label tempLabel = new Label("", skin);
        table.add(tempLabel).expand();
        table.row();

        table.add(rebuildAutomataSouth()).uniform().left();
        table.add(rebuildLevelSouth()).uniform().right();

        stage.addActor(table);
    }

    private HorizontalGroup rebuildAutomataNorth() {
        HorizontalGroup automataNorth = new HorizontalGroup();
        scoreLabel = new Label("0", skin, Constants.DEFAULT_FONT_NAME, Color.WHITE);
        automataNorth.addActor(scoreLabel);

        actionSelectBox = new SelectBox<AutomatonAction>(skin);
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
        automataNorth.addActor(actionSelectBox);

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
        automataNorth.addActor(transitionSelectBox);

        return automataNorth;
    }

    private HorizontalGroup rebuildLevelNorth() {
        HorizontalGroup levelNorth = new HorizontalGroup();

        fpsLabel = new Label("FPS: 60", skin);
        levelNorth.addActor(fpsLabel);

        return levelNorth;
    }

    private HorizontalGroup rebuildAutomataSouth() {
        HorizontalGroup automataSouth = new HorizontalGroup();

        buttonGroup = new ButtonGroup();
        selectionButton = new TextButton("select state", skin);
        buttonGroup.add(selectionButton);
        automataSouth.addActor(selectionButton);

        createStateButton = new TextButton("add state", skin);
        buttonGroup.add(createStateButton);
        automataSouth.addActor(createStateButton);

        deleteStateButton = new TextButton("delete state", skin);
        buttonGroup.add(deleteStateButton);
        automataSouth.addActor(deleteStateButton);

        createTransitionButton = new TextButton("create transition", skin);
        buttonGroup.add(createTransitionButton);
        automataSouth.addActor(createTransitionButton);

        deleteTransitionButton = new TextButton("delete transition", skin);
        buttonGroup.add(deleteTransitionButton);
        automataSouth.addActor(deleteTransitionButton);

        return automataSouth;
    }

    private HorizontalGroup rebuildLevelSouth() {
        HorizontalGroup levelSouth = new HorizontalGroup();

        startSimulationButton = new TextButton(Constants.START_SIM_BTN_TEXT, skin);
        startSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.startSimulation();
            }
        });
        levelSouth.addActor(startSimulationButton);

        pauseSimulationButton = new TextButton(Constants.PAUSE_SIM_BTN_TEXT, skin);
        pauseSimulationButton.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.pauseSimulation();
            }
        });
        levelSouth.addActor(pauseSimulationButton);

        simulationSpeedSlider = new Slider(1.0f, 10.0f, 1.0f, false, skin);
        simulationSpeedSlider.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                gameController.setSimulationSpeed(((Slider)actor).getValue());
            }
        });
        levelSouth.addActor(simulationSpeedSlider);

        return levelSouth;
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
        viewportGUI.apply();
        updateScore();
        updateFpsCounter();
        stage.draw();
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
        float viewportHeight = height / (1.0f + Constants.UPPER_BORDER_RATIO + Constants.LOWER_BORDER_RATIO);
        int borderY = (int)(height - viewportHeight * (1.0f + Constants.UPPER_BORDER_RATIO));
        int measure = (int)Math.min(width / 2.0f, viewportHeight);
        int paddingX = ((int)(width / 2.0f) - measure) / 2;
        int paddingY = ((int)viewportHeight - measure) / 2;
        leftViewport.update(measure, measure);
        leftViewport.setScreenPosition(paddingX, borderY + paddingY);
        rightViewport.update(measure, measure);
        rightViewport.setScreenPosition(width / 2 + paddingX, borderY + paddingY);

        leftCamera.zoom = Constants.VIEWPORT_WIDTH / measure;

        viewportGUI.update((int)(width * (1.0f - 2 * Constants.GUI_BORDER_FACTOR)),
                (int)(height * (1.0f - 2 * Constants.GUI_BORDER_FACTOR)), true);
        viewportGUI.setScreenPosition((int)(width * Constants.GUI_BORDER_FACTOR),
                (int)(height * Constants.GUI_BORDER_FACTOR));

        gameController.getAutomataController().getCurrentAutomaton().recalculateTransitions();

//        Gdx.app.debug(TAG, "right camera (" + rightCamera.viewportWidth + ", " + rightCamera.viewportHeight + ")");
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    public Stage getStage() {
        return stage;
    }

    public SelectBox<AutomatonAction> getActionSelectBox() {
        return actionSelectBox;
    }

    public SelectBox<String> getTransitionSelectBox() {
        return transitionSelectBox;
    }

    public ButtonGroup getButtonGroup() {
        return buttonGroup;
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

    public ScreenViewport getViewportGUI() {
        return viewportGUI;
    }
}
