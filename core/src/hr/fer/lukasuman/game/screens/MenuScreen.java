package hr.fer.lukasuman.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import hr.fer.lukasuman.game.*;

public class MenuScreen extends AbstractGameScreen {
    private static final String TAG = MenuScreen.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private Stage stage;
    private Stack stack;
    private Image imgBackground;
    private TextButton btnMenuPlay;
    private TextButton btnMenuCustomPlay;
    private TextButton btnMenuOptions;
    private TextButton btnMenuExitGame;
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;

    private CheckBox chkDebug;
    private CheckBox chkShowFpsCounter;
    private SelectBox<String> languageSelectBox;

    private Skin skin;
    //debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private float debugRebuildStage;

    private GameScreen gameScreen;
    private GameScreen customGameScreen;

    public MenuScreen (DirectedGame game) {
        super(game);
    }

    private void rebuildStage () {
        skin = Assets.getInstance().getAssetManager().get(Constants.SKIN_LIBGDX_UI);

        Table layerBackground = buildBackgroundLayer();
        Table layerControls = buildControlsLayer();
        Table layerOptionsWindow = buildOptionsWindowLayer();

        stage.clear();
        stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stack.add(layerBackground);
        stack.add(layerControls);
        stage.addActor(layerOptionsWindow);
    }

    private Table buildBackgroundLayer () {
        Table layer = new Table();
        imgBackground = new Image((Texture)Assets.getInstance().getAssetManager().get(Constants.MENU_BACKGROUND_TEXTURE));
        imgBackground.setScaling(Scaling.fill);
        layer.add(imgBackground).fill();
        return layer;
    }

    private Table buildControlsLayer () {
        Table layer = new Table();
        btnMenuPlay = new TextButton(getBundle().get(LocalizationKeys.PLAY), skin);
        layer.add(btnMenuPlay).pad(Constants.MENU_BUTTON_PADDING);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();
        btnMenuCustomPlay = new TextButton(getBundle().get(LocalizationKeys.CUSTOM_PLAY), skin);
        layer.add(btnMenuCustomPlay).pad(Constants.MENU_BUTTON_PADDING);
        btnMenuCustomPlay.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCustomPlayClicked();
            }
        });
        layer.row();
        btnMenuOptions = new TextButton(getBundle().get(LocalizationKeys.OPTIONS), skin);
        layer.add(btnMenuOptions).pad(Constants.MENU_BUTTON_PADDING);
        btnMenuOptions.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        layer.row();
        btnMenuExitGame = new TextButton(getBundle().get(LocalizationKeys.EXIT), skin);
        layer.add(btnMenuExitGame).pad(Constants.MENU_BUTTON_PADDING);
        btnMenuExitGame.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onExitClicked();
            }
        });
        if (debugEnabled) layer.debug();
        return layer;
    }

    private Table buildOptionsWindowLayer () {
        winOptions = new Window(getBundle().get(LocalizationKeys.OPTIONS), skin);
        winOptions.add(buildOptWin()).row();
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
        winOptions.setColor(1, 1, 1, 0.8f);
        winOptions.pack();
        winOptions.setPosition((Gdx.graphics.getWidth() - winOptions.getWidth()) / 2.0f,
                (Gdx.graphics.getHeight() - winOptions.getHeight()) / 2.0f);
        winOptions.setVisible(false);
        if (debugEnabled) winOptions.debug();
        return winOptions;
    }

    private Table buildOptWin() {
        Table table = new Table();
        table.pad(10, 10, 0, 10);

        Table langTable = new Table();
        langTable.add(new Label(getBundle().get(LocalizationKeys.LANGUAGE), skin));
        String[] array = {"hr", "en"};
        languageSelectBox = new SelectBox<>(skin);
        languageSelectBox.setItems(array);
        langTable.add(languageSelectBox);
        table.add(langTable);
        table.row();

        table.add(new Label("Debug", skin, Constants.DEFAULT_FONT_NAME, Color.RED));
        table.row();
        table.columnDefaults(0).padRight(10);
        table.columnDefaults(1).padRight(10);

        table.add(new Label(getBundle().get(LocalizationKeys.SHOW_FPS), skin));
        chkShowFpsCounter = new CheckBox("", skin);
        table.add(chkShowFpsCounter);
        table.row();

        table.add(new Label(getBundle().get(LocalizationKeys.SHOW_DEBUG), skin));
        chkDebug = new CheckBox("", skin);
        table.add(chkDebug);
        table.row();
        return table;
    }

    private Table buildOptWinButtons () {
        Table tbl = new Table();
        Label lbl = new Label("", skin);
        lbl.setColor(0.75f, 0.75f, 0.75f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skin.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
        tbl.row();
        lbl = new Label("", skin);
        lbl.setColor(0.5f, 0.5f, 0.5f, 1);
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()));
        lbl.getStyle().background = skin.newDrawable("white");
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
        tbl.row();
        // + Save Button with event handler
        btnWinOptSave = new TextButton(getBundle().get(LocalizationKeys.SAVE), skin);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });
        // + Cancel Button with event handler
        btnWinOptCancel = new TextButton(getBundle().get(LocalizationKeys.CANCEL), skin);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });
        return tbl;
    }

    private void onPlayClicked() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        if (gameScreen == null) {
            gameScreen = new GameScreen(game, this, false);
        }
        game.setScreen(gameScreen, transition);
    }

    private void onCustomPlayClicked() {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        if (customGameScreen == null) {
            customGameScreen = new GameScreen(game, this, true);
        }
        game.setScreen(customGameScreen, transition);
    }

    private void onOptionsClicked() {
        loadSettings();
        btnMenuPlay.setVisible(false);
        btnMenuCustomPlay.setVisible(false);
        btnMenuOptions.setVisible(false);
        btnMenuExitGame.setVisible(false);
        winOptions.setVisible(true);
    }

    private void onExitClicked() {
        Gdx.app.exit();
    }

    private void loadSettings() {
        GamePreferences prefs = GamePreferences.getInstance();
        prefs.load();
        chkDebug.setChecked(prefs.debug);
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
        languageSelectBox.setSelected(prefs.language);
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.getInstance();
        prefs.debug = chkDebug.isChecked();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        if (!prefs.language.equals(languageSelectBox.getSelected())) {
            prefs.language = languageSelectBox.getSelected();
            prefs.save();
            Assets.getInstance().loadLocale();
            rebuildStage();
            AutomataGame.updateTitle();
        } else {
            prefs.save();
        }
    }

    private void onSaveClicked() {
        saveSettings();
        onCancelClicked();
    }

    private void onCancelClicked() {
        btnMenuPlay.setVisible(true);
        btnMenuCustomPlay.setVisible(true);
        btnMenuOptions.setVisible(true);
        btnMenuExitGame.setVisible(true);
        winOptions.setVisible(false);
    }

    @Override
    public void render(float deltaTime) {
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        stage = new Stage(new FillViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        rebuildStage();
    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void pause() {
        Gdx.app.debug(TAG, "menu screen paused");
    }

    @Override
    public void resume() {
        super.resume();
        Gdx.app.debug(TAG, "menu screen resumed");
    }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }

    public Stage getStage() {
        return stage;
    }
}
