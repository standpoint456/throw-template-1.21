package com.throwable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.throwable.Throw.FuseTime;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@Mixin(Item.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "use")
	private void init(Level world, Player user, InteractionHand hand,
					  CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if(!world.isClientSide){
			if(user.getItemInHand(hand).getItem()== Items.TNT) {
				ItemStack itemStack = user.getItemInHand(hand);
				PrimedTnt tnt = new PrimedTnt(world, user.getX(), user.getY(), user.getZ(), user);
				setVelocity(tnt, user, user.getXRot(), user.getYRot());
				world.addFreshEntity(tnt);
				tnt.setFuse(world.getGameRules().getRule(FuseTime).get());
				itemStack.consume(1, user);
			}
		}
	}

	@Unique
	void setVelocity(PrimedTnt tnt, Entity shooter, float pitch, float yaw) {
		float f = -Mth.sin(yaw * ((float)Math.PI / 180))
				* Mth.cos(pitch * ((float)Math.PI / 180));
		float g = -Mth.sin((pitch + (float) 2) *
				((float)Math.PI / 180));
		float h = Mth.cos(yaw * ((float)Math.PI / 180))
				* Mth.cos(pitch * ((float)Math.PI / 180));
		tnt.setDeltaMovement(f, g, h);
		Vec3 vec3d = shooter.getKnownMovement();
		tnt.setDeltaMovement(tnt.getDeltaMovement().add(vec3d.x,vec3d.y, vec3d.z));
	}
}