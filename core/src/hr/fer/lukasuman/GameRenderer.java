package hr.fer.lukasuman;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.automata.AutomataState;
import hr.fer.lukasuman.automata.TransitionInput;

import java.util.Map;

public class GameRenderer implements Disposable {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GameController gameController;
    private ShapeRenderer transitionRenderer;

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
    }

    public void render () {
        renderTransitionLines();
        renderStateObjects();
        renderTransitionTexts();
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

        for (AutomataState state : gameController.automata.getStates()) {
            for (Map.Entry<TransitionInput, AutomataState> entry : state.getTransitions().entrySet()) {
                TransitionInput input = entry.getKey();
                AutomataState nextState = entry.getValue();

                transitionRenderer.line(state.getX(), state.getY(), nextState.getX(), nextState.getY());
            }
        }

        transitionRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private void renderTransitionTexts() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (AutomataState state : gameController.automata.getStates()) {
            for (Map.Entry<TransitionInput, AutomataState> entry : state.getTransitions().entrySet()) {
                TransitionInput input = entry.getKey();
                AutomataState nextState = entry.getValue();

                float middleX = (state.getX() + nextState.getX()) / 2;
                float middleY = (state.getY() + nextState.getY()) / 2;
                middleX = (state.getX() + middleX) / 2;
                middleY = (state.getY() + middleY) / 2;
                Constants.TRANSITION_FONT.draw(batch, input.getInputKey(), middleX, middleY);
            }
        }

        batch.end();
    }

    public void resize (int width, int height) {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();
    }

    @Override
    public void dispose () {
        batch.dispose();
    }
}
