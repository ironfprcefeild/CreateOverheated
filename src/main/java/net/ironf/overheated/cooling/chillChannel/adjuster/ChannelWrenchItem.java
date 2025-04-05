package net.ironf.overheated.cooling.chillChannel.adjuster;

import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ChannelWrenchItem extends Item {
    public ChannelWrenchItem(Properties p) {
        super(p);
    }


    //Item can be used to set the target (or hook) of the absorber or expeller blocks
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching()){
            context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);
            Minecraft.getInstance().gui.setSubtitle(Component.translatable("coverheated.chill_channel.wrench.bound_initial.cleared"));
            return InteractionResult.SUCCESS;
        } else if (!context.getItemInHand().getOrCreateTag().getBoolean("initialselected")){
            //Clicking first block
            context.getItemInHand().getOrCreateTag().putLong("initialpos",context.getClickedPos().asLong());
            Minecraft.getInstance().gui.setSubtitle(Component.translatable("coverheated.chill_channel.wrench.bound_initial"));
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof IChillChannelHook hookBe){
              if (hookBe.canBeRouted()){
                  //We have clicked a proper block and can now set the hook
                  hookBe.setHookTarget(BlockPos.of(context.getItemInHand().getOrCreateTag().getLong("initialpos")));
                  context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);
                  Minecraft.getInstance().gui.setSubtitle(Component.translatable("coverheated.chill_channel.wrench.bound_hook"));
                  return InteractionResult.SUCCESS;
              }
            }
        }
        return InteractionResult.PASS;
    }

}
