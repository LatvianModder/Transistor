package com.latmod.transistor;

import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TransistorData implements ICapabilitySerializable<NBTTagCompound>
{
	@CapabilityInject(TransistorData.class)
	public static Capability<TransistorData> CAP;

	@SuppressWarnings("ConstantConditions")
	public static TransistorData get(ItemStack stack)
	{
		return stack.getCapability(CAP, null);
	}

	public static final String[] KEYS = new String[16];

	static
	{
		for (int i = 0; i < 4; i++)
		{
			KEYS[i] = "Attack_" + (i + 1);

			for (int j = 0; j < 2; j++)
			{
				KEYS[i * 2 + 4 + j] = "Upgrade_" + (i + 1) + "_" + (j + 1);
			}

			KEYS[i + 12] = "Passive_" + (i + 1);
		}
	}

	public long created = -1L;
	public int energy = 16000;
	public long rechargeAt;
	public int memory = 16;
	public int selected = 0;
	public int xp = 0;
	public int points = 0;
	public int unlocked = 0;
	public byte cachedMemoryUsage = -1;
	public final TransistorFunction[] functions = new TransistorFunction[16];
	public final Map<String, Object> customTempData = new HashMap<>();

	public TransistorData()
	{
		Arrays.fill(functions, TransistorFunctions.EMPTY);
		unlockFunction(TransistorFunctions.CRASH);
		setAttack(0, TransistorFunctions.CRASH);
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setLong("Created", created);
		nbt.setInteger("Energy", energy);
		nbt.setLong("RechargeAt", rechargeAt);
		nbt.setByte("Memory", (byte) memory);
		nbt.setByte("Selected", (byte) selected);
		nbt.setInteger("XP", xp);
		nbt.setInteger("Points", points);
		nbt.setInteger("Unlocked", unlocked);

		for (int i = 0; i < functions.length; i++)
		{
			if (!functions[i].isEmpty())
			{
				nbt.setByte(KEYS[i], functions[i].index);
			}
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		created = nbt.hasKey("Created") ? nbt.getLong("Created") : -1;
		energy = nbt.getInteger("Energy");
		rechargeAt = nbt.getLong("RechargeAt");
		memory = nbt.getByte("Memory") & 255;
		selected = nbt.getByte("Selected") & 3;
		xp = nbt.getInteger("XP");
		points = nbt.getInteger("Points");
		unlocked = nbt.getInteger("Unlocked");
		cachedMemoryUsage = -1;
		NBTTagList list = nbt.getTagList("Available", Constants.NBT.TAG_STRING);

		for (int i = 0; i < list.tagCount(); i++)
		{
			TransistorFunction function = TransistorFunctions.get(list.getStringTagAt(i));

			if (!function.isEmpty())
			{
				unlocked |= 1 << function.index;
			}
		}

		TransistorFunction function;

		for (int i = 0; i < functions.length; i++)
		{
			if (nbt.hasKey(KEYS[i], Constants.NBT.TAG_ANY_NUMERIC))
			{
				function = TransistorFunctions.get(nbt.getByte(KEYS[i]));
			}
			else
			{
				function = TransistorFunctions.get(nbt.getString(KEYS[i]));
			}

			functions[i] = isFunctionUnlocked(function) ? function : TransistorFunctions.EMPTY;
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP;
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
	{
		return capability == CAP ? (T) this : null;
	}

	public long getTick(World world)
	{
		long l = world.getTotalWorldTime() - created;

		if (l < 0L)
		{
			l = 0L;
			created = world.getTotalWorldTime();
		}

		return l;
	}

	public int getMaxEnergy()
	{
		return memory * 1000;
	}

	public int getSelected()
	{
		return selected;
	}

	public boolean setSelected(int value)
	{
		byte v = (byte) MathHelper.clamp(value, 0, 3);

		if (getSelected() != v)
		{
			selected = v;
			customTempData.clear();
			return true;
		}

		return false;
	}

	public void addXP(World world, int i)
	{
		xp += i;
	}

	public int getNextLevelXP()
	{
		return 100;
	}

	public boolean isFunctionUnlocked(TransistorFunction function)
	{
		return !function.isEmpty() && (unlocked & (1 << function.index)) != 0;
	}

	public boolean isSlotUnlocked(int index)
	{
		return index < 4 || (unlocked & (1 << (index + 12))) != 0;
	}

	public int isFunctionInUse(TransistorFunction function)
	{
		if (!isFunctionUnlocked(function))
		{
			return -1;
		}

		for (int i = 0; i < 16; i++)
		{
			if (getFunction(i).equals(function))
			{
				return i;
			}
		}

		return -1;
	}

	public void unlockFunction(TransistorFunction function)
	{
		if (!function.isEmpty())
		{
			unlocked |= (1 << function.index);
		}
	}

	public boolean unlockSlot(int index, boolean checkPoints)
	{
		if (index < 4 || isSlotUnlocked(index))
		{
			return false;
		}
		else if (checkPoints)
		{
			int p = 1;

			if (index >= 12)
			{
				p = 2;
			}

			if (points >= p)
			{
				points -= p;
			}
			else
			{
				return false;
			}
		}

		unlocked |= (1 << index + 12);
		return true;
	}

	public boolean isOverloaded(TransistorFunction function)
	{
		return false;
	}

	public TransistorFunction getFunction(int index)
	{
		return index < 0 || index >= 16 ? TransistorFunctions.EMPTY : functions[index];
	}

	public void setFunction(int index, TransistorFunction function)
	{
		if (index < 0 || index >= 16 || getFunction(index).equals(function))
		{
			return;
		}

		functions[index] = function;
		cachedMemoryUsage = -1;
	}

	public TransistorFunction getAttack(int index)
	{
		return index < 0 || index >= 4 ? TransistorFunctions.EMPTY : getFunction(index);
	}

	public void setAttack(int index, TransistorFunction function)
	{
		if (index >= 0 && index < 4)
		{
			setFunction(index, function);
		}
	}

	public TransistorFunction getSelectedAttack()
	{
		return getAttack(getSelected());
	}

	public TransistorFunction getUpgrade(int attack, int slot)
	{
		return attack < 0 || attack >= 4 || slot < 0 || slot >= 2 ? TransistorFunctions.EMPTY : getFunction(4 + attack * 2 + slot);
	}

	public void setUpgrade(int attack, int slot, TransistorFunction function)
	{
		if (attack >= 0 && attack < 4 && slot >= 0 && slot < 2)
		{
			setFunction(4 + attack * 2 + slot, function);
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

	public boolean isUpgradeSlotUnlocked(int attack, int slot)
	{
		return attack >= 0 && attack < 4 && slot >= 0 && slot < 2 && isSlotUnlocked(attack * 2 + slot + 4);
	}

	public TransistorFunction getPassive(int index)
	{
		return index < 0 || index >= 4 ? TransistorFunctions.EMPTY : getFunction(index + 12);
	}

	public void setPassive(int index, TransistorFunction function)
	{
		if (index >= 0 && index < 4)
		{
			setFunction(index + 12, function);
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

	public boolean isPassiveSlotUnlocked(int index)
	{
		return index >= 0 && index < 4 && isSlotUnlocked(index + 12);
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
	@SuppressWarnings("unchecked")
	public <E> E getCustomTempData(String key)
	{
		return (E) customTempData.get(key);
	}

	public void update(EntityPlayer player, boolean isSelected)
	{
		long now = player.world.getTotalWorldTime();

		if (created == -1L)
		{
			created = now;
		}

		getSelectedAttack().onUpdate(this, player, isSelected);

		for (int i = 0; i < 4; i++)
		{
			getPassive(i).onPassiveUpdate(this, player, isSelected);
		}

		if (energy < getMaxEnergy() && now >= rechargeAt)
		{
			energy = Math.min(getMaxEnergy(), energy + 1000);
			rechargeAt = now + 200L;
		}
	}

	public byte getUsedMemory()
	{
		if (cachedMemoryUsage >= 0)
		{
			return cachedMemoryUsage;
		}

		cachedMemoryUsage = 0;

		for (int i = 0; i < 16; i++)
		{
			TransistorFunction function = getFunction(i);

			if (!function.isEmpty())
			{
				cachedMemoryUsage += function.memory;
			}
		}

		return cachedMemoryUsage;
	}

	public boolean installFunction(int index, int f)
	{
		if (index < 0 || index >= 16 || !getFunction(index).isEmpty() || !isSlotUnlocked(index))
		{
			return false;
		}

		TransistorFunction function = TransistorFunctions.get(f);

		if (!function.isEmpty() && isFunctionUnlocked(function) && getUsedMemory() + function.memory <= memory)
		{
			if (index >= 4 && index < 12 && getAttack((index - 4) / 2).isEmpty())
			{
				return false;
			}

			setFunction(index, function);
			return true;
		}

		return false;
	}

	public boolean uninstallFunction(int index)
	{
		if (index < 0 || index >= 16 || getFunction(index).isEmpty())
		{
			return false;
		}

		if (index < 4)
		{
			setFunction(index * 2 + 4, TransistorFunctions.EMPTY);
			setFunction(index * 2 + 5, TransistorFunctions.EMPTY);
		}

		setFunction(index, TransistorFunctions.EMPTY);
		return true;
	}

	public boolean installMemory()
	{
		if (points >= 1 && memory < 32)
		{
			memory++;
			points--;
			return true;
		}

		return false;
	}

	public boolean canUseEnergy(int energy)
	{
		return energy > 0;
	}

	public boolean useEnergy(World world, int e)
	{
		if (canUseEnergy(e))
		{
			energy = Math.max(0, energy - e);
			rechargeAt = Math.max(rechargeAt, world.getTotalWorldTime() + (energy <= 0 ? 600L : 200L));
			return true;
		}

		return false;
	}
}