package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.LocalizationKeys;
import hr.fer.lukasuman.game.control.GameController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AutomataController {
    private static final String TAG = AutomataController.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private List<DrawableAutomaton> automata;
    private DrawableAutomaton currentAutomaton;

    private GameController gameController;

    public AutomataController(GameController gameController) {
        this.gameController = gameController;
        init();
    }

    public void init() {
        automata = new ArrayList<>();
        currentAutomaton = new DrawableAutomaton(getBundle().get((LocalizationKeys.AUTOMATON)), findAvailableID());
        automata.add(currentAutomaton);
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
            newAutomaton.setUniqueID(findAvailableID());
            newAutomaton.setChangesPending(false);
            newAutomaton.setCurrentState(null);
            addAutomaton(newAutomaton);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            return false;
        } catch (InvalidClassException exc) {
            showMessage(getBundle().get(LocalizationKeys.AUTOMATON_VERSION_ERROR_MESSAGE));
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
        gameController.setSelectedState(null);
        gameController.setSelectedTransition(null);
    }

    public void selectNextAutomaton() {
        int currentIndex = automata.indexOf(currentAutomaton);
        Gdx.app.debug(TAG, "automatons: " + automata.size() + " current index: " + currentIndex);
        currentIndex++;
        if (currentIndex >= automata.size()) {
            currentIndex -= automata.size();
        }
        Gdx.app.debug(TAG, "automatons: " + automata.size() + " next index: " + currentIndex);
        setCurrentAutomaton(automata.get(currentIndex));
    }

    public void selectPrevAutomaton() {
        int currentIndex = automata.indexOf(currentAutomaton);
        Gdx.app.debug(TAG, "automatons: " + automata.size() + " current index: " + currentIndex);
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex += automata.size();
        }
        Gdx.app.debug(TAG, "automatons: " + automata.size() + " previous index: " + currentIndex);
        setCurrentAutomaton(automata.get(currentIndex));
    }

    public void addNewAutomaton() {
        this.addAutomaton(new DrawableAutomaton(getBundle().get(LocalizationKeys.AUTOMATON), findAvailableID()));
    }

    public void addAutomaton(DrawableAutomaton automaton) {
        automata.add(automaton);
        setCurrentAutomaton(automaton);
    }

    private int findAvailableID() {
        int id = 0;
        for (Automaton automaton: automata) {
            if (automaton.getUniqueID() >= id) {
                id = automaton.getUniqueID() + 1;
            }
        }
        return id;
    }

    private void showMessage(String message) {
        gameController.getGameRenderer().getStageManager().showInformation(message);
    }
}
