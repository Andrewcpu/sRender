import events.EventManager;
import logic.Controls;
import render.Renderer;
import render.notifications.Notification;
import render.notifications.NotificationManager;
import world.Location;
import world.blocks.BlockType;
import world.entities.Bird;
import world.entities.Fairy;
import world.player.Player;
import world.player.inventory.ItemStack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class Main extends JFrame {
    public static void main(String[] args) {
        new Main();
    }

    private final MainCanvas canvas = new MainCanvas();

    public static NotificationManager notificationManager = new NotificationManager();
    public static EventManager eventManager = new EventManager();

    private static final int FPS = 70;

    private Main(){
        setBounds(0,0,1000,1000);
        add(canvas);
        addKeyListener(canvas);
        addMouseMotionListener(canvas);
        addMouseListener(canvas);
        canvas.setBounds(getBounds());
        setVisible(true);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::repaint,1000/FPS,1000/FPS,TimeUnit.MILLISECONDS);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::tick, 20, 20, TimeUnit.MILLISECONDS);

    }

    private void tick(){
        canvas.tick();
    }
}

class MainCanvas extends JComponent implements KeyListener, MouseMotionListener, MouseListener{


    private final Player player = new Player(0,0,0);
    private final render.Renderer renderer = new render.Renderer(player.getCamera());


    private final List<Integer> keys = new ArrayList<>();

    private boolean paused = false;



    public MainCanvas(){
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);


        setupPlayer();


    }

    private void setupPlayer(){
        ItemStack i = ItemStack.blockBuilder(new Color(0,0,255,50), 1, BlockType.STONE);
        player.getInventory().addItem(i);
        player.getInventory().addItem(ItemStack.lightBuilder(new Color(255,0,0), 5, 10 * Renderer.CUBE_DEFAULT_SIZE));
        player.getInventory().addItem(ItemStack.lightBuilder(new Color(0,255,0), 5, 10 * Renderer.CUBE_DEFAULT_SIZE));
        player.getInventory().addItem(ItemStack.lightBuilder(new Color(0,0,255), 5, 10 * Renderer.CUBE_DEFAULT_SIZE));
        //renderer.registerLightSource(player.getLightSource());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1)
            render.Renderer.getInstance().breakBlock(player);
        else
            render.Renderer.getInstance().placeBlock(player);
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

    @Override
    public void mouseDragged(MouseEvent e) {
        if(paused) return;

        player.getCamera().setSideRotation(player.getCamera().getSideRotation() + Math.toRadians((e.getPoint().getX() - lastPoint.getX()) * Controls.SENSITIVITY));
        player.getCamera().setVerticalRotation(player.getCamera().getVerticalRotation() - Math.toRadians((e.getPoint().getY() - lastPoint.getY()) * Controls.SENSITIVITY ));
        lastPoint = e.getPoint();
    }

    private Point lastPoint = new Point(0,0);

    @Override
    public void mouseMoved(MouseEvent e) {
        if(paused) return;
        player.getCamera().setSideRotation(player.getCamera().getSideRotation() + Math.toRadians((e.getPoint().getX() - lastPoint.getX()) * Controls.SENSITIVITY));
        player.getCamera().setVerticalRotation(player.getCamera().getVerticalRotation() - Math.toRadians((e.getPoint().getY() - lastPoint.getY()) * Controls.SENSITIVITY ));
        lastPoint = e.getPoint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(keys.contains(e.getKeyCode())) return;
        keys.add(e.getKeyCode());
        handlePress(e);
    }

    private void handlePress(KeyEvent e){
        int code = e.getKeyCode();
        if(code == Controls.INVENTORY_0){
            player.getInventory().setSelectedSlot(0);
        }
        if(code == Controls.INVENTORY_1){
            player.getInventory().setSelectedSlot(1);

        }
        if(code == Controls.INVENTORY_2){
            player.getInventory().setSelectedSlot(2);

        }
        if(code == Controls.INVENTORY_3){
            player.getInventory().setSelectedSlot(3);

        }
        if(code == Controls.INVENTORY_4){
            player.getInventory().setSelectedSlot(4);

        }
        if(code == Controls.INVENTORY_5){
            player.getInventory().setSelectedSlot(5);

        }
        if(code == Controls.INVENTORY_6){
            player.getInventory().setSelectedSlot(6);

        }
        if(code == Controls.INVENTORY_7){
            player.getInventory().setSelectedSlot(7);

        }
        if(code == Controls.INVENTORY_8){
            player.getInventory().setSelectedSlot(8);

        }
        if(code == Controls.INVENTORY_9){
            player.getInventory().setSelectedSlot(9);

        }

        if(code == KeyEvent.VK_H){
            player.damage(1.0, 20.0);
        }

        if(code == KeyEvent.VK_N){
            NotificationManager.getInstance().queue(new Notification("Lady gaga"));
        }

        if(code == KeyEvent.VK_R){
            Renderer.getInstance().getWorld().updateLighting(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
       //     Renderer.getInstance().getWorld().getChunks().forEach(chunk -> chunk.updateChunk());
        }
        if(code == KeyEvent.VK_G){
            Renderer.getInstance().getWorld().regenerate();
        }

        if(code == KeyEvent.VK_ESCAPE){
            paused = !paused;
        }

        if(code == KeyEvent.VK_P){
            Renderer.getInstance().getWorld().spawnParticle(player.getLocation(), Color.RED);
        }

        if(code == KeyEvent.VK_F){
            Fairy fairy = new Fairy(new Location(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(), Renderer.getInstance().getWorld()));
            Renderer.getInstance().getWorld().getEntities().add(fairy);
        }

        if(code == KeyEvent.VK_B){
            Bird bird = new Bird(new Location(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ(), Renderer.getInstance().getWorld()));
            Renderer.getInstance().getWorld().getEntities().add(bird);

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(Integer.valueOf(e.getKeyCode()));
    }


    public void tick(){
        player.tick();
        NotificationManager.getInstance().tick();


        for(Integer i : keys){
            if(i == Controls.FORWARD){
                player.getCamera().forward();
            }
            if(i == Controls.BACKWARD){
                player.getCamera().backwards();
            }
            if(i == Controls.LEFT){
                player.getCamera().left();
            }
            if(i == Controls.RIGHT){
                player.getCamera().right();
            }
            if(i == Controls.UP){
                player.getCamera().up();
            }
            if(i == Controls.DOWN){
                player.getCamera().down();
            }
        }

        Renderer.getInstance().getWorld().tick();
    }


    @Override
    public void paint(Graphics graphics){


        BufferedImage bufimage = new BufferedImage(getWidth(), getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufimage.getGraphics();

        player.getCamera().setRenderInstance();
        renderer.updateRenderBlocks();

        renderer.render(g);
        renderer.renderGUI(g, player);
        renderer.renderNotifications(g, NotificationManager.getInstance());

        graphics.drawImage(bufimage, 0, 0, null);

    }
}
