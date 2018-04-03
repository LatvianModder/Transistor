package com.latmod.transistor.client;

import com.latmod.transistor.Transistor;
import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.TransistorItems;
import com.latmod.transistor.functions.TransistorFunctions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Transistor.MOD_ID, value = Side.CLIENT)
public class TransistorClientEventHandler
{
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event)
	{
		for (TransistorFunction function : TransistorFunctions.getAll())
		{
			function.sprite = event.getMap().registerSprite(new ResourceLocation(Transistor.MOD_ID, "functions/" + function.toString()));
		}
	}

	@SubscribeEvent
	public static void onWorldRender(RenderWorldLastEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

		if (stack.getItem() != TransistorItems.TRANSISTOR)
		{
			stack = player.getHeldItem(EnumHand.OFF_HAND);
		}

		if (stack.getItem() == TransistorItems.TRANSISTOR)
		{
			TransistorData data = TransistorData.get(stack);
			data.getSelectedAttack().onWorldRender(data, event.getPartialTicks());
		}
	}
}