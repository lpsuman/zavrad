package hr.fer.lukasuman.game.automata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.I18NBundle;
import hr.fer.lukasuman.game.Assets;
import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.GamePreferences;

import java.util.*;

public class AutomatonTransition {
    private static final String TAG = AutomatonTransition.class.getName();
    private static I18NBundle getBundle() {
        return Assets.getInstance().getAssetManager().get(Constants.BUNDLE);
    }

    private static final float CURVE_DEPTH = 0.25f;
    private static final float CURVE_STEEPNESS = 0.25f;
    private static final float ARROW_ANGLE = 30.0f;
    private static final float ARROW_LENGTH = Constants.STATE_SIZE / 4.0f;
    private static final float LOOP_ANGLE = 60.0f;
    private static final float LOOP_STEEPNESS = 2.0f;
    private static final Vector2 DEFAULT_LOOP_DIRECTION = new Vector2(0.0f, -1.0f);

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
    private Vector2 manualLoopPositon;

    private Matrix4 mat4;
    private float dx;
    private float dy;
    private float angle;

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
        if (startState.equals(endState)) {
            calculateLoopControlPoints();
        } else {
            calculateControlPoints();
        }
        calculateBezierPoints();
        calculateArrowPoints();
        calculateTextRotationMatrix();
    }

    private void calculateLoopControlPoints() {
        startPoint.set(startState.getX(), startState.getY());
        if (manualLoopPositon == null) {
            norm.set(0.0f, 0.0f);
            DrawableAutomaton automaton = (DrawableAutomaton) startState.getParent();
            for (AutomatonTransition transition : automaton.getTransitionSet()) {
                if ((transition.startState.equals(this.startState) || transition.endState.equals(this.endState))
                        && !transition.startState.equals(transition.endState)) {
                    temp.set(transition.middlePoint.x - this.startPoint.x, transition.middlePoint.y - this.startPoint.y);
                    temp.nor();
                    norm.add(temp);
                }
            }
        } else {
            norm.set(manualLoopPositon);
        }
        if (norm.isZero()) {
            norm.set(DEFAULT_LOOP_DIRECTION);
        }
        norm.set(-norm.x, -norm.y);
        norm.setLength(Constants.STATE_SIZE / 2.0f);

        temp.set(norm);
        temp.rotate(LOOP_ANGLE / 2.0f);
        dataSet[0].set(startPoint);
        dataSet[0].add(temp);
        temp.setLength(LOOP_STEEPNESS * Constants.STATE_SIZE);
        dataSet[1].set(startPoint);
        dataSet[1].add(temp);

        temp.set(norm);
        temp.rotate(-LOOP_ANGLE / 2.0f);
        dataSet[3].set(startPoint);
        dataSet[3].add(temp);
        temp.setLength(LOOP_STEEPNESS * Constants.STATE_SIZE);
        dataSet[2].set(startPoint);
        dataSet[2].add(temp);

        norm.setLength(LOOP_STEEPNESS * 0.75f * Constants.STATE_SIZE);
        middlePoint.set(startPoint);
        middlePoint.add(norm);
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
        norm.set(dataSet[2].x - dataSet[3].x, dataSet[2].y - dataSet[3].y);
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

    private void calculateTextRotationMatrix() {
        dx = dataSet[3].x - dataSet[0].x;
        dy = dataSet[3].y - dataSet[0].y;
        angle = (float)Math.atan2(dy, dx);
        if (angle > Math.PI / 2.0) {
            angle -= Math.PI;
        } else if (angle < -Math.PI / 2.0) {
            angle += Math.PI;
        }
        mat4 = new Matrix4();
        mat4.rotate(new Vector3(0, 0, 1), (float)Math.toDegrees(angle));
        mat4.trn(middlePoint.x, middlePoint.y, 0);
    }

    public void debug() {
        Gdx.app.debug(TAG, String.format("dx=%.4f dy=%.4f angle=%.4f degrees=%.4f", dx, dy, angle, Math.toDegrees(angle)));
    }

    public void drawLines(ShapeRenderer transitionRenderer) {
        if (GamePreferences.getInstance().debug) {
            drawControlPolygon(transitionRenderer);
        }
        for(int i = 0; i < points.length - 1; i++) {
            transitionRenderer.line(points[i], points[i+1]);
        }

        transitionRenderer.line(leftArrowPoint, dataSet[dataSet.length - 1]);
        transitionRenderer.line(rightArrowPoint, dataSet[dataSet.length - 1]);
    }

    public void drawLabels(SpriteBatch batch, BitmapFont font) {
        batch.setTransformMatrix(mat4);
        batch.begin();
        float posY = 0.0f;
        boolean isFirst = true;
        for (String label : transitionLabels) {
            String text = getBundle().get(label);
            glyphLayout.reset();
            glyphLayout.setText(font, text);
            if (isFirst) {
                isFirst = false;
                posY = ((float)transitionLabels.size() / 2.0f) * glyphLayout.height;
            }
            font.draw(batch, text, -glyphLayout.width / 2.0f, posY);
            posY -= glyphLayout.height;
        }
        batch.end();
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

    public void setManualLoopPositon(Vector2 manualLoopPositon) {
        if (this.manualLoopPositon == null) {
            this.manualLoopPositon = new Vector2();
        }
        this.manualLoopPositon.set(startPoint.x - manualLoopPositon.x, startPoint.y - manualLoopPositon.y);
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
