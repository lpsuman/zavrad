package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.control.InputController;
import hr.fer.lukasuman.game.render.GameRenderer;

public class GameScreen extends AbstractGameScreen {
    private static final String TAG = GameScreen.class.getName();

    private GameController gameController;
    private GameRenderer gameRenderer;
    private InputController inputController;
    private boolean paused;
    private InputMultiplexer inputMultiplexer;

    private MenuScreen menuScreen;
    private boolean isGameLoaded;

    public GameScreen (DirectedGame game, MenuScreen menuScreen) {
        super(game);
        this.menuScreen = menuScreen;
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

        if (!isGameLoaded) {
            gameController = new GameController(game);
        }
        gameRenderer = new GameRenderer(gameController, this);

        if (!isGameLoaded) {
            isGameLoaded = true;
            gameController.setGameRenderer(gameRenderer);
        }

        Gdx.input.setInputProcessor(getInputProcessor());
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
        Gdx.app.debug(TAG, "game screen paused");
    }

    @Override
    public void resume () {
        super.resume();
        paused = false;
        Gdx.app.debug(TAG, "game screen resumed");
    }

    @Override
    public InputProcessor getInputProcessor() {
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameRenderer.getUpperLeftStage());
        inputMultiplexer.addProcessor(gameRenderer.getUpperRightStage());
        inputMultiplexer.addProcessor(gameRenderer.getLowerLeftStage());
        inputMultiplexer.addProcessor(gameRenderer.getLowerRightStage());
        inputController = new InputController(gameController, gameRenderer, menuScreen);
        inputMultiplexer.addProcessor(inputController);
        return inputMultiplexer;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }
}
