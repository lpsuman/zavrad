package hr.fer.lukasuman;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class GameRenderer implements Disposable {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GameController gameController;

    public GameRenderer (GameController gameController) {
        this.gameController = gameController;
        init();
    }

    private void init () {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
    }

    public void render () {

    }

    public void resize (int width, int height) {

    }

    @Override
    public void dispose () {

    }
}
