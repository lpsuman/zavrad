package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;

import java.util.*;

public class AutomatonTransition {

    private static final float CURVE_DEPTH = 0.25f;
    private static final float CURVE_STEEPNESS = 0.25f;
    private static final float ARROW_ANGLE = 30.0f;
    private static final float ARROW_LENGTH = Constants.STATE_SIZE / 4.0f;

    private static final GlyphLayout glyphLayout = new GlyphLayout();

    private Set<String> transitionLabels;
    private AutomatonState startState;
    private AutomatonState endState;

    private Bezier<Vector2> bezier;
    private Vector2[] dataSet;
    private Vector2[] points;
    private Vector2 temp;
    private Vector2 norm;

    private Vector2 startPoint;
    private Vector2 endPoint;
    private Vector2 middlePoint;
    private Vector2 shiftedMiddlePoint;

    private Vector2 leftArrowPoint;
    private Vector2 rightArrowPoint;

    public AutomatonTransition(Set<String> transitionLabels, AutomatonState startState,
                               AutomatonState endState, Vector2 endPoint) {
        this.transitionLabels = transitionLabels;
        this.startState = startState;
        this.endState = endState;

        dataSet = new Vector2[4];
        dataSet[0] = new Vector2();
        dataSet[1] = new Vector2();
        dataSet[2] = new Vector2();
        dataSet[3] = new Vector2();
        temp = new Vector2();
        norm = new Vector2();

        startPoint = new Vector2();
        this.endPoint = new Vector2();
        if (endPoint != null) {
            this.endPoint.set(endPoint);
        }
        middlePoint = new Vector2();
        shiftedMiddlePoint = new Vector2();

        leftArrowPoint = new Vector2();
        rightArrowPoint = new Vector2();
        recalculate();
    }

    public AutomatonTransition(String label, AutomatonState startState, AutomatonState endState) {
        this(new HashSet<>(Arrays.asList(label)), startState, endState, null);
    }

    public AutomatonTransition(String label, AutomatonState startState, Vector2 endPoint) {
        this(new HashSet<>(Arrays.asList(label)), startState, null, endPoint);
    }

    public void recalculate() {
        calculateControlPoints();
        calculateBezierPoints();
        calculateArrowPoints();
    }

    private void calculateControlPoints() {
        startPoint.set(startState.getX(), startState.getY());
        if (endState != null) {
            endPoint.set(endState.getX(), endState.getY());
        }
        middlePoint.x = (startPoint.x + endPoint.x) / 2.0f;
        middlePoint.y = (startPoint.y + endPoint.y) / 2.0f;

        temp.set(endPoint);
        temp.sub(startPoint);
        norm.set(temp);
        norm.rotate90(1);
        float len = Math.min(temp.len() / 2.0f, 2.0f * Constants.STATE_SIZE);
        norm.setLength(len * CURVE_DEPTH);
        temp.setLength(len * CURVE_STEEPNESS);
        middlePoint.add(norm);

        dataSet[1].set(middlePoint);
        dataSet[1].sub(temp);
        dataSet[2].set(middlePoint);
        dataSet[2].add(temp);

        dataSet[0].set(startPoint);
        temp.set(dataSet[1]);
        temp.sub(dataSet[0]);
        temp.setLength(Constants.STATE_SIZE / 2.0f);
        dataSet[0].add(temp);

        dataSet[3].set(endPoint);
        temp.set(dataSet[2]);
        temp.sub(dataSet[3]);
        temp.setLength(Constants.STATE_SIZE / 2.0f);
        dataSet[3].add(temp);

        norm.set(temp);
    }

    private void calculateBezierPoints() {
        bezier = new Bezier<>(dataSet);
        int numOfPoints = (int)(endPoint.sub(startPoint).len() * Constants.BEZIER_FIDELITY);
        points = new Vector2[numOfPoints];

        for (int i = 0; i < numOfPoints; i++) {
            points[i] = new Vector2();
            bezier.valueAt(points[i], (float)i / (numOfPoints - 1));
        }
    }

    private void calculateArrowPoints() {
        norm.setLength(ARROW_LENGTH);

        temp.set(dataSet[3]);
        norm.rotate(ARROW_ANGLE);
        temp.add(norm);
        rightArrowPoint.set(temp);

        temp.set(dataSet[3]);
        norm.rotate(ARROW_ANGLE * -2.0f);
        temp.add(norm);
        leftArrowPoint.set(temp);
    }

    public void drawLines(ShapeRenderer transitionRenderer) {
        if (GamePreferences.getInstance().debug) {
            drawControlPolygon(transitionRenderer);
        }
        for(int i = 0; i < points.length - 1; i++) {
            transitionRenderer.line(points[i], points[i+1]);
        }

        transitionRenderer.line(leftArrowPoint, dataSet[3]);
        transitionRenderer.line(rightArrowPoint, dataSet[3]);
    }

    public void drawLabels(SpriteBatch batch, BitmapFont font) {
        float posY = 0.0f;
        for (String label : transitionLabels) {
            glyphLayout.setText(font, label);
            if (posY == 0.0f) {
                posY = middlePoint.y + (transitionLabels.size() / 2.0f) * glyphLayout.height;
            }
            font.draw(batch, label, middlePoint.x - glyphLayout.width / 2.0f, posY);
            posY -= glyphLayout.height;
        }
    }

    private void drawControlPolygon(ShapeRenderer transitionRenderer) {
        for(int i = 0; i < dataSet.length - 1; i++) {
            transitionRenderer.line(dataSet[i], dataSet[i+1]);
        }
    }

    public void addLabel(String newLabel) {
        if (transitionLabels.contains(newLabel)) {
            return;
        }
        if (startState.getTransitions().get(newLabel) != null) {
            return;
        }
        transitionLabels.add(newLabel);
        startState.getTransitions().put(newLabel, endState);
    }

    public void removeLabel(String label) {
        if (transitionLabels.contains(label)) {
            transitionLabels.remove(label);
            startState.getTransitions().remove(label);
        }
    }

    public Vector2 getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Vector2 endPoint) {
        this.endPoint = endPoint;
    }

    public Vector2 getMiddlePoint() {
        return middlePoint;
    }

    public Set<String> getTransitionLabels() {
        return transitionLabels;
    }

    public AutomatonState getStartState() {
        return startState;
    }

    public AutomatonState getEndState() {
        return endState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatonTransition that = (AutomatonTransition) o;

        if (!startState.equals(that.startState)) return false;
        return endState != null ? endState.equals(that.endState) : that.endState == null;
    }

    @Override
    public int hashCode() {
        int result = startState.hashCode();
        result = 31 * result + (endState != null ? endState.hashCode() : 0);
        return result;
    }
}
