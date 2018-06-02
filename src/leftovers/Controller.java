package leftovers;

public class Controller {
    private String[] elements;
    private int[] game;

    public Controller() {
        elements = new String[]{"Defense", "Air", "Earth", "Fire", "Water"};
        game = new int[9];
    }

    private int resultRobin(int firstplayerElement, int secondplayerElement) {
        int[] result = { 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, -1, 0, 1, 0, 0, 0, -1, 0, 1, 0, 1, 0, -1, 0 };
        return result[5 * firstplayerElement + secondplayerElement];
        //©Robin
    }

    private int result(int firstplayerElement, int secondplayerElement) {
        int[][] result = {
            {0, 0, 0, 0, 0},//Defense
            {0, 0, 1, 0, -1},//Air
            {0, -1, 0, 1, 0},//Earth
            {0, 0, -1, 0, 1},//Fire
            {0, 1, 0, -1, 0}//Water
        };
        return result[firstplayerElement][secondplayerElement];
        //©Robin
    }
}
