package world.generators;

import javafx.geometry.Point3D;
import render.Renderer;
import world.*;
import world.blocks.BlockType;
import world.blocks.Cube;
import world.blocks.LightBlock;

import java.awt.*;

public class WorldGenerator {
    private World world;
    public WorldGenerator (World world){
        this.world = world;
    }

    public void generate(){
        int chunkNum = 1;

        OpenSimplexNoise noise = new OpenSimplexNoise();


        double radius = 700;
        double seed = Math.random() * 100.0;

        boolean islandGenerator = true;

        for(int i = -chunkNum; i<chunkNum; i++){
            for(int j = -chunkNum; j<chunkNum; j++){


                int nX = i * 1000;
                int nZ = j * 1000;

                Chunk chunk = new Chunk(i , j , world);

                for(int x = 0; x<1000; x+= Renderer.CUBE_DEFAULT_SIZE){
                    for(int z = 0; z<1000; z+=Renderer.CUBE_DEFAULT_SIZE) {
                        double nY = 0;
                        for(int y = islandGenerator ? -(Renderer.CUBE_DEFAULT_SIZE * 20) : -(Renderer.CUBE_DEFAULT_SIZE * 3); y<=0; y+=Renderer.CUBE_DEFAULT_SIZE ){

                            if(islandGenerator && new Point3D(nX + x, y + nY, nZ + z).distance(0,0,0) > radius || islandGenerator && new Point3D(nX + x, nY + y, nZ + z).distance(0,-250,0) < 150) continue;

                            Cube cube = new Cube(nX + x,y + nY,nZ + z, Renderer.CUBE_DEFAULT_SIZE, Renderer.CUBE_DEFAULT_SIZE, y>= 0 ? BlockType.GRASS : BlockType.STONE);
                            if(y >= 0)
                            {
                                cube.setColor(new Color(0, 100 + (int)(20 * noise.eval(x / Renderer.CUBE_DEFAULT_SIZE,z / Renderer.CUBE_DEFAULT_SIZE)), 0));
                                if(Math.random() < 0.005){
                                    cube = new LightBlock(nX + x, nY + y + Renderer.CUBE_DEFAULT_SIZE, nZ + z, Renderer.CUBE_DEFAULT_SIZE * 8);
                                    cube.setColor(new Color(255, 223, 0));
                                }
                            }
                            else
                            {
                                int v = (int)(Math.abs(noise.eval((seed + nX + x) , (seed + nY + y) , (seed + nZ + z) )) * 255);
                                cube.setColor(new Color(v,v,v));
                            }
                            cube.setChunk(chunk);
                            placeBlock(cube, chunk);
                        }
                    }
                }
                world.getChunks().add(chunk);
            }
        }
        getWorld().updateLighting();
    }


    World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    void placeBlock(Cube cube, Chunk chunk){
        if(chunk.getBlockAt(cube.getX(),cube.getY(),cube.getZ()) != null) return;
        chunk.addBlock(cube);
    }

}
