package com.latmod.transistor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
				return TransistorData.get(stack).getMode();
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
		if (hand == EnumHand.MAIN_HAND)
		{
			if (player.isSneaking())
			{
				if (!world.isRemote)
				{
					TransistorData.get(player.getHeldItem(hand)).selectNext();
				}
			}
			else if (world.isRemote)
			{
				Transistor.PROXY.openGui(TransistorData.get(player.getHeldItem(hand)));
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase living, ItemStack stack)
	{
		TransistorData data = TransistorData.get(stack);
		return data.getSelectedAttack().onAttack(data, living);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		return true;
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
		return false;
	}

	// Block Harvesting //

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
		return TransistorData.get(stack).isAttackSelected(TransistorFunctions.CRACK);
	}

	@Override
	public float getDestroySpeed(ItemStack stack, IBlockState state)
	{
		TransistorData data = TransistorData.get(stack);

		if (data.isAttackSelected(TransistorFunctions.CRACK))
		{
			return 10F;
		}

		return 0F;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity)
	{
		return true;
	}

	// Info //

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

		if (GuiScreen.isShiftKeyDown())
		{
			tooltip.add("");
			tooltip.add(I18n.format("transistor.available_functions") + ": ");
			for (TransistorFunction function : data.getAvailable())
			{
				tooltip.add("  " + function.getDisplayName());
			}
		}
	}

	@Override
	public String getHighlightTip(ItemStack stack, String displayName)
	{
		TransistorFunction function = TransistorData.get(stack).getSelectedAttack();

		if (!function.isEmpty())
		{
			return displayName + ": " + function.getDisplayName();
		}

		return displayName;
	}
}