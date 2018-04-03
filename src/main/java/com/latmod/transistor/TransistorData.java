package com.latmod.transistor;

import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TransistorData implements ICapabilityProvider, IEnergyStorage
{
	@CapabilityInject(TransistorData.class)
	public static Capability<TransistorData> CAP;

	public static TransistorData get(ItemStack stack)
	{
		return stack.getCapability(CAP, null);
	}

	public final ItemStack stack;
	private long created = -1;
	private int energy = -1;
	private byte memory = -1;
	private byte selected = -1;
	private int xp = -1;
	private int points = -1;
	private int unlocked = -1;
	private byte cachedMemoryUsage = -1;
	private final TransistorFunction[] passive = new TransistorFunction[4];
	private final TransistorFunction[] attack = new TransistorFunction[4];
	private final TransistorFunction[] upgrade = new TransistorFunction[8];
	private final Map<String, Object> customTempData = new HashMap<>();

	public TransistorData(ItemStack is)
	{
		stack = is;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP ? (T) this : null;
	}

	public long getTimeCreated()
	{
		if (created == -1)
		{
			created = stack.hasTagCompound() && stack.getTagCompound().hasKey("Created") ? stack.getTagCompound().getLong("Created") : -1;
		}

		return created;
	}

	public void setTimeCreated(long value)
	{
		if (getTimeCreated() != value)
		{
			created = value;
			stack.setTagInfo("Created", new NBTTagLong(created));
		}
	}

	public long getTick(World world)
	{
		long l = world.getTotalWorldTime() - getTimeCreated();

		if (l < 0L)
		{
			l = 0L;
			setTimeCreated(world.getTotalWorldTime());
		}

		return l;
	}

	@Override
	public int getEnergyStored()
	{
		if (energy == -1)
		{
			energy = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Energy") : 0;
		}

		return energy;
	}

	public void setEnergyStored(int value)
	{
		if (getEnergyStored() != value)
		{
			energy = value;
			stack.setTagInfo("Energy", new NBTTagInt(energy));
		}
	}

	public int getMemory()
	{
		if (memory < 0)
		{
			memory = stack.hasTagCompound() ? stack.getTagCompound().getByte("Memory") : 0;

			if (memory <= 0)
			{
				memory = 16;
			}
		}

		return memory;
	}

	public void setMemory(byte value)
	{
		if (getMemory() != value)
		{
			memory = value;
			stack.setTagInfo("Memory", new NBTTagInt(memory));
		}
	}

	public byte getSelected()
	{
		if (selected < 0)
		{
			selected = stack.hasTagCompound() ? stack.getTagCompound().getByte("Selected") : 0;
		}

		return selected;
	}

	public void setSelected(int value)
	{
		byte v = (byte) MathHelper.clamp(value, 0, 3);

		if (getSelected() != v)
		{
			selected = v;
			stack.setTagInfo("Selected", new NBTTagByte(selected));
			customTempData.clear();
		}
	}

	public int getXP()
	{
		if (xp < 0)
		{
			xp = stack.hasTagCompound() ? stack.getTagCompound().getInteger("XP") : 0;
		}

		return xp;
	}

	public void setXP(int value)
	{
		if (getXP() != value)
		{
			xp = value;
			stack.setTagInfo("XP", new NBTTagInt(xp));
		}
	}

	public int getNextLevelXP()
	{
		return 100;
	}

	public int getPoints()
	{
		if (points < 0)
		{
			points = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Points") : 0;
		}

		return points;
	}

	public void setPoints(int value)
	{
		if (getPoints() != value)
		{
			points = value;
			stack.setTagInfo("Points", new NBTTagInt(points));
		}
	}

	public int getUnlocked()
	{
		if (unlocked < 0)
		{
			unlocked = 0;

			if (stack.hasTagCompound())
			{
				unlocked = stack.getTagCompound().getInteger("Unlocked");

				if (stack.getTagCompound().hasKey("Available", Constants.NBT.TAG_LIST))
				{
					NBTTagList list = stack.getTagCompound().getTagList("Available", Constants.NBT.TAG_STRING);

					for (int i = 0; i < list.tagCount(); i++)
					{
						TransistorFunction function = TransistorFunctions.get(list.getStringTagAt(i));

						if (!function.isEmpty())
						{
							unlocked |= 1 << function.index;
						}
					}
				}
			}
		}

		return unlocked;
	}

	public void setUnlocked(int value)
	{
		if (getUnlocked() != value)
		{
			unlocked = value;
			stack.setTagInfo("Unlocked", new NBTTagInt(unlocked));
		}
	}

	public boolean isUnlocked(TransistorFunction function)
	{
		return !function.isEmpty() && (getUnlocked() & (1 << function.index)) != 0;
	}

	public boolean isFunctionInUse(TransistorFunction function)
	{
		if (!isUnlocked(function))
		{
			return false;
		}

		for (int i = 0; i < 4; i++)
		{
			if (getAttack(i).equals(function))
			{
				return true;
			}

			for (int j = 0; j < 2; j++)
			{
				if (getUpgrade(i, j).equals(function))
				{
					return true;
				}
			}

			if (getPassive(i).equals(function))
			{
				return true;
			}
		}

		return false;
	}

	public boolean isUpgradeSlotUnlocked(int attack, int slot)
	{
		return attack >= 0 && attack < 4 && slot >= 0 && slot < 2 && (getUnlocked() & (1 << (attack * 2 + slot + 16))) != 0;
	}

	public boolean isPassiveSlotUnlocked(int index)
	{
		return index >= 0 && index < 4 && (getUnlocked() & (1 << (index + 24))) != 0;
	}

	public void unlock(TransistorFunction function)
	{
		if (!function.isEmpty())
		{
			setUnlocked(getUnlocked() | (1 << function.index));
		}
	}

	private TransistorFunction getFunction(String key)
	{
		if (stack.hasTagCompound())
		{
			TransistorFunction function;

			if (stack.getTagCompound().hasKey(key, Constants.NBT.TAG_ANY_NUMERIC))
			{
				function = TransistorFunctions.get(stack.getTagCompound().getByte(key));
			}
			else
			{
				function = TransistorFunctions.get(stack.getTagCompound().getString(key));
			}

			if (isUnlocked(function))
			{
				return function;
			}
		}

		return TransistorFunctions.EMPTY;
	}

	private void setFunction(String key, TransistorFunction function)
	{
		if (!function.isEmpty())
		{
			stack.setTagInfo(key, new NBTTagByte(function.index));
		}
		else if (stack.hasTagCompound())
		{
			stack.getTagCompound().removeTag(key);
		}
	}

	public TransistorFunction getAttack(int index)
	{
		if (index < 0 || index >= 4)
		{
			return TransistorFunctions.EMPTY;
		}

		if (attack[index] == null)
		{
			attack[index] = getFunction("Attack_" + (index + 1));
			cachedMemoryUsage = -1;
		}

		return attack[index];
	}

	public void setAttack(int index, TransistorFunction function)
	{
		if (index >= 0 && index < 4 && !getAttack(index).equals(function))
		{
			attack[index] = function;
			setFunction("Attack_" + (index + 1), function);
			cachedMemoryUsage = -1;
		}
	}

	public TransistorFunction getSelectedAttack()
	{
		return getAttack(getSelected());
	}

	public TransistorFunction getUpgrade(int attack, int slot)
	{
		if (attack < 0 || attack >= 4 || slot < 0 || slot >= 2)
		{
			return TransistorFunctions.EMPTY;
		}

		int index = attack * 2 + slot;

		if (upgrade[index] == null)
		{
			upgrade[index] = getFunction("Upgrade_" + (attack + 1) + "_" + (slot + 1));
			cachedMemoryUsage = -1;
		}

		return upgrade[index];
	}

	public void setUpgrade(int attack, int slot, TransistorFunction function)
	{
		if (attack >= 0 && attack < 4 && slot >= 0 && slot < 2 && !getUpgrade(attack, slot).equals(function))
		{
			upgrade[attack * 2 + slot] = function;
			setFunction("Upgrade_" + (attack + 1) + "_" + (slot + 1), function);
			cachedMemoryUsage = -1;
		}
	}

	public boolean hasUpgrade(TransistorFunction function)
	{
		for (int i = 0; i < 2; i++)
		{
			if (getUpgrade(getSelected(), i).equals(function))
			{
				return true;
			}
		}

		return false;
	}

	public TransistorFunction getPassive(int index)
	{
		if (index < 0 || index >= 4)
		{
			return TransistorFunctions.EMPTY;
		}

		if (passive[index] == null)
		{
			passive[index] = getFunction("Passive_" + (index + 1));
			cachedMemoryUsage = -1;
		}

		return passive[index];
	}

	public void setPassive(int index, TransistorFunction function)
	{
		if (index >= 0 && index < 4 && !getPassive(index).equals(function))
		{
			passive[index] = function;
			setFunction("Passive_" + (index + 1), function);
			cachedMemoryUsage = -1;
		}
	}

	public boolean hasPassive(TransistorFunction function)
	{
		for (int i = 0; i < 4; i++)
		{
			if (getPassive(i).equals(function))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		int e = getEnergyStored();
		int r = Math.min(getMaxEnergyStored() - e, maxReceive);

		if (!simulate && r > 0)
		{
			setEnergyStored(e + r);
		}

		return r;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return 0;
	}

	@Override
	public int getMaxEnergyStored()
	{
		return getMemory() * 1000;
	}

	@Override
	public boolean canExtract()
	{
		return false;
	}

	@Override
	public boolean canReceive()
	{
		return true;
	}

	public void setCustomTempData(String key, @Nullable Object object)
	{
		if (object == null)
		{
			customTempData.remove(key);
		}
		else
		{
			customTempData.put(key, object);
		}
	}

	@Nullable
	public <E> E getCustomTempData(String key)
	{
		return (E) customTempData.get(key);
	}

	public void update(EntityPlayer player, boolean isSelected)
	{
		if (getTimeCreated() == -1L)
		{
			setTimeCreated(player.world.getTotalWorldTime());
		}

		getSelectedAttack().onUpdate(this, player, isSelected);

		for (int i = 0; i < 4; i++)
		{
			getPassive(i).onPassiveUpdate(this, player, isSelected);
		}

		if (!player.world.isRemote && getTick(player.world) % 200L == 0L)
		{
			receiveEnergy(1000, false);
		}
	}

	public void selectNext()
	{
		int s = getSelected();
		int o = s;

		while (true)
		{
			s = (s + 1) % 4;

			if (s == o || !getAttack(s).isEmpty())
			{
				setSelected(s);
				break;
			}
		}
	}

	public byte getUsedMemory()
	{
		if (cachedMemoryUsage >= 0)
		{
			return cachedMemoryUsage;
		}

		cachedMemoryUsage = 0;

		for (int i = 0; i < 4; i++)
		{
			TransistorFunction function = getAttack(i);

			if (!function.isEmpty())
			{
				cachedMemoryUsage += function.memory;
			}

			for (int j = 0; j < 2; j++)
			{
				function = getUpgrade(i, j);

				if (!function.isEmpty())
				{
					cachedMemoryUsage += function.memory;
				}
			}

			function = getPassive(i);

			if (!function.isEmpty())
			{
				cachedMemoryUsage += function.memory;
			}
		}

		return cachedMemoryUsage;
	}
}