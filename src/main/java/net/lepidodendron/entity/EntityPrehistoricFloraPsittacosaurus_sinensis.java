
package net.lepidodendron.entity;

import net.ilexiconn.llibrary.client.model.tools.ChainBuffer;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.lepidodendron.LepidodendronMod;
import net.lepidodendron.block.base.IAdvancementGranter;
import net.lepidodendron.entity.ai.*;
import net.lepidodendron.entity.base.EntityPrehistoricFloraAgeableBase;
import net.lepidodendron.entity.base.EntityPrehistoricFloraLandBase;
import net.lepidodendron.entity.util.IScreamer;
import net.lepidodendron.entity.util.ITrappableLand;
import net.lepidodendron.util.CustomTrigger;
import net.lepidodendron.util.ModTriggers;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.List;

public class EntityPrehistoricFloraPsittacosaurus_sinensis extends EntityPrehistoricFloraLandBase implements IScreamer, ITrappableLand, IAdvancementGranter {

	public BlockPos currentTarget;
	@SideOnly(Side.CLIENT)
	public ChainBuffer chainBuffer;
	//private int inPFLove;
	public ChainBuffer tailBuffer;
	private boolean screaming;
	private int alarmCooldown;
	public Animation STAND_ANIMATION;
	public Animation SCRATCH_ANIMATION;
	private int standCooldown;

