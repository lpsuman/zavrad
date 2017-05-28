package hr.fer.lukasuman.game.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.*;
import com.kotcrab.vis.ui.VisUI;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.automata.*;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.*;
import hr.fer.lukasuman.game.screens.GameScreen;

import java.util.Map;

public class GameRenderer implements Disposable {
    private static final String TAG = GameRenderer.class.getName();

    private GameController gameController;
    private GameScreen gameScreen;
    private boolean isCustomPlay;
    private StageManager stageManager;

    private OrthographicCamera fullCamera;
    private OrthographicCamera leftCamera;
    private OrthographicCamera rightCamera;

    private ScreenViewport fullCameraViewport;
    private ScreenViewport leftViewport;
    private ScreenViewport rightViewport;

    private Texture stateTexture;
    private Texture runningStateTexture;
    private SpriteBatch batch;
    private Sprite playerSprite;
    private Sprite normalStateOverlay;
    private Sprite selectedStateOverlay;
    private Sprite stateCirclesSprite;
    private ShapeRenderer transitionRenderer;
    private GlyphLayout glyphLayout;
    private BitmapFont stateFont;
    private AutomatonTransition tempTransition;

    public GameRenderer (GameController gameController, GameScreen gameScreen, boolean isCustomPlay) {
        this.gameController = gameController;
        this.gameScreen = gameScreen;
        this.isCustomPlay = isCustomPlay;
        init();
    }

    private void init () {
        initCameras();

        batch = new SpriteBatch();
        stateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_STATE_TEXTURE);
//        selectedStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_SELECTED_STATE_TEXTURE);
        runningStateTexture = Assets.getInstance().getAssetManager().get(Constants.AUTOMATA_RUNNING_STATE_TEXTURE);
        playerSprite = new Sprite((Texture) Assets.getInstance().getAssetManager().get(Constants.PLAYER_TEXTURE));
        transitionRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();

        stateCirclesSprite = new Sprite(Assets.getInstance().getStartStateCircle());
        stateCirclesSprite.setSize(Constants.START_STATE_CIRCLE_SIZE, Constants.START_STATE_CIRCLE_SIZE);
        normalStateOverlay = new Sprite(Assets.getInstance().getNormalStateBorder());
        normalStateOverlay.setSize(Constants.STATE_SIZE, Constants.STATE_SIZE);
        normalStateOverlay.setOriginCenter();
        selectedStateOverlay = new Sprite(Assets.getInstance().getSelectedStateBorder());
        selectedStateOverlay.setSize(Constants.STATE_SIZE, Constants.STATE_SIZE);
        selectedStateOverlay.setOriginCenter();

//        stateFont = new BitmapFont();
//        stateFont.getData().setScale(Constants.STATE_FONT_SCALE);
//        stateFont.setColor(0.0f, 0.0f, 0.0f, 1.0f);
//        stateFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        stateFont = Assets.getInstance().getFonts().defaultBig;

        stageManager = new StageManager(this);

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initCameras() {
        fullCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        leftCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        rightCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);

        fullCameraViewport = new ScreenViewport(fullCamera);
        leftViewport = new ScreenViewport(leftCamera);
        rightViewport = new ScreenViewport(rightCamera);

