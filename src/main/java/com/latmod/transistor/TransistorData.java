package com.latmod.transistor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashSet;

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
	private int energy;
	private byte memory;
	private byte selected;
	private int xp;
	private final LinkedHashSet<TransistorFunction> available;
	private final TransistorFunction[] passive;
	private final TransistorFunction[] attack;
	private final TransistorFunction[] upgrade;
	private byte cachedMemoryUsage;

	public TransistorData(ItemStack is)
	{
		stack = is;
		available = new LinkedHashSet<>(2);
		passive = new TransistorFunction[2];
		attack = new TransistorFunction[4];
		upgrade = new TransistorFunction[8];
		reset();
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

	public void reset()
	{
		energy = -1;
		memory = -1;
		selected = -1;
		xp = -1;
		available.clear();
		Arrays.fill(passive, null);
		Arrays.fill(attack, null);
		Arrays.fill(upgrade, null);
		cachedMemoryUsage = -1;
	}

	public void init()
	{
		if (!available.isEmpty())
		{
			return;
		}

		reset();
		available.add(TransistorFunctions.CRASH);
		available.add(TransistorFunctions.BREACH);

		NBTTagList a = new NBTTagList();

		for (TransistorFunction function : available)
		{
			a.appendTag(new NBTTagString(function.toString()));
		}

		stack.setTagInfo("Available", a);

		setAttack(0, TransistorFunctions.CRASH);
		setAttack(1, TransistorFunctions.BREACH);
	}

	@Override
	public int getEnergyStored()
	{
		init();

		if (energy == -1)
		{
			energy = stack.hasTagCompound() ? stack.getTagCompound().getInteger("Energy") : 0;
		}

		return energy;
	}

	public void setEnergyStored(int e)
	{
		if (getEnergyStored() != e)
		{
			energy = e;
			stack.setTagInfo("Energy", new NBTTagInt(energy));
		}
	}

	public int getMemory()
	{
		init();

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
		init();

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
		}
	}

	public int getXP()
	{
		init();

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

	public LinkedHashSet<TransistorFunction> getAvailable()
	{
		if (available.isEmpty() && stack.hasTagCompound())
		{
			NBTTagList list = stack.getTagCompound().getTagList("Available", Constants.NBT.TAG_STRING);

			for (int i = 0; i < list.tagCount(); i++)
			{
				TransistorFunction function = TransistorFunction.get(list.getStringTagAt(i));

				if (!function.isEmpty())
				{
					available.add(function);
				}
			}
		}

		init();
		return available;
	}

	public void setAvailable(LinkedHashSet<TransistorFunction> collection)
	{
		if (!getAvailable().equals(collection))
		{
			available.clear();
			available.addAll(collection);

			NBTTagList a = new NBTTagList();

			for (TransistorFunction function : available)
			{
				a.appendTag(new NBTTagString(function.toString()));
			}

			stack.setTagInfo("Available", a);
		}
	}

	public boolean isAvailable(TransistorFunction function)
	{
		return !function.isEmpty() && getAvailable().contains(function);
	}

	public void setAvailable(TransistorFunction function, boolean value)
	{
		if (function.isEmpty())
		{
			return;
		}

		LinkedHashSet<TransistorFunction> set = new LinkedHashSet<>(getAvailable());

		if (value ? set.add(function) : set.remove(function))
		{
			setAvailable(set);
		}
	}

	public TransistorFunction getAttack(int index)
	{
		if (index < 0 || index >= 4)
		{
			return TransistorFunction.EMPTY;
		}

		if (attack[index] == null)
		{
			attack[index] = stack.hasTagCompound() ? TransistorFunction.get(stack.getTagCompound().getString("Attack_" + (index + 1))) : TransistorFunction.EMPTY;

			if (!isAvailable(attack[index]))
			{
				attack[index] = TransistorFunction.EMPTY;
			}
		}

		return attack[index];
	}

	public void setAttack(int index, TransistorFunction function)
	{
		if (!getAttack(index).equals(function))
		{
			attack[index] = function;

			if (!function.isEmpty())
			{
				stack.setTagInfo("Attack_" + (index + 1), new NBTTagString(function.toString()));
			}
			else if (stack.hasTagCompound())
			{
				stack.getTagCompound().removeTag("Attack_" + (index + 1));
			}

			cachedMemoryUsage = -1;
		}
	}

	public TransistorFunction getSelectedAttack()
	{
		return getAttack(getSelected());
	}

	public boolean isAttackSelected(TransistorFunction function)
	{
		return getSelectedAttack().equals(function);
	}

	public TransistorFunction getUpgrade(int attack, int slot)
	{
		if (attack < 0 || attack >= 4 || slot < 0 || slot >= 2)
		{
			return TransistorFunction.EMPTY;
		}

		int index = attack * 2 + slot;

		if (upgrade[index] == null)
		{
			upgrade[index] = stack.hasTagCompound() ? TransistorFunction.get(stack.getTagCompound().getString("Upgrade_" + (attack + 1) + "_" + (slot + 1))) : TransistorFunction.EMPTY;

			if (!isAvailable(upgrade[index]))
			{
				upgrade[index] = TransistorFunction.EMPTY;
			}
		}

		return upgrade[index];
	}

	public void setUpgrade(int attack, int slot, TransistorFunction function)
	{
		if (!getUpgrade(attack, slot).equals(function))
		{
			upgrade[attack * 2 + slot] = function;

			if (!function.isEmpty())
			{
				stack.setTagInfo("Upgrade_" + (attack + 1) + "_" + (slot + 1), new NBTTagString(function.toString()));
			}
			else if (stack.hasTagCompound())
			{
				stack.getTagCompound().removeTag("Upgrade_" + (attack + 1) + "_" + (slot + 1));
			}

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
		if (index < 0 || index >= 2)
		{
			return TransistorFunction.EMPTY;
		}

		if (passive[index] == null)
		{
			passive[index] = stack.hasTagCompound() ? TransistorFunction.get(stack.getTagCompound().getString("Passive_" + (index + 1))) : TransistorFunction.EMPTY;

			if (!isAvailable(passive[index]))
			{
				passive[index] = TransistorFunction.EMPTY;
			}
		}

		return passive[index];
	}

	public void setPassive(int index, TransistorFunction function)
	{
		if (!getPassive(index).equals(function))
		{
			passive[index] = function;

			if (!function.isEmpty())
			{
				stack.setTagInfo("Passive_" + (index + 1), new NBTTagString(function.toString()));
			}
			else if (stack.hasTagCompound())
			{
				stack.getTagCompound().removeTag("Passive_" + (index + 1));
			}

			cachedMemoryUsage = -1;
		}
	}

	public boolean hasPassive(TransistorFunction function)
	{
		for (int i = 0; i < 2; i++)
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

	public void update(EntityPlayer player, boolean isSelected)
	{
		for (int i = 0; i < 2; i++)
		{
			getPassive(i).onPassiveUpdate(this, player, isSelected);
		}

		if (!player.world.isRemote && player.world.getTotalWorldTime() % 200L == 0L)
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

	public float getMode()
	{
		if (getSelectedAttack().isEmpty())
		{
			return 1F;
		}
		else
		{
			return getSelectedAttack() == TransistorFunctions.BREACH ? 0.5F : 0F;
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
		}

		for (int i = 0; i < 2; i++)
		{
			TransistorFunction function = getPassive(i);

			if (!function.isEmpty())
			{
				cachedMemoryUsage += function.memory;
			}
		}

		return cachedMemoryUsage;
	}
}