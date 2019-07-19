package world.blocks;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import logic.Direction;
import logic.SRStorage;
import render.RenderBlock;
import render.Camera;
import render.RenderType;
import render.Renderer;
import render.particles.Particle;
import world.Chunk;
import world.LightSource;
import world.PhysicsBody;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Cube {
    private double x, y, z;
    private double width, height;

    private Chunk chunk = null;

    private final Point3D[] nodes = new Point3D[8];



    private final int[][] sides = new int[6][4];


    private final HashMap<Direction,Boolean> renderFace = new HashMap<>();


    private final Color[] colors = new Color[6];

    private final PhysicsBody physicsBody;

    private final SRStorage data = new SRStorage();



    private static final int FRONT_TOP_LEFT = 0;
    private static final int FRONT_TOP_RIGHT = 1;
    private static final int FRONT_BOTTOM_LEFT = 2;
    private static final int FRONT_BOTTOM_RIGHT = 3;
    private static final int BACK_TOP_LEFT = 4;
    private static final int BACK_TOP_RIGHT = 5;
    private static final int BACK_BOTTOM_LEFT= 6;
    private static final int BACK_BOTTOM_RIGHT= 7;


    private boolean solid = true;


    private BlockType blockType;

    private LightSource[] closestLightSource = new LightSource[6];


    public Cube(double x, double y, double z, double width, double height, BlockType blockType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.blockType = blockType;
        physicsBody = new PhysicsBody(x + width / 2,y + height / 2,z + width / 2,width,height,width);



        updateNodes();

        sides[0] = new int[]{FRONT_TOP_RIGHT,FRONT_TOP_LEFT,FRONT_BOTTOM_LEFT,FRONT_BOTTOM_RIGHT}; // front
        sides[1] = new int[]{FRONT_TOP_LEFT,FRONT_BOTTOM_LEFT,BACK_BOTTOM_LEFT,BACK_TOP_LEFT}; // left
        sides[2] = new int[]{FRONT_TOP_RIGHT,FRONT_BOTTOM_RIGHT,BACK_BOTTOM_RIGHT,BACK_TOP_RIGHT}; // right
        sides[3] = new int[]{BACK_TOP_RIGHT,BACK_TOP_LEFT,BACK_BOTTOM_LEFT,BACK_BOTTOM_RIGHT}; // back
        sides[4] = new int[]{FRONT_TOP_RIGHT,FRONT_TOP_LEFT,BACK_TOP_LEFT,BACK_TOP_RIGHT}; // top
        sides[5] = new int[]{FRONT_BOTTOM_RIGHT,FRONT_BOTTOM_LEFT,BACK_BOTTOM_LEFT,BACK_BOTTOM_RIGHT}; // bottom


        for(int i = 0; i<6; i++){
            colors[i] = Color.BLACK;
        }

        //FACES TELL YOU WHICH NODES CONNECT. DONT MESS THIS UP.
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public SRStorage getData() {
        return data;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }

    public PhysicsBody getPhysicsBody() {
        return physicsBody;
    }

    public Color getColor(int side) {
        return colors[side];
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
        update();
    }

    public void destroy(){
        chunk.getBlocks().remove(this);
    }

    public void setColor(Color color, int side) {
        colors[side] = color;
        if(color.getAlpha() < 255)
            setSolid(false);
        else
            setSolid(true);
    }

    public void setColor(Color color){
        for(int i = 0; i<6; i++){
            setColor(color, i);
        }
    }


    private Point3D location = null;

    public Point3D getLocation(){
        return location;
    }

    private Point3D getFrontTopLeft(){
        return new Point3D(getX(), getY(), getZ());
    }
    private Point3D getFrontTopRight(){
        return new Point3D(getX() + getWidth(), getY(), getZ());
    }
    private Point3D getFrontBottomLeft(){
        return new Point3D(getX(), getY() + getHeight(), getZ());
    }
    private Point3D getFrontBottomRight(){
        return new Point3D(getX() + getWidth(), getY() + getHeight(), getZ());
    }
    private Point3D getBackTopLeft(){
        return new Point3D(getX(), getY(), getZ() + getWidth());
    }
    private Point3D getBackTopRight(){
        return new Point3D(getX() + getWidth(), getY(), getZ() + getWidth());
    }
    private Point3D getBackBottomLeft(){
        return new Point3D(getX(), getY() + getHeight(), getZ() + getWidth());
    }
    private Point3D getBackBottomRight(){
        return new Point3D(getX() + getWidth(), getY() + getHeight(), getZ() + getWidth());
    }


    public void updateLighting(){

            closestLightSource = new LightSource[6];

            Object[] keys = renderFace.keySet().toArray();
            for(int i = 0; i<renderFace.size(); i++){
                Object direction = keys[i];
                int n;
                if(direction == Direction.SOUTH)
                    n = 0;
                else if(direction == Direction.WEST)
                    n = 1;
                else if(direction == Direction.EAST)
                    n = 2;
                else if(direction == Direction.NORTH)
                    n = 3;
                else if(direction == Direction.DOWN)
                    n = 4;
                else if(direction == Direction.UP)
                    n = 5;
                else
                    n = 6; // impossible
                closestLightSource[n] = getChunk().getWorld().getClosestLightSourceWithinRange(getMiddleOfFace(n));
            }


    }

    public List<LightSource> getAllActiveLightSources(){
        return Arrays.stream(closestLightSource).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public LightSource[] getClosestLightSource() {
        return closestLightSource;
    }

    public Point3D getMiddleOfCube(){
        return CoordinateUtilities.averagePoint(new Point3D[]{getFrontTopLeft(), getBackBottomRight()});
    }

    private Point3D getMiddleOfFace(int face){
        int[] nodeIDs = sides[face];
        return CoordinateUtilities.averagePoint(new Point3D[]{nodes[nodeIDs[0]], nodes[nodeIDs[1]], nodes[nodeIDs[2]], nodes[nodeIDs[3]]});
    }


    public RenderBlock getRenderBlock(Camera camera){
        Point3D[] points = new Point3D[nodes.length];
        double[] translation = {camera.getRenderInstance().getX(),camera.getRenderInstance().getY(),camera.getRenderInstance().getZ()};

        for(int n = 0; n<nodes.length; n++){
            Point3D nodeA = nodes[n];

            Point3D node = new Point3D(nodeA.getX() - translation[0], nodeA.getY() - translation[1], nodeA.getZ() - translation[2] );


            double[] point = rotationMatrix(node, camera);

            points[n] = new Point3D(point[0]  , point[1]  ,point[2]);
        }

        return new RenderBlock(points, sides, colors, this, RenderType.BLOCK);
    }

    public double[] rotationMatrix(Point3D node, Camera camera){
        double[] point = {node.getX(),node.getY(),node.getZ()};
        double[] pointA = CoordinateUtilities.rotateAroundY3D(new Point3D(point[0], point[1], point[2]), camera.getSideRotation());
        double[] pointB = CoordinateUtilities.rotateAroundX3D(new Point3D(pointA[0], pointA[1], pointA[2]), camera.getVerticalRotation());
        return pointB;
    }
    public Point3D rotationMatrix3DPoint(Point3D node, Camera camera){
        double[] matrix = rotationMatrix(node, camera);
        return new Point3D(matrix[0], matrix[1], matrix[2]);
    }

    private boolean isFaceTouching(Direction direction){
        Point3D dTM = CoordinateUtilities.directionToMovement(direction);
        dTM = dTM.multiply(getWidth());
        Cube c = chunk.getWorld().getBlockAt(getX() + dTM.getX(), getY() + dTM.getY(), getZ() + dTM.getZ());
        return c != null && c.isSolid();
    }


    //counter intuitive, this is true if you SHOULDN'T RENDER THE FACE, you should know this by now though it's just sad at this point.
    public boolean renderFace(Direction d){
        return renderFace.get(d);
    }

    public void update(){

        renderFace.clear();

        boolean renderNorth = isFaceTouching(Direction.NORTH);
        boolean renderSouth = isFaceTouching(Direction.SOUTH);
        boolean renderEast = isFaceTouching(Direction.EAST);
        boolean renderWest = isFaceTouching(Direction.WEST);
        boolean renderUp = isFaceTouching(Direction.UP);
        boolean renderDown =isFaceTouching(Direction.DOWN);

        renderFace.put(Direction.NORTH, renderNorth);
        renderFace.put(Direction.WEST, renderWest);
        renderFace.put(Direction.EAST, renderEast);
        renderFace.put(Direction.SOUTH, renderSouth);
        renderFace.put(Direction.UP, renderUp);
        renderFace.put(Direction.DOWN,renderDown);

        location = new Point3D(x,y,z);

    }

    public boolean isSideVisible(){
        for(Direction d : renderFace.keySet()){
            if(!renderFace.get(d))
                return true;
        }
        return false;
    }



    public Point3D[] getNodes() {
        return nodes;
    }


    public int[][] getSides() {
        return sides;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        updateNodes();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        updateNodes();
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
        updateNodes();
    }

    private void updateNodes(){
        location = new Point3D(x,y,z);


        nodes[0] = getFrontTopLeft();
        nodes[1] = getFrontTopRight();

        nodes[2] = getFrontBottomLeft();
        nodes[3] = getFrontBottomRight();

        nodes[4] = getBackTopLeft();
        nodes[5] = getBackTopRight();

        nodes[6] = getBackBottomLeft();
        nodes[7] = getBackBottomRight();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        updateNodes();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
        updateNodes();
    }


    public boolean contains(Point3D point3D){
        return contains(point3D.getX(),point3D.getY(),point3D.getZ());
    }
    public boolean contains(double x, double y, double z){
        double xMin = Math.min(getFrontTopLeft().getX(), getBackBottomRight().getX());
        double yMin = Math.min(getFrontTopLeft().getY(), getBackBottomRight().getY());
        double zMin = Math.min(getFrontTopLeft().getZ(), getBackBottomRight().getZ());

        double xMax = Math.max(getFrontTopLeft().getX(), getBackBottomRight().getX());
        double yMax = Math.max(getFrontTopLeft().getY(), getBackBottomRight().getY());
        double zMax = Math.max(getFrontTopLeft().getZ(), getBackBottomRight().getZ());


        if(x >= xMin && x < xMax){
            if(y >= yMin && y < yMax){
                if(z >= zMin && z < zMax){
                    return true;
                }
            }
        }
        return false;
    }

    public void tick(){
        if(!renderFace.get(Direction.DOWN)) {
            if (Math.random() < 0.00004) {
                Particle particle = new Particle(getColor(4), getBackBottomLeft().getX(), getBackBottomLeft().getY() - 10 , getBackBottomLeft().getZ());
                particle.setMovement(new double[]{0, -1, 0});
                Renderer.getInstance().registerParticle(particle);

            }
        }
    }



}
