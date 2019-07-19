package render;

import events.EventFactory;
import events.EventManager;
import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import logic.Direction;
import logic.ErrorLocation;
import logic.ErrorManagement;
import render.notifications.Notification;
import render.notifications.NotificationManager;
import render.particles.Particle;
import world.*;
import world.blocks.BlockType;
import world.blocks.Cube;
import world.blocks.LightBlock;
import world.entities.Entity;
import world.player.Player;
import world.player.inventory.ItemStack;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Renderer {

    private List<RenderBlock> renderingBlocks = new CopyOnWriteArrayList<>();

    private final World world;

    private final Camera camera;


    public static final int RENDER_DISTANCE = 500000;


    public static final int DIMENSION = 1000;



    private static final Color DAY = new Color(16, 233,255);
    private static final Color NIGHT = new Color(2,0, 26);

    public static final int CUBE_DEFAULT_SIZE = 50; // px=

    private List<LightSource> lightSources = new CopyOnWriteArrayList<>();

    private List<Particle> particles = new CopyOnWriteArrayList<>();

    public Renderer(Camera camera){
        this.camera = camera;
        instance = this;
        this.world = new World();

        camera.setSpeed(10);
        cubeInHand = new Cube(14,14,20,10,10,BlockType.STONE);
    }

    public World getWorld() {
        return world;
    }

    private Chunk getChunkAt(double x, double z){
        for(Chunk chunk : world.getChunks()){
            if(chunk.getX() <= x && chunk.getX() + 1000 >= x){
                if(chunk.getZ() <= z && chunk.getZ() + 1000 >= z){
                    return chunk;
                }
            }
        }
        return null;
    }

    public List<Particle> getParticles(){
        return this.particles;
    }


    private List<Particle> particleAdditionBuffer = new CopyOnWriteArrayList<>();
    private List<Particle> particleRemovalBuffer = new CopyOnWriteArrayList<>();

    public void registerParticle(Particle particle){
        this.particleAdditionBuffer.add(particle);
    }

    public void killParticle(Particle particle){
        particleRemovalBuffer.add(particle);
    }

    public Cube canMove(Point3D target){
        Chunk chunk = getChunkAt(target.getX(),target.getZ());
        if(chunk == null) return null;

        for(Cube cube : chunk.getBlocks())
            if(cube.getPhysicsBody().getBounds().intersects(camera.getPhysicsBody().moveTarget(target)))
                return cube;


        ErrorManagement.error(ErrorLocation.RENDERING, "Unable to locate which cube the player has interacted with.");

        return null;
    }


    public Camera getCamera() {
        return camera;
    }

    private static Renderer instance = null;
    public static Renderer getInstance(){
        return instance;
    }
    private Cube selectedCube = null;
    private Direction selectedFace = null;


    public void registerLightSource(LightSource source){
        this.lightSources.add(source);
    }

    public void unregisterLightSource(LightSource source){
        this.lightSources.remove(source);
    }


    public void breakBlock(Player player){
        if(selectedCube == null) return;
        EventManager.getInstance().throwEvent(EventFactory.blockBreakEvent(player,selectedCube));
        selectedCube.destroy();
        selectedCube.getChunk().blockUpdate(selectedCube);
        Color color = selectedCube.getColor(0);
        ItemStack blockStack = ItemStack.blockBuilder(color, 1, selectedCube.getBlockType());
        player.getInventory().addItem(blockStack);
        world.updateLighting(selectedCube.getX(),selectedCube.getY(),selectedCube.getZ());
    }

    public void placeBlock(Player player){
        if(selectedCube == null) return;
        if(player.getInventory().getSelectedItem() == null) return;
        if(!player.getInventory().getSelectedItem().isPlaceable()) return;


        Chunk chunk = selectedCube.getChunk();
        Point3D slope = CoordinateUtilities.directionToMovement(selectedFace).multiply(-selectedCube.getWidth());
        Cube cube = new Cube(selectedCube.getX() + slope.getX(),selectedCube.getY() + slope.getY(),selectedCube.getZ() + slope.getZ(),CUBE_DEFAULT_SIZE,CUBE_DEFAULT_SIZE, player.getInventory().getSelectedItem().getBlockType());
        double intensity = -1;
        if(player.getInventory().getSelectedItem().getBlockType() == BlockType.LIGHT){
            cube = new LightBlock(selectedCube.getX() + slope.getX(),selectedCube.getY() + slope.getY(),selectedCube.getZ() + slope.getZ(), player.getInventory().getSelectedItem().getData().getInt("intensity"));
            intensity = player.getInventory().getSelectedItem().getData().getInt("intensity");
        }
        cube.setColor(player.getInventory().getSelectedItem().getData().getColorStack("color_stack").get(0));
        cube.getData().merge(player.getInventory().getSelectedItem().getData());
        player.getInventory().placeItem();
        cube.setChunk(chunk);
        chunk.getBlocks().add(cube);
        chunk.blockUpdate(cube);
        EventManager.getInstance().throwEvent(EventFactory.blockPlaceEvent(player,cube));
        if(cube instanceof LightBlock)
            world.updateLighting(cube.getX(),cube.getY(),cube.getZ(), intensity);
        else
            world.updateLighting(selectedCube);


    }


    public List<LightSource> getLightSources() {
        return lightSources;
    }

    public void updateRenderBlocks(){

        try {
            particles.addAll(particleAdditionBuffer);
            particles.removeAll(particleRemovalBuffer);
            particleAdditionBuffer = new CopyOnWriteArrayList<>();
            particleRemovalBuffer = new CopyOnWriteArrayList<>();

            Point3D cameraLocation = this.camera.getRenderInstance();
            List<Cube> visible = getWorld().getCubesWithinRange(cameraLocation, RENDER_DISTANCE);
            renderingBlocks = visible.stream().filter(cube -> cube.isSideVisible()).map(c -> c.getRenderBlock(camera)).collect(Collectors.toList());
            for(int i =0; i<particles.size(); i++){
                Particle particle = particles.get(i);
                if(particle.getColor().getAlpha() > 0){
                    renderingBlocks.add(particle.getRenderBlock(camera));
                }
                else{
                    killParticle(particle);
                }
            }
            renderingBlocks.addAll(getWorld().getEntities().stream().map(e -> e.getRenderBlock((camera))).collect(Collectors.toList()));
            double rotation = getWorld().getTime() / 10000.0 * 360.0;

            double[] point = CoordinateUtilities.rotateAroundX3D(new Point3D(1, -2000, 1), Math.toRadians(rotation));
            point = CoordinateUtilities.rotateAroundY3D(new Point3D(point[0], point[1], point[2]), Math.toRadians(rotation));


            RenderShape shape = new RenderShape(point[0], point[1], point[2], 200, 200, Color.white);
            shape.setFadeInDistance(false);
            renderingBlocks.add(shape.getRenderBlock(camera));


            point = CoordinateUtilities.rotateAroundX3D(new Point3D(1, 2000, 1), Math.toRadians(rotation));
            point = CoordinateUtilities.rotateAroundY3D(new Point3D(point[0], point[1], point[2]), Math.toRadians(rotation));


            shape = new RenderShape(point[0], point[1], point[2], 200, 200, new Color(155,0, 41));
            shape.setFadeInDistance(false);
            renderingBlocks.add(shape.getRenderBlock(camera));

            Collections.sort(renderingBlocks);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        particles.removeAll(particles.stream().filter(p -> p.getColor().getAlpha() == 0).collect(Collectors.toList()));
    }

    public List<RenderBlock> getRenderingBlocks() {
        return renderingBlocks;
    }

    /*
    So the way this works is that there is a "screen" at Z=1 and I want to find the point of intersection between that plane at Z = 1 and the vector stemming from the camera to the target point

     */


    public Color skyColor = null;
    public void render(Graphics screenGraphics){

        BufferedImage img = new BufferedImage(DIMENSION, DIMENSION, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);




        selectedCube = null;
        selectedFace = null;
        skyColor = CoordinateUtilities.fadeFromToPercent(DAY,NIGHT,0.5);

        g.setColor(skyColor);
        g.fillRect(0,0,DIMENSION,DIMENSION);


        Polygon targetFace = null;
        double dist = 0;


        for(RenderBlock block : renderingBlocks){
            Cube cube = block.getCube();

            for(int n = 0; n<block.getRenderSides().size(); n++){
                RenderSide side = block.getRenderSides().get(n);

                if(block.getType() == RenderType.BLOCK && cube.renderFace(side.getDirection())) continue;

                Point3D a = side.getA();
                Point3D b = side.getB();
                Point3D c = side.getC();
                Point3D d = side.getD();


                if(a == null || b == null || c == null || d== null) continue;

                double found = 0;
                if(CoordinateUtilities.isVisible(a, camera.getFov() + 5)  ){
                    found++;
                }
                if(CoordinateUtilities.isVisible(b, camera.getFov() + 5)  ){
                    found++;
                }
                if(CoordinateUtilities.isVisible(c, camera.getFov() + 5)  ){
                    found++;
                }
                if(CoordinateUtilities.isVisible(d, camera.getFov() + 5)  ){
                    found++;
                }


                if(found < 2) continue;

                Polygon polygon = side.getDrawingPolygon(camera);

                double avgDist = side.distance();

                if(block.getType() == RenderType.BLOCK)
                    side.applyLighting();

                if(block.isFadeInDistance()){
                    double perc = avgDist * 100 / RENDER_DISTANCE;
                    g.setColor(CoordinateUtilities.fadeFromToPercent(side.getColor(), skyColor, 0.01 * (0.01 * perc * perc)));
                }
                else{
                    g.setColor(side.getColor());
                }

                g.fillPolygon(polygon);


                if(polygon.contains(DIMENSION / 2, DIMENSION / 2) && avgDist < dist || polygon.contains(DIMENSION / 2, DIMENSION / 2) && dist == 0){
                    targetFace = polygon;
                    selectedCube = block.getCube();
                    dist = avgDist;

                    if(side.getN()== 0){
                        selectedFace = Direction.NORTH;
                    }
                    else if(side.getN()== 1){
                        selectedFace = Direction.EAST;
                    }
                    else if(side.getN()== 2){
                        selectedFace = Direction.WEST;
                    }
                    else if(side.getN()== 3){
                        selectedFace = Direction.SOUTH;
                    }
                    else if(side.getN()== 4){
                        selectedFace = Direction.UP;
                    }
                    else if(side.getN()== 5){
                        selectedFace = Direction.DOWN;
                    }
                }

            }
        }
        if(targetFace != null){
            g.setColor(new Color(0,0,0,(int)(125 - (75 * Math.sin(Math.toRadians(camera.getTicks()))))));
            g.fillPolygon(targetFace);
            g.setColor(Color.BLACK);
            g.drawPolygon(targetFace);
        }

        screenGraphics.drawImage(img, 0,0, null);
    }

    public void renderGUI(Graphics g, Player player){
        g.setColor(new Color(0,0,0,255));

        g.drawRect(DIMENSION / 2 - 5, DIMENSION / 2 - 5, 10, 10);
        double inventoryWidth = 0.5 * DIMENSION;
        double inventoryHeight = DIMENSION * .05;

        int invX = (int)(DIMENSION - inventoryWidth);

        g.fillRoundRect(invX / 2, DIMENSION - (int)inventoryHeight *  2, (int)inventoryWidth, (int)inventoryHeight, 50, 50);


        g.setColor(new Color(255,0,0,50));
        g.fillRoundRect(invX / 2, DIMENSION - (int) inventoryHeight * 3, (int) inventoryWidth / 2, (int) inventoryHeight / 2, (int)inventoryHeight / 2, (int)inventoryHeight / 2);
        g.setColor(new Color(255,0,0));
        g.fillRoundRect(invX / 2, DIMENSION - (int) inventoryHeight * 3, (int) (inventoryWidth / 2 * player.getHealth()), (int) inventoryHeight / 2, (int)inventoryHeight / 2, (int)inventoryHeight / 2);


        double unitWidth = inventoryWidth / player.getInventory().getContents().length;

        for(int i = 0; i<player.getInventory().getContents().length; i++){
            ItemStack itemStack = player.getInventory().getItem(i);
            if(itemStack == null) continue;
            if(itemStack.isPlaceable()){
                g.setColor(itemStack.getData().getColorStack("color_stack").get(0));
                g.fillOval(invX / 2 + (int)(i * unitWidth), DIMENSION - (int)inventoryHeight * 2, (int)unitWidth, (int)inventoryHeight);
                if(i == player.getInventory().getSelectedSlot()){
                    g.setColor(Color.WHITE);
                    g.drawOval(invX / 2 + (int)(i * unitWidth), DIMENSION - (int)inventoryHeight * 2, (int)unitWidth, (int)inventoryHeight);

                }


            }
        }
       // renderHand(g,player);
    }

    public void renderNotifications(Graphics g, NotificationManager manager){
        if(manager.getCurrentNotification() != null){
            Notification notification = manager.getCurrentNotification();
            Color c = new Color(notification.getColor().getRed(), notification.getColor().getGreen(), notification.getColor().getBlue(), (int)(manager.getOpacity() * notification.getColor().getAlpha()));
            g.setColor(c);
            g.fillRect(DIMENSION - 400, 20, 400, 75);
            g.setColor(new Color(255,255,255, (int)(manager.getOpacity() * notification.getColor().getAlpha())));
            g.drawString(manager.getCurrentNotification().getText(), DIMENSION - 350, 46);
        }
    }
    private Cube cubeInHand = null;
    private void renderHand(Graphics g, Player player){
        if(player.getInventory().getSelectedItem() == null) return;
        if(player.getInventory().getSelectedItem().getData().getColorStack("color_stack") == null) return;
        Color c = player.getInventory().getSelectedItem().getData().getColorStack("color_stack").get(0);
        cubeInHand.setColor(c);
//        double[] d = CoordinateUtilities.map3DTo2DScreen(14,14,15,10,player.getCamera().getFov());
        g.setColor(cubeInHand.getColor(0));
        double[] lastPoint = new double[]{0,0};
        for(Point3D p : cubeInHand.getNodes()){
            double[] d = CoordinateUtilities.map3DTo2DScreen(p.getX(),p.getY(),p.getZ(), 16, player.getCamera().getFov());
            g.drawLine((int)d[0],(int)d[1], (int)lastPoint[0], (int)lastPoint[1]);


            lastPoint = d;
        }

    }
}
