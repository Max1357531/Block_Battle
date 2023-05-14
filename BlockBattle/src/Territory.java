import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class Territory {

    ArrayList<Hexagon> hexes = new ArrayList<>();
    public Hexagon capital;
    private static final ArrayList<Integer> ids = new ArrayList<>();
    int id;
    public Game game;
    public Nation ownedBy = null;
    public HashSet<Territory> neighbours = new HashSet<>();
    public LinkedHashSet<Path2D> diceGeometry = new LinkedHashSet<>();

    public ArrayList<Hexagon> getHexes() {
        return hexes;
    }

    public int partRend = 0;


    public int numDice = 0;

    public Territory(int[][] coords, Game game) {
        this.game = game;
        generateHexes(coords);
        generateNeighbours();
        do {
            id = (int) (Math.random() * 1000000);
        } while (ids.contains(id));
        ids.add(id);
    }

    public void generateHexes(int[][] coordinatesList) {
        for (int[] ints : coordinatesList) {
            hexes.add(new Hexagon(this, ints));
        }
        capital = hexes.get(0);
    }

    public void generateNeighbours() {
        for (Hexagon hex : hexes) {
            hex.neighbours.clear();
            hex.neighboursDir.clear();
            hex.neighboursFriendlyDir.clear();
            for (Hexagon potentialNeighbour : hexes) {
                int[][] deltas = new int[][]{{0, -1, 4}, {1, -1, 5}, {1, 0, 0}, {0, 1, 1}, {-1, 1, 2}, {-1, 0, 3}};

                for (int[] delta : deltas) {
                    if (hex.coordinates[0] + delta[0] == potentialNeighbour.coordinates[0]) {
                        if (hex.coordinates[1] + delta[1] == potentialNeighbour.coordinates[1]) {
                            hex.neighbours.add(potentialNeighbour);
                            hex.neighboursDir.add(delta[2]);

                        }
                    }
                }
            }
            if (game.board.gameStage == 2) {
                int[][] deltas = new int[][]{{0, -1, 4}, {1, -1, 5}, {1, 0, 0}, {0, 1, 1}, {-1, 1, 2}, {-1, 0, 3}};
                for (int[] delta : deltas) {
                    Hexagon neighbour = game.board.findHexagon(hex.coordinates[0] + delta[0], hex.coordinates[1] + delta[1]);
                    if (neighbour != null) {
                        if (neighbour.territory.ownedBy == this.ownedBy) {
                            hex.neighboursFriendlyDir.add(delta[2]);
                        } else {
                            hex.neighboursEnemyDir.add(delta[2]);
                        }
                    }


                }

            }
        }
    }


    public void generateTerritoryNeighbours() {
        neighbours.clear();
        for (Hexagon hex : hexes) {
            for (int[] deltas : Board.getDeltasNAway(1)) {
                Hexagon neighbour = game.board.findHexagon(hex.coordinates[0] + deltas[0], hex.coordinates[1] + deltas[1]);
                if (neighbour != null) {
                    if (neighbour.territory != this) {
                        neighbours.add(neighbour.territory);
                    }
                }
            }
        }
    }


    public void expandTerritory(boolean forced) {
        boolean done = Math.random() >= Math.pow(Math.E, -hexes.size() / 10.0) && !forced;
        int i = 0;
        while (!done) {
            int[] coords = hexes.get((int) (Math.random() * hexes.size())).coordinates;
            int[] delta = Board.neighbours.get((int) (Math.random() * 6));
            Hexagon randomNeighbour = game.board.findHexagon(coords[0] + delta[0], coords[1] + delta[1]);
            if ((randomNeighbour == null) && game.board.withinBounds(coords[0] + delta[0], coords[1] + delta[1])) {
                hexes.add(new Hexagon(this, new int[]{coords[0] + delta[0], coords[1] + delta[1]}));

                generateNeighbours();
                done = true;
            }

            i++;
            if ((i >= 30 && !forced) || (i > 500)) {
                done = true;
            }
        }
    }


    public void render(Graphics2D g) {
        for (Hexagon hex : hexes) {
            hex.render(g);
        }

    }

    public static void partRender(Territory t){
        if (t != null){
            t.partRend = 5;
        }
    }

    static public Point interpolate(Point p1, Point p2, double t) {
        return new Point((int) Math.round(p1.x * (1 - t) + p2.x * t),
                (int) Math.round(p1.y * (1 - t) + p2.y * t));
    }

    public void findCapital() {
        double bestValue = 0;
        Hexagon bestHex = hexes.get(0);
        HashSet<int[]> delOne = Board.getDeltasNAway(1);
        HashSet<int[]> delTwo = Board.getDeltasNAway(2);
        int[][] delCapBase = {{1,0},{0,1}};
        int[][] enemCapBase = {{1,-1},{0,-1},{1,-2},{1,-3},{1,-1},{2,-2},{2,-3},{-1,1},{-1,2},{-2,3},{0,1},{0,2},{-1,3}};
        for (Hexagon hex : hexes) {
            double capVal = 0;
            for (int[] delta : delOne) {
                for (Hexagon potentialNeighbour : hexes) {
                    if (hex.coordinates[1] + delta[1] == potentialNeighbour.coordinates[1] && hex.coordinates[0] + delta[0] == potentialNeighbour.coordinates[0]) {
                        capVal += 1;
                        break;
                    }
                }
            }
            for (int[] delta : delTwo) {
                for (Hexagon potentialNeighbour : hexes) {
                    if (hex.coordinates[1] + delta[1] == potentialNeighbour.coordinates[1] && hex.coordinates[0] + delta[0] == potentialNeighbour.coordinates[0]) {
                        capVal += 0.2;
                        break;
                    }
                }
            }
            for (int[] delta: delCapBase){
                for (Hexagon potentialNeighbour : hexes) {
                    if (hex.coordinates[1] + delta[1] == potentialNeighbour.coordinates[1] && hex.coordinates[0] + delta[0] == potentialNeighbour.coordinates[0]) {
                        capVal += 10;
                        break;
                    }
                }
            }
            for (int[] delta: enemCapBase){
                for (Hexagon potentialNeighbourCap : game.board.getAllCapitals()) {
                    if (potentialNeighbourCap.territory.ownedBy != this.ownedBy){
                        if (hex.coordinates[1] + delta[1] == potentialNeighbourCap.coordinates[1] && hex.coordinates[0] + delta[0] == potentialNeighbourCap.coordinates[0]) {
                            System.out.println("_______");
                            capVal -= 15;
                            break;
                        }
                    }

                }
            }

            if (capVal > bestValue) {
                bestHex = hex;
                bestValue = capVal;
            }

        }
        capital = bestHex;

    }




    public void calculateDiceGeometry() {
        diceGeometry.clear();
        for (int dice = 1; dice <= numDice; dice++) {

            double[][] cube = game.mapper.getCube(capital.coordinates[0], capital.coordinates[1], dice);


            int[][] faces = new int[][]{{0, 1, 6, 5}, {6, 1, 2, 3}, {3, 4, 5, 6}};
            for (int[] face : faces) {

                    //g.drawLine((int)cube[0][face[i]], (int)cube[1][face[i]], (int)cube[0][face[(i+1)%4]], (int)cube[1][face[(i+1)%4]]);

                    Point p1 = new Point((int) cube[0][face[0]], (int) cube[1][face[0]]);
                    Point p2 = new Point((int) cube[0][face[1]], (int) cube[1][face[1]]);
                    Point p3 = new Point((int) cube[0][face[2]], (int) cube[1][face[2]]);
                    Point p4 = new Point((int) cube[0][face[3]], (int) cube[1][face[3]]);

                    Point p1p2a = interpolate(p1, p2, 0.2);
                    Point p1p2b = interpolate(p1, p2, 0.8);

                    Point p2p3a = interpolate(p2, p3, 0.2);
                    Point p2p3b = interpolate(p2, p3, 0.8);

                    Point p3p4a = interpolate(p3, p4, 0.2);
                    Point p3p4b = interpolate(p3, p4, 0.8);

                    Point p4p1a = interpolate(p4, p1, 0.2);
                    Point p4p1b = interpolate(p4, p1, 0.8);


                    QuadCurve2D c1 = new QuadCurve2D.Double(p1p2b.x, p1p2b.y, p2.x, p2.y, p2p3a.x, p2p3a.y);
                    QuadCurve2D c2 = new QuadCurve2D.Double(p2p3b.x, p2p3b.y, p3.x, p3.y, p3p4a.x, p3p4a.y);
                    QuadCurve2D c3 = new QuadCurve2D.Double(p3p4b.x, p3p4b.y, p4.x, p4.y, p4p1a.x, p4p1a.y);
                    QuadCurve2D c4 = new QuadCurve2D.Double(p4p1b.x, p4p1b.y, p1.x, p1.y, p1p2a.x, p1p2a.y);


                    Path2D path = new Path2D.Double();
                    AffineTransform at = new AffineTransform();
                    path.moveTo(p1p2a.x, p1p2a.y);
                    path.lineTo(p1p2b.x, p1p2b.y);
                    path.append(c1.getPathIterator(at), true);
                    path.lineTo(p2p3b.x, p2p3b.y);
                    path.append(c2.getPathIterator(at), true);
                    path.lineTo(p3p4b.x, p3p4b.y);
                    path.append(c3.getPathIterator(at), true);
                    path.lineTo(p4p1b.x, p4p1b.y);
                    path.append(c4.getPathIterator(at), true);
                    path.closePath();
                    diceGeometry.add(path);

            }
        }

    }

    public void diceRender(Graphics2D g) {

        for (Path2D path : diceGeometry) {

            g.setColor(Mapper.reduceSaturation(ownedBy.color, (float) 0.5, (float) 0.9));

            g.fill(path);

            g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

            g.setColor(Color.black);

            if (this == game.turnHandler.currentTerritory) {
                if(game.turnHandler.attackStage == 0){
                    int red;
                    int fazeLevel = game.tickcount % 25;
                    if (fazeLevel <= 10) {
                        red = (int) (255 / 10.0 * fazeLevel);
                        g.setColor(new Color(red, 0, 0));
                    } else if (fazeLevel <= 20){
                        red = (int) (255 / 10.0 * (20 - fazeLevel));
                        g.setColor(new Color(red, 0, 0));
                    }
                }

            }



            g.draw(path);

        }

    }


}
