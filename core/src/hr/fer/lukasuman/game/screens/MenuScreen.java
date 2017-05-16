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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;

public class MenuScreen extends AbstractGameScreen {
    private static final String TAG = MenuScreen.class.getName();

    private Stage stage;
    private Stack stack;
    private Image imgBackground;
    private TextButton btnMenuPlay;
    private TextButton btnMenuOptions;
    private Window winOptions;
    private TextButton btnWinOptSave;
    private TextButton btnWinOptCancel;
    private CheckBox chkShowFpsCounter;

    private Skin skin;
    //debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f;
    private boolean debugEnabled = false;
    private float debugRebuildStage;

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
        btnMenuPlay = new TextButton("Play", skin);
        layer.add(btnMenuPlay);
        btnMenuPlay.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onPlayClicked();
            }
        });
        layer.row();
        btnMenuOptions = new TextButton("Options", skin);
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onOptionsClicked();
            }
        });
        if (debugEnabled) layer.debug();
        return layer;
    }

    private Table buildOptionsWindowLayer () {
        winOptions = new Window("Options", skin);
        // + Debug: Show FPS Counter
        winOptions.add(buildOptWinDebug()).row();
        // + Separator and Buttons (Save, Cancel)
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
        // Make options window slightly transparent
        winOptions.setColor(1, 1, 1, 0.8f);
        // Hide options window by default
        winOptions.setVisible(false);
        if (debugEnabled) winOptions.debug();
        // Let TableLayout recalculate widget sizes and positions
        winOptions.pack();
        // Move options window to bottom right corner
        winOptions.setPosition
                (Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50,
                        50);
        return winOptions;
    }

    private Table buildOptWinDebug () {
        Table tbl = new Table();

        tbl.pad(10, 10, 0, 10);
        tbl.add(new Label("Debug", skin, Constants.DEFAULT_FONT_NAME, Color.RED)).colspan(3);
        tbl.row();
        tbl.columnDefaults(0).padRight(10);
        tbl.columnDefaults(1).padRight(10);

        chkShowFpsCounter = new CheckBox("", skin);
        tbl.add(new Label("Show FPS Counter", skin));
        tbl.add(chkShowFpsCounter);
        tbl.row();
        return tbl;
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
        btnWinOptSave = new TextButton("Save", skin);
        tbl.add(btnWinOptSave).padRight(30);
        btnWinOptSave.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onSaveClicked();
            }
        });
        // + Cancel Button with event handler
        btnWinOptCancel = new TextButton("Cancel", skin);
        tbl.add(btnWinOptCancel);
        btnWinOptCancel.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                onCancelClicked();
            }
        });
        return tbl;
    }

    private void onPlayClicked () {
        ScreenTransition transition = ScreenTransitionFade.init(0.75f);
        game.setScreen(new GameScreen(game), transition);
    }

    private void onOptionsClicked () {
        loadSettings();
        btnMenuPlay.setVisible(false);
        btnMenuOptions.setVisible(false);
        winOptions.setVisible(true);
    }

    private void loadSettings() {
        GamePreferences prefs = GamePreferences.getInstance();
        prefs.load();
        chkShowFpsCounter.setChecked(prefs.showFpsCounter);
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.getInstance();
        prefs.showFpsCounter = chkShowFpsCounter.isChecked();
        prefs.save();
    }

    private void onSaveClicked() {
        saveSettings();
        onCancelClicked();
    }
    private void onCancelClicked() {
        btnMenuPlay.setVisible(true);
        btnMenuOptions.setVisible(true);
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

    @Override public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show () {
        stage = new Stage(new FillViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        rebuildStage();
    }

    @Override public void hide () {
        stage.dispose();
        skin.dispose();
    }

    @Override public void pause () { }

    @Override
    public InputProcessor getInputProcessor() {
        return stage;
    }
}
