package net.foxelfire.tutorialmod.entity.custom;


import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CedarBoatEntity extends Entity {

    private int lives;
    protected int bodyTrackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverYaw;
    protected double serverPitch;
    protected ArrayList<Float> seatList;
    protected int wobbleTimer = 20;

    public final AnimationState frontRowingAnimationState = new AnimationState();
    public final AnimationState backRowingAnimationState = new AnimationState();
    public final AnimationState rotatingLeftAnimationState = new AnimationState();
    public final AnimationState rotatingRightAnimationState = new AnimationState();
    public final AnimationState wobblingAnimationState = new AnimationState();
    private static final List<Float> positions = List.of(1.2f, .2f, -.8f, -1.8f); // all four passenger z positions

    private static final TrackedData<Boolean> FRONT_PLAYER_INPUTTING = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BACK_PLAYER_INPUTTING = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SHOULD_WOBBLE = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_0_CHEST = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_1_CHEST = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_2_CHEST = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_3_CHEST = DataTracker.registerData(CedarBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    //player 2 is useless fn but will do smth. TODO: make a second player able to input boat movement commands (and evaluate the sum of the first player's request and theirs)
    
    public CedarBoatEntity(EntityType<? extends CedarBoatEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.lives = 6;
        this.seatList = new ArrayList<Float>(positions); // done separately from positions itself bc chests can remove seats
    }

    private void acceptOrRejectRiders() {
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
        if(this.getPassengerList().size() > this.getMaxPassengers()){ // someone got in, but a chest has stolen their seat! Kick them out before we start indexing our passenger list out of bounds!
            Entity passenger = this.getPassengerList().get(this.getMaxPassengers());
            passenger.stopRiding();
        }
    }

    public Vec3d applyMovementInput(Vec3d movementInput, float slipperiness) { // reimplemented from LivingEntity's
        this.updateVelocity(this.getMovementSpeed(slipperiness), movementInput);
        this.move(MovementType.SELF, this.getVelocity());
        Vec3d vec3d = this.getVelocity();
        if (this.getBlockStateAtPos().isOf(Blocks.POWDER_SNOW)) {
            vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
        }
        return vec3d;
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
        setShouldWobble(true);
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
        if(source.getAttacker() != null){
            Vector3f attackVector = source.getAttacker().getMovementDirection().getUnitVector().mul(.6f);
            this.addVelocity(attackVector.x, attackVector.y, attackVector.z);
        }
        lives--;
        return true;
    }

    protected void dropItems(DamageSource source) {
        this.dropItem(this.asItem());
    }

    @Override
    public double getLerpTargetX() {
        return this.bodyTrackingIncrements > 0 ? this.serverX : this.getX();
    }

    @Override
    public double getLerpTargetY() {
        return this.bodyTrackingIncrements > 0 ? this.serverY : this.getY();
    }

    @Override
    public double getLerpTargetZ() {
        return this.bodyTrackingIncrements > 0 ? this.serverZ : this.getZ();
    }

    @Override
    public float getLerpTargetPitch() {
        return this.bodyTrackingIncrements > 0 ? (float)this.serverPitch : this.getPitch();
    }

    @Override
    public float getLerpTargetYaw() {
        return this.bodyTrackingIncrements > 0 ? (float)this.serverYaw : this.getYaw();
    }

    protected int getMaxPassengers() {
        return seatList.size(); // there can only be as many passengers as seats for them to sit in - bc some seats may be taken up by chests
    }

    private float getMovementSpeed(double slipperiness){ // reimplemented from LivingEntity's
        float movementSpeed = !this.isSubmergedInWater() && this.isTouchingWater() ? 0.4f : 0.1f;
        if (this.isOnGround()) {
            return (float)(movementSpeed * (0.21600002f / (slipperiness)));
        }
        return this.getControllingPassenger() instanceof PlayerEntity ? movementSpeed * 0.1f : 0.02f;
    }

    public boolean getPlayer1Inputting(){
        return this.dataTracker.get(FRONT_PLAYER_INPUTTING);
    }

    public boolean getPlayer2Inputting(){
        return this.dataTracker.get(BACK_PLAYER_INPUTTING);
    }

    public boolean getShouldWobble(){
        return this.dataTracker.get(SHOULD_WOBBLE);
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0f, 0.3f, this.seatList.get(this.getPassengerList().indexOf(passenger)));
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
        this.dataTracker.startTracking(FRONT_PLAYER_INPUTTING, false);
        this.dataTracker.startTracking(BACK_PLAYER_INPUTTING, false);
        this.dataTracker.startTracking(SHOULD_WOBBLE, false);
        this.dataTracker.startTracking(SEAT_0_CHEST, false);
        this.dataTracker.startTracking(SEAT_1_CHEST, false);
        this.dataTracker.startTracking(SEAT_2_CHEST, false);
        this.dataTracker.startTracking(SEAT_3_CHEST, false);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        BlockHitResult interactionLocation = (BlockHitResult)player.raycast(2, 1, true);
        if(player.getStackInHand(hand).getItem().equals(Items.CHEST)){
            if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -1.2), .67) && !this.isChestPresent(3)){ // 4th chest
                this.dataTracker.set(SEAT_3_CHEST, true);
                player.getStackInHand(hand).decrement(1);
                this.seatList.remove(-1.8f); // 4th seat, removing by raw values instead of indices bc those can change if more than one chest is present
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), 1.2), .67) && !this.isChestPresent(0)){ //1st chest
                this.dataTracker.set(SEAT_0_CHEST, true);
                player.getStackInHand(hand).decrement(1);
                this.seatList.remove(1.2f);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), .6), .67) && !this.isChestPresent(1)){ // 2nd chest
                this.dataTracker.set(SEAT_1_CHEST, true);
                player.getStackInHand(hand).decrement(1);
                this.seatList.remove(.2f);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -.6), .67) && !this.isChestPresent(2)){ // 3rd chest
                this.dataTracker.set(SEAT_2_CHEST, true);
                player.getStackInHand(hand).decrement(1);
                this.seatList.remove(-.8f);
            }
        } else if (!this.getWorld().isClient()) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    public boolean isChestPresent(int seatIndex){
        switch (seatIndex) {
            case 0:
                return this.dataTracker.get(SEAT_0_CHEST);
            case 1:
                return this.dataTracker.get(SEAT_1_CHEST);
            case 2:
                return this.dataTracker.get(SEAT_2_CHEST);
            case 3:
                return this.dataTracker.get(SEAT_3_CHEST);
            default:
                TutorialMod.LOGGER.error("Tried to check a nonexistent long boat seat index!");
                return false;
        }
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
    public void kill(){
        int chests = 0;
        for(int i = 0; i < 4; i++){
            if(this.isChestPresent(i)){
                chests++;
            }
        }
        if(chests > 0 && chests < 5){
            dropStack(new ItemStack(Items.CHEST, chests));
        }
       super.kill();
    }

    private void limitYawValue(){
        while (this.getYaw() - this.prevYaw < -180.0f) {
                this.prevYaw -= 360.0f;
            }
        while (this.getYaw() - this.prevYaw >= 180.0f) {
                this.prevYaw += 360.0f;
        }
    }

    private void playPlayerAnimations(Vec3d controlledMovementInput){
        if(this.getPlayer1Inputting()){
            this.frontRowingAnimationState.startIfNotRunning(this.age);
        } else if(this.frontRowingAnimationState.isRunning()){
            frontRowingAnimationState.stop();
        }
        if(controlledMovementInput.getX() < 0){
            if(!this.getPlayer1Inputting()){
                rotatingRightAnimationState.startIfNotRunning(this.age);
            } else {
                rotatingRightAnimationState.stop();
            }
            this.setYaw(this.getYaw()+1);
            rotatingLeftAnimationState.stop();
        } else if(controlledMovementInput.getX() > 0){
            if(!this.getPlayer1Inputting()){
                rotatingLeftAnimationState.startIfNotRunning(this.age);
            }
            rotatingRightAnimationState.stop();
            this.setYaw(this.getYaw()-1);
        } else {
            rotatingLeftAnimationState.stop();
            rotatingRightAnimationState.stop();
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

    public void setPlayer1Inputting(boolean isRiding){
        this.dataTracker.set(FRONT_PLAYER_INPUTTING, isRiding);
    }

    public void setPlayer2Inputting(boolean isRiding){
        this.dataTracker.set(BACK_PLAYER_INPUTTING, isRiding);
    }

    public void setShouldWobble(boolean shouldWobble){
        this.dataTracker.set(SHOULD_WOBBLE, shouldWobble);
        TutorialMod.LOGGER.info("Should Wobble: " + getShouldWobble());
    }

    private void stopAllAnimations(){
        setPlayer1Inputting(false);
        setPlayer2Inputting(false);
        this.rotatingLeftAnimationState.stop();
        this.rotatingRightAnimationState.stop();
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.isRemoved()) {
            this.tickMovement();
            if(this.getWorld().isClient()){
                wobble();
            }
        }
        checkBlockCollision();
        acceptOrRejectRiders();
    }

    public void tickMovement(){ // reimplemented from LivingEntity's
        limitYawValue();
        if (this.isLogicalSideForUpdatingMovement()) {
            this.bodyTrackingIncrements = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.bodyTrackingIncrements > 0) {
            this.lerpPosAndRotation(this.bodyTrackingIncrements, this.serverX, this.serverY, this.serverZ, this.serverYaw, this.serverPitch);
            --this.bodyTrackingIncrements;
        } else if (!this.canMoveVoluntarily()) {
            this.setVelocity(this.getVelocity().multiply(0.98));
        }
        double velocityX = this.getVelocity().x;
        double velocityY = this.getVelocity().y;
        double velocityZ = this.getVelocity().z;
        if (Math.abs(velocityX) < 0.003) {
            velocityX = 0.0;
        }
        if (Math.abs(velocityY) < 0.003) {
            velocityY = 0.0;
        }
        if (Math.abs(velocityZ) < 0.003) {
            velocityZ = 0.0;
        }
        this.setVelocity(velocityX, velocityY, velocityZ);
        if(this.getFirstPassenger() instanceof PlayerEntity){
            travelControlled((PlayerEntity)this.getFirstPassenger());
        } else {
            this.travel(this.getVelocity()); // gravity and stuff
            stopAllAnimations();
        }
    }

    @SuppressWarnings("deprecation")
    private void travel(Vec3d movementInput){ // reimplemented from LivingEntity's
        if (this.isLogicalSideForUpdatingMovement()){
            double fallingSpeed = 0.04;
            BlockPos blockUnderUs = this.getVelocityAffectingPos();
            float slipperiness = this.getWorld().getBlockState(blockUnderUs).getBlock().getSlipperiness();
            float friction = this.isOnGround() ? slipperiness : 0.91f;
            Vec3d movement = this.applyMovementInput(movementInput, slipperiness);
            double downwardMovement = movement.y;
            if(!this.getWorld().isClient() || this.getWorld().isChunkLoaded(blockUnderUs)){ // deprecated?? then why does travel() use it? TODO: switch to newer isChunkLoaded(chunkX, chunkZ)
                if(!this.hasNoGravity() && !this.isTouchingWater()){
                    downwardMovement -= fallingSpeed;
                } else if(this.isSubmergedInWater()){
                    downwardMovement = fallingSpeed;
                } else if(this.isTouchingWater()){
                    downwardMovement = 0;
                    movement.add(movement.x*2, 0, 0);
                }
            } else {
                downwardMovement = this.getY() > (double)this.getWorld().getBottomY() ? -0.1 : 0.0;
            }
            this.setVelocity(movement.x*(double)friction, downwardMovement*0.98, movement.z*(double)friction);
        }
    }
    
    private void travelControlled(PlayerEntity rider){ // reimplemented from combo of LivingEntity's + AbstractHorseEntity's getControlledMovementInput() override
        Vec3d controlledMovementInput = new Vec3d(rider.sidewaysSpeed*0.5f, 0.0, rider.forwardSpeed);
        this.playPlayerAnimations(controlledMovementInput);
        if(this.isLogicalSideForUpdatingMovement()){
            controlledMovementInput.subtract(controlledMovementInput.getX(), 0, 0); // zeros out sideways movement so we don't drift while rotating
            this.travel(controlledMovementInput);
            setPlayer1Inputting(controlledMovementInput.z != 0 ? true : false);
        } else {
            this.setVelocity(Vec3d.ZERO);
            this.tryCheckBlockCollision();
        }
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.serverX = x;
        this.serverY = y;
        this.serverZ = z;
        this.serverYaw = yaw;
        this.serverPitch = pitch;
        this.bodyTrackingIncrements = interpolationSteps;
    }

    private void wobble(){
        if(getShouldWobble()){
            TutorialMod.LOGGER.info("Wobble Timer: " + wobbleTimer + ",  time wobbling: " + wobblingAnimationState.getTimeRunning() + ", isWobbling: " + wobblingAnimationState.isRunning() + ", btw am i the client? " + this.getWorld().isClient());
            wobblingAnimationState.startIfNotRunning(this.age);
            wobbleTimer--;
            if(wobbleTimer <= 0){
                setShouldWobble(false);
                return;
            }
        } else {
            if(wobblingAnimationState.isRunning()){
                TutorialMod.LOGGER.info("Hi there, i'm stopping your animation!");
                wobblingAnimationState.stop();
            }
            wobbleTimer = 20;
        }
    }
            
    @Override
    protected void readCustomDataFromNbt(NbtCompound var1) {
        
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound var1) {
        
    }

}
