import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Test program for the AI solver of 2048.
 * This program uses the AI solver to solve a number of games and produces statistics about success rate of the algorithm
 *
 * Created by Nam Phung on 30/11/2017.
 */
public class TestProgram {
    private static final int TEST_TIME = 1000;

    private static String getOutPut(ArrayList<Integer> values) {
        Map<Integer, Integer> count = new TreeMap<>();

        for (int value : values) {
            if (count.containsKey(value)) {
                count.put(value, count.get(value) + 1);
            } else {
                count.put(value, 1);
            }
        }

        String output = "";

        for (int key : count.keySet()) {
            output = output + key + "-" + (count.get(key) / (TEST_TIME / 100)) + "% ";
        }

        return output;
    }

    public static void main(String[] args) {
        BaseAI playerAI =  new PlayerAI();
        BaseAI randomAI = new RandomAI();

        ArrayList<Integer> maxVals = new ArrayList<>(TEST_TIME);

        for (int i = 1; i < TEST_TIME + 1; i++) {
            System.out.println("Test game: " + i);
            Game game = new Game(playerAI);
            game.runWithTimer();

            maxVals.add(game.getBoard().getMaxTile());
        }

        String output = getOutPut(maxVals);

        System.out.println(output);
    }
}
