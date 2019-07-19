package world.generators;

import world.Chunk;
import world.World;
import world.blocks.BlockType;
import world.blocks.Cube;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CarPathGenerator extends WorldGenerator {
    public CarPathGenerator(World world) {
        super(world);
    }


    public List<double[]> getPoints() throws Exception{
        List<double[]> points = new ArrayList<>();

        for(String s : getLocationHistory()){
            String[] coords = s.split(",");
            double scale = 200;
            points.add(new double[]{Double.parseDouble(coords[0]) * scale, Double.parseDouble(coords[1]) * scale});
        }


        return points;
    }

    private String[] getLocationHistory() throws Exception {

        String url = "http://frcs.online/g/api/get_location_history.php";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "3DRendering");

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        return response.toString().split("\\|");

    }


    @Override
    public void generate() {


        Chunk chunk = new Chunk(1,1,getWorld());

        try {
            for(double[] cords : getPoints()){
                Cube cube = new Cube(1 + cords[0], 1, 1 + cords[1], 50, 50, BlockType.STONE );
                cube.setChunk(chunk);
                cube.setColor(Color.RED);
                placeBlock(cube, chunk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        getWorld().getChunks().add(chunk);

    }
}
