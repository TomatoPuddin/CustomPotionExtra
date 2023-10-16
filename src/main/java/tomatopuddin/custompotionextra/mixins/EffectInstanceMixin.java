package tomatopuddin.custompotionextra.mixins;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin {
    @Inject(method = "load(Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/potion/EffectInstance;", at=@At(value = "HEAD"), cancellable = true)
    private static void loadEx(CompoundNBT nbt, CallbackInfoReturnable<EffectInstance> cir) {
        INBT idnbt = nbt.get("Id");

        Effect effect = null;
        if(idnbt instanceof NumberNBT) {
            effect = Effect.byId(((NumberNBT) idnbt).getAsInt());
        } else if(idnbt instanceof StringNBT) {
            ResourceLocation loc = ResourceLocation.tryParse(idnbt.getAsString());
            if(loc != null) {
                effect = ForgeRegistries.POTIONS.getValue(loc);
            }
        }

        if(effect == null) {
            cir.setReturnValue(null);
            return;
        }

        cir.setReturnValue(EffectInstance.loadSpecifiedEffect(effect, nbt));
    }
}
