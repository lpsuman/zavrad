package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AutomataController implements Disposable {
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }
    private static final String DEFAULT_AUTOMATON_LABEL = "automaton";

    private List<DrawableAutomaton> automata;
    private DrawableAutomaton currentAutomaton;
    private Texture stateTexture;
    private int automatonID;

    public AutomataController() {
        init();
    }

    public void init() {
        automata = new ArrayList<>();
        stateTexture = new Texture(Constants.AUTOMATA_STATE_TEXTURE);
        currentAutomaton = new DrawableAutomaton(stateTexture, getBundle().get((LocalizationKeys.AUTOMATON)));
        automata.add(currentAutomaton);
        automatonID = 0;
    }

    public boolean saveAutomaton(FileHandle file) {
        try (FileOutputStream fileOut = new FileOutputStream(file.path());
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            currentAutomaton.setName(file.nameWithoutExtension());
            Automaton automaton = currentAutomaton.getSerializable();
            objOut.writeObject(automaton);
            currentAutomaton.setChangesPending(false);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            return false;
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean loadAutomaton(FileHandle file) {
        try (FileInputStream fileOut = new FileInputStream(file.path());
             ObjectInputStream objOut = new ObjectInputStream(fileOut)) {
            Automaton automaton = (Automaton)objOut.readObject();
            DrawableAutomaton newAutomaton = new DrawableAutomaton(automaton);
            addAutomaton(newAutomaton);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            return false;
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        } catch (ClassNotFoundException exc) {
            exc.printStackTrace();
            return false;
        }
        return true;
    }

    public DrawableAutomaton getCurrentAutomaton() {
        return currentAutomaton;
    }

    public void setCurrentAutomaton(DrawableAutomaton currentAutomaton) {
        if (!automata.contains(currentAutomaton)) {
            return;
        }
        this.currentAutomaton = currentAutomaton;
    }

    public void selectNextAutomaton() {
        int currentIndex = automata.indexOf(currentAutomaton);
        currentIndex++;
        if (currentIndex >= automata.size()) {
            currentIndex -= automata.size();
        }
        currentAutomaton = automata.get(currentIndex);
    }

    public void selectPrevAutomaton() {
        int currentIndex = automata.indexOf(currentAutomaton);
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex += automata.size();
        }
        currentAutomaton = automata.get(currentIndex);
    }

    public void addNewAutomaton() {
        this.addAutomaton(new DrawableAutomaton(getBundle().get(LocalizationKeys.AUTOMATON)));
    }

    public void addAutomaton(DrawableAutomaton automaton) {
        automata.add(automaton);
        setCurrentAutomaton(automaton);
    }

    @Override
    public void dispose() {
        stateTexture.dispose();
    }
}
