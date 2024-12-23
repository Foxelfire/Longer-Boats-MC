package net.foxelfire.tutorialmod.entity.custom;


import java.util.List;

import org.joml.Vector3f;

import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CedarBoatEntity extends Entity {

    private int lives;
    
    public CedarBoatEntity(EntityType<? extends CedarBoatEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.lives = 6;
    }

    private void acceptNearbyRiders() {
        List<Entity> list = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(0.15f, 0.1, 1.0f), EntityPredicates.canBePushedBy(this));
        for(Entity entity : list){
            if (!this.getWorld().isClient() && !(this.getControllingPassenger() instanceof PlayerEntity)
            && this.getPassengerList().size() < this.getMaxPassengers()
            && !entity.hasVehicle() && entity instanceof LivingEntity
            && !(entity instanceof PlayerEntity || entity instanceof WaterCreatureEntity)) {
                entity.startRiding(this);
                continue;
            }
            this.pushAwayFrom(entity);
        }
    }

    private void acceptPlayerInput(Vec3d movementInput){
        float f = MathHelper.sin(this.getYaw() * ((float)Math.PI / 180));
        float g = MathHelper.cos(this.getYaw() * ((float)Math.PI / 180));
        Vec3d finalMovement = new Vec3d(movementInput.x * (double)g - movementInput.z * (double)f, movementInput.y, movementInput.z * (double)g + movementInput.x * (double)f);
        this.setVelocity(this.getVelocity().add(finalMovement.multiply(.05f)));
    }


    public Item asItem(){
        return ModItems.CEDAR_BOAT_ITEM;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() <= 4;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public boolean collidesWith(Entity other) {
        return BoatEntity.canCollide(this, other);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if(this.isInvulnerableTo(source)){
            return false;
        }
        if (this.getWorld().isClient() || this.isRemoved()) {
            return true;
        }
        if (source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode) {
            this.discard();
        }
        if(lives <= 1){
            dropItems(source);
            this.kill();
        }
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        this.reactToHit(source);
        lives--;
        return true;
    }

    protected void dropItems(DamageSource source) {
        this.dropItem(this.asItem());
    }

    private void fallAndDrag() {
        if(!this.isOnGround() || this.isSubmergedInWater()){
            this.setVelocity(this.getVelocity().add(0, -0.04f, 0));
        }
        if(this.isTouchingWater() && !this.isSubmergedInWater()){
            this.setVelocity(this.getVelocity().multiply(1, 0, 1));
        }
        float blockSlipperiness = this.getWorld().getBlockState(this.getPosWithYOffset(-1)).getBlock().getSlipperiness();
        float drag = this.isOnGround() ? blockSlipperiness * 0.91f : 0.91f; // magic code stolen from LivingEntity.travel()
        this.setVelocity((float)(this.getVelocity().x*drag), this.getVelocity().y, (float)(this.getVelocity().z*drag));
        // the above mess is bc we're doing movement both on the client AND the server, and we have to keep them in sync
        // to reduce movement lag as a fix for normal boat movement being very broken. Since the packets sent
        // to set the player's speed store their data in floats, any doubles in the server's velocity with eventually cause
        // issues due to differences in the client and server's rounding causing desyncs
    }

    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer) {
        float rotationalSpeed = controllingPlayer.sidewaysSpeed;
        float forwardSpeed = controllingPlayer.forwardSpeed;
        if (forwardSpeed <= 0.0f) {
            forwardSpeed *= 0.25f;
        }
        this.setYaw(this.getYaw() + -rotationalSpeed*1.25f);
        controllingPlayer.setYaw(controllingPlayer.getYaw() + -rotationalSpeed);
        return new Vec3d(0, 0.0, forwardSpeed);
    }

    protected int getMaxPassengers() {
        return 4;
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0f, dimensions.height - 0.2f, 0.6f + this.getPassengerList().indexOf(passenger)*-0.75f);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.EVENTS;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource){
        return false;
    }

    @Override
    protected void initDataTracker() {
        
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        if (!this.getWorld().isClient()) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void onBubbleColumnSurfaceCollision(boolean drag) {
        this.getWorld().addParticle(ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
            this.emitGameEvent(GameEvent.SPLASH, this.getControllingPassenger());
        }
        Vec3d vec3d = this.getVelocity();
        float suction = drag ? -0.7f : 0.3f;
        scheduleVelocityUpdate();
        this.setVelocity(vec3d.x, suction, vec3d.z);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof BoatEntity || entity instanceof CedarBoatEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public void tick(){
        super.tick();
        checkBlockCollision();
        acceptNearbyRiders();
        if(this.getFirstPassenger() instanceof PlayerEntity){
            acceptPlayerInput(this.getControlledMovementInput((PlayerEntity)this.getFirstPassenger()));
        }
        fallAndDrag();
        scheduleVelocityUpdate();
        this.move(MovementType.SELF, this.getVelocity());
    }
            
            
    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {
        
    }

    protected void reactToHit(DamageSource source){
        if(this.isLogicalSideForUpdatingMovement() && source.getAttacker() instanceof LivingEntity){
            this.scheduleVelocityUpdate();
            this.addVelocity(source.getAttacker().getRotationVector().multiply(.6));
        }
    }
    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {
        
    }

}
