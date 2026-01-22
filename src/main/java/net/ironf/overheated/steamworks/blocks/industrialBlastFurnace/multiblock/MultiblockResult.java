package net.ironf.overheated.steamworks.blocks.industrialBlastFurnace.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public record MultiblockResult(boolean success, BlockPos errorPos, String message) {

    public static MultiblockResult VALID = new MultiblockResult(true, null, null);

    public static MultiblockResult ERROR(BlockPos pos, String message) {
        return new MultiblockResult(false, pos, message);
    }

    public boolean error(){return !success;}


    public static MultiblockResult Read(CompoundTag tag,String s){
        return new MultiblockResult(
                tag.getBoolean(s+"valid"),
                tag.contains(s+"blockpos")
                        ? BlockPos.of(tag.getLong(s+"blockpos"))
                        : null,
                tag.contains(s+"messagekey")
                        ? tag.getString(s+"messagekey")
                        : null
                );
    }
    public void write(CompoundTag tag,String s){
        tag.putBoolean(s+"valid",success);
        if (errorPos != null){
            tag.putLong(s+"blockpos",errorPos.asLong());
        }
        if (message != null){
            tag.putString(s+"messagekey",message);
        }
    }
}
