package net.ironf.overheated.steamworks;

import com.google.gson.JsonObject;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class SteamFluidIngredient extends FluidIngredient {

    @Override
    protected boolean testInternal(FluidStack t) {return AllSteamFluids.isSteam(t);}
    @Override
    protected void readInternal(FriendlyByteBuf buffer) {}
    @Override
    protected void writeInternal(FriendlyByteBuf buffer) {}
    @Override
    protected void readInternal(JsonObject json) {}
    @Override
    protected void writeInternal(JsonObject json) {}

    @Override
    protected List<FluidStack> determineMatchingFluidStacks() {
        return List.of();
    }

    public SteamFluidIngredient(int amount){
        this.amountRequired = amount;
    }

    @Override
    public boolean test(FluidStack t) {
        return AllSteamFluids.isSteam(t);
    }
}
