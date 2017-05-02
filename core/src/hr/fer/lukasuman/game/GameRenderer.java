package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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

    private SpriteBatch batch;
    private GameController gameController;
    private ShapeRenderer transitionRenderer;

    private Sprite playerSprite;

    public GameRenderer (GameController gameController) {
        this.gameController = gameController;
        init();
    }

    private void init () {
        fullCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        leftCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH / 2, Constants.VIEWPORT_HEIGHT);
        rightCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH / 2, Constants.VIEWPORT_HEIGHT);

        leftViewport = new ScreenViewport(leftCamera);
        rightViewport = new ScreenViewport(rightCamera);

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

        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true); // flip y-axis
        cameraGUI.update();

        playerSprite = new Sprite((Texture)Assets.getInstance().getAssetManager().get(Constants.PLAYER_TEXTURE));

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render () {
        renderAutomata(batch);
        renderLevel(batch);
        renderGUI(batch);
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

    private void renderGUI(SpriteBatch batch) {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        //TODO
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

    public void resize (int width, int height) {
        leftViewport.update(width / 2, height);
        rightViewport.update(width / 2, height);
        rightViewport.setScreenX(width / 2);

        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}
