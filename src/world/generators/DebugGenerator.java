package world.generators;

import world.blocks.BlockType;
import world.Chunk;
import world.blocks.Cube;
import world.World;

import java.awt.*;

public class DebugGenerator extends WorldGenerator {
    public DebugGenerator(World world) {
        super(world);
    }

    @Override
    public void generate() {

        System.out.println("GENERATING STARTING");

        Chunk chunk = new Chunk(1,1,getWorld());

        for(int j = 0; j<=50; j++){
            Cube cube = new Cube(1 + (j * 50),1,1,50,50,BlockType.STONE);
            cube.setChunk(chunk);
            Color color = new Color((int)(Math.random() * 0x1000000));

            for(int i = 0 ; i < 6; i++){
                Color nC = new Color(color.getRGB());
//                for(int l = 0; l<Math.random() * 6; l++)
//                    nC = nC.darker();
                cube.setColor(nC,  i);
            }

            placeBlock(cube, chunk);
        }


        getWorld().getChunks().add(chunk);

        System.out.println("GENERATING ENDING " + getWorld().getChunks().size());
        System.out.println("GENERATING ENDING " + getWorld().getChunks().get(0).getBlocks().size());

    }
}
