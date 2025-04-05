package net.ironf.overheated.cooling.chillChannel.adjuster;

import net.ironf.overheated.Overheated;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.io.Console;

public class ChannelWrenchItem extends Item {
    public ChannelWrenchItem(Properties p) {
        super(p);
    }


    //Item can be used to set the target (or hook) of the absorber or expeller blocks
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Overheated.LOGGER.info("Used On");
        if (context.getPlayer() != null && context.getPlayer().isCrouching()){
            context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);
            Overheated.LOGGER.info("Point A (Cleared)");
            return InteractionResult.SUCCESS;
        } else if (!context.getItemInHand().getOrCreateTag().getBoolean("initialselected")){
            //Clicking first block
            context.getItemInHand().getOrCreateTag().putLong("initialpos",context.getClickedPos().asLong());
            context.getItemInHand().getOrCreateTag().putBoolean("initialselected",true);

            Overheated.LOGGER.info("Point B (Set Initial)");
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof IChillChannelHook hookBe){
                Overheated.LOGGER.info("Point C (BE pulled)");
                BlockPos initialPos = BlockPos.of(context.getItemInHand().getOrCreateTag().getLong("initialpos"));
                if (hookBe.canBeRouted() && initialPos.distSqr(initialPos) <= 32){
                  Overheated.LOGGER.info("Point D (Set Draw From)");
                  //We have clicked a proper block and can now set the hook
                  hookBe.setHookTarget(initialPos);
                  context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);

                  return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

}
