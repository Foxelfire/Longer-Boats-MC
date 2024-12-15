package net.foxelfire.tutorialmod.entity.custom;


import java.util.List;

import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.util.ModNetworkingConstants;
import net.minecraft.client.network.ClientPlayerEntity;
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
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.GravityField;
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

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
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
        if(!this.getWorld().isClient()){
            if(!this.isOnGround() || this.isSubmergedInWater()){
                this.setVelocity(this.getVelocity().add(0, -0.04, 0));
            }
            if(this.isTouchingWater() && !this.isSubmergedInWater()){
                this.setVelocity(this.getVelocity().multiply(1, 0, 1));
            }
            float blockSlipperiness = this.getWorld().getBlockState(this.getPosWithYOffset(-1)).getBlock().getSlipperiness();
            float drag = this.isOnGround() ? blockSlipperiness * 0.91f : 0.91f; // magic code stolen from LivingEntity.travel()
            this.setVelocity(this.getVelocity().multiply(drag, 1, drag));
        }
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
    // TODO: figure out why tf a client-server desync happens here?
    public void onBubbleColumnSurfaceCollision(boolean drag) {
        this.getWorld().addParticle(ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.getWorld().playSound(this.getX(), this.getY(), this.getZ(), this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
            this.emitGameEvent(GameEvent.SPLASH, this.getControllingPassenger());
        }
        if (!this.getWorld().isClient()) {
            Vec3d vec3d = this.getVelocity();
            double d = drag ? -0.7 : 0.3;
            scheduleVelocityUpdate();
            this.setVelocity(vec3d.x, d, vec3d.z);
        }   
    }

    private void sendC2SMovementInputPacket(PlayerEntity player) {
        if(this.getWorld().isClient()){
            ClientPlayerEntity rider = (ClientPlayerEntity)player;
            float clientYawVelocity = 0;
            float clientForwardMovement = 0;
            if (rider.input.pressingLeft) {
                clientYawVelocity -= 1.0f;
            }
            if (rider.input.pressingRight) {
                clientYawVelocity += 1.0f;
            }
            if (rider.input.pressingForward) {
                clientForwardMovement += 0.04f;
            }
            if (rider.input.pressingBack) {
                clientForwardMovement -= 0.005f;
            }
            Vec3d velocityInfo = new Vec3d(0.0, 0.0, clientForwardMovement);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.getId());
            buf.writeFloat(clientYawVelocity);
            buf.writeVec3d(velocityInfo);
            ClientPlayNetworking.send(ModNetworkingConstants.BOAT_MOVEMENT_PACKET_ID, buf);
        }
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
            if(this.getWorld().isClient()){
                sendC2SMovementInputPacket((ClientPlayerEntity)this.getFirstPassenger()); 
                // the server needs to know where we're going before applying other velocity modifiers
                // so that we can receive an accurate movement packet back when we call scheduleVelocityUpdate()
                // instead of a stale one that doesn't know the boat moved somewhere else
            }
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
