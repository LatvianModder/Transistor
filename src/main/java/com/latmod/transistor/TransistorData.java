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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class TransistorData implements ICapabilityProvider
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

	public final ItemStack stack;
	private boolean loaded = false;
	private long created;
	private int energy;
	private long rechargeAt;
	private byte memory;
	private byte selected;
	private int xp;
	private int points;
	private int unlocked;
	private byte cachedMemoryUsage;
	private final TransistorFunction[] functions = new TransistorFunction[16];
	private final Map<String, Object> customTempData = new HashMap<>();

	public TransistorData(ItemStack is)
	{
		stack = is;
	}

	private NBTTagCompound getNBT()
	{
		NBTTagCompound nbt = stack.getTagCompound();

		if (nbt == null)
		{
			nbt = new NBTTagCompound();
			stack.setTagCompound(nbt);
		}

		return nbt;
	}

	private void load()
	{
		if (loaded)
		{
			return;
		}

		loaded = true;
		NBTTagCompound nbt = getNBT();
		created = nbt.hasKey("Created") ? nbt.getLong("Created") : -1;
		energy = nbt.getInteger("Energy");
		rechargeAt = nbt.getLong("RechargeAt");
		memory = nbt.getByte("Memory");
		selected = nbt.getByte("Selected");
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

	public long getTimeCreated()
	{
		load();
		return created;
	}

	public void setTimeCreated(long value)
	{
		if (getTimeCreated() != value)
		{
			created = value;
			getNBT().setLong("Created", created);
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

	public int getEnergy()
	{
		load();
		return energy;
	}

	public void setEnergy(int value)
	{
		if (getEnergy() != value)
		{
			energy = value;
			getNBT().setInteger("Energy", energy);
		}
	}

	public long getRechargeAt()
	{
		load();
		return rechargeAt;
	}

	public void setRechargeAt(long value)
	{
		if (getRechargeAt() != value)
		{
			rechargeAt = value;
			getNBT().setLong("RechargeAt", rechargeAt);
		}
	}

	public int getMaxEnergy()
	{
		return getMemory() * 1000;
	}

	public int getMemory()
	{
		load();
		return memory;
	}

	public void setMemory(byte value)
	{
		if (getMemory() != value)
		{
			memory = value;
			getNBT().setInteger("Memory", memory);
		}
	}

	public byte getSelected()
	{
		load();
		return selected;
	}

	public boolean setSelected(int value)
	{
		byte v = (byte) MathHelper.clamp(value, 0, 3);

		if (getSelected() != v)
		{
			selected = v;
			getNBT().setByte("Selected", selected);
			customTempData.clear();
			return true;
		}

		return false;
	}

	public int getXP()
	{
		load();
		return xp;
	}

	public void setXP(int value)
	{
		if (getXP() != value)
		{
			xp = value;
			getNBT().setInteger("XP", xp);
		}
	}

	public void addXP(World world, int xp)
	{
		setXP(getXP() + xp);
	}

	public int getNextLevelXP()
	{
		return 100;
	}

	public int getPoints()
	{
		load();
		return points;
	}

	public void setPoints(int value)
	{
		if (getPoints() != value)
		{
			points = value;
			getNBT().setInteger("Points", points);
		}
	}

	public int getUnlocked()
	{
		load();
		return unlocked;
	}

	public void setUnlocked(int value)
	{
		if (getUnlocked() != value)
		{
			unlocked = value;
			getNBT().setInteger("Unlocked", unlocked);
		}
	}

	public boolean isFunctionUnlocked(TransistorFunction function)
	{
		return !function.isEmpty() && (getUnlocked() & (1 << function.index)) != 0;
	}

	public boolean isSlotUnlocked(int index)
	{
		return index < 4 || (getUnlocked() & (1 << (index + 12))) != 0;
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
			setUnlocked(getUnlocked() | (1 << function.index));
		}
	}

	public boolean unlockSlot(int index, boolean points)
	{
		if (index < 4 || isSlotUnlocked(index))
		{
			return false;
		}
		else if (points)
		{
			int p = 1;

			if (index >= 12)
			{
				p = 2;
			}

			if (getPoints() >= p)
			{
				setPoints(getPoints() - p);
			}
			else
			{
				return false;
			}
		}

		setUnlocked(getUnlocked() | (1 << (index + 12)));
		return true;
	}

	public boolean isOverloaded(TransistorFunction function)
	{
		return false;
	}

	public TransistorFunction getFunction(int index)
	{
		if (index < 0 || index >= 16)
		{
			return TransistorFunctions.EMPTY;
		}

		if (functions[index] == null)
		{
			TransistorFunction function;

			if (getNBT().hasKey(KEYS[index], Constants.NBT.TAG_ANY_NUMERIC))
			{
				function = TransistorFunctions.get(getNBT().getByte(KEYS[index]));
			}
			else
			{
				function = TransistorFunctions.get(getNBT().getString(KEYS[index]));
			}

			functions[index] = isFunctionUnlocked(function) ? function : TransistorFunctions.EMPTY;
		}

		return functions[index];
	}

	public void setFunction(int index, TransistorFunction function)
	{
		if (index < 0 || index >= 16)
		{
			return;
		}

		if (!getFunction(index).equals(function))
		{
			functions[index] = function;
			cachedMemoryUsage = -1;

			if (!function.isEmpty())
			{
				getNBT().setByte(KEYS[index], function.index);
			}
			else
			{
				getNBT().removeTag(KEYS[index]);
			}
		}
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
		if (getTimeCreated() == -1L)
		{
			setTimeCreated(player.world.getTotalWorldTime());
		}

		getSelectedAttack().onUpdate(this, player, isSelected);

		for (int i = 0; i < 4; i++)
		{
			getPassive(i).onPassiveUpdate(this, player, isSelected);
		}

		if (getEnergy() < getMaxEnergy() && player.world.getTotalWorldTime() >= getRechargeAt())
		{
			setEnergy(Math.min(getMaxEnergy(), getEnergy() + 1000));
			setRechargeAt(player.world.getTotalWorldTime() + 200L);
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

		if (!function.isEmpty() && isFunctionUnlocked(function) && getUsedMemory() + function.memory <= getMemory())
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
		if (getPoints() >= 1 && getMemory() < 32)
		{
			setMemory((byte) (getMemory() + 1));
			setPoints(getPoints() - 1);
			return true;
		}

		return false;
	}

	public boolean canUseEnergy(int energy)
	{
		return getEnergy() > 0;
	}

	public boolean useEnergy(World world, int energy)
	{
		if (canUseEnergy(energy))
		{
			setEnergy(Math.max(0, getEnergy() - energy));
			setRechargeAt(Math.max(getRechargeAt(), world.getTotalWorldTime() + (getEnergy() <= 0 ? 600L : 200L)));
			return true;
		}

		return false;
	}
}