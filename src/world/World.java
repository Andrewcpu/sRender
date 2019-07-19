package world;

import javafx.geometry.Point3D;
import render.Renderer;
import render.particles.Particle;
import world.blocks.Cube;
import world.entities.Entity;
import world.generators.CarPathGenerator;
import world.generators.WorldGenerator;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class World {
    private final List<Chunk> chunks = new ArrayList<>();

    private List<Entity> entities = new CopyOnWriteArrayList<>();

    private int time = 0; // max time = 20000

    private final WorldGenerator worldGenerator;


    public World() {
        worldGenerator = new WorldGenerator(this);
        generate();
    }

    public List<Chunk> getChunks() {
        return chunks;
    }


    public Cube getBlockAt(double x, double y, double z) {
        Chunk chunk = getChunkAt(x, y, z);
        if (chunk == null) return null;
        return chunk.getBlockAt(x, y, z);
    }

    private Chunk getChunkAt(double x, double y, double z) {
        for (int i = 0; i<chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            if (chunk.contains(x, y, z)) {
                return chunk;
            }
        }
        return null;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    //Functionality has been implemented into getBlockAt(x,y,z). Use this method at own risk. May break in future depending on how annoying I am feeling.
    @Deprecated
    public Cube getSpecificBlockAt(double x, double y, double z) {
        Chunk chunk = getChunkAt(x, y, z);
        if (chunk == null) return null;
        for (Cube cube : chunk.getBlocks()) {
            if (cube.contains(new Point3D(x, y, z))) {
                return cube;
            }
        }

        return null;
    }

    public Particle spawnParticle(Point3D location, Color color){
        Particle particle = new Particle(color, location.getX(),location.getY(),location.getZ());
        Renderer.getInstance().registerParticle(particle);
        return particle;
    }


    private void reloadWorld() {
        for (Chunk chunk : chunks) {
            chunk.updateChunk();
        }
        updateLighting();
    }

    public void tick() {
        time++;
        if (time >= 10000) {
            time -= 10000;
            updateLighting();
        }
        Renderer.getInstance().getLightSources().forEach(LightSource::tick);
        Renderer.getInstance().getParticles().forEach(Particle::tick);
        getChunks().forEach(Chunk::tick);
        getEntities().forEach(Entity::tick);
    }

    public int getTime() {
        return time;
    }


    public void setTime(int time) {
        this.time = time;
    }

    private void generate() {
        worldGenerator.generate();
        reloadWorld();
    }

    public void regenerate(){
        chunks.clear();
        generate();
    }

    /*
    WARNING: WILL CAUSE INTENSE LAG.
     */
    public void updateLighting() {
        for (Chunk chunk : chunks) {
            for (Cube cube : chunk.getBlocks()) {
                cube.updateLighting();
            }
        }
    }

    public void updateLighting(double x, double y, double z){
        updateLighting(x,y,z, 10 * Renderer.CUBE_DEFAULT_SIZE);
    }
    public void updateLighting(double x, double y, double z, double range){
        List<Cube> cubes = getCubesWithinRange(new Point3D(x,y,z),range);
        new Thread(()->cubes.forEach(Cube::updateLighting)).start();
    }

    public void updateLighting(Cube cube){
        for(int i = 0; i<cube.getAllActiveLightSources().size(); i++){
            LightSource source = cube.getAllActiveLightSources().get(i);
            updateLighting(cube.getX(),cube.getY(),cube.getZ(), source.getIntensity() - cube.getLocation().distance(source.getLocation()));
        }
    }


    public List<Cube> getCubesWithinRange(Point3D a, double range){
        List<Cube> nearbyCubes = new ArrayList<>();
        for(int i = 0; i<chunks.size(); i++){
            Chunk chunk = chunks.get(i);
            nearbyCubes.addAll(chunk.getBlocks().stream().filter(c -> c.isSideVisible()).filter((c) -> c.getLocation().distance(a) < range).collect(Collectors.toList()));
        }
        nearbyCubes = nearbyCubes.stream().sorted(Comparator.comparingDouble( c -> c.getLocation().distance(a))).collect(Collectors.toList());
        return nearbyCubes;
    }

    public LightSource getClosestLightSourceWithinRange(Point3D a) {
        return Renderer.getInstance().getLightSources().stream().filter(l -> l.distanceTo(a) < l.getIntensity())
                .sorted(Comparator.comparingDouble(l -> l.distanceTo(a)))
                .filter(b -> hasLineOfSight(a, b.getCube()))
                .findFirst().orElse(null);
    }

    public boolean hasLineOfSight(Point3D a, Cube b){


        Point3D cubeCenter = b.getMiddleOfCube();

        double dx = cubeCenter.getX() - a.getX();
        double dy = cubeCenter.getY() - a.getY();
        double dz = cubeCenter.getZ() - a.getZ();



        double steps = 25.0;

        dx /= steps;
        dy /= steps;
        dz /= steps;

        for(int step = 1; step<= steps; step++)
        {
            Cube c = getBlockAt(a.getX() + (dx * step), a.getY() + (dy * step), a.getZ() + (dz * step));
            if(c == null) continue;
            if(c == b) return true;
            if(!c.isSolid()) continue;
            return false;
        }
        return false;
    }

}