	public EntityPrehistoricFloraPsittacosaurus_sinensis(World world) {
		super(world);
		setSize(0.375F, 0.5F);
		minWidth = 0.12F;
		maxWidth = 0.375F;
		maxHeight = 0.5F;
		maxHealthAgeable = 12.0D;
		STAND_ANIMATION = Animation.create(125);
		SCRATCH_ANIMATION = Animation.create(55);
		if (FMLCommonHandler.instance().getSide().isClient()) {
			tailBuffer = new ChainBuffer();
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world.isRemote && !this.isAIDisabled()) {
			tailBuffer.calculateChainSwingBuffer(60, 10, 5F, this);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource ds, float i) {
		Entity e = ds.getTrueSource();
		if (e instanceof EntityLivingBase) {
			EntityLivingBase ee = (EntityLivingBase) e;
			this.setAlarmTarget(ee);
			List<EntityPrehistoricFloraPsittacosaurus_sinensis> Psittacosaurus = this.world.getEntitiesWithinAABB(EntityPrehistoricFloraPsittacosaurus_sinensis.class, new AxisAlignedBB(this.getPosition().add(-8, -4, -8), this.getPosition().add(8, 4, 8)));
			for (EntityPrehistoricFloraPsittacosaurus_sinensis currentPsittacosaurus : Psittacosaurus) {
				currentPsittacosaurus.setAnimation(NO_ANIMATION);
				currentPsittacosaurus.setRevengeTarget(ee);
				currentPsittacosaurus.setAlarmTarget(ee);
				currentPsittacosaurus.alarmCooldown = rand.nextInt(20);
			}
		}
		return super.attackEntityFrom(ds, i);
	}

	@Override
	public Animation[] getAnimations() {
		return new Animation[]{ATTACK_ANIMATION, ROAR_ANIMATION, MAKE_NEST_ANIMATION, LAY_ANIMATION, EAT_ANIMATION, STAND_ANIMATION, SCRATCH_ANIMATION};
	}

	@Override
	public int getEggType(@Nullable String variantIn) {
		return 0; //small
	}

	public void setScreaming(boolean screaming) {
		this.screaming = screaming;
	}

	public boolean getScreaming() {
		return this.screaming;
	}

	@Nullable
	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

	public static String getPeriod() {return "Early Cretaceous";}

	//public static String getHabitat() {return "Terrestrial Therapsid";}

	@Override
	public boolean hasNest() {
		return true;
	}

	@Override
	public int getAttackLength() {
		return 20;
	}

	@Override
	public int getEatLength() {
		return 20;
	}

	@Override
	public int getRoarLength() {
		return 20;
	}

	@Override
	public String getTexture() {
		return this.getTexture();
	}

	@Override
	public boolean dropsEggs() {
		return false;
	}

	@Override
	public boolean laysEggs() {
		return true;
	}

	public float getAISpeedLand() {
		float speedBase = 0.335F;
		if (this.getTicks() < 0) {
			return 0.0F; //Is laying eggs
		}
		if (this.getAnimation() == DRINK_ANIMATION || this.getAnimation() == MAKE_NEST_ANIMATION  || this.getAnimation() == STAND_ANIMATION|| this.getAnimation() == SCRATCH_ANIMATION  ) {
			return 0.0F;
		}
		if (this.getIsFast()) {
			speedBase = speedBase * 2.65F;
		}
		return speedBase;
	}

	@Override
	public int getTalkInterval() {
		return 80;
	}

	@Override
	public int getAdultAge() {
		return 64000;
	}

	public AxisAlignedBB getAttackBoundingBox() {
		float size = this.getRenderSizeModifier() * 0.25F;
		return this.getEntityBoundingBox().grow(1.0F + size, 1.0F + size, 1.0F + size);
	}

	@Override
	public float getEyeHeight()
	{
		return Math.max(super.getEyeHeight(), this.height * 1.05F);
	}

	protected void initEntityAI() {
		tasks.addTask(0, new EntityMateAIAgeableBase(this, 1.0D));
		tasks.addTask(1, new EntityTemptAI(this, 1, false, true, 0));
		tasks.addTask(2, new LandEntitySwimmingAI(this, 0.75, false));
		tasks.addTask(3, new AttackAI(this, 1.0D, false, this.getAttackLength()));
		tasks.addTask(4, new PanicScreamAI(this, 1.0));
		tasks.addTask(5, new LandWanderNestAI(this));
		tasks.addTask(6, new LandWanderFollowParent(this, 1.05D));
		tasks.addTask(7, new LandWanderHerd(this, 1.00D, Math.max(1F, this.width) * this.getNavigator().getPathSearchRange() * 0.75F, 5));
		tasks.addTask(8, new LandWanderAvoidWaterAI(this, 1.0D));
		tasks.addTask(9, new EntityWatchClosestAI(this, EntityPlayer.class, 6.0F));
		tasks.addTask(9, new EntityWatchClosestAI(this, EntityPrehistoricFloraAgeableBase.class, 8.0F));
		tasks.addTask(11, new EntityLookIdleAI(this));
		this.targetTasks.addTask(0, new EatItemsEntityPrehistoricFloraAgeableBaseAI(this, 1));
		this.targetTasks.addTask(1, new EntityHurtByTargetSmallerThanMeAI(this, false));
	}

	@Override
	public String[] getFoodOreDicts() {
		return ArrayUtils.addAll(DietString.PLANTS, DietString.FRUIT);
	}

	@Override
	public boolean panics() {
		return true;
	}

	
	
	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEFINED;
	}


	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
	}

	@Override
	public SoundEvent getAmbientSound() {
	    return (SoundEvent) SoundEvent.REGISTRY
	            .getObject(new ResourceLocation("lepidodendron:psittacosaurus_idle"));
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
	    return (SoundEvent) SoundEvent.REGISTRY
	            .getObject(new ResourceLocation("lepidodendron:psittacosaurus_hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
	    return (SoundEvent) SoundEvent.REGISTRY
	            .getObject(new ResourceLocation("lepidodendron:psittacosaurus_death"));
	}

	public SoundEvent getAlarmSound() {
		return (SoundEvent) SoundEvent.REGISTRY
				.getObject(new ResourceLocation("lepidodendron:psittacosaurus_alarm"));
	}

	public void playAlarmSound()
	{
		SoundEvent soundevent = this.getAlarmSound();
		//System.err.println("looking for alarm sound");
		if (soundevent != null)
		{
			//System.err.println("playing alarm sound");
			this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
			this.alarmCooldown = 20;
		}
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}

	@Override
	public boolean getCanSpawnHere() {
		return this.posY < (double) this.world.getSeaLevel() && this.isInWater();
	}
	

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.getAnimation() == ATTACK_ANIMATION && this.getAnimationTick() == 11 && this.getAttackTarget() != null) {
			launchAttack();
		}

		//System.err.println("this.getMateable() " + this.getMateable() + " inPFLove " + this.inPFLove);

		if (this.standCooldown > 0) {
			this.standCooldown -= rand.nextInt(3) + 1;
		}
		if (this.standCooldown < 0) {
			this.standCooldown = 0;
		}
		AnimationHandler.INSTANCE.updateAnimations(this);

	}

	public static final PropertyDirection FACING = BlockDirectional.FACING;

	public boolean testLay(World world, BlockPos pos) {
		//System.err.println("Testing laying conditions");
		BlockPos posNest = pos;
		if (isLayableNest(world, posNest)) {
			String eggRenderType = new Object() {
				public String getValue(BlockPos posNest, String tag) {
					TileEntity tileEntity = world.getTileEntity(posNest);
					if (tileEntity != null)
						return tileEntity.getTileData().getString(tag);
					return "";
				}
			}.getValue(new BlockPos(posNest), "egg");

			//System.err.println("eggRenderType " + eggRenderType);

			if (eggRenderType.equals("")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (this.getAnimation() == NO_ANIMATION) {
			this.setAnimation(ATTACK_ANIMATION);
			//System.err.println("set attack");
		}
		return false;
	}

	@Override
	public void onEntityUpdate() {
		if (this.alarmCooldown > 0) {
			this.alarmCooldown -= 1;
		}
		if (this.getScreaming() && alarmCooldown <= 0) {
			this.playAlarmSound();
		}

		//Sometimes stand up and look around:
		if ((!this.world.isRemote) && this.getEatTarget() == null && this.getAttackTarget() == null && this.getRevengeTarget() == null
				&& !this.getIsMoving() && this.getAnimation() == NO_ANIMATION && standCooldown == 0) {
			if(rand.nextInt(10) > 4){
				this.setAnimation(SCRATCH_ANIMATION);
			} else {
				this.setAnimation(STAND_ANIMATION);
			}
			this.standCooldown = 3000;
		}
		//forces animation to return to base pose by grabbing the last tick and setting it to that.
		if ((!this.world.isRemote) && this.getAnimation() == STAND_ANIMATION && this.getAnimationTick() == STAND_ANIMATION.getDuration() - 1) {
			this.standCooldown = 3000;
			this.setAnimation(NO_ANIMATION);
		}
		if ((!this.world.isRemote) && this.getAnimation() == SCRATCH_ANIMATION && this.getAnimationTick() == SCRATCH_ANIMATION.getDuration() - 1) {
			this.standCooldown = 3000;
			this.setAnimation(NO_ANIMATION);
		}
		super.onEntityUpdate();
	}

	public boolean isDirectPathBetweenPoints(Vec3d vec1, Vec3d vec2) {
		RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y, vec2.z), false, true, false);
		return movingobjectposition == null || movingobjectposition.typeOfHit != RayTraceResult.Type.BLOCK;
	}

	@Nullable
	protected ResourceLocation getLootTable() {
		if (!this.isPFAdult()) {
			return LepidodendronMod.PSITTACOSAURUS_SINENSIS_LOOT_YOUNG;
		}
		return LepidodendronMod.PSITTACOSAURUS_SINENSIS_LOOT;
	}

	@Nullable
	@Override
	public CustomTrigger getModTrigger() {
		return ModTriggers.CLICK_PSITTACOSAURUS_SINENSIS;
	}

	//Rendering taxidermy:
	//--------------------


}