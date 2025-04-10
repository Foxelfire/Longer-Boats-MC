package net.foxelfire.tutorialmod.entity.custom;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.foxelfire.tutorialmod.TutorialMod;
import net.foxelfire.tutorialmod.item.ModItems;
import net.foxelfire.tutorialmod.screen.LongBoatScreenHandler;
import net.foxelfire.tutorialmod.util.ModNetworkingConstants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.RideableInventory;
import net.minecraft.entity.VariantHolder;
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
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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

public abstract class AbstractLongBoatEntity extends Entity implements RideableInventory,
VehicleInventory, ExtendedScreenHandlerFactory, VariantHolder<LongBoatVariant> {

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
    protected Map<Integer, Float> seatIndexesToPositions = Collections.synchronizedMap(new HashMap<>());
    /* ooh, the inventory size is a doozy. So we don't actually know the inventory size until we read our inventory
     * from NBT, because our size can change based on our amounts of chests, data which is stored through NBT. That task of reading NBT is done server-side,
     * and the server's files always run before the client's. this.size() doesn't actually work until all our chest-related tracked data is already tracked, defined, and set,
     * because its logic dependent on how many chests in total we have, which when the server is initially loading this file, will not have been tracked and initialized(?) yet.
     * This checks if our tracked data is already set, which will be true on the client since the server has already loaded this file, but false on the server itself. So basically, the server
     * gets this fake size of 27 while this entity's tracked data is being waited on, to keep it up and running until we can calculate the real size when the server calls
     * readCustomDataFromNBT(), sets the proper tracked data values for itself, calls resetInventory() referencing our newly changed this.size() return value, and fixes everything.
     * The client can't actually read those tracked data values that fast, so we send a packet to let it know the new inventory size.
     */
    protected int inventorySize = this.dataTracker.containsKey(SEAT_0_CHEST) ? this.size() : 27;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    protected boolean inventoryDirty = false; 
    protected int wobbleTimer = 20;
    public final AnimationState frontRowingAnimationState = new AnimationState();
    public final AnimationState backRowingAnimationState = new AnimationState();
    public final AnimationState rotatingLeftAnimationState = new AnimationState();
    public final AnimationState rotatingRightAnimationState = new AnimationState();
    public final AnimationState rotatingBackLeftAnimationState = new AnimationState();
    public final AnimationState rotatingBackRightAnimationState = new AnimationState();
    private static final List<Float> positions = List.of(1.2f, .2f, -.8f, -1.8f); // all four passenger z positions

    private static final TrackedData<Boolean> FRONT_PLAYER_INPUTTING = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BACK_PLAYER_INPUTTING = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_0_CHEST = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN); // no array or list data tracking? Mojang whyyy
    private static final TrackedData<Boolean> SEAT_1_CHEST = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_2_CHEST = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SEAT_3_CHEST = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HAS_SCREEN = DataTracker.registerData(AbstractLongBoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    
    public AbstractLongBoatEntity(EntityType<? extends AbstractLongBoatEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
        this.lives = 6;
        for (int i = 0; i < 4; i++){
            seatIndexesToPositions.put(i, positions.get(i));
        }
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
        return ModItems.LONG_BOAT_OAK_ITEM;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < this.getMaxPassengers();
    }

    protected boolean canAddPassenger() {
        return this.getPassengerList().size() < this.getMaxPassengers();
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    protected void changeInvSizeDuringGameplay(){
        if(!this.inventoryDirty){
            inventoryDirty = true;
            DefaultedList<ItemStack> newInventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            if(this.getNumberOfChests() > 0 && this.getInventory() != null){ // checks if our current inventory has slots yet
                DefaultedList<ItemStack> savedInventory = this.getInventory();
                for(int i = 0; i < savedInventory.size(); i++){ // copying current inventory so when we recreate it with the new size the values already present won't be deleted
                    if(savedInventory.get(i) != null){
                        newInventory.set(i, savedInventory.get(i));
                    }
                }
            }
            this.inventory = newInventory;
            inventoryDirty = false;
            // tell any other players that might be on the server that our inventory has changed
            if(!this.getWorld().isClient()){
                this.sendS2CInventoryPacket(newInventory, false, -1);
            }
        }
    }

    public void chestSeatAt(int seatIndex, PlayerEntity player, Hand hand){
        setChestPresent(seatIndex, true);
        TutorialMod.LOGGER.info("Are we on the client? " + this.getWorld().isClient());
        if(player != null && hand != null){
            player.getStackInHand(hand).decrement(1);
        }
        changeInvSizeDuringGameplay();
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

    @Nullable
    @Override
    public LivingEntity getControllingPassenger(){
        return (LivingEntity)this.getFirstPassenger();
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

    protected Optional<Integer> getFirstAvailableSeat(Entity passenger){
        ArrayList<Integer> nonChestedSeats = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            if(!this.getChestPresent(i)){
                nonChestedSeats.add(i);
            }
        }
        if(this.getPassengerList().indexOf(passenger) != -1){
            return Optional.of(nonChestedSeats.get(this.getPassengerList().indexOf(passenger)));
        }
        return Optional.empty();
    }

    public boolean getHasScreen(){
        return this.dataTracker.get(HAS_SCREEN);
    }

    public DefaultedList<ItemStack> getInventoryTabAt(int index){
        DefaultedList<ItemStack> tab = DefaultedList.ofSize(27, ItemStack.EMPTY);
        for(int i = 0; i < 27; i++){
            tab.set(i, this.getInventory().get(i+(27*index)));
        }
        return tab;
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
        return 4 - getNumberOfChests(); // there can only be as many passengers as seats for them to sit in - bc some seats may be taken up by chests
    }

    private float getMovementSpeed(double slipperiness){ // reimplemented from LivingEntity's
        float movementSpeed = !this.isSubmergedInWater() && this.isTouchingWater() ? 0.4f : 0.1f;
        if (this.isOnGround()) {
            return (float)(movementSpeed * (0.21600002f / (slipperiness)));
        }
        return this.getControllingPassenger() instanceof PlayerEntity ? movementSpeed * 0.1f : 0.02f;
    }

    public int getNumberOfChests(){
        int num = 0;
        for(int i = 0; i < 4; i++){
            if(this.getChestPresent(i)){
                num++;
            }
        }
        return num;
    }

    @Override
    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float zPosition = 0.0f;
        if(!this.getFirstAvailableSeat(passenger).isEmpty()){
            zPosition = this.seatIndexesToPositions.get(getFirstAvailableSeat(passenger).get());
        } else if(zPosition == 0.0f){
            passenger.stopRiding();
        }
        return new Vector3f(0.0f, 0.3f, zPosition);
    }

    public boolean getPlayer1Inputting(){
        return this.dataTracker.get(FRONT_PLAYER_INPUTTING);
    }

    public boolean getPlayer2Inputting(){
        return this.dataTracker.get(BACK_PLAYER_INPUTTING);
    }

    @Nullable
    public Entity getSecondaryControllingPassenger(){
        if(this.getNumberOfChests() == 4){
            return this.getPassengerList().get(2);
        }
        int indexIn = 2;
        for(int i = 0; i < 3; i++){
            if(this.getChestPresent(i)){
                indexIn--;
            }
        }
        return this.getPassengerList().size() > indexIn ? this.getPassengerList().get(indexIn) : null;
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
        this.dataTracker.startTracking(SEAT_0_CHEST, false);
        this.dataTracker.startTracking(SEAT_1_CHEST, false);
        this.dataTracker.startTracking(SEAT_2_CHEST, false);
        this.dataTracker.startTracking(SEAT_3_CHEST, false);
        this.dataTracker.startTracking(HAS_SCREEN, false);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        BlockHitResult interactionLocation = (BlockHitResult)player.raycast(3, 1, true);
        if(player.getStackInHand(hand).getItem().equals(Items.CHEST)){
            if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -1.2), .67) && !this.getChestPresent(3)){
                TutorialMod.LOGGER.info("Slot 3");
                chestSeatAt(3, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), 1.2), .67) && !this.getChestPresent(0)){
                TutorialMod.LOGGER.info("Slot 0");
                chestSeatAt(0, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), .6), .67) && !this.getChestPresent(1)){
                TutorialMod.LOGGER.info("Slot 1");
                chestSeatAt(1, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -.6), .67) && !this.getChestPresent(2)){
                TutorialMod.LOGGER.info("Slot 2");
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

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player){
        super.onStartedTrackingBy(player);
        this.sendS2CInventoryPacket(this.inventory, false, -1);
    }

    public void playPlayerAnimations(Vec3d controlledMovementInput){
        if(this.getPlayer1Inputting() && controlledMovementInput.getX() == 0){
            this.frontRowingAnimationState.startIfNotRunning(this.age);
        } else if(this.frontRowingAnimationState.isRunning()){
            frontRowingAnimationState.stop();
        }
        if(this.getPlayer2Inputting() && controlledMovementInput.getX() == 0){
            this.backRowingAnimationState.startIfNotRunning(this.age);
        } else if(this.backRowingAnimationState.isRunning()){
            backRowingAnimationState.stop();
        }
        if(controlledMovementInput.getX() < 0){
            if(this.getPlayer1Inputting()){
                rotatingRightAnimationState.startIfNotRunning(this.age);
            } else if(this.getPlayer2Inputting()){
                rotatingBackRightAnimationState.startIfNotRunning(this.age);
            } else {
                rotatingRightAnimationState.stop();
            }
            this.setYaw(this.getYaw()+1);
            rotatingLeftAnimationState.stop();
        } else if(controlledMovementInput.getX() > 0){
            if(this.getPlayer1Inputting()){
                rotatingLeftAnimationState.startIfNotRunning(this.age);
            } else if(this.getPlayer2Inputting()){
                rotatingBackLeftAnimationState.startIfNotRunning(this.age);
            } else {
                rotatingLeftAnimationState.stop();
            }
            rotatingRightAnimationState.stop();
            this.setYaw(this.getYaw()-1);
        } else {
            rotatingLeftAnimationState.stop();
            rotatingRightAnimationState.stop();
            rotatingBackLeftAnimationState.stop();
            rotatingBackRightAnimationState.stop();
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof BoatEntity || entity instanceof AbstractLongBoatEntity) {
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

    public void setHasScreen(boolean hasScreen){
        this.dataTracker.set(HAS_SCREEN, hasScreen);
    }

    private void stopAllAnimations(){
        setPlayer1Inputting(false);
        setPlayer2Inputting(false);
        this.rotatingLeftAnimationState.stop();
        this.rotatingRightAnimationState.stop();
        this.rotatingBackLeftAnimationState.stop();
        this.rotatingBackRightAnimationState.stop();
    }

    public void stopServerMovement(){
        this.setVelocity(Vec3d.ZERO);
        this.tryCheckBlockCollision();
    }

    @Override
    public void tick(){
        super.tick();
        if (!this.isRemoved()) {
            this.tickMovement();
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
        if(this.getControllingPassenger() instanceof PlayerEntity){
            if(this.getSecondaryControllingPassenger() instanceof PlayerEntity && this.getSecondaryControllingPassenger() != this.getControllingPassenger()){
                travelControlled((PlayerEntity)this.getControllingPassenger(), (PlayerEntity)this.getSecondaryControllingPassenger());
            } else {
                travelControlled((PlayerEntity)this.getFirstPassenger(), null);
            }
        } else if(this.getSecondaryControllingPassenger() instanceof PlayerEntity){ // yeah i don't think this can ever happen, but i'll check for it
            travelControlled(null, (PlayerEntity)this.getSecondaryControllingPassenger());
        } else {
            this.travel(this.getVelocity()); // gravity and stuff
            stopAllAnimations();
        }
    }

    @SuppressWarnings("deprecation")
    public void travel(Vec3d movementInput){ // reimplemented from LivingEntity's
        if (this.isLogicalSideForUpdatingMovement()){
            double fallingSpeed = 0.04;
            BlockPos blockUnderUs = this.getVelocityAffectingPos();
            float slipperiness = this.getWorld().getBlockState(blockUnderUs).getBlock().getSlipperiness();
            float friction = this.isOnGround() ? slipperiness : 0.91f;
            Vec3d movement = this.applyMovementInput(movementInput, slipperiness);
            double downwardMovement = movement.y;
            if(!this.getWorld().isClient() || this.getWorld().isChunkLoaded(blockUnderUs)){ // deprecated?? then why does travel() use it? we'll have to switch to newer isChunkLoaded(chunkX, chunkZ) sometime
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
    
    private void travelControlled(@Nullable PlayerEntity riderOne, @Nullable PlayerEntity riderTwo){ // reimplemented from combo of LivingEntity's + AbstractHorseEntity's getControlledMovementInput() override
        if(!this.getWorld().isClient() && riderTwo != null && riderOne != null){
            this.sendS2CMovementValuesPacket(riderOne, riderTwo); // TODO: change packet to send to all players / test with three players?
            this.sendS2CMovementValuesPacket(riderTwo, riderOne);
            this.stopServerMovement();
        } else {
            Vec3d controlledMovementInput = travelSpeedCalc(riderOne, riderTwo);
            if(this.isLogicalSideForUpdatingMovement()){
                this.travel(controlledMovementInput);
                controlledMovementInput.subtract(controlledMovementInput.getX(), 0, 0); // zeros out sideways movement so we don't drift while rotating
            }
            this.playPlayerAnimations(controlledMovementInput);
        }
    }

    private Vec3d travelSpeedCalc(@Nullable PlayerEntity pOne, @Nullable PlayerEntity pTwo){
        float sidewaysSpeed = 0.0f;
        float forwardSpeed = 0.0f;
        if(pOne != null){
            forwardSpeed+=pOne.forwardSpeed;
            sidewaysSpeed+=pOne.sidewaysSpeed;
            setPlayer1Inputting(pOne.forwardSpeed != 0 || pOne.sidewaysSpeed != 0);
        }
        if(pTwo != null){
            forwardSpeed+=pTwo.forwardSpeed;
            sidewaysSpeed+=pTwo.sidewaysSpeed;
            setPlayer2Inputting(pTwo.forwardSpeed != 0 || pTwo.sidewaysSpeed != 0);
        }
        return new Vec3d(sidewaysSpeed*0.5, 0, forwardSpeed);
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
            
    @Override
    // We're always on the server here.
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        int[] badNBTFormattedArray = nbt.getIntArray("ChestsInAllSeats");
        for(int i = 0; i < 4; i++){
            this.setChestPresent(i, badNBTFormattedArray[i] > 0);
        }
        if(this.getNumberOfChests() > 0){
            this.readInventoryFromNbt(nbt);
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        int[] badNBTFormattedArray = new int[4];
        for(int i = 0; i < 4; i++){
            badNBTFormattedArray[i] = (this.getChestPresent(i) == true ? 1 : 0);
        }
        nbt.putIntArray("ChestsInAllSeats", badNBTFormattedArray);
        if(getNumberOfChests() > 0){
            this.writeInventoryToNbt(nbt);
        }
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
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId == null || !playerEntity.isSpectator() && !this.dataTracker.get(HAS_SCREEN)) {
            this.setHasScreen(true);
            return new LongBoatScreenHandler(syncId, playerInventory, this);
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

    public void setInventory(DefaultedList<ItemStack> inventory){
        this.inventory = inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if(this.getNumberOfChests() > 0 && !this.getHasScreen()){
            player.openHandledScreen((ExtendedScreenHandlerFactory)this);
            if (!player.getWorld().isClient) {
                this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        
    }

    /* The following are inventory-related methods that are not in the interface and packet sending methods. */

    public void readInventoryFromNbt(NbtCompound nbt){
        this.resetInventory();
        /* the below line was previously in an if-else supposed to support pre-generated boats with loot tables
           in VehicleInventory.readInventoryFromNbt. we don't need to support these (yet) so we can just send it directly to the reader. */
        Inventories.readNbt(nbt, this.getInventory());
    }

    public void sendS2CInventoryPacket(DefaultedList<ItemStack> inventory, boolean inScreen, int nextTab){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(inventory.size());
        for (ItemStack item : inventory) {
            buf.writeItemStack(item);
        }
        buf.writeInt(this.getId());
        buf.writeBoolean(inScreen);
        buf.writeInt(nextTab);
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity)player, ModNetworkingConstants.INVENTORY_S2C_SYNCING_PACKET_ID, buf);
        }
    }

    public void sendC2SInventoryPacket(DefaultedList<ItemStack> inventory, int tab){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(inventory.size());
        for (ItemStack item : inventory) {
            buf.writeItemStack(item);
        }
        buf.writeInt(this.getId());
        buf.writeInt(tab);
        buf.writeInt(-1);
        ClientPlayNetworking.send(ModNetworkingConstants.INVENTORY_C2S_SYNCING_PACKET_ID, buf);
    }

    public void sendC2SInventoryPacket(DefaultedList<ItemStack> inventory, int prevTab, int tab){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(inventory.size());
        for (ItemStack item : inventory) {
            buf.writeItemStack(item);
        }
        buf.writeInt(this.getId());
        buf.writeInt(prevTab);
        buf.writeInt(tab);
        ClientPlayNetworking.send(ModNetworkingConstants.INVENTORY_C2S_SYNCING_PACKET_ID, buf);
    }

    public void sendS2CMovementValuesPacket(PlayerEntity recipient, PlayerEntity otherPlayer){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(otherPlayer.getId());
        buf.writeFloat(otherPlayer.forwardSpeed);
        buf.writeFloat(otherPlayer.sidewaysSpeed);
        ServerPlayNetworking.send((ServerPlayerEntity)recipient, ModNetworkingConstants.TOTAL_MOVEMENT_INPUTS_S2C_PACKET_ID, buf);
    }
}
