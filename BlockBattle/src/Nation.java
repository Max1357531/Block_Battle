import java.awt.*;

import java.util.ArrayList;


public class Nation {
    private static final ArrayList<Integer> ids = new ArrayList<Integer>();
    int id;
    public Color color;
    public Game game;
    public int number;
    public int DRAFT_HEIGHT = 50;
    public int DRAFT_WIDTH = 100;


    public Nation(Color color, Game game, int number) {
        this.game = game;
        this.color = color;
        this.number = number;
        do {
            id = (int) (Math.random() * 1000000);
        } while (ids.contains(id));
        ids.add(id);
    }

    public void render(Graphics2D g, int numberNations, int number) {
        g.setColor(color);

        g.fillRect(20+DRAFT_WIDTH*(number/3),(int)(Game.HEIGHT*0.8)+DRAFT_HEIGHT*((number)%3),DRAFT_WIDTH,DRAFT_HEIGHT);

        g.setColor(Mapper.reduceSaturation(color, (float) 0.5, (float) 0.9));
        g.fillOval(20+DRAFT_WIDTH*(number/3)+ DRAFT_WIDTH/2-20,(int)(Game.HEIGHT*0.8)+DRAFT_HEIGHT*((number)%3) +DRAFT_HEIGHT/2-20,40,40);

        g.setStroke(new BasicStroke(5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g.setColor(Color.BLACK);
        if (game.board.nations.get(game.turnHandler.currentNation) == this && game.board.gameStage == 2){

            g.setColor(Color.RED);
        }

        Rectangle rect = new Rectangle(20+DRAFT_WIDTH*(number/3),(int)(Game.HEIGHT*0.8)+DRAFT_HEIGHT*((number)%3),DRAFT_WIDTH,DRAFT_HEIGHT);

        g.drawRect(20+DRAFT_WIDTH*(number/3),(int)(Game.HEIGHT*0.8)+DRAFT_HEIGHT*((number)%3),DRAFT_WIDTH,DRAFT_HEIGHT);

        g.setColor(Color.BLACK);
        game.turnHandler.drawCenteredString(g,String.valueOf(calculateDraft()),rect,new Font ("TimesRoman", Font.BOLD | Font.ITALIC, 20));


        /*g.setColor(Mapper.reduceSaturation(color, (float) 0.7, 1));
        g.fillRect((int) ((Game.WIDTH / numberNations) * (0.5 + number)) - 15, (int) (Game.HEIGHT * 0.9) - 15, 30, 30);
        g.setColor(Color.black);
        g.drawString(String.valueOf(calculateDraft()), (int) ((Game.WIDTH / numberNations) * (0.5 + number)), (int) (Game.HEIGHT * 0.9));
        */
    }

    public void tick() {

    }

    public  ArrayList<Territory> getTerritories() {
        ArrayList<Territory> territories = new ArrayList<>();
        for (Territory t: game.board.territories) {
            if (t.ownedBy == this) {
                territories.add(t);
            }
        }
        return territories;
    }

    public int calculateDraft() {
        int maxSize = 0;
        for (Territory t : game.board.territories) {
            if (t.ownedBy == this) {
                int size = game.board.connectedSameNation(t).size();
                if (size >= maxSize) {
                    maxSize = size;
                }
            }
        }
        return maxSize;
    }

}
