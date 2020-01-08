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

package org.orecruncher.mobeffects.footsteps.accents;

import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.mobeffects.library.FootstepLibrary;
import org.orecruncher.sndctrl.audio.acoustic.IAcoustic;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
class WaterLoggedAccent implements IFootstepAccentProvider {

    @Override
    public void provide(
            @Nonnull final LivingEntity entity,
            @Nonnull final BlockPos blockPos,
            @Nonnull final BlockState posState,
            @Nonnull final ObjectArray<IAcoustic> acoustics) {
        if (posState.getBlock() instanceof IWaterLoggable) {
            final IFluidState fluid = posState.getFluidState();
            if (!fluid.isEmpty())
                acoustics.add(FootstepLibrary.getWaterLoggedAcoustic());
        }
    }
}
