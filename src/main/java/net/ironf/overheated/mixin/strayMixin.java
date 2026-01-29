package net.ironf.overheated.mixin;

import net.ironf.overheated.AllFluids;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

import java.util.logging.Level;

@Mixin(net.minecraft.world.entity.monster.Stray.class)
public abstract class strayMixin extends AbstractSkeleton {

    //Dummy Methods
    protected strayMixin(EntityType<? extends AbstractSkeleton> p_32133_, net.minecraft.world.level.Level p_32134_) {
        super(p_32133_, p_32134_);
    }

    //Method Injection
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(AllFluids.SLUDGE.BUCKET.get())) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, player, AllFluids.STRAY_SAUCE.BUCKET.get().getDefaultInstance());
            player.setItemInHand(hand, itemstack1);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return InteractionResult.PASS;
    }

}
