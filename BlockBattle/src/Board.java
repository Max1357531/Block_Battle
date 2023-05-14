import java.awt.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Board {
    int i;
    public Game game;
    public ArrayList<Nation> nations = new ArrayList<Nation>();
    public ArrayList<Territory> territories = new ArrayList<Territory>();
    public static Map<Integer, int[]> neighbours = new HashMap<Integer, int[]>();
    public int noTerritories;
    public int noCol;
    public int noRow;
    public int gameStage = -1;
    int terrBuff = 0;
    int[] natBuff = new int[0];


    public Board(Game game, int noTerritories, int width, int height) {
        this.game = game;
        this.noTerritories = noTerritories;
        this.noCol = width;
        this.noRow = (int) (height / Mapper.yScale * 0.7);

        neighbours.put(0, new int[]{1, 0});
        neighbours.put(1, new int[]{0, 1});
        neighbours.put(2, new int[]{-1, 1});
        neighbours.put(3, new int[]{-1, 0});
        neighbours.put(4, new int[]{0, -1});
        neighbours.put(5, new int[]{1, -1});

        getDeltasNAway(2);

    }

    public void tick() {

        while (gameStage == -1) {
            i = 0;
            territories.clear();
            nations.clear();
            gameStage = 0;
            game.turnHandler.currentTerritory = null;
            game.reRender = 5;
        }

        while(gameStage == 0){
            if (territories.size() < noTerritories) {
                boolean done = false;
                while (!done) {
                    int xCart = (int) (Math.random() * noCol);
                    int yCart = (int) (Math.random() * noRow);
                    int xCoord;
                    int yCoord;
                    if (yCart % 2 == 0) {
                        xCoord = (int) Math.round(-yCart / 2.0);
                        yCoord = yCart;
                    } else {
                        xCoord = (int) Math.round((-yCart + 1) / 2.0);
                        yCoord = yCart;
                    }
                    xCoord += xCart;

                    if (findHexagon(xCoord, yCoord) == null && overpopulationLevel(xCoord, yCoord) <= Math.random()) {
                        territories.add(new Territory(new int[][]{{xCoord, yCoord}}, game));
                        done = true;
                    }

                }

            } else if (!allConnected()) {

                for (Nation nation : nations) {
                    nation.tick();
                }
                for (Territory territory : territories) {
                    territory.getHexes().get(0).neutralColor = Color.red;
                    territory.expandTerritory(false);
                    territory.getHexes().get(0).neutralColor = Color.lightGray;
                    territory.generateTerritoryNeighbours();
                }
            } else if (i < 500) {
                boolean none = true;

                for (Territory territory : territories) {
                    i++;
                    if (territory.neighbours.size() <= 2 || i < 50) {
                        territory.expandTerritory(true);
                        territory.generateTerritoryNeighbours();
                        none = false;
                    }
                }
                if (none && i > 50) {
                    i = 500;
                }
            } else if (i == 500) {
                i++;

                for (int b = 0; b < 50; b++) {
                    for (int yHex = 0; yHex < noRow; yHex++) {
                        for (int xHex = -(int) (yHex / 2.0); xHex < noCol - (int) (yHex / 2.0); xHex++) {
                            if (findHexagon(xHex, yHex) == null) {
                                HashSet<Hexagon> neighbours = findNeighboursFromCoordinates(xHex, yHex);
                                if (neighbours.size() >= 5) {
                                    int itemIndex = (int) (Math.random() * neighbours.size());
                                    int a = 0;
                                    for (Hexagon hex : neighbours) {
                                        if (a == itemIndex) {
                                            Territory t = hex.territory;
                                            t.hexes.add(new Hexagon(t, new int[]{xHex, yHex}));
                                            t.generateNeighbours();
                                            t.generateTerritoryNeighbours();
                                        }
                                        a++;
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                System.out.println("done");
                gameStage = 1;
            }
        }

    }

    public void nationTick() {
        boolean allCorrect = false;
        while(!allCorrect && gameStage != -2) {
            nations.clear();
            for (Territory t : territories) {
                t.ownedBy = null;
            }
            for (int i = 1; i <= Game.noPlayers; i++) {
                nations.add(new Nation(Color.getHSBColor((float) (1.0 / Game.noPlayers) * i, 1, (float) 0.8), game,i-1));
            }

            for (int i = 0; i < territories.size(); i++) {
                boolean found = false;
                while (!found) {
                    int random = (int) (Math.random() * territories.size());
                    if (territories.get(random).ownedBy == null) {
                        territories.get(random).ownedBy = nations.get(i % nations.size());
                        found = true;
                    }
                }


            }
            int totalDraft = 0;
            for (Nation nation : nations) {
                totalDraft += nation.calculateDraft();
            }
            double averageDraft = (1.0 * totalDraft) / nations.size();
            allCorrect = true;
            for (Nation nation : nations) {
                int draft = nation.calculateDraft();
                if (draft > averageDraft + 0.5 || draft < averageDraft - 0.5) {
                    allCorrect = false;
                }
            }
        }


        if (allCorrect && gameStage == 1) {
            for (Territory terr: territories){
                terr.generateTerritoryNeighbours();
                terr.generateNeighbours();
                terr.findCapital();
            }

            gameStage = -2;
            terrBuff = territories.size();
            natBuff = new int[nations.size()];
            for (int i = 0; i < natBuff.length; i++){
                natBuff[i] += territories.size()/nations.size()*2;
            }
        }

        else if (gameStage == -2){
            if (terrBuff >0){
                territories.get(terrBuff-1).numDice = 1;
                territories.get(terrBuff-1).calculateDiceGeometry();
                terrBuff -= 1;
            }
            if (terrBuff == 0 && Arrays.stream(natBuff).sum() > 0){
                boolean check = false;
                while (!check){
                    int rand = ThreadLocalRandom.current().nextInt(0, territories.size());
                    if (natBuff[territories.get(rand).ownedBy.number] > 0){
                        if (territories.get(rand).numDice <8){

                            territories.get(rand).numDice += 1;
                            natBuff[territories.get(rand).ownedBy.number] -= 1;
                            territories.get(rand).calculateDiceGeometry();
                            check = true;
                        }
                    }
                }


            }
            if (terrBuff == 0 && Arrays.stream(natBuff).sum() == 0){
                gameStage = 2;
                game.turnHandler.calculateDiceGeometry();
                for (Territory territory : territories) {
                    territory.generateNeighbours();
                    territory.generateTerritoryNeighbours();

                    territory.calculateDiceGeometry();
                }
                game.reRender = 3;
            }


        }
    }


    public ArrayList<Hexagon> getAllHexagons() {
        ArrayList<Hexagon> all = new ArrayList<>();
        for (Territory t : territories) {
            all.addAll(t.getHexes());
        }
        return all;
    }
    public ArrayList<Hexagon> getAllCapitals() {
        ArrayList<Hexagon> all = new ArrayList<>();
        for (Territory t : territories) {
            all.add(t.capital);
        }
        return all;
    }


    public HashSet<Territory> connectedSameNation(Territory original) {
        boolean done = false;
        HashSet<Territory> territories = new HashSet<>();
        territories.add(original);
        while (!done) {
            int size = territories.size();
            HashSet<Territory> iterator = (HashSet<Territory>) territories.clone();
            for (Territory t : iterator) {
                for (Territory t2 : t.neighbours) {
                    if (original.ownedBy == t2.ownedBy) {
                        territories.add(t2);
                    }
                }
            }

            if (size == territories.size()) {
                done = true;
            }
        }
        return territories;
    }

    public boolean allConnected() {
        HashSet<Territory> connected = new HashSet<>();
        connected.add(territories.get(0));
        int count = 0;
        while (count != connected.size()) {
            count = connected.size();
            HashSet<Territory> temporary = new HashSet<>();
            for (Territory t : connected) {
                temporary.addAll(t.neighbours);
            }
            connected.addAll(temporary);
        }
        return connected.size() == Game.noTerritories;
    }

    public void render(Graphics2D g) {



        for (int a = 0; a < nations.size(); a++) {
            nations.get(a).render(g, nations.size(), a);
        }
        if (gameStage == 2){
            game.board.nations.get(game.turnHandler.currentNation).render(g,nations.size(),game.turnHandler.currentNation);
        }



        for (Territory territory : territories) {
            if (game.reRender > 0 || territory.partRend >0){
                territory.render(g);
            }

        }

        for (Hexagon hexagon : getAllHexagons()) {
            if (game.reRender > 0 || hexagon.territory.partRend >0) {
                hexagon.renderFriendlyBorders(g);
            }
        }

        for (Hexagon hexagon : getAllHexagons()) {
            if (game.reRender > 0 || hexagon.territory.partRend >0) {
                hexagon.renderBorders(g);
            }
        }



        for (Territory territory : territories) {
            territory.diceRender(g);
        }

        for (Territory territory : territories) {
            if (territory.partRend >0){
                territory.partRend -= 1;
            }
        }




    }


    public double overpopulationLevel(int xCoord, int yCoord) {
        double popLevel = 0;
        popLevel += 10 * neighboursNAway(xCoord, yCoord, 1);
        popLevel += neighboursNAway(xCoord, yCoord, 2);
        popLevel += 0.05 * neighboursNAway(xCoord, yCoord, 3);

        return popLevel / 10.0;

    }

    public int neighboursNAway(int xCoord, int yCoord, int n) {
        int num = 0;
        for (int[] deltas : getDeltasNAway(n)) {
            if (!(findHexagon(xCoord + deltas[0], yCoord + deltas[1]) == null)) {
                num += 1;
            }
        }
        return num;
    }

    public static HashSet<int[]> getDeltasNAway(int n) {
        HashSet<Integer> intDeltas = getDeltasNAwayTemp(n);
        HashSet<int[]> finalDeltas = new HashSet<>();
        for (int intDelta : intDeltas) {
            int y = (intDelta + 1000000) % 10000;
            if (y > 5000) {
                y = y - 10000;
            }
            int x = (intDelta - y) / 10000;
            finalDeltas.add(new int[]{x, y});
        }
        return finalDeltas;
    }

    public static HashSet<Integer> getDeltasNAwayTemp(int n) {
        HashSet<Integer> finalDeltas = new HashSet<>();
        HashSet<Integer> nMinus2 = new HashSet<>();
        if (n == 1) {
            for (int[] deltas : neighbours.values()) {
                finalDeltas.add(10000 * deltas[0] + deltas[1]);
            }
            return finalDeltas;
        }
        if (n == 2) {
            nMinus2.add(0);
        } else {
            nMinus2.addAll(getDeltasNAwayTemp(n - 2));
        }
        HashSet<Integer> nMinus1 = getDeltasNAwayTemp(n - 1);

        for (int deltaN1 : nMinus1) {
            for (int delta : getDeltasNAwayTemp(1)) {
                int newDelta = (deltaN1 + delta);
                if (!(nMinus1.contains(newDelta) || nMinus2.contains(newDelta))) {
                    finalDeltas.add(newDelta);
                }

            }
        }
        return finalDeltas;

    }

    public boolean withinBounds(int xHex, int yHex) {
        int yCart = yHex;
        int xCart = xHex + (int) (yCart / 2.0);
        return yCart >= 0 && yCart < noRow && xCart >= 0 && xCart < noCol;
    }


    public Hexagon findHexagon(int xCoord, int yCoord) {
        for (Territory territory : territories) {
            for (Hexagon hex : territory.getHexes()) {
                int[] coords = hex.coordinates;
                if ((xCoord == coords[0]) && (yCoord == coords[1])) {
                    return hex;
                }
            }
        }
        return null;
    }

    public HashSet<Hexagon> findNeighboursFromCoordinates(int xCoord, int yCoord) {
        HashSet<Hexagon> list = new HashSet<>();
        for (int[] delta : Board.getDeltasNAway(1)) {
            Hexagon h = findHexagon(xCoord + delta[0], yCoord + delta[1]);
            if (h != null) {
                list.add(h);
            }
        }
        return list;
    }
}
