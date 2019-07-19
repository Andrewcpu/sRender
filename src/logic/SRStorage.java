package logic;


import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class SRStorage {
    private final HashMap<String,Object> storage;
    public SRStorage(){
        storage = new HashMap<>();
    }


    public void store(String key, Object val){
        storage.remove(key);
        storage.put(key,val);
    }

    public boolean getBoolean(String key){
        return (boolean)(storage.get(key));
    }

    public int getInt(String key){
        return (int)storage.get(key);
    }

    public double getDouble(String key){
        return (double)storage.get(key);
    }

    public Color getColor(String key){
        return (Color)storage.get(key);
    }

    public List<Color> getColorStack(String key){
        return (List<Color>)storage.get(key);
    }

    private Object getValue(String key){
        return storage.get(key);
    }

    private Set<String> keys() {
        return storage.keySet();
    }

    public void merge(SRStorage sRUnit){ //takes from the SRStorage unit and puts it into this instance.
        for(String key : sRUnit.keys()){
            store(key, sRUnit.getValue(key));
        }
    }
}
