package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import hr.fer.lukasuman.game.control.GameController;
import hr.fer.lukasuman.game.GamePreferences;
import hr.fer.lukasuman.game.control.InputController;
import hr.fer.lukasuman.game.render.GameRenderer;
import hr.fer.lukasuman.game.render.StageManager;

public class GameScreen extends AbstractGameScreen {
    private static final String TAG = GameScreen.class.getName();

    private GameController gameController;
    private GameRenderer gameRenderer;
    private InputController inputController;
    private boolean paused;
    private InputMultiplexer inputMultiplexer;

    private boolean isCustomPlay;
    private MenuScreen menuScreen;
    private boolean isGameLoaded;

    public GameScreen (DirectedGame game, MenuScreen menuScreen, boolean isCustomPlay) {
        super(game);
        this.menuScreen = menuScreen;
        this.isCustomPlay = isCustomPlay;
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
        if (!isGameLoaded) {
            gameController = new GameController(game, isCustomPlay);
            isGameLoaded = true;
        }
        gameRenderer = new GameRenderer(gameController, this, isCustomPlay);
        gameController.setGameRenderer(gameRenderer);
        StageManager stageManager = gameRenderer.getStageManager();

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stageManager.getUpperLeftStage());
        inputMultiplexer.addProcessor(stageManager.getUpperRightStage());
        inputMultiplexer.addProcessor(stageManager.getLowerLeftStage());
        inputMultiplexer.addProcessor(stageManager.getLowerRightStage());
        inputController = new InputController(gameController, gameRenderer, menuScreen);
        inputMultiplexer.addProcessor(inputController);

        Gdx.input.setInputProcessor(inputMultiplexer);
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
        return inputMultiplexer;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public GameController getGameController() {
        return gameController;
    }
}
