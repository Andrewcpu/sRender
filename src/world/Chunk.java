package world;

import javafx.geometry.Point3D;
import logic.CoordinateUtilities;
import logic.Direction;
import render.Renderer;
import world.blocks.Cube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chunk {
    private List<Cube> blocks = new CopyOnWriteArrayList<>();
    private final World world;
    // will hold 1000x1000 square
    private final int x;
    private final int z;




    public Chunk(int x, int z, World world) {
        this.x = x;
        this.z = z;
        this.world = world;
    }

    public World getWorld() {
        return world;
    }


    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void addBlock(Cube cube){
        blocks.add(cube);
    }

    public List<Cube> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Cube> blocks) {
        this.blocks = blocks;
    }

    public void updateChunk(){
        for(Cube cube : blocks)
            cube.update();
    }

    public void blockUpdate(Cube cube){
        for(Direction d : Direction.values()){
            Point3D step = CoordinateUtilities.directionToMovement(d);
            step = step.multiply(Renderer.CUBE_DEFAULT_SIZE);
            Cube c = world.getBlockAt(cube.getX() + step.getX(), cube.getY() + step.getY(), cube.getZ() + step.getZ());

            if(c == null) continue;

            c.update();
        }
    }

    public Cube getBlockAt(double x, double y, double z){
        if(!contains(x,y,z)) return null;
        for(int i = 0; i<blocks.size(); i++){
            if(blocks.get(i) == null) continue;
            if(blocks.get(i).contains(x,y,z)) return  blocks.get(i);
        }
        return null;
    }

    public boolean contains(double x, double y, double z){
        if(getX() * 1000 <= x && getX() * 1000 + 1000 > x){
            if(getZ() * 1000 <= z && getZ() * 1000 + 1000 > z){
                return true;
            }
        }
        return false;
    }

    public void tick(){
        getBlocks().forEach(Cube::tick);
    }
}
