package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;

public class MenuScreen extends AbstractGameScreen {
    private static final String TAG = MenuScreen.class.getName();

    private Stage stage;
    private Image imgBackground;
    private TextButton btnMenuPlay;
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    TextButton.TextButtonStyle textButtonStyle;
    private CheckBox chkSound;

    //debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private float debugRebuildStage;

    public MenuScreen (Game game) {
        super(game);
    }

    private void rebuildStage () {
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = Assets.getInstance().fonts.defaultNormal;
        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();

        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);
        stage.addActor(layerOptionsWindow);
    }

    private Table buildBackgroundLayer () {
        Table layer = new Table();
        imgBackground = new Image((Texture)Assets.getInstance().assetManager.get(Constants.MENU_BACKGROUND_TEXTURE));
        layer.add(imgBackground);
        return layer;
    }

    private Table buildControlsLayer () {
        Table layer = new Table();
        btnMenuPlay = new TextButton("Play", textButtonStyle);
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        if (debugEnabled) layer.debug();
        return layer;
    }

    private Table buildOptionsWindowLayer () {
        Table layer = new Table();
        return layer;
    }

    private void onPlayClicked () {
        game.setScreen(new GameScreen(game));
    }

    @Override
    public void render (float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (debugEnabled) {
            debugRebuildStage -= deltaTime;
            if (debugRebuildStage <= 0) {
                debugRebuildStage = DEBUG_REBUILD_INTERVAL;
                rebuildStage();
            }
        }

        stage.act(deltaTime);
        stage.draw();
    }

    @Override public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show () {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
    }

    @Override public void hide () {
        stage.dispose();
    }

    @Override public void pause () { }
}