        fullCamera.position.set(0, 0, 0);
        fullCamera.update();
        leftCamera.position.set(0, 0, 0);
        leftCamera.update();
        rightCamera.position.set(0, 0, 0);
        rightCamera.update();
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
            if (state.equals(automaton.getCurrentState())
                    || (!gameController.isSimulationStarted() && !state.isValid())) {
                sprite.setTexture(runningStateTexture);
            }
            sprite.draw(batch);
            sprite.setTexture(stateTexture);
            if (state.equals(gameController.getSelectedState()) && !gameController.isSimulationStarted()) {
                selectedStateOverlay.setPosition(state.getX() - Constants.STATE_SIZE / 2.0f,
                        state.getY() - Constants.STATE_SIZE / 2.0f);
                selectedStateOverlay.draw(batch);
            } else {
                normalStateOverlay.setPosition(state.getX() - Constants.STATE_SIZE / 2.0f,
                        state.getY() - Constants.STATE_SIZE / 2.0f);
                normalStateOverlay.draw(batch);
            }
            String text = state.getLabel();
            glyphLayout.setText(stateFont, text);
            stateFont.draw(batch, text, state.getX() - glyphLayout.width / 2.0f, state.getY() + glyphLayout.height);
            text = "[" + state.getAction().toString() + "]";
            glyphLayout.setText(stateFont, text);
            stateFont.draw(batch, text, state.getX() - glyphLayout.width / 2.0f, state.getY() - Constants.STATE_LABEL_PADDING);
        }

        AutomatonState startState = automaton.getStartState();
        if (startState != null) {
            stateCirclesSprite.setPosition(startState.getX() - Constants.START_STATE_CIRCLE_SIZE / 2.0f,
                    startState.getY() - Constants.START_STATE_CIRCLE_SIZE / 2.0f);
            stateCirclesSprite.draw(batch);
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
        if (gameController.isSimulationStarted()) {
            playerSprite.setSize(level.getBlockSize(), level.getBlockSize());
            float halfWidth = playerSprite.getWidth() / 2.0f;
            float halfHeight = playerSprite.getHeight() / 2.0f;
            playerSprite.setOrigin(halfWidth, halfHeight);
            GridPoint2 currentPlayerPositon = level.getCurrentPosition();
            if (currentPlayerPositon != null && level.isPositionWithinLevel(currentPlayerPositon)) {
                Vector2 playerPos = level.calcPos(currentPlayerPositon.x, currentPlayerPositon.y);
                playerSprite.setPosition(playerPos.x - halfWidth, playerPos.y - halfHeight);
                playerSprite.setRotation(-90.0f * level.getCurrentDirection().getDegrees());
                playerSprite.draw(batch);
            }
        }
        batch.end();
    }

    private void renderTransitionLines() {
        Gdx.gl.glLineWidth(Constants.TRANSITIONS_LINE_WIDTH);
        transitionRenderer.setProjectionMatrix(leftCamera.combined);
        transitionRenderer.begin(ShapeRenderer.ShapeType.Line);
        transitionRenderer.setColor(Constants.TRANSITION_COLOR);

        for (AutomatonTransition transition : gameController.getAutomataController().getCurrentAutomaton().getTransitionSet()) {
            if (transition.equals(gameController.getSelectedTransition()) && !gameController.isSimulationStarted()) {
                transitionRenderer.setColor(Constants.SELECTED_TRANSITION_COLOR);
                transition.drawLines(transitionRenderer);
                transitionRenderer.setColor(Constants.TRANSITION_COLOR);
            } else {
                transition.drawLines(transitionRenderer);
            }
        }
        if (tempTransition != null) {
            tempTransition.drawLines(transitionRenderer);
        }

        transitionRenderer.end();
        Gdx.gl.glLineWidth(1);

        BitmapFont font = Assets.getInstance().getFonts().defaultNormal;
        Matrix4 oldTransformMatrix = batch.getTransformMatrix().cpy();
        for (AutomatonTransition transition : gameController.getAutomataController().getCurrentAutomaton().getTransitionSet()) {
            transition.drawLabels(batch, font);
        }
        if (tempTransition != null) {
            tempTransition.drawLabels(batch, font);
        }
        batch.setTransformMatrix(oldTransformMatrix);
    }

    private void renderGUI() {
        stageManager.renderStages();

        if (GamePreferences.getInstance().debug) {
            fullCameraViewport.apply();
            Gdx.gl.glLineWidth(3);
            transitionRenderer.setProjectionMatrix(fullCamera.combined);
            transitionRenderer.begin(ShapeRenderer.ShapeType.Line);
            transitionRenderer.setColor(0.0f, 0.0f, 0.0f, 1.0f);
            transitionRenderer.line(0.0f, fullCameraViewport.getScreenHeight() / 2.0f,
                    fullCameraViewport.getWorldWidth(), fullCameraViewport.getScreenHeight() / 2.0f);
            transitionRenderer.line(0.0f, Gdx.graphics.getHeight() / 2.0f, Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight() / 2.0f);
            transitionRenderer.end();
            Gdx.gl.glLineWidth(1);
        }
    }

    public void resize(int width, int height) {
        int viewportHeight = (int)(height / (1.0f + Constants.UPPER_BORDER_RATIO + Constants.LOWER_BORDER_RATIO));
        int upperHeight = (int)(height - viewportHeight * (1.0f + Constants.LOWER_BORDER_RATIO));
        int lowerHeight = (int)(height - viewportHeight * (1.0f + Constants.UPPER_BORDER_RATIO));
        int measure = (int)Math.min(width / 2.0f, viewportHeight);
        int paddingX = ((int)(width / 2.0f) - measure) / 2;
        int paddingY = (viewportHeight - measure) / 2;

//        Gdx.app.debug(TAG, String.format("\nwidth: %d\nheight: %d\nviewportHeight: %d\nupperHeight: %d\nlowerHeight:" +
//                " %d\nmeasure: %d\npaddingX: %d\npaddingY: %d", width, height, viewportHeight, upperHeight,
//                lowerHeight, measure, paddingX, paddingY));

        leftViewport.update(measure, measure);
        leftViewport.setScreenPosition(paddingX, lowerHeight + paddingY);
        rightViewport.update(measure, measure);
        rightViewport.setScreenPosition(width / 2 + paddingX, lowerHeight + paddingY);
        fullCameraViewport.update(width, height, true);
        fullCameraViewport.setScreenPosition(0, 0);

        leftCamera.zoom = Constants.VIEWPORT_WIDTH / measure;

        stageManager.resize(width, height, measure, upperHeight, lowerHeight, paddingX, paddingY);

        gameController.getAutomataController().getCurrentAutomaton().recalculateTransitions();
    }

    @Override
    public void dispose() {
        stageManager.dispose();
        transitionRenderer.dispose();
        batch.dispose();
        VisUI.dispose();
    }

    public AutomatonTransition getTempTransition() {
        return tempTransition;
    }

    public void setTempTransition(AutomatonTransition tempTransition) {
        this.tempTransition = tempTransition;
    }

    public ScreenViewport getLeftViewport() {
        return leftViewport;
    }

    public ScreenViewport getRightViewport() {
        return rightViewport;
    }

    public StageManager getStageManager() {
        return stageManager;
    }

    public GameController getGameController() {
        return gameController;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public boolean isCustomPlay() {
        return isCustomPlay;
    }
}
