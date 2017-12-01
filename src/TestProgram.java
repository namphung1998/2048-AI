import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by phungvanquan on 30/11/2017.
 */
public class TestProgram {
    public static void main(String[] args) {
        ArrayList<Integer> maxVals = new ArrayList<>(100);
        for (int i = 1; i < 101; i++) {
            Game game = new Game();
            game.run();

            maxVals.add(game.getBoard().getMaxTile());
        }

        Map<Integer, Integer> count = new TreeMap<>();

        for (int val : maxVals) {
            if (count.containsKey(val)) {
                count.put(val, count.get(val) + 1);
            } else {
                count.put(val, 1);
            }
        }

        String output = "";

        for (int key : count.keySet()) {
            output = output + key + "-" + count.get(key) + "% ";
        }

        System.out.println(output);
    }
}
