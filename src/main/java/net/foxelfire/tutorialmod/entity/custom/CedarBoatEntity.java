package net.foxelfire.tutorialmod.entity.custom;


import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
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
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CedarBoatEntity extends Entity implements RideableInventory,
VehicleInventory{

    private int lives;
    @Nullable
    private Identifier lootTableId;
    private long lootTableSeed;
    protected int bodyTrackingIncrements;
    protected double serverX;
    protected double serverY;
    protected double serverZ;
    protected double serverYaw;
    protected double serverPitch;
    protected ArrayList<Float> seatList;
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(0, ItemStack.EMPTY);
    protected boolean inventoryDirty = false; 
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
        this.seatList = new ArrayList<Float>(positions); // done separately from positions itself bc chests can remove seats, so it can't be final
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

    public void chestSeatAt(int seatIndex, PlayerEntity player, Hand hand){
        setChestPresent(seatIndex, true);
        player.getStackInHand(hand).decrement(1);
        updateInventorySize();
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

    public boolean getChestPresent(int seatIndex){
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

    protected int getNumberOfChests(){
        int chests = 0;
        for(int i = 0; i < 4; i++){
            if(this.getChestPresent(i)){
                chests++;
            }
        }
        return chests;
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

    protected void updateInventorySize(){
        if(!this.inventoryDirty){
            inventoryDirty = true;
            int chestNum = this.getNumberOfChests();
            DefaultedList<ItemStack> newInventory = DefaultedList.ofSize(chestNum*27, ItemStack.EMPTY);
            if(chestNum > 0){ // checks if our current inventory has slots yet
                DefaultedList<ItemStack> savedInventory = this.getInventory();
                for(int i = 0; i < savedInventory.size(); i++){ // copying current inventory so when we recreate it with the new size the values already present won't be deleted
                    if(savedInventory.get(i) != null){
                        newInventory.set(i, savedInventory.get(i));
                    }
                }
            }
            this.inventory = newInventory;
            inventoryDirty = false;
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        BlockHitResult interactionLocation = (BlockHitResult)player.raycast(3, 1, true);
        if(player.getStackInHand(hand).getItem().equals(Items.CHEST)){
            if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -1.2), .67) && !this.getChestPresent(3)){ // 4th chest
                TutorialMod.LOGGER.info("Slot 3");
                this.seatList.remove(-1.8f); // this removal is put here instead of in chestSeatAt() bc it caused a client-server desync there??? Very confused
                chestSeatAt(3, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), 1.2), .67) && !this.getChestPresent(0)){ //1st chest
                TutorialMod.LOGGER.info("Slot 0");
                this.seatList.remove(1.2f);
                chestSeatAt(0, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), .6), .67) && !this.getChestPresent(1)){ // 2nd chest
                TutorialMod.LOGGER.info("Slot 1");
                this.seatList.remove(-.8f);
                chestSeatAt(1, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -.6), .67) && !this.getChestPresent(2)){ // 3rd chest
                TutorialMod.LOGGER.info("Slot 2");
                this.seatList.remove(.2f);
                chestSeatAt(2, player, hand);
            }
        } else if (!this.getWorld().isClient()) {
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
    public void kill(){
        int chests = this.getNumberOfChests();
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

    public void setChestPresent(int index, boolean present){
        switch (index) {
            case 0:
                this.dataTracker.set(SEAT_0_CHEST, present);
                break;
            case 1:
                this.dataTracker.set(SEAT_1_CHEST, present);
                break;
            case 2:
                this.dataTracker.set(SEAT_2_CHEST, present);
                break;
            case 3:
                this.dataTracker.set(SEAT_3_CHEST, present);
                break;
            default:
                break;
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
    /* The following methods are the ones involving our inventory interfaces, starting with ones we intentionally
     * overwrote from the defaults, and continuing with the ones we had to implement ourselves.
     * I have sectioned them off from our other methods for better organization. If we had any good organization to begin with.
     */
    @Override
    public boolean canPlayerAccess(PlayerEntity player) {
        return !this.isRemoved() && this.getPos().isInRange(player.getPos(), 8.0) && this.getNumberOfChests() > 0;
    }

    @Override
    public int size() {
        return this.getNumberOfChests()*27;
    }

    @Override
    public ItemStack getStack(int slot) {
        if(slot > this.getNumberOfChests()*26){ // dont know where these methods gets called so made this check as an OOB failsafe
            return ItemStack.EMPTY;
        }
        return this.getInventoryStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if(slot > this.getNumberOfChests()*26){
            return ItemStack.EMPTY;
        }
        return this.removeInventoryStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        if(slot > this.getNumberOfChests()*26){ 
            return ItemStack.EMPTY;
        }
        return this.removeInventoryStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if(slot > this.getNumberOfChests()*26){
            return;
        }
        this.setInventoryStack(slot, stack);
    }

    @Override
    public void markDirty() {
        this.inventoryDirty = true;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.canPlayerAccess(player);
    }

    @Override
    public void clear() {
        this.clearInventory();
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId == null || !playerEntity.isSpectator()) {
            //TODO return our screen handler
        }
        return null;
    }

    @Override
    @Nullable
    public Identifier getLootTableId() {
        return this.lootTableId;
    }

    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        player.openHandledScreen(this);
        if (!player.getWorld().isClient) {
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinBrain.onGuardedBlockInteracted(player, true);
        }
    }

}
