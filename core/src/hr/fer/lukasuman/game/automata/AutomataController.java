package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import hr.fer.lukasuman.game.Constants;

import java.io.*;
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
        automata = new ArrayList<>();
        stateTexture = new Texture(Constants.AUTOMATA_STATE_TEXTURE);
        currentAutomaton = new DrawableAutomaton(stateTexture, "automaton");
    }

    public boolean saveAutomaton(FileHandle file) {
        try (FileOutputStream fileOut = new FileOutputStream(file.path());
             ObjectOutputStream objOut = new ObjectOutputStream(fileOut)) {
            Automaton automaton = currentAutomaton.getSerializable();
            objOut.writeObject(automaton);
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
            setCurrentAutomaton(newAutomaton);
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

    public void addAutomaton(DrawableAutomaton automaton) {
        automata.add(automaton);
    }

    @Override
    public void dispose() {
        stateTexture.dispose();
    }
}
