package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hr.fer.lukasuman.game.automata.AutomatonState;
import hr.fer.lukasuman.game.level.Level;
import hr.fer.lukasuman.game.level.blocks.AbstractBlock;

import java.util.Map;

public class GameRenderer implements Disposable {

    private static final String TAG = GameRenderer.class.getName();

    private OrthographicCamera fullCamera;
    private OrthographicCamera leftCamera;
    private OrthographicCamera rightCamera;
    private OrthographicCamera cameraGUI;

    private ScreenViewport leftViewport;
    private ScreenViewport rightViewport;
    private ScreenViewport viewportGUI;

    private SpriteBatch batch;
    private GameController gameController;
    private ShapeRenderer transitionRenderer;

    private Sprite playerSprite;

    private Stage stage;
    private Skin skin;
    private Label scoreLabel;
    private Label fpsLabel;
    private ButtonGroup buttonGroup;
    private TextButton selectionButton;
    private TextButton createStateButton;
    private TextButton createTransition;

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
        transitionRenderer = new ShapeRenderer();

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

        playerSprite = new Sprite((Texture)Assets.getInstance().getAssetManager().get(Constants.PLAYER_TEXTURE));

        stage = new Stage(viewportGUI);
        rebuildStage();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void rebuildStage () {
        skin = Assets.getInstance().getAssetManager().get(Constants.SKIN_LIBGDX_UI);

        Table table = new Table();
        table.setFillParent(true);
        HorizontalGroup northGroup = new HorizontalGroup();
        table.top().add(northGroup);
        HorizontalGroup southGroup = new HorizontalGroup();
        table.top().add(southGroup);

        HorizontalGroup automataNorth = new HorizontalGroup();
        HorizontalGroup levelNorth = new HorizontalGroup();
        northGroup.left().addActor(automataNorth);
        northGroup.right().addActor(levelNorth);

        scoreLabel = new Label("0", skin, "default-font", Color.WHITE);
        automataNorth.addActor(scoreLabel);
        fpsLabel = new Label("FPS: 60", skin);
        table.add(fpsLabel);

        buttonGroup = new ButtonGroup();
        createStateButton = new TextButton("add state", skin);
        buttonGroup.add(createStateButton);
        table.add(createStateButton);

        stage.addActor(table);
    }

    public void render () {
        renderAutomata(batch);
        renderLevel(batch);
        renderGUI();
    }

    private void renderAutomata(SpriteBatch batch) {
        leftViewport.apply();
        batch.setProjectionMatrix(leftCamera.combined);
        batch.begin();
        for(Sprite sprite : gameController.getAutomataController().getCurrentAutomaton().getStateSprites().values()) {
            sprite.draw(batch);
        }
        renderTransitionLines();
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
        Vector2 playerPos = level.calcPos(level.getCurrentX(), level.getCurrentY());
        playerSprite.setPosition(playerPos.x - halfWidth, playerPos.y - halfHeight);
        playerSprite.draw(batch);
        batch.end();
    }

    private void renderTransitionLines() {
        Gdx.gl.glLineWidth(Constants.TRANSITIONS_LINE_WIDTH);
        transitionRenderer.setProjectionMatrix(leftCamera.combined);
        transitionRenderer.begin(ShapeRenderer.ShapeType.Line);
        transitionRenderer.setColor(Constants.TRANSITION_COLOR);

        for (AutomatonState state : gameController.getAutomataController().getCurrentAutomaton().getStates()) {
            for (Map.Entry<String, AutomatonState> entry : state.getTransitions().entrySet()) {
                String input = entry.getKey();
                AutomatonState nextState = entry.getValue();

                transitionRenderer.line(state.getX(), state.getY(), nextState.getX(), nextState.getY());
            }
        }

        transitionRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void renderGUI() {
        viewportGUI.apply();
        updateScore();
        updateFpsCounter();
        stage.draw();
    }

    private void updateScore() {
        scoreLabel.setText("states: " + gameController.getScore());
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
        int borderY = (int)(height * Constants.UPPER_BORDER_RATIO);
        int heightWithoutBorder = (int)(height * (1.0f - Constants.UPPER_BORDER_RATIO - Constants.LOWER_BORDER_RATIO));
        leftViewport.update(width / 2, heightWithoutBorder);
        leftViewport.setScreenY(borderY);
        rightViewport.update(width / 2, heightWithoutBorder);
        rightViewport.setScreenX(width / 2);
        rightViewport.setScreenY(borderY);

        viewportGUI.update(width, height, true);

        Gdx.app.debug(TAG, "right camera (" + rightCamera.viewportWidth + ", " + rightCamera.viewportHeight + ")");
    }

    @Override
    public void dispose () {
        batch.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
