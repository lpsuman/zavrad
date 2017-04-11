package hr.fer.lukasuman;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AutomataGame extends ApplicationAdapter {

	private static final String TAG = AutomataGame.class.getName();

	private GameController gameController;
	private GameRenderer gameRenderer;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		gameController = new GameController();
		gameRenderer = new GameRenderer(gameController);
	}

	@Override
	public void render () {
		gameController.update(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gameRenderer.render();
	}
	
	@Override
	public void dispose () {
		gameRenderer.dispose();
	}

	@Override
	public void resize (int width, int height) {
		gameRenderer.resize(width, height);
	}
}
