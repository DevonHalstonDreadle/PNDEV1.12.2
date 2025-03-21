
package net.lepidodendron.tileentity;

import net.lepidodendron.LepidodendronConfig;
import net.lepidodendron.block.*;
import net.lepidodendron.util.BlockSounds;
import net.lepidodendron.util.Functions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityPortalBlock extends TileEntity implements ITickable {

	private int animationTick;
	private int animationTickAmbient;
	private boolean isActive;

	@Override
	public double getMaxRenderDistanceSquared() {
		return 256*256;
	}

	public static boolean isPartOfValidPortal(World world, BlockPos pos, IBlockState portalState) {
		boolean xChecked = false;
		if (world.getBlockState(pos.east()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.east()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.east().up()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.east().up()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.east().down()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.east().down()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.west()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.west()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.west().up()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.west().up()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.west().down()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.west().down()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.X) {
				xChecked = true;
			}
		}
		else if (world.getBlockState(pos.up()).getBlock() == portalState.getBlock()) {
			xChecked = true;
		}
		else if (world.getBlockState(pos.down()).getBlock() == portalState.getBlock()) {
			xChecked = true;
		}

		boolean zChecked = false;
		if (world.getBlockState(pos.north()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.north()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.north().up()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.north().up()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.north().down()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.north().down()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.south()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.south()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.south().up()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.south().up()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.south().down()).getBlock() == portalState.getBlock()) {
			if (world.getBlockState(pos.south().down()).getValue(BlockPortal.AXIS) == EnumFacing.Axis.Z) {
				zChecked = true;
			}
		}
		else if (world.getBlockState(pos.up()).getBlock() == portalState.getBlock()) {
			zChecked = true;
		}
		else if (world.getBlockState(pos.down()).getBlock() == portalState.getBlock()) {
			zChecked = true;
		}
		if ((!xChecked) & (!zChecked)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void update()
	{
		if (this.isActive) {
			//deal with ambient ticking
			if (this.animationTickAmbient == 90 && this.animationTick == 90 && this.world.rand.nextInt(425) == 0) { //restart the ambient ticker
				animationTickAmbient = 0;
			}
			if (this.animationTickAmbient <= 90) { //4++ seconds
				this.animationTickAmbient ++;
			}
			if (this.animationTickAmbient > 90) {
				this.animationTickAmbient = 90;
			}
			if (this.animationTick < 90) {
				this.animationTickAmbient = 90;
			}

			//Are we part of a valid portal?
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockOverworld.block) {
				IBlockState portalOverworld = Functions.getPortalState(0);
				if (portalOverworld != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalOverworld)) {
						this.setIsActive(false);
					}
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockPrecambrian.block) {
				IBlockState portalPrecambrian = Functions.getPortalState(LepidodendronConfig.dimPrecambrian);
				if (portalPrecambrian != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalPrecambrian)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 6) {
					world.playSound(null, pos, BlockSounds.PORTAL_PRECAMBRIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCambrian.block) {
				IBlockState portalCambrian = Functions.getPortalState(LepidodendronConfig.dimCambrian);
				if (portalCambrian != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalCambrian)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 30) {
					world.playSound(null, pos, BlockSounds.PORTAL_CAMBRIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockOrdovician.block) {
				IBlockState portalOrdovician = Functions.getPortalState(LepidodendronConfig.dimOrdovician);
				if (portalOrdovician != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalOrdovician)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 30) {
					world.playSound(null, pos, BlockSounds.PORTAL_ORDOVICIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block) {
				IBlockState portalSilurian = Functions.getPortalState(LepidodendronConfig.dimSilurian);
				if (portalSilurian != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalSilurian)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 40) {
					world.playSound(null, pos, BlockSounds.PORTAL_SILURIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockDevonian.block) {
				IBlockState portalDevonian = Functions.getPortalState(LepidodendronConfig.dimDevonian);
				if (portalDevonian != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalDevonian)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 6) {
					world.playSound(null, pos, BlockSounds.PORTAL_DEVONIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCarboniferous.block) {
				IBlockState portalCarboniferous = Functions.getPortalState(LepidodendronConfig.dimCarboniferous);
				if (portalCarboniferous != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalCarboniferous)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 6) {
					world.playSound(null, pos, BlockSounds.PORTAL_CARBONIFEROUS, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockPermian.block) {
				IBlockState portalPermian = Functions.getPortalState(LepidodendronConfig.dimPermian);
				if (portalPermian != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalPermian)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 6) {
					world.playSound(null, pos, BlockSounds.PORTAL_PERMIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockTriassic.block) {
				IBlockState portalTriassic = Functions.getPortalState(LepidodendronConfig.dimTriassic);
				if (portalTriassic != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalTriassic)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 20) {
					world.playSound(null, pos, BlockSounds.PORTAL_TRIASSIC, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}
			if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockJurassic.block) {
				IBlockState portalJurassic = Functions.getPortalState(LepidodendronConfig.dimJurassic);
				if (portalJurassic != null) {
					if (!isPartOfValidPortal(this.world, this.pos, portalJurassic)) {
						this.setIsActive(false);
					}
				}
				if (this.getIsActive() && this.getAnimationTick() == 20) {
					world.playSound(null, pos, BlockSounds.PORTAL_JURASSIC, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}

			if (this.animationTick <= 90) { //4++ seconds
				setAnimationTick(this.animationTick + 1);
				if (this.getAnimationTick() == 1) {
					world.playSound(null, pos, BlockSounds.PORTAL_INITIALISE, SoundCategory.BLOCKS, 1.0F, 1.0F);
				}
				if (this.animationTick == 8
					|| (((int)(this.animationTick / 3) == world.rand.nextInt(30))
					&& (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCambrian.block
						|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block
						|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block)))
				{
					EnumParticleTypes particleType = EnumParticleTypes.BLOCK_DUST;
					SoundEvent soundEvent = SoundEvents.ENTITY_PAINTING_PLACE;
					int particler = 5;
					if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockOverworld.block) {
						particler = 0;
					}
					if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockPrecambrian.block) {
						particleType = EnumParticleTypes.FLAME;
						particler = 4;
					}
					if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCambrian.block
							|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block
							|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block) {
						particleType = EnumParticleTypes.WATER_SPLASH;
						particler = 4;
					}
					if (this.animationTick == 8 && world.getBlockState(this.getPos()).getBlock() != BlockPortalBlockOverworld.block) {
						world.playSound(null, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, soundEvent, SoundCategory.BLOCKS, 0.65F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
					}

					for (int i = 0; i < particler; i++) {
						IBlockState state = world.getBlockState(pos);
						world.spawnParticle(particleType, this.pos.getX() + 0.5D, this.pos.getY() + 1D, this.pos.getZ() + 0.25D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), 0.15D + world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX() + 0.5D, this.pos.getY() + 1D, this.pos.getZ() + 0.75D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), 0.15D + world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));

						world.spawnParticle(particleType, this.pos.getX() + 1.0D, this.pos.getY() + 0.25D, this.pos.getZ() + 0.5D, 0.15 + world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX() + 1.0D, this.pos.getY() + 0.75D, this.pos.getZ() + 0.5D, 0.15 + world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));

						world.spawnParticle(particleType, this.pos.getX(), this.pos.getY() + 0.25D, this.pos.getZ() + 0.5D, -(0.15 + world.rand.nextDouble() * 0.075), world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX(), this.pos.getY() + 0.75D, this.pos.getZ() + 0.5D, -(0.15 + world.rand.nextDouble() * 0.075), world.rand.nextDouble() * 0.075, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));

						world.spawnParticle(particleType, this.pos.getX() + 0.25D, this.pos.getY() + 0.5D, this.pos.getZ() + 1.0D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), world.rand.nextDouble() * 0.075, 0.15 + world.rand.nextDouble() * 0.075, Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX() + 0.75D, this.pos.getY() + 0.5D, this.pos.getZ() + 1.0D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), world.rand.nextDouble() * 0.075, 0.15 + world.rand.nextDouble() * 0.075, Block.getStateId(state));

						world.spawnParticle(particleType, this.pos.getX() + 0.25D, this.pos.getY() + 0.5D, this.pos.getZ(), world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), world.rand.nextDouble() * 0.075, -(0.15 + world.rand.nextDouble() * 0.075), Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX() + 0.75D, this.pos.getY() + 0.5D, this.pos.getZ(), world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), world.rand.nextDouble() * 0.075, -(0.15 + world.rand.nextDouble() * 0.075), Block.getStateId(state));

						world.spawnParticle(particleType, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.25D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), -(0.05D + world.rand.nextDouble() * 0.025), world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));
						world.spawnParticle(particleType, this.pos.getX() + 0.5D, this.pos.getY(), this.pos.getZ() + 0.75D, world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), -(0.05D + world.rand.nextDouble() * 0.025), world.rand.nextDouble() * 0.075 * (world.rand.nextInt(3) - 1), Block.getStateId(state));

					}
				}
			}
			if (this.animationTick > 90) {
				setAnimationTick(90);
			}
			if (this.animationTick == 88) {
				world.notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.getPos()), this.getWorld().getBlockState(this.getPos()), 3);
			}
			if (this.animationTick == 89) {
				world.notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.getPos()), this.getWorld().getBlockState(this.getPos()), 3);
			}
		}
		else {
			if (this.animationTick > 0) { //4++ seconds
				--this.animationTick;
			}
			if (this.animationTick < 0) {
				this.animationTick = 0;
			}
			if (this.animationTick == 88) {
				world.notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.getPos()), this.getWorld().getBlockState(this.getPos()), 3);
			}
			if (this.animationTick == 89) {
				world.notifyBlockUpdate(this.getPos(), this.getWorld().getBlockState(this.getPos()), this.getWorld().getBlockState(this.getPos()), 3);
			}
			if (this.getAnimationTick() == 75) { //3.5 seconds (ticks 15 to 85)
				if (world.getBlockState(this.getPos()).getBlock() != BlockPortalBlockOverworld.block
						|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockPrecambrian.block) {
					//No sound for closing this one
				}
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCambrian.block
						|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block
						|| world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockSilurian.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_WATER_DRAIN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockDevonian.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_DEVONIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockCarboniferous.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_CARBONIFEROUS, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockPermian.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_PERMIAN, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}

			if (this.getAnimationTick() == 65) {
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockTriassic.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_TRIASSIC, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}

			if (this.getAnimationTick() == 52) {
				if (world.getBlockState(this.getPos()).getBlock() == BlockPortalBlockJurassic.block) {
					world.playSound(null, pos, BlockSounds.PORTAL_JURASSIC, SoundCategory.BLOCKS, 0.5F, 1.0F);
				}
			}

		}
	}

	public boolean isFaceActive(World world, BlockPos pos, EnumFacing face) {
		return world.getBlockState(pos.offset(face)).getBlockFaceShape(world, pos.offset(face), face.getOpposite()) != BlockFaceShape.SOLID
			&& (!(world.getBlockState(pos.offset(face)) instanceof BlockPortalBlock));
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		//return new AxisAlignedBB(pos, pos.add(2, 2, 2));
		return INFINITE_EXTENT_AABB;
	}

	public int getAnimationTickAmbient() {
		return this.animationTickAmbient;
	}

	public int getAnimationTick() {
		return this.animationTick;
	}

	public void setAnimationTick(int tick) {
		this.animationTick = tick;
	}

	public boolean getIsActive() {
		return this.isActive;
	}

	public void setIsActive(boolean active) {
		this.isActive = active;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new SPacketUpdateTileEntity(pos, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager netManager, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if (compound.hasKey("animationTick")) {
			this.animationTick = compound.getInteger("animationTick");
		}
		if (compound.hasKey("animationTickAmbient")) {
			this.animationTickAmbient = compound.getInteger("animationTickAmbient");
		}
		if (compound.hasKey("isActive")) {
			this.isActive = compound.getBoolean("isActive");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		compound.setInteger("animationTick", this.animationTick);
		compound.setInteger("animationTickAmbient", this.animationTickAmbient);
		compound.setBoolean("isActive", this.isActive);
		return compound;
	}

}