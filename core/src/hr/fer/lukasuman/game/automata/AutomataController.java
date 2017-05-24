package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AutomataController implements Disposable {

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
        currentAutomaton = new DrawableAutomaton(stateTexture, DEFAULT_AUTOMATON_LABEL);
        automatonID = 0;
    }

    public boolean saveAutomaton(FileHandle file) {
        try (FileOutputStream fileOut = new FileOutputStream(file.path());
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
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

    public void addNewAutomaton() {
        this.addAutomaton(new DrawableAutomaton(DEFAULT_AUTOMATON_LABEL + ++automatonID));
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
