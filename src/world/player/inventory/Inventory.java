package world.player.inventory;

import world.blocks.BlockType;

public class Inventory {
    private ItemStack[] contents;


    private int selectedSlot = 0;

    public Inventory(){
        contents = new ItemStack[10];
    }


    public void addItem(ItemStack itemStack){

        ItemStack block= getByBlockType(itemStack.getBlockType());
        if(block != null){
            block.addColorToStack(itemStack.getData().getColorStack("color_stack"));
            block.setAmount(block.getAmount() + 1);
        }
        else{
            int i = 0;
            while(contents[i] != null)
                i++;
            contents[i] = itemStack;
        }
    }

    private ItemStack getByBlockType(BlockType type){
        for(int i = 0; i<contents.length; i++){
            if (contents[i] != null) {
                if(contents[i].getBlockType() == type){
                    return contents[i];
                }
            }
        }
        return null;
    }



    public ItemStack[] getContents(){
        return this.contents;
    }

    public void clear(){
        contents = new ItemStack[10];
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public ItemStack getItem(int index){
        return getContents()[index];
    }

    public ItemStack getSelectedItem(){
        return getContents()[getSelectedSlot()];
    }

    public void placeItem(){
        getContents()[selectedSlot].setAmount(getContents()[selectedSlot].getAmount() - 1);
        getContents()[selectedSlot].placed();
        if(getContents()[selectedSlot].getAmount() <= 0)
            getContents()[selectedSlot] = null;
    }

}
