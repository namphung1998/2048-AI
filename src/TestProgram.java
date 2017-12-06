import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by phungvanquan on 30/11/2017.
 */
public class TestProgram {
    private static final int TEST_TIME = 100;

    public static void main(String[] args) {
        BaseAI playerAI =  new PlayerAI();
        BaseAI randomAI = new RandomAI();


        ArrayList<Integer> maxVals = new ArrayList<>(TEST_TIME);
        for (int i = 1; i < TEST_TIME + 1; i++) {
            System.out.println("Test game: " + i);
            Game game = new Game(playerAI);
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
            output = output + key + "-" + (count.get(key) / (TEST_TIME / 100)) + "% ";
        }

        System.out.println(output);
    }
}
