
import java.awt.*;
import java.awt.image.BufferStrategy;



public class Game extends Canvas implements Runnable, Renderable {


    public static final int WIDTH = 1400;
    public static final int HEIGHT = 1000;
    private Thread thread;
    public int tickcount;
    private boolean running = false;
    public Board board;
    private final Window window;
    public Mapper mapper;
    public TurnHandler turnHandler;
    public static final int hexRad = 30;
    public static final int noTerritories = 30;
    public static final int noPlayers = 6;
    public static final int noHumanPlayers = 1;
    public static final boolean forcePlayerFirst = true;
    private BufferStrategy bs;
    private Graphics2D g;
    public int reRender = 3;

    public Game() {

        tickcount = 0;
        mapper = new Mapper(hexRad);
        turnHandler = new TurnHandler(this, noPlayers, noHumanPlayers);
        board = new Board(this, noTerritories, (int) Math.round((WIDTH - 2 * hexRad) / (hexRad * 2.0 * Math.cos(Math.toRadians(30)))), (int) Math.round((HEIGHT * 1.3) / (hexRad * 2.0)));
        this.addMouseListener(new MouseClick(this));
        this.addKeyListener(new KeyInput(this));
        window = new Window(WIDTH, HEIGHT, "Block Battle", this);

        bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        g = (Graphics2D) bs.getDrawGraphics();
    }

    public synchronized void start() {
        thread = new Thread(this);
        running = true;
        thread.start();

    }

    public synchronized void stop() {
        try {
            thread.join();
            running = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void run() {
        System.out.println("Game Start");
        long lastTime = System.nanoTime();
        double amountOfTicks = 30.0;
        double ns = 1000000000 / amountOfTicks;

        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;


        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                tick();
                delta--;
            }
            if (running)
                render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    private void tick() {
        tickcount++;
        if (board.gameStage == 0 || board.gameStage == -1) {
            board.tick();
        }
        if (board.gameStage == 1|| board.gameStage == -2) {
            board.nationTick();
        }
        if (board.gameStage == 2) {
            turnHandler.tick();
        }


    }

    public void render() {



        bs = this.getBufferStrategy();


        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        g = (Graphics2D) bs.getDrawGraphics();


        if (reRender>0){
            g.setColor(Color.white);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }


        g.setFont(new Font("Dialog", Font.BOLD, 15));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);



        board.render(g);
        if (board.gameStage == 2) {
            turnHandler.render(g);
        }

        g.dispose();

        if (reRender >0 && board.gameStage == 2){
            reRender -= 1;
        }

        bs.show();



    }

    public static void main(String[] args) {
        new Game();
    }

}
