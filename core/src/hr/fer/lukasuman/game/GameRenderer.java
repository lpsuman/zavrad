package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.automata.AutomatonState;

import java.util.Map;

public class GameRenderer implements Disposable {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GameController gameController;
    private ShapeRenderer transitionRenderer;
    private OrthographicCamera cameraGUI;

    public GameRenderer (GameController gameController) {
        this.gameController = gameController;
        init();
    }

    private void init () {
        transitionRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        gameController.setCamera(camera);
        camera.position.set(0, 0, 0);
        camera.update();

        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true); // flip y-axis
        cameraGUI.update();
    }

    public void render () {
        renderLevel(batch);
        renderGUI(batch);
        renderTransitionLines();
        renderStateObjects();
        renderTransitionTexts();
    }

    private void renderLevel(SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameController.levelController.render(batch);
        batch.end();
    }

    private void renderGUI(SpriteBatch batch) {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        //TODO
        batch.end();
    }

    private void renderStateObjects() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for(Sprite sprite : gameController.stateSprites.values()) {
            sprite.draw(batch);
        }
        batch.end();
    }

    private void renderTransitionLines() {
        Gdx.gl.glLineWidth(Constants.TRANSITIONS_LINE_WIDTH);
        transitionRenderer.setProjectionMatrix(camera.combined);
        transitionRenderer.begin(ShapeRenderer.ShapeType.Line);
        transitionRenderer.setColor(Constants.TRANSITION_COLOR);

        for (AutomatonState state : gameController.automataController.getStates()) {
            for (Map.Entry<String, AutomatonState> entry : state.getTransitions().entrySet()) {
                String input = entry.getKey();
                AutomatonState nextState = entry.getValue();

                transitionRenderer.line(state.getX(), state.getY(), nextState.getX(), nextState.getY());
            }
        }

        transitionRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void renderTransitionTexts() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (AutomatonState state : gameController.automataController.getStates()) {
            for (Map.Entry<String, AutomatonState> entry : state.getTransitions().entrySet()) {
                String input = entry.getKey();
                AutomatonState nextState = entry.getValue();

                float middleX = (state.getX() + nextState.getX()) / 2;
                float middleY = (state.getY() + nextState.getY()) / 2;
                middleX = (state.getX() + middleX) / 2;
                middleY = (state.getY() + middleY) / 2;
                Constants.TRANSITION_FONT.draw(batch, input, middleX, middleY);
            }
        }

        batch.end();
    }

    public void resize (int width, int height) {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();
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
