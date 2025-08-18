package net.ironf.overheated.cooling.chillChannel.adjuster;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTarget;
import net.ironf.overheated.cooling.chillChannel.network.IChillChannelHook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChannelWrenchItem extends Item {
    public ChannelWrenchItem(Properties p) {
        super(p);
    }

    //Item can be used to set the target (or hook) of the absorber or expeller blocks
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() != null && context.getPlayer().isCrouching()){
            //Clear Selection
            context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);
            displayClientMessage(context,"coverheated.chill_channel.wrench.cleared");
            return InteractionResult.SUCCESS;
        } else if (!context.getItemInHand().getOrCreateTag().getBoolean("initialselected")){
            //Clicking first block
            BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());
            if (be instanceof IChillChannelHook) {
                context.getItemInHand().getOrCreateTag().putLong("initialpos",context.getClickedPos().asLong());
                context.getItemInHand().getOrCreateTag().putBoolean("initialselected",true);
                displayClientMessage(context,"coverheated.chill_channel.wrench.bound_initial");
            }

            return InteractionResult.SUCCESS;
        } else {
            //Getting Block Entity that has been clicked, checking if it can be adjusted
            BlockPos initialPos = BlockPos.of(context.getItemInHand().getOrCreateTag().getLong("initialpos"));
            BlockEntity dbe = context.getLevel().getBlockEntity(context.getClickedPos());
            BlockEntity sbe = context.getLevel().getBlockEntity(initialPos);

            if (dbe instanceof IChillChannelHook destinationBE && sbe instanceof IChillChannelHook startBE){

                if (startBE.canBeRouted() && initialPos.distSqr(context.getClickedPos()) <= 32){
                    displayClientMessage(context,"coverheated.chill_channel.wrench.bound_hook");
                    //We have clicked a proper block and can now set the hook
                    destinationBE.setDrawFrom(initialPos);
                    startBE.setSendToo(context.getClickedPos());
                    context.getItemInHand().getOrCreateTag().putBoolean("initialselected",false);
                  return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public void displayClientMessage(UseOnContext context, String message){
        context.getPlayer().displayClientMessage(Component.translatable(message),true);
    }

    private static BlockPos lastShownPos = null;
    private static AABB lastShownAABB = null;

    @OnlyIn(Dist.CLIENT)
    public static void clientTick() {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        ItemStack heldItemMainhand = player.getMainHandItem();
        if (!(heldItemMainhand.getItem() instanceof ChannelWrenchItem))
            return;
        if (!heldItemMainhand.hasTag())
            return;
        CompoundTag stackTag = heldItemMainhand.getOrCreateTag();
        if (!stackTag.contains("initialpos"))
            return;

        BlockPos selectedPos = BlockPos.of(stackTag.getLong("initialpos"));

        if (!selectedPos.equals(lastShownPos)) {
            lastShownAABB = getBounds(selectedPos);
            lastShownPos = selectedPos;
        }

        CreateClient.OUTLINER.showAABB("target", lastShownAABB)
                .colored(0x34e8eb)
                .lineWidth(1 / 16f);
    }

    @OnlyIn(Dist.CLIENT)
    private static AABB getBounds(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        DisplayTarget target = AllDisplayBehaviours.targetOf(world, pos);

        if (target != null)
            return target.getMultiblockBounds(world, pos);

        BlockState state = world.getBlockState(pos);
        VoxelShape shape = state.getShape(world, pos);
        return shape.isEmpty() ? new AABB(BlockPos.ZERO)
                : shape.bounds()
                .move(pos);
    }
}
