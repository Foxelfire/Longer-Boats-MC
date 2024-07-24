package net.foxelfire.tutorialmod.entity.custom;

import net.foxelfire.tutorialmod.entity.ModEntities;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PorcupineEntity extends AnimalEntity{

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public PorcupineEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setupAnimationStates(){
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
         } else {
            --this.idleAnimationTimeout;
         }
    }

    @Override
    public void tick(){
        super.tick();
        if(this.getWorld().isClient()){
            setupAnimationStates();
        }
    }

    @Override
    protected void initGoals(){
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(2, new AnimalMateGoal(this, 0.75));
        this.goalSelector.add(3, new TemptGoal(this, 0.75, Ingredient.ofItems(Items.APPLE), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 0.5));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.5));
        this.goalSelector.add(6, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createPorcupineAttributes(){
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
            .add(EntityAttributes.GENERIC_ARMOR, 0.7f)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4);
    }

    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity arg1) {
        return ModEntities.PORCUPINE.create(world);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
      return stack.isOf(Items.APPLE);
    }

    protected void updateLimbs(float posDelta) {
      float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0F, 1.0F) : 0.0f;
      this.limbAnimator.updateLimbs(f, 0.2F);
   }

    @Override
    public SoundEvent getAmbientSound(){
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source){
        return SoundEvents.ENTITY_PANDA_HURT;
    }
    @Override
    public SoundEvent getDeathSound(){
        return SoundEvents.ENTITY_FOX_DEATH;
    }
}
