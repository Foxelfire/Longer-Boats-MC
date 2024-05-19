package net.foxelfire.tutorialmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundBlock extends Block{

    private SoundEvent soundToPlay;

    public SoundBlock(Settings settings, SoundEvent sound) {
        super(settings);
        soundToPlay = sound;
    }
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        world.playSound(player, pos, soundToPlay, SoundCategory.BLOCKS, 1f, 1f);
        return ActionResult.SUCCESS;
    }
}
