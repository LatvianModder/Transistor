package com.latmod.transistor;

import com.google.common.collect.Multimap;
import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ItemTransistor extends Item
{
	public ItemTransistor(String id)
	{
		setRegistryName(Transistor.MOD_ID, id);
		setUnlocalizedName(Transistor.MOD_ID + "." + id);
		setCreativeTab(CreativeTabs.COMBAT);
		setMaxStackSize(1);
		setFull3D();

		addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				TransistorData data = TransistorData.get(stack);
				int energy = data.getEnergyStored();

				if (energy <= 0)
				{
					return 1F;
				}

				double e = energy / (double) data.getMaxEnergyStored();

				if (e < 0.1D)
				{
					return Minecraft.getSystemTime() % 800L >= 400L ? 0.5F : 0F;
				}

				return e < 1D ? 0.5F : 0F;
			}
		});
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
	{
		return new TransistorData(stack);
	}

	// Core functions //

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		if (world.isRemote)
		{
			Transistor.PROXY.openGui(TransistorData.get(player.getHeldItem(hand)), hand);
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
	{
		if (entity instanceof EntityPlayer)
		{
			TransistorData data = TransistorData.get(stack);
			return data.getSelectedAttack().onAttack(data, (EntityPlayer) entity);
		}

		return false;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		if (attacker instanceof EntityPlayer)
		{
			TransistorData data = TransistorData.get(stack);
			return data.getSelectedAttack().hitEntity(data, (EntityPlayer) attacker, target);
		}

		return false;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected)
	{
		if (entity instanceof EntityPlayer)
		{
			TransistorData data = TransistorData.get(stack);
			data.update((EntityPlayer) entity, isSelected);
		}
	}

	@Override
	public int getEntityLifespan(ItemStack stack, World world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entity)
	{
		entity.setEntityInvulnerable(true);
		return false;
	}

	// Block Harvesting //

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
		return TransistorData.get(stack).getSelectedAttack().canHarvestBlock(state);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		TransistorData data = TransistorData.get(stack);
		return data.getSelectedAttack().getBlockDestroySpeed(data, state);
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity)
	{
		return true;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			TransistorData data = TransistorData.get(stack);
			data.getSelectedAttack().getAttributeModifiers(data, map);
		}

		return map;
	}

	// Info //

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (isInCreativeTab(tab))
		{
			TransistorData data = TransistorData.get(new ItemStack(this));
			data.unlock(TransistorFunctions.CRASH);
			data.setAttack(0, TransistorFunctions.CRASH);
			data.setEnergyStored(data.getMaxEnergyStored());
			items.add(data.stack);

			data = TransistorData.get(new ItemStack(this));
			data.setUnlocked(0xFFFFFFFF);
			data.setMemory((byte) 32);
			data.setAttack(0, TransistorFunctions.CRASH);
			data.setAttack(1, TransistorFunctions.BREACH);
			data.setAttack(2, TransistorFunctions.PING);
			data.setAttack(3, TransistorFunctions.JAUNT);
			data.setPassive(0, TransistorFunctions.HELP);
			data.setPassive(1, TransistorFunctions.CULL);
			data.setEnergyStored(data.getMaxEnergyStored());
			items.add(data.stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		TransistorData data = TransistorData.get(stack);

		if (GuiScreen.isShiftKeyDown())
		{
			for (int i = 0; i < 4; i++)
			{
				String num = (i + 1) + ": ";

				if (i == data.getSelected())
				{
					num = TextFormatting.GREEN + num + TextFormatting.GRAY;
				}

				tooltip.add(num + data.getAttack(i).getDisplayName());

				for (int j = 0; j < 2; j++)
				{
					TransistorFunction upgrade = data.getUpgrade(i, j);

					if (!upgrade.isEmpty())
					{
						tooltip.add("   + " + upgrade.getDisplayName());
					}
				}
			}

			tooltip.add("");
		}

		tooltip.add(I18n.format("transistor.energy") + ": " + TextFormatting.AQUA + data.getEnergyStored() + TextFormatting.GRAY + " / " + TextFormatting.AQUA + data.getMaxEnergyStored());
		tooltip.add(I18n.format("transistor.memory") + ": " + TextFormatting.GOLD + data.getUsedMemory() + TextFormatting.GRAY + " / " + TextFormatting.GOLD + data.getMemory());
		tooltip.add(I18n.format("transistor.xp") + ": " + TextFormatting.GREEN + data.getXP());

		if (GuiScreen.isCtrlKeyDown())
		{
			tooltip.add("");
			tooltip.add(I18n.format("transistor.available_functions") + ": ");

			for (TransistorFunction function : TransistorFunctions.getAll())
			{
				if (data.isUnlocked(function))
				{
					tooltip.add("  " + function.getDisplayName());
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getHighlightTip(ItemStack stack, String displayName)
	{
		TransistorFunction function = TransistorData.get(stack).getSelectedAttack();

		if (!function.isEmpty())
		{
			return displayName + ": " + function.getDisplayName();
		}

		return displayName;
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		TransistorData data = TransistorData.get(stack);
		return data.getEnergyStored() > 0 && data.getEnergyStored() < data.getMaxEnergyStored();
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack)
	{
		TransistorData data = TransistorData.get(stack);
		return 1D - MathHelper.clamp(data.getEnergyStored() / (double) data.getMaxEnergyStored(), 0D, 1D);
	}
}