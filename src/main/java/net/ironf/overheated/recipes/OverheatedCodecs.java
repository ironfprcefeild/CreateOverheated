package net.ironf.overheated.recipes;

import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import net.ironf.overheated.laserOptics.backend.heatUtil.HeatData;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public class OverheatedCodecs {

    /// Ingredient Lists
    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> STREAM_ING_LIST = new StreamCodec<>() {
        public NonNullList<Ingredient> decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> output = NonNullList.withSize(size,Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                output.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }
            return output;
        }

        public void encode(RegistryFriendlyByteBuf buf, NonNullList<Ingredient> list) {
            //Size
            buf.writeInt(list.size());
            //Entries
            for (Ingredient ing : list){
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf,ing);
            }
        }
    };

    /// Item Stack Lists
    public static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<ItemStack>> STREAM_STACK_LIST = new StreamCodec<>() {
        public NonNullList<ItemStack> decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<ItemStack> output = NonNullList.withSize(size,ItemStack.EMPTY);
            for (int i = 0; i < size; i++) {
                output.set(i, ItemStack.STREAM_CODEC.decode(buf));
            }
            return output;
        }

        public void encode(RegistryFriendlyByteBuf buf, NonNullList<ItemStack> list) {
            //Size
            buf.writeInt(list.size());
            //Entries
            for (ItemStack stack : list){
                ItemStack.STREAM_CODEC.encode(buf,stack);
            }
        }
    };


    /// HeatData Codecs
    public static final StreamCodec<RegistryFriendlyByteBuf, HeatData> HEAT_DATA = new StreamCodec<>() {
        public HeatData decode(RegistryFriendlyByteBuf buf) {
            return new HeatData(buf.readFloat(),buf.readFloat(),buf.readFloat());
        }

        public void encode(RegistryFriendlyByteBuf buf, HeatData hd) {
            buf.writeFloat(hd.Heat);
            buf.writeFloat(hd.SuperHeat);
            buf.writeFloat(hd.OverHeat);
        }
    };


    /// Fatty mc fat face composites
    //(theese are just composite stream codecs with more than 6 options)
    public static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> fatComposite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> codec7, final Function<C, T7> getter7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory) {
        return new StreamCodec<B, C>() {
            public C decode(B b) {
                T1 t1 = (T1)codec1.decode(b);
                T2 t2 = (T2)codec2.decode(b);
                T3 t3 = (T3)codec3.decode(b);
                T4 t4 = (T4)codec4.decode(b);
                T5 t5 = (T5)codec5.decode(b);
                T6 t6 = (T6)codec6.decode(b);
                T7 t7 = (T7)codec7.decode(b);
                return (C)factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            public void encode(B b, C c) {
                codec1.encode(b, getter1.apply(c));
                codec2.encode(b, getter2.apply(c));
                codec3.encode(b, getter3.apply(c));
                codec4.encode(b, getter4.apply(c));
                codec5.encode(b, getter5.apply(c));
                codec6.encode(b, getter6.apply(c));
                codec7.encode(b, getter7.apply(c));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> fatComposite(final StreamCodec<? super B, T1> codec1, final Function<C, T1> getter1, final StreamCodec<? super B, T2> codec2, final Function<C, T2> getter2, final StreamCodec<? super B, T3> codec3, final Function<C, T3> getter3, final StreamCodec<? super B, T4> codec4, final Function<C, T4> getter4, final StreamCodec<? super B, T5> codec5, final Function<C, T5> getter5, final StreamCodec<? super B, T6> codec6, final Function<C, T6> getter6, final StreamCodec<? super B, T7> codec7, final Function<C, T7> getter7, final StreamCodec<? super B, T8> codec8, final Function<C, T8> getter8, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory) {
        return new StreamCodec<B, C>() {
            public C decode(B b) {
                T1 t1 = (T1)codec1.decode(b);
                T2 t2 = (T2)codec2.decode(b);
                T3 t3 = (T3)codec3.decode(b);
                T4 t4 = (T4)codec4.decode(b);
                T5 t5 = (T5)codec5.decode(b);
                T6 t6 = (T6)codec6.decode(b);
                T7 t7 = (T7)codec7.decode(b);
                T8 t8 = (T8)codec8.decode(b);
                return (C)factory.apply(t1, t2, t3, t4, t5, t6, t7, t8);
            }

            public void encode(B b, C c) {
                codec1.encode(b, getter1.apply(c));
                codec2.encode(b, getter2.apply(c));
                codec3.encode(b, getter3.apply(c));
                codec4.encode(b, getter4.apply(c));
                codec5.encode(b, getter5.apply(c));
                codec6.encode(b, getter6.apply(c));
                codec7.encode(b, getter7.apply(c));
                codec8.encode(b, getter8.apply(c));
            }
        };
    }

}
