package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.Constants;

import java.util.ArrayList;
import java.util.List;

public class AutomataController implements Disposable {

    private List<DrawableAutomaton> automata;
    private DrawableAutomaton currentAutomaton;
    private Texture stateTexture;

    public AutomataController() {
        init();
    }

    public void init() {
        automata = new ArrayList<DrawableAutomaton>();
        stateTexture = new Texture(Constants.AUTOMATA_STATE_TEXTURE);
        currentAutomaton = new DrawableAutomaton(stateTexture);
    }

    public DrawableAutomaton getCurrentAutomaton() {
        return currentAutomaton;
    }

    @Override
    public void dispose() {
        stateTexture.dispose();
    }
}
