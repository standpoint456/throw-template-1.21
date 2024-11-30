package com.throwable.mixin;

import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.throwable.Throw.FuseTime;

import net.minecraft.entity.Entity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldProperties;

@Mixin(Item.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "use")
	private void init(World world, PlayerEntity user, Hand hand,
					  CallbackInfoReturnable<ActionResult> cir) {
		if(!world.isClient){
			if(user.getStackInHand(hand).getItem()== Items.TNT) {
				ItemStack itemStack = user.getStackInHand(hand);
				TntEntity tnt = new TntEntity(world, user.getX(), user.getY(), user.getZ(), user);
				setVelocity(tnt, user, user.getPitch(), user.getYaw());
				world.spawnEntity(tnt);
				tnt.setFuse(world.getGameRules().get(FuseTime).get());
				itemStack.decrementUnlessCreative(1, user);
			}
		}
	}

	@Unique
	void setVelocity(TntEntity tnt, Entity shooter, float pitch, float yaw) {
		float f = -MathHelper.sin(yaw * ((float)Math.PI / 180))
				* MathHelper.cos(pitch * ((float)Math.PI / 180));
		float g = -MathHelper.sin((pitch + (float) 2) *
				((float)Math.PI / 180));
		float h = MathHelper.cos(yaw * ((float)Math.PI / 180))
				* MathHelper.cos(pitch * ((float)Math.PI / 180));
		tnt.setVelocity(f, g, h);
		Vec3d vec3d = shooter.getMovement();
		tnt.setVelocity(tnt.getVelocity().add(vec3d.x,vec3d.y, vec3d.z));
	}
}