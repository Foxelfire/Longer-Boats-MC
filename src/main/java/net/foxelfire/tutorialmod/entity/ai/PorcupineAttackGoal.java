package net.foxelfire.tutorialmod.entity.ai;

import net.foxelfire.tutorialmod.entity.custom.PorcupineEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;

public class PorcupineAttackGoal extends MeleeAttackGoal{
    // this entire class only exists to make attack cooldowns and animations. That's it.
    private final PorcupineEntity entity;
    private final int attackLengthInTicks = 20; // the attack is 2 seconds long, but happens 1s into the animation
    private int ticksUntilNextAttack = 20;
    private boolean waitingForNextAttack = false;

    public PorcupineAttackGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = ((PorcupineEntity)mob); // for setAttacking(t/f) calls
    }

    @Override
    public void start(){
        super.start();
        ticksUntilNextAttack = 20; // yes, ik its initialized to 20. its for if we stop being angry and return to it later
    }

    @Override
    public void tick(){
        super.tick();
        if(waitingForNextAttack){
            this.ticksUntilNextAttack = Math.max(ticksUntilNextAttack - 1, 0); // decrements ticksUntilNextAttack each tick until it hits 0, making it actually function as a cooldown
        }
    }

    @Override
    public void stop(){
        entity.setAttacking(false);
        super.stop();
    }
    @Override
    protected void attack(LivingEntity target){ 
        /* WARNING FOR ANY FUTURE FOXEL CHANGING THIS - this is called in tick() of MeleeAttackGoal. This is a loop. 
        The changes you make to variables here will happen every tick, potentially making some ifs unreachable. */
        if(isTargetCloseEnough(target)){
            waitingForNextAttack = true;
            if(shouldPlayAttackAnimation()){
                entity.setAttacking(true);
            }
            if(this.ticksUntilNextAttack <= 0){
                this.mob.getLookControl().lookAt(target.getX(), target.getEyeY(), target.getZ()); 
                // this.mob is MeleeAttackGoal-speak for the attacker
                vanillaAttack(target); 
            }
        } else {
            this.ticksUntilNextAttack = attackLengthInTicks*2;
            waitingForNextAttack = false;
            entity.setAttacking(false);
            entity.attackAnimationCountdown = 0; // let entity.setupAnimationStates() know we want instant anim when we start attacking
        }
    }

    protected boolean shouldPlayAttackAnimation(){
        return this.ticksUntilNextAttack <= attackLengthInTicks;
    }
    protected boolean isTargetCloseEnough(LivingEntity target){
        return this.entity.distanceTo(target) <= 4f;
        /* the 2f here is in the F3 coordinates units.
        the position of the attacker (us) and target is also in these units, so you end up starting at an
        infinitely small point in the middle of your model and ending at the other entity's also infinitely small middle
        when calculating distance, no accounting for hitboxes. so keep that in mind when you're changing this value.*/
    }
    
    protected void vanillaAttack(LivingEntity target){
        this.ticksUntilNextAttack = attackLengthInTicks*2;
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(target); // does all damage calculations, accounts for shields, if in water/sunlight, literally everything. this is the magic.
    }
}
