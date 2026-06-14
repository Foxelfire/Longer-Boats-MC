package net.foxelfire.longer_boats.entity.custom;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.foxelfire.longer_boats.util.InventorySyncC2SPayload;
import net.foxelfire.longer_boats.util.InventorySyncS2CPayload;
import net.foxelfire.longer_boats.util.MovementInputS2CPayload;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.foxelfire.longer_boats.screen.LongBoatScreenHandler;
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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
VehicleInventory, ExtendedScreenHandlerFactory<LongBoatScreenHandler>, VariantHolder<LongBoatVariant> {

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
    /* We don't know the inventory size until we read our inventory from NBT,
     * because our size can change based on our amounts of chests, which is data that needs to be stored through NBT.
     * Therefore, this.size() won't work until all our chest-related tracked data is already tracked, defined, and set,
     * which when the server is initially loading this file, will not have been tracked and initialized(?) yet.
     *
     * Until I figure out how to make Minecraft not require a defined inventory size until we can track data,
     * this try-catch is necessary to get the server to the next step of loading when the data tracker isn't working yet on initial load.
       We have to give it a fake inventory size of 27 so it continues for now (read: forever).*/
    protected int inventorySize;
    {
        try {
            if(dataTracker.get(SEAT_0_CHEST)){
                inventorySize = this.size();
            }
        } catch (Exception e) {
            inventorySize = 27;
        }
    }
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
    protected boolean inventoryDirty = false; 
    protected int soundTimer = 0;
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
        this.lives = 20;
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
        return this.getVariant().getItem();
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
                this.sendInventoryToClient(newInventory, false, -1);
            }
        }
    }

    public void chestSeatAt(int seatIndex, PlayerEntity player, Hand hand){
        setChestPresent(seatIndex, true);
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
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        if(source.getAttacker() != null){
            Vector3f attackVector = source.getAttacker().getMovementDirection().getUnitVector().mul(.2f);
            this.addVelocity(attackVector.x, attackVector.y, attackVector.z);
        }
        lives-=amount;
        if(lives <= 1){
            this.kill();
        }
        return true;
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
        if(this.getPassengerList().contains(passenger)){
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

    public int getNumberOfControllers(){
        int players = 0;
        if(this.getControllingPassenger() != null && this.getControllingPassenger() instanceof PlayerEntity){
            players++;
        }
        if(this.getSecondaryControllingPassenger() != null && this.getSecondaryControllingPassenger() instanceof PlayerEntity){
            players++;
        }
        return players;
    }

    @Override
    protected Vec3d getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        float zPosition = 0.0f;
        if(this.getFirstAvailableSeat(passenger).isPresent()){
            zPosition = this.seatIndexesToPositions.get(getFirstAvailableSeat(passenger).get());
        } else {
            passenger.stopRiding();
        }
        return new Vec3d(0.0f, 0.3f, zPosition);
    }

    public boolean getPlayer1Inputting(){
        return this.dataTracker.get(FRONT_PLAYER_INPUTTING);
    }

    public boolean getPlayer2Inputting(){
        return this.dataTracker.get(BACK_PLAYER_INPUTTING);
    }

    @Nullable
    public Entity getSecondaryControllingPassenger(){
        if(this.getNumberOfChests() == 0 && this.getPassengerList().size() == 4){
            return this.getPassengerList().get(2);
        }
        int indexIn = 2;
        for(int i = 0; i < 3; i++){
            if(this.getChestPresent(i)){
                indexIn--;
            }
        }
        return this.getPassengerList().size() > indexIn && indexIn > 0 ? this.getPassengerList().get(indexIn) : null;
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.EVENTS;
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource){
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(FRONT_PLAYER_INPUTTING, false);
        builder.add(BACK_PLAYER_INPUTTING, false);
        builder.add(SEAT_0_CHEST, false);
        builder.add(SEAT_1_CHEST, false);
        builder.add(SEAT_2_CHEST, false);
        builder.add(SEAT_3_CHEST, false);
        builder.add(HAS_SCREEN, false);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        BlockHitResult interactionLocation = (BlockHitResult)player.raycast(3, 1, true);
        if(player.getStackInHand(hand).getItem().equals(Items.CHEST)){
            if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -1.2), .67) && !this.getChestPresent(3)){
                chestSeatAt(3, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), 1.2), .67) && !this.getChestPresent(0)){
                chestSeatAt(0, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), .6), .67) && !this.getChestPresent(1)){
                chestSeatAt(1, player, hand);
            } else if(interactionLocation.getPos().isInRange(this.getPos().offset(Direction.fromRotation(this.getYaw()), -.6), .67) && !this.getChestPresent(2)){
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
        for (ItemStack stack : this.getInventory()) {
            dropStack(stack);
        }
        dropStack(new ItemStack(this.asItem(), 1));
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
        this.sendInventoryToClient(this.inventory, false, -1);
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
        if(frontRowingAnimationState.isRunning() || backRowingAnimationState.isRunning() && !this.getWorld().isClient()){
            if(this.soundTimer == 20 && !this.getWorld().isClient()){
                this.getWorld().playSound(null, this.getBlockPos(), this.isInFluid() ? SoundEvents.ENTITY_BOAT_PADDLE_WATER : SoundEvents.ENTITY_BOAT_PADDLE_LAND,
                SoundCategory.NEUTRAL, 1f, 1f);
                this.soundTimer = 0;
            }
            this.soundTimer++;
        }
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
    
    private void travelControlled(@Nullable PlayerEntity riderOne, @Nullable PlayerEntity riderTwo){
        Vec3d controlledMovementInput = travelSpeedCalc(riderOne, riderTwo);
        if(!this.getWorld().isClient() && riderTwo != null && riderOne != null){
            this.sendMovementToClient(riderTwo);
            this.sendMovementToClient(riderOne);
            this.stopServerMovement();
        } else {  // reimplemented from combo of LivingEntity's + AbstractHorseEntity's getControlledMovementInput() override
            if(this.isLogicalSideForUpdatingMovement()){
                this.travel(controlledMovementInput);
                controlledMovementInput.subtract(controlledMovementInput.getX(), 0, 0); // zeros out sideways movement so we don't drift while rotating
            }
        }
        this.playPlayerAnimations(controlledMovementInput);
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
    public void writeInventoryToNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup){
        int[] badNBTFormattedArray = nbt.getIntArray("ChestsInAllSeats");
        for(int i = 0; i < 4; i++){
            this.setChestPresent(i, badNBTFormattedArray[i] > 0);
        }
        if(this.getNumberOfChests() > 0){
            VehicleInventory.super.readInventoryFromNbt(nbt, registryLookup);
        }
    }

    @Override
    public void readInventoryFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup){
        int[] badNBTFormattedArray = nbt.getIntArray("ChestsInAllSeats");
        for(int i = 0; i < 4; i++){
            this.setChestPresent(i, badNBTFormattedArray[i] > 0);
        }
        if(this.getNumberOfChests() > 0){
            VehicleInventory.super.readInventoryFromNbt(nbt, registryLookup);
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
    public LongBoatScreenHandler getScreenOpeningData(ServerPlayerEntity player) {
        return null;
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
        if(this.getNumberOfChests() > 0 && !this.getHasScreen()){
            player.openHandledScreen(this);
            if (!player.getWorld().isClient) {
                this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }
        }
    }

    /* The following are inventory-related methods that are not in the interface and packet sending methods. */

    public void sendInventoryToClient(DefaultedList<ItemStack> inventory, boolean inScreen, int nextTab){
        ArrayList<ItemStack> newInv = new ArrayList<ItemStack>();
        for(int i = 0; i < this.size(); i++){
            newInv.add(inventory.get(i));
        }
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            ServerPlayNetworking.send((ServerPlayerEntity)player, new InventorySyncS2CPayload(newInv, inScreen, this.getId(), nextTab));
        }
    }

    public void sendInventoryToServer(DefaultedList<ItemStack> inventory, int tab){
        ArrayList<ItemStack> newInv = new ArrayList<ItemStack>();
        for(int i = 0; i < this.size(); i++){
            newInv.add(inventory.get(i));
        }
        ClientPlayNetworking.send(new InventorySyncC2SPayload(newInv, this.getId(), 0, tab));
    }

    public void sendInventoryToServer(DefaultedList<ItemStack> inventory, int prevTab, int tab){
        ArrayList<ItemStack> newInv = new ArrayList<ItemStack>();
        for(int i = 0; i < this.size(); i++){
            newInv.add(inventory.get(i));
        }
        ClientPlayNetworking.send(new InventorySyncC2SPayload(newInv, this.getId(), prevTab, tab));
    }

    public void sendMovementToClient(PlayerEntity otherPlayer){
        ServerPlayNetworking.send((ServerPlayerEntity)otherPlayer, new MovementInputS2CPayload(otherPlayer.getId(), otherPlayer.forwardSpeed, otherPlayer.sidewaysSpeed));
    }
    /* loot table stuff we don't need*/

    public RegistryKey<LootTable> getLootTable(){
        return null;
    }

    public void setLootTable(RegistryKey<LootTable> table){

    }
}