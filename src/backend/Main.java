package backend;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        //Start new game
        String[] players = new String[] {"Joris", "Stockfish"};
        int[] stars = new int[] {42, 112};
        Game avatarcarto = new Game(players, stars);
        Engine stockfish = new Engine();
        System.out.println("Setup ready");

        for (int i = 0; i < 9; i++) {
            System.out.print("\n[" + (i + 1) + "]. Human do your move (w,f,e,a,d): ");
            char move = input.next().charAt(0);
            avatarcarto.doMove(move);
            System.out.println("[" + (i + 1) + "]. You did move: " + avatarcarto.getMove(0, i));
            avatarcarto.doMove(stockfish.getMove(i));
            System.out.println("[" + (i + 1) + "]. Engine did move: " + avatarcarto.getMove(1, i));
            System.out.println("Score: " + avatarcarto.getScore(0) + " - " + avatarcarto.getScore(1));
        }
    }
}
