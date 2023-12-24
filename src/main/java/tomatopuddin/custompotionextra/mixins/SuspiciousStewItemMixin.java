package tomatopuddin.custompotionextra.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.*;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SuspiciousStewItem.class)
public class SuspiciousStewItemMixin extends Item {
    public SuspiciousStewItemMixin(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Inject(method = "saveMobEffect(Lnet/minecraft/item/ItemStack;Lnet/minecraft/potion/Effect;I)V", at = @At(value = "HEAD"), cancellable = true)
    private static void saveMobEffectEx(ItemStack p_220037_0_, Effect p_220037_1_, int p_220037_2_, CallbackInfo ci) {
        CompoundNBT compoundnbt = p_220037_0_.getOrCreateTag();
        ListNBT listnbt = compoundnbt.getList("Effects", 9);
        CompoundNBT compoundnbt1 = new CompoundNBT();
        ResourceLocation loc = ForgeRegistries.POTIONS.getKey(p_220037_1_);
        if (loc != null) {
            compoundnbt1.putString("EffectId", loc.toString());
        } else {
            compoundnbt1.putByte("EffectId", (byte) Effect.getId(p_220037_1_));
        }
        compoundnbt1.putInt("EffectDuration", p_220037_2_);
        listnbt.add(compoundnbt1);
        compoundnbt.put("Effects", listnbt);
        ci.cancel();
    }

    @Inject(method = "finishUsingItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void finishUsingItemeX(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemstack = super.finishUsingItem(p_77654_1_, p_77654_2_, p_77654_3_);
        CompoundNBT compoundnbt = p_77654_1_.getTag();
        if (compoundnbt != null && compoundnbt.contains("Effects", 9)) {
            ListNBT listnbt = compoundnbt.getList("Effects", 10);

            for (int i = 0; i < listnbt.size(); ++i) {
                int j = 160;
                CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                if (compoundnbt1.contains("EffectDuration", 3)) {
                    j = compoundnbt1.getInt("EffectDuration");
                }

                Effect effect = null;
                INBT idNbt = compoundnbt1.get("EffectId");
                if (idNbt instanceof NumberNBT) {
                    effect = Effect.byId(((NumberNBT)idNbt).getAsByte());
                } else if (idNbt instanceof StringNBT) {
                    ResourceLocation loc = ResourceLocation.tryParse(idNbt.getAsString());
                    if (loc != null) {
                        effect = ForgeRegistries.POTIONS.getValue(loc);
                    }
                }
                if (effect != null) {
                    p_77654_3_.addEffect(new EffectInstance(effect, j));
                }
            }
        }

        ItemStack itemStack = p_77654_3_ instanceof PlayerEntity && ((PlayerEntity) p_77654_3_).abilities.instabuild ? itemstack : new ItemStack(Items.BOWL);
        cir.setReturnValue(itemStack);
    }
}
