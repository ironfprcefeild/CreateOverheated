package net.ironf.overheated.steamworks;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;

import java.util.List;
import java.util.stream.Stream;

public class SteamFluidIngredient extends FluidIngredient {

    @Override
    public boolean test(FluidStack t) {
        return AllSteamFluids.isSteam(t);
    }

    @Override
    protected Stream<FluidStack> generateStacks() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public FluidIngredientType<?> getType() {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
