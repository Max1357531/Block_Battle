import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.HashSet;

public class KeyInput extends KeyAdapter {
    public Game game;

    public KeyInput(Game game) {
        this.game = game;
    }

    public void keyPressed(KeyEvent event) {
        int key = event.getKeyCode();
        if (key == KeyEvent.VK_DOWN) {
            if (Mapper.yScale >= 0.5) {
                Mapper.yScale -= 0.1;
            }
        }
        if (key == KeyEvent.VK_UP) {
            if (Mapper.yScale <= 1) {
                Mapper.yScale += 0.1;
            }
        }
        if (key == KeyEvent.VK_W) {
            Mapper.yRef -= 10;
        }
        if (key == KeyEvent.VK_D) {
            Mapper.xRef += 10;
        }
        if (key == KeyEvent.VK_S) {
            Mapper.yRef += 10;
        }
        if (key == KeyEvent.VK_A) {
            Mapper.xRef -= 10;
        }
        if (key == KeyEvent.VK_R) {
            game.board.gameStage = -1;
        }
        if (key == KeyEvent.VK_SPACE) {
            game.turnHandler.endTurnClicked();
        }


        if (key == KeyEvent.VK_O) {
            Hexagon h = game.board.territories.get(0).getHexes().get(0);
            int[] coords = h.coordinates;
            for (int[] deltas : Board.getDeltasNAway(5)) {
                Hexagon h2 = game.board.findHexagon(coords[0] + deltas[0], coords[1] + deltas[1]);
                if (!(h2 == null)) {
                    h2.neutralColor = Color.red;
                }

            }
        }
        if (key == KeyEvent.VK_P) {
            System.out.println(game.turnHandler.currentTerritory);
        }
    }
}
