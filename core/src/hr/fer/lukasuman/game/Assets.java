package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.I18NBundleLoader;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import hr.fer.lukasuman.game.level.blocks.EmptyBlock;
import hr.fer.lukasuman.game.level.blocks.GoalBlock;
import hr.fer.lukasuman.game.level.blocks.StartBlock;
import hr.fer.lukasuman.game.level.blocks.WallBlock;

import java.util.Locale;

public class Assets implements Disposable, AssetErrorListener {

    //TODO add textures to atlas

    public static final String TAG = Assets.class.getName();
    private static final Assets instance = new Assets();

    private AssetManager assetManager;
    private AssetFonts fonts;

    private Texture startStateCircle;
    private Texture normalStateBorder;
    private Texture selectedStateBorder;

    private Assets () {}

    public static Assets getInstance() {
        return instance;
    }

    public void init (AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);

        assetManager.load(Constants.MENU_BACKGROUND_TEXTURE, Texture.class);
        assetManager.load(Constants.AUTOMATA_STATE_TEXTURE, Texture.class);
//        assetManager.load(Constants.AUTOMATA_SELECTED_STATE_TEXTURE, Texture.class);
        assetManager.load(Constants.AUTOMATA_RUNNING_STATE_TEXTURE, Texture.class);
        assetManager.load(Constants.PLAYER_TEXTURE, Texture.class);
        assetManager.load(Constants.TEXTURE_ATLAS_LIBGDX_UI, TextureAtlas.class);

        fonts = new AssetFonts();
        ObjectMap<String, Object> fontMap = new ObjectMap<>();
        fontMap.put("default-font", fonts.defaultNormal);
        fontMap.put("default-small", fonts.defaultSmall);
        fontMap.put("default-big", fonts.defaultBig);

/* Create the SkinParameter and supply the ObjectMap to it */
        SkinLoader.SkinParameter parameter = new SkinLoader.SkinParameter(Constants.TEXTURE_ATLAS_LIBGDX_UI, fontMap);
        assetManager.load(Constants.SKIN_LIBGDX_UI, Skin.class, parameter);
        loadBlockTextures();

        loadLocale();
        assetManager.finishLoading();
        output();
        initPixmapTextures();
    }

    private void output() {
        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
            Gdx.app.debug(TAG, "asset: " + a);
    }

    public void loadLocale() {
        Locale locale = Locale.forLanguageTag(GamePreferences.getInstance().language);
        if (assetManager.isLoaded(Constants.BUNDLE)) {
            assetManager.unload(Constants.BUNDLE);
        }
        assetManager.load(Constants.BUNDLE, I18NBundle.class, new I18NBundleLoader.I18NBundleParameter(locale));
        assetManager.finishLoading();
        output();
    }

    private void loadBlockTextures() {
        if (EmptyBlock.TEXTURE != null) {
            assetManager.load(EmptyBlock.TEXTURE, Texture.class);
        }
        assetManager.load(WallBlock.TEXTURE, Texture.class);
        assetManager.load(StartBlock.TEXTURE, Texture.class);
        assetManager.load(GoalBlock.TEXTURE, Texture.class);
    }

    private void initPixmapTextures() {
        Pixmap stateCircles = new Pixmap(Constants.STATE_CIRCLES_PIXMAP_SIZE, Constants.STATE_CIRCLES_PIXMAP_SIZE, Pixmap.Format.RGBA8888);
        stateCircles.setBlending(Pixmap.Blending.None);
        stateCircles.setFilter(Pixmap.Filter.BiLinear);
        int halfWidth = stateCircles.getWidth() / 2;

        stateCircles.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        stateCircles.fillCircle(halfWidth, halfWidth, halfWidth);
        stateCircles.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        stateCircles.fillCircle(halfWidth, halfWidth, (int)(halfWidth * (1 - Constants.START_STATE_CIRCLE_LINE_WIDTH_RATIO)));
        startStateCircle = new Texture(stateCircles);
        stateCircles.fill();

        stateCircles.setColor(0.0f, 0.0f, 0.0f, 1.0f);
        stateCircles.fillCircle(halfWidth, halfWidth, halfWidth);
        stateCircles.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        stateCircles.fillCircle(halfWidth, halfWidth, (int)(halfWidth * (1 - Constants.STATE_BORDER_LINE_WIDTH_RATIO)));
        normalStateBorder = new Texture(stateCircles);
        stateCircles.fill();

        stateCircles.setColor(Constants.SELECTED_STATE_BORDER_COLOR);
        stateCircles.fillCircle(halfWidth, halfWidth, halfWidth);
        stateCircles.setColor(1.0f, 1.0f, 1.0f, 0.0f);
        stateCircles.fillCircle(halfWidth, halfWidth, (int)(halfWidth * (1 - Constants.STATE_BORDER_LINE_WIDTH_RATIO)));
        selectedStateBorder = new Texture(stateCircles);
    }

    @Override
    public void dispose () {
        assetManager.dispose();
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", throwable);
    }

    public class AssetFonts {
        public final BitmapFont defaultSmall;
        public final BitmapFont defaultNormal;
        public final BitmapFont defaultBig;

        public AssetFonts () {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/AbhayaLibre-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.characters = "abcćčdđefghijklmnopqrsštuvwxyzž" +
                    "ABCĆČDĐEFGHIJKLMNOPQRSŠTUVWXYZŽ" +
                    "1234567890*[]().,\n:\"\'!?/#$%&|-_<>";
            parameter.size = (int)(12 * Constants.FONT_RESOLUTION_FACTOR);
            defaultSmall = generator.generateFont(parameter);
            parameter.size = (int)(16 * Constants.FONT_RESOLUTION_FACTOR);
            defaultNormal = generator.generateFont(parameter);
            parameter.size = (int)(20 * Constants.FONT_RESOLUTION_FACTOR);
            defaultBig = generator.generateFont(parameter);
            generator.dispose();

            defaultSmall.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultSmall.getData().setScale(1 / Constants.FONT_RESOLUTION_FACTOR);
            defaultNormal.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultNormal.getData().setScale(1 / Constants.FONT_RESOLUTION_FACTOR);
            defaultBig.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultBig.getData().setScale(1 / Constants.FONT_RESOLUTION_FACTOR);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AssetFonts getFonts() {
        return fonts;
    }

    public Texture getStartStateCircle() {
        return startStateCircle;
    }

    public Texture getNormalStateBorder() {
        return normalStateBorder;
    }

    public Texture getSelectedStateBorder() {
        return selectedStateBorder;
    }
}
