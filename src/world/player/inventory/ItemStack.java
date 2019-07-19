package world.player.inventory;

import logic.SRStorage;
import world.blocks.BlockType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ItemStack {
    private ItemType type;

    private BlockType blockType = null;

    private int amount; // max 100
    private String name;

    private SRStorage data;

    private ItemStack(ItemType type, int amount, String name) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.data = new SRStorage();
        this.data.store("color_stack", new ArrayList<Color>());
    }


    public BlockType getBlockType() {
        return blockType;
    }

    private void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }

    public SRStorage getData() {
        return data;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(SRStorage data) {
        this.data = data;
    }

    public boolean isPlaceable(){
        return type == ItemType.PLACEABLE;
    }

    private void addColorToStack(Color color){
        List<Color> color_stack = getData().getColorStack("color_stack");
        color_stack.add(color);
        getData().store("color_stack", color_stack);
    }
    public void addColorToStack(List<Color> colors){
        List<Color> color_stack = getData().getColorStack("color_stack");
        color_stack.addAll(colors);
        getData().store("color_stack", color_stack);
    }


    public void placed(){
        List<Color> color_stack = getData().getColorStack("color_stack");
        color_stack.remove(0);
        getData().store("color_stack",color_stack);
    }

    public static ItemStack blockBuilder(Color color, int amount, BlockType blockType){
        ItemStack i = new ItemStack(ItemType.PLACEABLE, amount, blockType.toString() + " Block");
        i.addColorToStack(color);
        i.setBlockType(blockType);
        return i;
    }
    public static ItemStack lightBuilder(Color color, int amount, int intensity){
        ItemStack i = new ItemStack(ItemType.PLACEABLE, amount, "Light Block");
        for(int amt = 0; amt<amount; amt++)
            i.addColorToStack(color);
        i.getData().store("intensity", intensity);
        i.setBlockType(BlockType.LIGHT);
        return i;
    }

}
