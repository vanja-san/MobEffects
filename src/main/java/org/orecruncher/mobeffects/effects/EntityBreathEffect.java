/*
 *  Dynamic Surroundings: Mob Effects
 *  Copyright (C) 2019  OreCruncher
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package org.orecruncher.mobeffects.effects;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.orecruncher.lib.GameUtils;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.effects.AbstractEntityEffect;
import org.orecruncher.lib.effects.EntityEffectManager;
import org.orecruncher.lib.random.MurmurHash3;
import org.orecruncher.mobeffects.MobEffects;
import org.orecruncher.mobeffects.effects.particles.BubbleBreathParticle;
import org.orecruncher.mobeffects.effects.particles.FrostBreathParticle;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class EntityBreathEffect extends AbstractEntityEffect {

    private static final ResourceLocation NAME = new ResourceLocation(MobEffects.MOD_ID, "breath");
    public static final FactoryHandler FACTORY = new FactoryHandler(
            EntityBreathEffect.NAME,
            entity -> new EntityBreathEffect());

    private int seed;

    public EntityBreathEffect() {
        super(NAME);
    }

    @Override
    public void intitialize(@Nonnull final EntityEffectManager state) {
        super.intitialize(state);
        this.seed = MurmurHash3.hash(getEntity().getEntityId());
    }

    @Override
    public void update() {
        final Entity entity = getEntity();
        if (isBreathVisible(entity)) {
            final int c = (int) ((GameUtils.getWorld().getDayTime() + this.seed));
            final BlockPos headPos = getHeadPosition(entity);
            final BlockState state = entity.getEntityWorld().getBlockState(headPos);
            if (showWaterBubbles(state)) {
                final int air = entity.getAir();
                if (air > 0) {
                    final int interval = c % 3;
                    if (interval == 0) {
                        createBubbleParticle(false);
                    }
                } else if (air == 0) {
                    // Need to generate a bunch of bubbles due to drowning
                    for (int i = 0; i < 8; i++) {
                        createBubbleParticle(true);
                    }
                }
            } else {
                final int interval = (c / 10) % 8;
                if (interval < 3 && showFrostBreath(entity, state, headPos)) {
                    createFrostParticle();
                }
            }
        }
    }

    protected boolean isBreathVisible(@Nonnull final Entity entity) {
        final PlayerEntity player = GameUtils.getPlayer();
        if (entity == player) {
            return !player.isSpectator();
        }
        return !entity.isInvisibleToPlayer(player) && player.canEntityBeSeen(entity);
    }

    protected BlockPos getHeadPosition(final Entity entity) {
        final double d0 = entity.posY + entity.getEyeHeight();
        return new BlockPos(entity.posX, d0, entity.posZ);
    }

    protected boolean showWaterBubbles(@Nonnull final BlockState headBlock) {
        return headBlock.getMaterial().isLiquid();
    }

    protected boolean showFrostBreath(final Entity entity, @Nonnull final BlockState headBlock, @Nonnull final BlockPos pos) {
        if (headBlock.getMaterial() == Material.AIR) {
            final World world = entity.getEntityWorld();
            return WorldUtils.isColdTemperature(WorldUtils.getTemperatureAt(world, pos));
        }
        return false;
    }

    protected void createBubbleParticle(boolean isDrowning) {
        final BubbleBreathParticle p = new BubbleBreathParticle((LivingEntity) getEntity(), isDrowning);
        addParticle(p);
    }

    protected void createFrostParticle() {
        final FrostBreathParticle p = new FrostBreathParticle((LivingEntity) getEntity());
        addParticle(p);
    }

}