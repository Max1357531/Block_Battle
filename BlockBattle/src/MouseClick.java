
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class MouseClick implements MouseListener {

    private final Game game;

    public MouseClick(Game game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
        for (Hexagon h : game.board.getAllHexagons()) {
            int[][] hexCoords = game.mapper.getIntHexagon(h.coordinates[0], h.coordinates[1]);
            Polygon hexPoly = new Polygon(hexCoords[0], hexCoords[1], 6);
            if (hexPoly.intersects(r)) {

                game.turnHandler.clickedHexagon(h);
                return;
            }
        }
        Rectangle rect = new Rectangle(Game.WIDTH-2*game.turnHandler.END_WIDTH,(int)(Game.HEIGHT*0.8)+game.turnHandler.END_HEIGHT/3,game.turnHandler.END_WIDTH,game.turnHandler.END_HEIGHT);
        if (rect.intersects(r)) {

            game.turnHandler.endTurnClicked();
            return;
        }
        game.turnHandler.clickedHexagon(null);

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {


    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
