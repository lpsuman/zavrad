package hr.fer.lukasuman.game.level.blocks;

import hr.fer.lukasuman.game.Constants;
import hr.fer.lukasuman.game.level.Direction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class BlockFactory {

    private static class ClassDirectionPair {
        private Class<? extends AbstractBlock> blockClass;
        private Direction direction;

        public ClassDirectionPair(Class<? extends AbstractBlock> blockClass, Direction direction) {
            this.blockClass = blockClass;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClassDirectionPair that = (ClassDirectionPair) o;

            if (!blockClass.equals(that.blockClass)) return false;
            return direction == that.direction;
        }

        @Override
        public int hashCode() {
            int result = blockClass.hashCode();
            result = 31 * result + direction.hashCode();
            return result;
        }
    }

    private static final Direction DEFAULT_DIRECTION = Direction.NORTH;

    private static Set<String> blockTypeNames;
    private static Set<AbstractBlock> blockTypes;
    private static Set<String> transitionTypes;
    private static Map<String, ClassDirectionPair> nameToBlockMap;
    private static Map<Integer, ClassDirectionPair> colorToBlockMap;

    static {
        //TODO add more general block mapping
        blockTypeNames = new HashSet<>(Arrays.asList(StartBlock.LABEL, GoalBlock.LABEL, EmptyBlock.LABEL, WallBlock.LABEL));
        blockTypes = new HashSet<>(Arrays.asList(
                new EmptyBlock(DEFAULT_DIRECTION),
                new WallBlock(DEFAULT_DIRECTION),
                new StartBlock(DEFAULT_DIRECTION),
                new GoalBlock(DEFAULT_DIRECTION)));
        transitionTypes = new HashSet<>(Arrays.asList(StartBlock.LABEL, GoalBlock.LABEL, EmptyBlock.LABEL,
                WallBlock.LABEL, Constants.REMAINING_TRANSITIONS_LABEL));
        nameToBlockMap = new HashMap<>();
        nameToBlockMap.put(EmptyBlock.LABEL, new ClassDirectionPair(EmptyBlock.class, DEFAULT_DIRECTION));
        nameToBlockMap.put(WallBlock.LABEL, new ClassDirectionPair(WallBlock.class, DEFAULT_DIRECTION));
        nameToBlockMap.put(GoalBlock.LABEL, new ClassDirectionPair(GoalBlock.class, DEFAULT_DIRECTION));

        for (int i = 0; i < Direction.values().length; i++) {
            nameToBlockMap.put(StartBlock.LABEL + " " + Direction.getByIndex(i).toString(),
                    new ClassDirectionPair(StartBlock.class, Direction.getByIndex(i)));
        }

        colorToBlockMap = new HashMap<>();
        colorToBlockMap.put(EmptyBlock.COLOR_IN_LEVEL, new ClassDirectionPair(EmptyBlock.class, DEFAULT_DIRECTION));
        colorToBlockMap.put(WallBlock.COLOR_IN_LEVEL, new ClassDirectionPair(WallBlock.class, DEFAULT_DIRECTION));
        colorToBlockMap.put(GoalBlock.COLOR_IN_LEVEL, new ClassDirectionPair(GoalBlock.class, DEFAULT_DIRECTION));

        for (int i = 0; i < StartBlock.COLORS_IN_LEVEL.size(); i++) {
            colorToBlockMap.put(StartBlock.COLORS_IN_LEVEL.get(i), new ClassDirectionPair(StartBlock.class, Direction.getByIndex(i)));
        }
    }

    private BlockFactory() {
    }

    public static AbstractBlock getBlockByName(String name) {
        return getBlockByClass(nameToBlockMap.get(name));
    }

    public static AbstractBlock getBlocByColor(int color) {
        return getBlockByClass(colorToBlockMap.get(color));
    }

    private static AbstractBlock getBlockByClass(ClassDirectionPair classDirectionPair) {
        if (classDirectionPair == null) {
            return null;
        }
        Class<? extends AbstractBlock> blockClass = classDirectionPair.blockClass;
        Direction direction = classDirectionPair.direction;
        if (blockClass == null || direction == null) {
            return null;
        }
        try {
            Constructor<? extends AbstractBlock> ctor = blockClass.getConstructor(Direction.class);
            AbstractBlock newBlock = ctor.newInstance(new Object[] { direction });
            return newBlock;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<String> getBlockTypeNames() {
        return blockTypeNames;
    }

    public static Set<AbstractBlock> getBlockTypes() {
        return blockTypes;
    }

    public static Set<String> getTransitionTypes() {
        return transitionTypes;
    }

    public static boolean isStart(AbstractBlock block) {
        return StartBlock.LABEL.equals(block.getLabel());
    }

    public static boolean isGoal(AbstractBlock block) {
        return GoalBlock.LABEL.equals(block.getLabel());
    }
}
