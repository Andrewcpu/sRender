package render;

import javafx.geometry.Point3D;
import logic.Direction;
import world.blocks.Cube;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RenderBlock implements Comparable<RenderBlock>{
    //renderblock holds nodes and face connection data, the data put here has already been rotated according to the camera.
//    private int[][] faces;
    private Point3D[] nodes;
    private final int[][] sides;
    private final Color[] colors;
    private final Cube cube;
    private List<RenderSide> renderSides = new ArrayList<>();


    private boolean fadeInDistance = true;


    private RenderType type;

    public RenderBlock(Point3D[] nodes, int[][] sides, Color[] colors, Cube cube, RenderType type) {
        this.nodes = nodes;
        this.sides = sides;
        this.colors = colors;
        this.cube = cube;
        this.type = type;
        orderSides();
    }

    public boolean isFadeInDistance() {
        return fadeInDistance;
    }

    public void setFadeInDistance(boolean fadeInDistance) {
        this.fadeInDistance = fadeInDistance;
    }

    public RenderType getType() {
        return type;
    }

    public void setType(RenderType type) {
        this.type = type;
    }

    private void orderSides(){
        for(int n = 0; n<sides.length; n++) {

            Direction dir = null;
            if(n == 0)
                dir = Direction.SOUTH;
            if(n == 1)
                dir = Direction.WEST;
            if(n == 2)
                dir = Direction.EAST;
            if(n == 3)
                dir = Direction.NORTH;
            if(n == 4)
                dir = Direction.DOWN;
            if(n == 5)
                dir = Direction.UP;

            Point3D a = nodes[sides[n][0]];
            Point3D b = nodes[sides[n][1]];
            Point3D c = nodes[sides[n][2]];
            Point3D d = nodes[sides[n][3]];
            RenderSide side = new RenderSide(a,b,c,d,colors[n], dir, cube);
            renderSides.add(side);
        }
        Collections.sort(renderSides);
    }

    public List<RenderSide> getRenderSides() {
        return renderSides;
    }

    public void setRenderSides(List<RenderSide> renderSides) {
        this.renderSides = renderSides;
    }

    public Cube getCube() {
        return cube;
    }

    public int[][] getSides() {
        return sides;
    }


    public Point3D[] getNodes() {
        return nodes;
    }

    public void setNodes(Point3D[] nodes) {
        this.nodes = nodes;
    }

    public Color getColor(int side) {
        return colors[side];
    }

    private double distance(){


        double smallestDistance = Double.MAX_VALUE;
        for(Point3D node : nodes){
            if(node == null) continue;
            double d = Math.sqrt(node.getX() * node.getX() + node.getY() * node.getY() + node.getZ() * node.getZ());
            if(d < smallestDistance)
                smallestDistance = d;
        }

//        return nodes[0].distance(0,0,0);
        return smallestDistance;
    }

    @Override
    public int compareTo(RenderBlock o) {
        return Double.compare(o.distance(),distance());
    }
}
