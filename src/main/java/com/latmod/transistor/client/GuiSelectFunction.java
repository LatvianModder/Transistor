package com.latmod.transistor.client;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.net.MessageSelectFunction;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiSelectFunction extends GuiScreen
{
	private final TransistorData data;
	private final Button[] buttons;

	private class Button
	{
		public int x, y;
		public List<String> hover = new ArrayList<>();

		public void click()
		{
		}

		public void draw(int mouseX, int mouseY)
		{
			drawRect(x - 1, y - 1, x + 33, y + 33, mouseOver(mouseX, mouseY) ? 0xAAFFFFFF : 0x33FFFFFF);
		}

		public boolean mouseOver(int mouseX, int mouseY)
		{
			return mouseX >= x && mouseX <= x + 32 && mouseY >= y && mouseY <= y + 32;
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
			hover.add(function.getDisplayName());
		}

		@Override
		public void click()
		{
			data.setSelected(index);
			TransistorNetHandler.NET.sendToServer(new MessageSelectFunction(index));
		}

		@Override
		public void draw(int mouseX, int mouseY)
		{
			super.draw(mouseX, mouseY);
			drawRect(x, y, x + 32, y + 32, 0xAA333333);

			if (function.isEmpty())
			{
				return;
			}

			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.enableTexture2D();
			GlStateManager.color(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(function.texture);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			bufferbuilder.pos(x, y + 32, 0).tex(0, 1).endVertex();
			bufferbuilder.pos(x + 32, y + 32, 0).tex(1, 1).endVertex();
			bufferbuilder.pos(x + 32, y, 0).tex(1, 0).endVertex();
			bufferbuilder.pos(x, y, 0).tex(0, 0).endVertex();
			tessellator.draw();
		}
	}

	public GuiSelectFunction(TransistorData d)
	{
		data = d;
		buttons = new Button[8];

		for (int i = 0; i < 4; i++)
		{
			buttons[i] = new ButtonFunction(i);
		}

		buttons[4] = new Button();
		buttons[5] = new Button();
		buttons[6] = new Button();
		buttons[7] = new Button();
	}

	@Override
	public void initGui()
	{
		int cx = width / 2;
		int cy = height / 2;

		for (int i = 0; i < 4; i++)
		{
			buttons[i].x = buttons[i + 4].x = cx - 76 + i * 40;
			buttons[i].y = cy - 48;
			buttons[i + 4].y = cy + 16;
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
		for (Button button : buttons)
		{
			button.draw(mouseX, mouseY);
		}

		for (Button button : buttons)
		{
			if (button.mouseOver(mouseX, mouseY))
			{
				GuiUtils.drawHoveringText(button.hover, mouseX, mouseY, width, height, width, fontRenderer);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
	{
		for (Button button : buttons)
		{
			if (button.mouseOver(mouseX, mouseY))
			{
				button.click();
				break;
			}
		}

		mc.displayGuiScreen(null);
	}
}