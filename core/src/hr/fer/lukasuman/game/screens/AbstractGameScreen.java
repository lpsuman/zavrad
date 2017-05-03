package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.DirectedGame;

public abstract class AbstractGameScreen implements Screen {
    protected DirectedGame game;

    public AbstractGameScreen (DirectedGame game) {
        this.game = game;
    }

    public abstract void render (float deltaTime);
    public abstract void resize (int width, int height);
    public abstract void show ();
    public abstract void hide ();
    public abstract void pause ();

    public void resume () {
        Assets.getInstance().init(new AssetManager());
    }

    public void dispose () {
        Assets.getInstance().dispose();
    }

    public abstract InputProcessor getInputProcessor();
}
