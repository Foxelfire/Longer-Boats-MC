package net.foxelfire.tutorialmod.entity.custom;

import net.foxelfire.tutorialmod.entity.ModEntities;
import net.foxelfire.tutorialmod.entity.ai.PorcupineAttackGoal;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

    private static final TrackedData<Boolean> ATTACKING = DataTracker.registerData(PorcupineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationCountdown = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationCountdown = 0; // this is used in PorcupineAttackGoal so it's public

    public PorcupineEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    private void setupAnimationStates(){ // this whole method is effectively a loop, since the tick() method that calls
        if (this.idleAnimationCountdown <= 0) { // it runs - you guessed it - every tick. that's what the timeouts are for:
            this.idleAnimationCountdown = this.random.nextInt(40) + 80; // they're tick counters.
            this.idleAnimationState.start(this.age);
         } else { // Experiment: what happens if we stop the idle animation? why do we need to stop the attack but not this one?
            --this.idleAnimationCountdown;
         }
         if(this.attackAnimationCountdown <= 0 && this.isAttacking()){
            this.attackAnimationCountdown = 40; // we can attack again in 40t = 2 seconds, starting to count next tick.
            this.attackAnimationState.start(this.age);
         } else {
            --this.attackAnimationCountdown; // we're not attacking this tick, get us closer to being able to play our animation when we can.
         }
         if(!this.isAttacking()){
            this.attackAnimationState.stop();
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
        this.goalSelector.add(1, new PorcupineAttackGoal(this, 1D, true));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(3, new AnimalMateGoal(this, 0.75));
        this.goalSelector.add(4, new TemptGoal(this, 0.75, Ingredient.ofItems(Items.APPLE), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 0.5));
        this.goalSelector.add(5, new WanderAroundGoal(this, 0.5));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        // this.targetSelector.add(0, new ActiveTargetGoal(this, something.class<something>, true));
    }

    public static DefaultAttributeContainer.Builder createPorcupineAttributes(){
        return MobEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
            .add(EntityAttributes.GENERIC_ARMOR, 0.7f)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
            .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4);
    }

    public void setAttacking(boolean isAttacking){
        this.dataTracker.set(ATTACKING, isAttacking);
    }

    public boolean isAttacking(){
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    protected void initDataTracker(){
        super.initDataTracker();
        this.dataTracker.startTracking(ATTACKING, false);
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
