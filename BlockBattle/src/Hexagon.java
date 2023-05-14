import java.awt.*;
import java.util.ArrayList;

public class Hexagon {
    private static final ArrayList<Integer> ids = new ArrayList<Integer>();
    int id;
    int[] coordinates;
    ArrayList<Hexagon> neighbours = new ArrayList<Hexagon>();
    ArrayList<Integer> neighboursDir = new ArrayList<Integer>(); //Valued 0-5 with 0 being to the right increasing counter-clockwise
    ArrayList<Integer> neighboursFriendlyDir = new ArrayList<Integer>();
    ArrayList<Integer> neighboursEnemyDir = new ArrayList<Integer>();
    double[][] hexCoords;
    int[][] intHexCoords;
    Territory territory;

    public static ArrayList<Hexagon> highlightTest = new ArrayList<Hexagon>();

    public Color neutralColor = Color.gray;


    public Hexagon(Territory territory, int[] coordinates) {
        this.territory = territory;
        this.coordinates = coordinates;
        this.hexCoords = territory.game.mapper.getHexagon(coordinates[0], coordinates[1]);
        this.intHexCoords = territory.game.mapper.getIntHexagon(coordinates[0], coordinates[1]);
        do {
            id = (int) (Math.random() * 1000000);
        } while (ids.contains(id));
        ids.add(id);
    }


    public void render(Graphics2D g) {
        if (territory.ownedBy == null) {
            g.setColor(neutralColor);
            if (territory.game.turnHandler.currentTerritory == territory || territory.game.board.gameStage == 1) {
                g.setColor(Mapper.reduceSaturation(neutralColor, 1, (float) 0.3));
            }

        } else {
            g.setColor(territory.ownedBy.color);
            if (territory.game.turnHandler.currentTerritory == territory||territory.game.turnHandler.attackedTerritory == territory || territory.game.board.gameStage == 1) {
                g.setColor(Mapper.reduceSaturation(territory.ownedBy.color, 1, (float) 0.3));
            }
        }
        this.hexCoords = territory.game.mapper.getHexagon(coordinates[0], coordinates[1]);
        this.intHexCoords = territory.game.mapper.getIntHexagon(coordinates[0], coordinates[1]);

        if (highlightTest.contains(this)){
            g.setColor(Color.yellow);
        }

        g.fillPolygon(intHexCoords[0], intHexCoords[1], 6);
    }

    public void renderFriendlyBorders(Graphics2D g) {

        for (int i = 0; i < 6; i++) {
            int hexRad = territory.game.mapper.hexRad;
            double radius = hexRad / 5.0;
            int angle = i * 60;
            if (!neighboursDir.contains(i)) {
                if (neighboursFriendlyDir.contains(i)) {
                    g.setColor(Mapper.reduceSaturation(territory.ownedBy.color, 1, (float) 0.5));
                    g.fillPolygon(territory.game.mapper.getBorder(hexCoords[0][i], hexCoords[1][i], hexCoords[0][(i + 5) % 6], hexCoords[1][(i + 5) % 6], angle, 10.0));
                    g.fillOval((int) Math.round(hexCoords[0][i] - radius / 2.0), (int) Math.round(hexCoords[1][i] - radius / 2.0), (int) Math.round(radius), (int) Math.round(radius));
                    g.fillOval((int) Math.round(hexCoords[0][(i + 5) % 6] - radius / 2.0), (int) Math.round(hexCoords[1][(i + 5) % 6] - radius / 2.0), (int) Math.round(radius), (int) Math.round(radius));
                }
            }
        }
    }

    public void renderBorders(Graphics2D g) {

        for (int i = 0; i < 6; i++) {
            if (!neighboursDir.contains(i)) {
                if (!neighboursFriendlyDir.contains(i)) {
                    g.setColor(Color.black);

                    int hexRad = territory.game.mapper.hexRad;
                    double radius = hexRad / 5.0;
                    int angle = i * 60;
                    g.fillPolygon(territory.game.mapper.getBorder(hexCoords[0][i], hexCoords[1][i], hexCoords[0][(i + 5) % 6], hexCoords[1][(i + 5) % 6], angle, 10.0));
                    g.fillOval((int) Math.round(hexCoords[0][i] - radius / 2.0), (int) Math.round(hexCoords[1][i] - radius / 2.0), (int) Math.round(radius), (int) Math.round(radius));
                    g.fillOval((int) Math.round(hexCoords[0][(i + 5) % 6] - radius / 2.0), (int) Math.round(hexCoords[1][(i + 5) % 6] - radius / 2.0), (int) Math.round(radius), (int) Math.round(radius));
                }
            }
        }


    }

}
