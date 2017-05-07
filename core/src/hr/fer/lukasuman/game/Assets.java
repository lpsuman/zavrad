package hr.fer.lukasuman.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {

    public static final String TAG = Assets.class.getName();
    private static final Assets instance = new Assets();

    private AssetManager assetManager;
    private AssetFonts fonts;

    private Assets () {}

    public static Assets getInstance() {
        return instance;
    }

    public void init (AssetManager assetManager) {
        this.assetManager = assetManager;
        assetManager.setErrorListener(this);
        assetManager.load(Constants.MENU_BACKGROUND_TEXTURE, Texture.class);
        assetManager.load(Constants.AUTOMATA_STATE_TEXTURE, Texture.class);
        assetManager.load(Constants.WALL_TEXTURE, Texture.class);
        assetManager.load(Constants.START_TEXTURE, Texture.class);
        assetManager.load(Constants.GOAL_TEXTURE, Texture.class);
        assetManager.load(Constants.PLAYER_TEXTURE, Texture.class);
        assetManager.load(Constants.TEXTURE_ATLAS_LIBGDX_UI, TextureAtlas.class);
        assetManager.load(Constants.SKIN_LIBGDX_UI, Skin.class, new SkinLoader.SkinParameter(Constants.TEXTURE_ATLAS_LIBGDX_UI));

        assetManager.finishLoading();

        fonts = new AssetFonts();

        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
            Gdx.app.debug(TAG, "asset: " + a);
    }

    @Override
    public void dispose () {
        assetManager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();
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
            defaultSmall = new BitmapFont();
            defaultNormal = new BitmapFont();
            defaultBig = new BitmapFont();

            defaultSmall.getData().setScale(1.0f);
            defaultNormal.getData().setScale(1.5f);
            defaultBig.getData().setScale(2.0f);

            defaultSmall.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AssetFonts getFonts() {
        return fonts;
    }
}
