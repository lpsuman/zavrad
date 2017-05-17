package hr.fer.lukasuman.game.level.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BlockFactory {

    private static Map<String, Class<? extends AbstractBlock>> nameToBlockMap;
    private static Map<Integer, Class<? extends AbstractBlock>> colorToBlockMap;

    static {
        nameToBlockMap = new HashMap<>();
        nameToBlockMap.put(EmptyBlock.LABEL, EmptyBlock.class);
        nameToBlockMap.put(WallBlock.LABEL, WallBlock.class);
        nameToBlockMap.put(StartBlock.LABEL, StartBlock.class);
        nameToBlockMap.put(GoalBlock.LABEL, GoalBlock.class);

        colorToBlockMap = new HashMap<>();
        colorToBlockMap.put(EmptyBlock.COLOR_IN_LEVEL, EmptyBlock.class);
        colorToBlockMap.put(WallBlock.COLOR_IN_LEVEL, WallBlock.class);
        colorToBlockMap.put(StartBlock.COLOR_IN_LEVEL, StartBlock.class);
        colorToBlockMap.put(GoalBlock.COLOR_IN_LEVEL, GoalBlock.class);
    }

    private BlockFactory() {
    }

    public static AbstractBlock getBlockByName(String name) {
        return getBlockByClass(nameToBlockMap.get(name));
    }

    public static AbstractBlock getBlocByColor(int color) {
        return getBlockByClass(colorToBlockMap.get(color));
    }

    private static AbstractBlock getBlockByClass(Class<? extends AbstractBlock> blockClass) {
        if (blockClass == null) {
            return null;
        }
        try {
            return blockClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<String> getBlockTypes() {
        return nameToBlockMap.keySet();
    }
}
