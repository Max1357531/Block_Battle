import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TurnHandler {
    private final Game game;
    public int currentNation = 0;
    public int tickCount;
    public Territory aiAttack;
    public int aiBuffer;
    public int[] playerComp;

    public static int AI_TICKSPEED = 2;
    public static int HUMAN_TICKSPEED = 5;
    public int globalTickspeed;
    public Territory currentTerritory;
    public Territory attackedTerritory;
    public int attackStage = 0;
    public int draftStage = 0;
    public int[] attRolls = new int[8];
    public Path2D[] attDiceGeo = new Path2D[8];
    public int[] defRolls = new int[8];
    public Path2D[] defDiceGeo = new Path2D[8];
    public int END_HEIGHT = 80;
    public int END_WIDTH = 100;
    public double AI_AGGRESSION = 0.5;
    public boolean playerTurnIndicator  = true;

    public TurnHandler(Game game, int noPlayers, int noHumanPlayers) {

        this.game = game;
        this.tickCount = 0;
        this.playerComp = new int[noPlayers];
        for (int i = 0; i < noHumanPlayers; i++) {
            playerComp[i] = 1;
        }

    }

    public void endTurnClicked(){
        System.out.println("a");
        if (game.board.gameStage == 2){
            System.out.println("b");
            if (playerComp[currentNation] == 1){
                System.out.println("c");
                if(attackStage == 0 && draftStage == 0){
                    System.out.println("d");
                    draftStage = 1;
                }
            }
        }

    }

    public void render(Graphics2D g) {




        int ycentral = (int) (Game.HEIGHT * 0.8) + 45-5;
        int xcentral = (int)((Game.WIDTH-20)/2.0)-5;
        int[] ycentres = new int[16];
        int[] xcentres = new int[16];


        if (playerComp[currentNation] == 1){
            g.setColor(Mapper.reduceSaturation(game.board.nations.get(currentNation).color, (float) 0.5, (float) 0.9));
            g.fillRect(Game.WIDTH-END_WIDTH*2,(int)(Game.HEIGHT*0.8)+END_HEIGHT/3,END_WIDTH,END_HEIGHT);
            Rectangle rect = new Rectangle(Game.WIDTH-2*END_WIDTH,(int)(Game.HEIGHT*0.8)+END_HEIGHT/3,END_WIDTH,END_HEIGHT);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
            g.drawRect(Game.WIDTH-END_WIDTH*2,(int)(Game.HEIGHT*0.8)+END_HEIGHT/3,END_WIDTH,END_HEIGHT);

            Rectangle rect2 = new Rectangle(Game.WIDTH-2*END_WIDTH,(int)(Game.HEIGHT*0.8)+END_HEIGHT/3+END_HEIGHT/4,END_WIDTH,END_HEIGHT/4);
            Rectangle rect3 = new Rectangle(Game.WIDTH-2*END_WIDTH,(int)(Game.HEIGHT*0.8)+END_HEIGHT/3+END_HEIGHT*2/4,END_WIDTH,END_HEIGHT/4);
            drawCenteredString(g,"End Turn",rect2,new Font ("SANS_SERIF", Font.BOLD | Font.ITALIC, 20));
            drawCenteredString(g,"(Space)",rect3,new Font ("SANS_SERIF", Font.BOLD | Font.ITALIC, 20));
        }



        for (int i= 0; i < 16; i++){
            if (i % 2 == 0){
                ycentres[i] = ycentral-DICE_WIDTH/2 - 3;
            }
            else{
                ycentres[i] = ycentral+DICE_WIDTH/2 + 3;
            }
            if (i < 8){
                xcentres[i] = xcentral - DICE_WIDTH*2 - (DICE_WIDTH+6)*(i/2);
            }
            else{
                xcentres[i] = xcentral + DICE_WIDTH*2 + (DICE_WIDTH+6)*((i-8)/2);
            }

        }


        if (currentTerritory != null) {
            if (attackStage >= 1) {

                for (int i = 0; i < currentTerritory.numDice; i++) {
                    g.setColor(Mapper.reduceSaturation(currentTerritory.ownedBy.color, (float) 0.5, (float) 0.9));
                    g.fill(attDiceGeo[i]);
                    g.setColor(Color.black);
                    g.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                    g.draw(attDiceGeo[i]);
                    int[][] pips = game.mapper.getDicePips(xcentres[i], ycentres[i], DICE_WIDTH, attRolls[i]);
                    g.setColor(Mapper.reduceSaturation(currentTerritory.ownedBy.color, (float) 0.8, (float) 0.2));

                    for (int[] pip : pips) {
                        g.fillOval(pip[0], pip[1], DICE_WIDTH / 6, DICE_WIDTH / 6);
                    }


                }

                if (attackStage >=globalTickspeed*3){
                    for (int i = 8; i < attackedTerritory.numDice+8; i++) {
                        g.setColor(Mapper.reduceSaturation(attackedTerritory.ownedBy.color, (float) 0.5, (float) 0.9));
                        g.fill(defDiceGeo[i-8]);
                        g.setColor(Color.black);
                        g.setStroke(new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                        g.draw(defDiceGeo[i-8]);
                        int[][] pips = game.mapper.getDicePips(xcentres[i],ycentres[i],DICE_WIDTH,defRolls[i-8]);
                        g.setColor(Mapper.reduceSaturation(attackedTerritory.ownedBy.color, (float) 0.8, (float) 0.2));
                        for(int[] pip :pips){
                            g.fillOval(pip[0],pip[1],DICE_WIDTH/6,DICE_WIDTH/6);
                        }
                    }
                }
                if (attackStage >= globalTickspeed*2){
                    g.setColor(Mapper.reduceSaturation(currentTerritory.ownedBy.color, (float) 0.5, (float) 0.9));
                    fillCenteredCircle(g,xcentral-(int)(DICE_WIDTH*0.7),ycentral,DICE_WIDTH);
                    g.setColor(Color.BLACK);
                    drawCenteredCircle(g,xcentral-(int)(DICE_WIDTH*0.7),ycentral,DICE_WIDTH);
                    Rectangle rect = new Rectangle(xcentral-(int)(DICE_WIDTH*0.7)-1,ycentral-1,2,2);
                    drawCenteredString(g,String.valueOf(Arrays.stream(attRolls).sum()),rect,new Font ("TimesRoman", Font.BOLD | Font.ITALIC, 20));
                }
                if (attackStage >= globalTickspeed*5){
                    g.setColor(Mapper.reduceSaturation(attackedTerritory.ownedBy.color, (float) 0.5, (float) 0.9));
                    fillCenteredCircle(g,xcentral+(int)(DICE_WIDTH*0.7),ycentral,DICE_WIDTH);
                    g.setColor(Color.BLACK);
                    drawCenteredCircle(g,xcentral+(int)(DICE_WIDTH*0.7),ycentral,DICE_WIDTH);
                    Rectangle rect = new Rectangle(xcentral+(int)(DICE_WIDTH*0.7)-1,ycentral-1,2,2);
                    drawCenteredString(g,String.valueOf(Arrays.stream(defRolls).sum()),rect,new Font ("TimesRoman", Font.BOLD | Font.ITALIC, 20));
                }

            }
        }



    }

    public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.drawOval(x,y,r,r);
    }

    public void fillCenteredCircle(Graphics2D g, int x, int y, int r) {
        x = x-(r/2);
        y = y-(r/2);
        g.fillOval(x,y,r,r);
    }

    public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public void tick() {
        if (playerComp[currentNation] == 0){
            globalTickspeed = AI_TICKSPEED;
        }
        else{
            globalTickspeed = HUMAN_TICKSPEED;
        }


        tickCount++;
        if (attackedTerritory != null && attackStage == 0) {
            attackStage = 1;
        }
        if (attackStage != 0) {
            for(Territory terr: game.board.territories){
                terr.calculateDiceGeometry();
            }
            if (attackStage <= globalTickspeed){
                for(int i = 0; i < 8; i++){
                    if (i < currentTerritory.numDice){
                        attRolls[i] = ThreadLocalRandom.current().nextInt(1, 6 + 1);
                    }
                    else{
                        attRolls[i] = 0;
                    }
                }
            }
            else if (attackStage <= globalTickspeed*4){
                for(int i = 0; i < 8; i++){
                    if (i < attackedTerritory.numDice){
                        defRolls[i] = ThreadLocalRandom.current().nextInt(1, 6 + 1);
                    }
                    else{
                        defRolls[i] = 0;
                    }
                }
            }

            if (attackStage == globalTickspeed*6){
                if(Arrays.stream(defRolls).sum() >= Arrays.stream(attRolls).sum()){
                    currentTerritory.numDice = 1;
                    currentTerritory.calculateDiceGeometry();
                }
                else{
                    attackedTerritory.numDice = currentTerritory.numDice - 1;
                    currentTerritory.numDice = 1;
                    attackedTerritory.ownedBy = currentTerritory.ownedBy;
                    currentTerritory.calculateDiceGeometry();
                    attackedTerritory.calculateDiceGeometry();
                    attackedTerritory.generateNeighbours();
                    for(Territory terr: attackedTerritory.neighbours){
                        terr.generateNeighbours();
                    }
                    currentTerritory.ownedBy.calculateDraft();
                    attackedTerritory.ownedBy.calculateDraft();

                }
                attackStage = -1;
                currentTerritory = null;
                attackedTerritory = null;
                game.reRender = 5;
            }
            attackStage++;

        }else if (draftStage > 0){


            boolean full = true;
            for (Territory t: game.board.nations.get(currentNation).getTerritories()){
                if (t.numDice < 8) {
                    full = false;

                    break;
                }
            }
            if (!full){
                boolean done = false;
                ArrayList<Territory> terrs =  game.board.nations.get(currentNation).getTerritories();
                while (!done){
                    Territory t = terrs.get(ThreadLocalRandom.current().nextInt(0, terrs.size()));
                    if (t.numDice < 8){
                        t.numDice ++;
                        t.calculateDiceGeometry();
                        done = true;
                    }
                }
            }

            if (draftStage == game.board.nations.get(currentNation).calculateDraft() || full){
                currentNation =(currentNation+1)%game.board.nations.size();
                draftStage = -1;
                game.reRender = 5;
            }
            draftStage ++;
        }else if (playerComp[currentNation] == 0 ){
            aiTick();




        }
    }



    public static int DICE_WIDTH = 70;

    public void calculateDiceGeometry(){

        int ycentral = (int) (Game.HEIGHT * 0.8) + 45;
        int xcentral = (int)((Game.WIDTH-20)/2.0);
        int[] ycentres = new int[16];
        int[] xcentres = new int[16];
        for (int i= 0; i < 16; i++){
            if (i % 2 == 0){
                ycentres[i] = ycentral-DICE_WIDTH/2 - 3;
            }
            else{
                ycentres[i] = ycentral+DICE_WIDTH/2 + 3;
            }
            if (i < 8){
                xcentres[i] = xcentral - DICE_WIDTH*2 - (DICE_WIDTH+6)*(i/2);
            }
            else{
                xcentres[i] = xcentral +  DICE_WIDTH*2 + (DICE_WIDTH+6)*((i-8)/2);
            }

        }


            for (int i = 0; i < 16 ; i++) {
                //g.drawLine((int)cube[0][face[i]], (int)cube[1][face[i]], (int)cube[0][face[(i+1)%4]], (int)cube[1][face[(i+1)%4]]);
                Point p1;
                Point p2;
                Point p3;
                Point p4;

                p1 = new Point(xcentres[i]-DICE_WIDTH/2, ycentres[i]+DICE_WIDTH/2);
                p2 = new Point(xcentres[i]+DICE_WIDTH/2, ycentres[i]+DICE_WIDTH/2);
                p3 = new Point(xcentres[i]+DICE_WIDTH/2, ycentres[i]-DICE_WIDTH/2);
                p4 = new Point(xcentres[i]-DICE_WIDTH/2, ycentres[i]-DICE_WIDTH/2);


                Point p1p2a = Territory.interpolate(p1, p2, 0.2);
                Point p1p2b = Territory.interpolate(p1, p2, 0.8);

                Point p2p3a = Territory.interpolate(p2, p3, 0.2);
                Point p2p3b = Territory.interpolate(p2, p3, 0.8);

                Point p3p4a = Territory.interpolate(p3, p4, 0.2);
                Point p3p4b = Territory.interpolate(p3, p4, 0.8);

                Point p4p1a = Territory.interpolate(p4, p1, 0.2);
                Point p4p1b = Territory.interpolate(p4, p1, 0.8);


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
                if (i < 8){
                    attDiceGeo[i] = path;
                }
                else{
                    defDiceGeo[i-8] = path;
                }
            }

        }
    public void aiTick(){
        int failed = 0;
        if (currentTerritory == null){
            ArrayList<Territory> territories = game.board.nations.get(currentNation).getTerritories();
            boolean allZero = true;
            for (Territory t: territories){
                if (t.numDice != 1){

                    for (Territory t2: t.neighbours){
                        if (t2.ownedBy != game.board.nations.get(currentNation)) {
                            allZero = false;
                            break;
                        }
                    }
                }
            }
            if (allZero) failed = 40;

            while (failed < 40){
                Territory randTerr = territories.get(ThreadLocalRandom.current().nextInt(0, territories.size()));


                ArrayList<Territory> randTerrNeighbours =  new ArrayList<Territory>(randTerr.neighbours);
                ArrayList<Territory> randTerrEnemNeighbours = new ArrayList<>();
                for (Territory t: randTerrNeighbours){
                    if (t.ownedBy != game.board.nations.get(currentNation)){
                        randTerrEnemNeighbours.add(t);
                    }
                }
                if (randTerrEnemNeighbours.size() != 0 && randTerr.numDice > 1){
                    int bestAttack = 100;
                    Territory bestAttackTerr = null;
                    for (Territory t: randTerrEnemNeighbours){
                        if (bestAttack>= t.numDice){
                            bestAttackTerr = t;
                            bestAttack = t.numDice;
                        }
                    }
                    if (bestAttackTerr.numDice == randTerr.numDice){
                        Random r = new Random();
                        if (r.nextDouble()<0.1){

                            currentTerritory = randTerr;
                            Territory.partRender(currentTerritory);
                            aiAttack = bestAttackTerr;
                            aiBuffer = 2;
                            break;
                        }else{
                            failed++;
                        }

                    } else if (bestAttackTerr.numDice < randTerr.numDice) {
                        currentTerritory = randTerr;
                        Territory.partRender(currentTerritory);
                        aiAttack = bestAttackTerr;
                        aiBuffer = 2;
                        break;
                    }else{
                        failed++;
                    }

                }

            }if (failed == 40){
                draftStage = 1;
            }
        }else{
            if (aiBuffer > 0){
                aiBuffer -= 1;
            }
            else{
                attackedTerritory = aiAttack;
                Territory.partRender(attackedTerritory);
            }
        }

    }

    public void clickedHexagon(Hexagon hexClicked) {

        if (playerComp[currentNation] == 1 && attackedTerritory == null && draftStage == 0 && game.board.gameStage == 2) {

            if (hexClicked == null) {

                Territory.partRender(currentTerritory);
                currentTerritory = null;
                return;
            }
            if (currentTerritory == hexClicked.territory) {
                Territory.partRender(currentTerritory);
                currentTerritory = null;
            } else if (currentTerritory == null) {
                if (hexClicked.territory.ownedBy == game.board.nations.get(currentNation)) {
                    Territory.partRender(currentTerritory);
                    Territory.partRender(hexClicked.territory);
                    currentTerritory = hexClicked.territory;

                }
            } else {
                if (hexClicked.territory.ownedBy == game.board.nations.get(currentNation)) {
                    Territory.partRender(currentTerritory);
                    Territory.partRender(hexClicked.territory);
                    currentTerritory = hexClicked.territory;
                } else if (currentTerritory.neighbours.contains(hexClicked.territory)) {
                    if (currentTerritory.numDice > 1) {



                        attackedTerritory = hexClicked.territory;
                        Territory.partRender(attackedTerritory);
                    }

                }
            }


        }


    }
}




