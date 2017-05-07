package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import hr.fer.lukasuman.game.DirectedGame;
import hr.fer.lukasuman.game.GameController;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.GameRenderer;

public class GameScreen extends AbstractGameScreen {
    private static final String TAG = GameScreen.class.getName();

    private GameController gameController;
    private GameRenderer gameRenderer;
    private boolean paused;

    public GameScreen (DirectedGame game) {
        super(game);
    }

    @Override
    public void render (float deltaTime) {
        if (!paused) {
            gameController.update(deltaTime);
        }
        // Sets the clear screen color to: Cornflower Blue
        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed / 255.0f, 0xff / 255.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameRenderer.render();
    }

    @Override
    public void resize (int width, int height) {
        gameRenderer.resize(width, height);
    }

    @Override
    public void show () {
        GamePreferences.getInstance().load();
        gameController = new GameController(game);
        gameRenderer = new GameRenderer(gameController);
        gameController.setGameRenderer(gameRenderer);
        Gdx.input.setInputProcessor(gameController);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide () {
        gameRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void pause () {
        paused = true;
    }

    @Override
    public void resume () {
        super.resume();
        paused = false;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return gameController;
    }
}
