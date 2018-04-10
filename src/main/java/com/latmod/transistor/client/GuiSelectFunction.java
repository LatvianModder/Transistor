package com.latmod.transistor.client;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.net.MessageSelectFunction;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiSelectFunction extends GuiScreen
{
	public static class Button
	{
		public int x, y, w = 32, h = 32;

		public void click()
		{
		}

		public void addHoverText(List<String> text)
		{
		}

		public void draw(int mouseX, int mouseY)
		{
			drawRect(x - 1, y - 1, x + w + 1, y + h + 1, mouseOver(mouseX, mouseY) ? 0xAAFFFFFF : 0x33FFFFFF);
			drawRect(x, y, x + w, y + h, 0xFF000000);
		}

		public boolean mouseOver(int mouseX, int mouseY)
		{
			return mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;
		}
	}

	private class ButtonFunction extends Button
	{
		private final int index;
		private final TransistorFunction function;

		private ButtonFunction(int i)
		{
			index = i;
			function = data.getAttack(index);
		}

		@Override
		public void click()
		{
			data.setSelected(index);
			TransistorNetHandler.NET.sendToServer(new MessageSelectFunction(index, hand));
		}

		@Override
		public void addHoverText(List<String> text)
		{
			text.add(function.getDisplayName());
		}

		@Override
		public void draw(int mouseX, int mouseY)
		{
			super.draw(mouseX, mouseY);

			drawRect(x, y, x + w, y + h, 0xAA333333);

			if (data.getSelected() == index)
			{
				drawRect(x - 1, y - 1, x + w + 1, y + h + 1, 0xFFFDB301);
			}

			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.enableTexture2D();
			GlStateManager.color(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(function.texture);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Widget.addFullRectToBuffer(buffer, x, y, w, h);
			tessellator.draw();

			if (mouseOver(mouseX, mouseY) && (Minecraft.getSystemTime() - openedAt) / 250D >= 1D)
			{
				drawRect(x, y, x + w, y + h, 0x33FFFFFF);
			}
		}
	}

	private class ButtonConfig extends Button
	{
		@Override
		public void click()
		{
			mc.displayGuiScreen(new GuiTransistor(hand));
		}

		@Override
		public void addHoverText(List<String> text)
		{
			text.add(I18n.format("transistor.configure"));
		}

		@Override
		public void draw(int mouseX, int mouseY)
		{
			super.draw(mouseX, mouseY);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.scale(2, 2, 2);
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.enableDepth();
			itemRender.renderItemAndEffectIntoGUI(mc.player, mc.player.getHeldItem(hand), 0, 0);
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
		}
	}

	private final TransistorData data;
	private final EnumHand hand;
	private final List<Button> buttons;
	private final long openedAt;
	private double modifier;

	public GuiSelectFunction(TransistorData d, EnumHand h)
	{
		data = d;
		hand = h;
		buttons = new ArrayList<>();
		openedAt = Minecraft.getSystemTime();

		buttons.add(new ButtonConfig());

		for (int i = 0; i < 4; i++)
		{
			ButtonFunction b = new ButtonFunction(i);

			if (!b.function.isEmpty())
			{
				buttons.add(b);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		modifier = Math.min((Minecraft.getSystemTime() - openedAt) / 250D, 1D);
		modifier = modifier * modifier;

		int cx = width / 2;
		int cy = height / 2;

		for (int i = 0; i < buttons.size(); i++)
		{
			double d = i * Math.PI * 2D / (double) buttons.size() - Math.PI * ((1D - modifier) + 1D) / 2D;
			buttons.get(i).x = cx + (int) (Math.cos(d) * 60D * modifier - 16D);
			buttons.get(i).y = cy + (int) (Math.sin(d) * 60D * modifier - 16D);
		}

		for (Button button : buttons)
		{
			button.draw(mouseX, mouseY);
		}

		if (modifier >= 1D)
		{
			for (Button button : buttons)
			{
				if (button.mouseOver(mouseX, mouseY))
				{
					List<String> text = new ArrayList<>();
					button.addHoverText(text);
					GuiUtils.drawHoveringText(text, mouseX, mouseY, width, height, width, fontRenderer);
				}
			}
		}
	}

	@Override
	protected void mouseReleased(int x, int y, int mouseButton)
	{
		super.mouseReleased(x, y, mouseButton);

		mc.displayGuiScreen(null);

		if (mc.currentScreen == null)
		{
			mc.setIngameFocus();
		}

		if (modifier >= 1D)
		{
			for (Button button : buttons)
			{
				if (button.mouseOver(x, y))
				{
					button.click();
					return;
				}
			}
		}
	}
}