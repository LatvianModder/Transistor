package com.latmod.transistor.client;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.TransistorItems;
import com.latmod.transistor.functions.TransistorFunctions;
import com.latmod.transistor.net.MessageSelectFunction;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiTransistor extends GuiScreen
{
	public TransistorData data;
	public final EnumHand hand;
	public int sizeX, sizeY, posX, posY;
	private final List<Widget> widgets;
	public TransistorFunction selectedFunction = TransistorFunctions.EMPTY;

	public GuiTransistor(EnumHand h)
	{
		hand = h;
		sizeX = 294;
		sizeY = 139;
		widgets = new ArrayList<>();
	}

	@Override
	public void initGui()
	{
		posX = (width - sizeX) / 2;
		posY = (height - sizeY) / 2;

		widgets.clear();

		for (int y = 0; y < 2; y++)
		{
			for (int x = 0; x < 8; x++)
			{
				widgets.add(new ButtonFunction(this, posX + 4 + x * 36, posY + 4 + y * 36, x + y * 8));
			}
		}

		for (int i = 0; i < 4; i++)
		{
			widgets.add(new ButtonAttack(this, posX + 70 + i * 40, posY + 81, i));
		}

		for (int i = 0; i < 8; i++)
		{
			widgets.add(new ButtonUpgrade(this, posX + 68 + i * 20, posY + 117, i / 2, i % 2));
		}

		for (int i = 0; i < 2; i++)
		{
			widgets.add(new ButtonPassive(this, posX + 48, posY + 88 + i * 20, i));
			widgets.add(new ButtonPassive(this, posX + 228, posY + 88 + i * 20, i + 2));
		}

		widgets.add(new BarMemory(this, posX + 6, posY + 82));
		widgets.add(new BarEnergy(this, posX + 21, posY + 82));
		widgets.add(new BarXP(this, posX + 260, posY + 82));
		widgets.add(new Bar(this, posX + 275, posY + 82));
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		ItemStack stack = mc.player.getHeldItem(hand);

		if (stack.getItem() != TransistorItems.TRANSISTOR)
		{
			mc.displayGuiScreen(null);
			return;
		}

		data = TransistorData.get(stack);

		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableTexture2D();
		GlStateManager.color(1F, 1F, 1F, 1F);

		mc.getTextureManager().bindTexture(Widget.TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		Widget.addModalRectToBuffer(buffer, posX, posY, 0, 0, sizeX, sizeY);
		tessellator.draw();

		for (Widget widget : widgets)
		{
			widget.draw(mouseX, mouseY);
		}

		if (!selectedFunction.isEmpty())
		{
			mc.getTextureManager().bindTexture(selectedFunction.texture);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			Widget.addFullRectToBuffer(buffer, mouseX - 8, mouseY - 8, 16, 16);
			tessellator.draw();
		}
		else
		{
			for (Widget widget : widgets)
			{
				if (widget.mouseOver(mouseX, mouseY))
				{
					List<String> text = new ArrayList<>();
					widget.addHoverText(text);
					GuiUtils.drawHoveringText(text, mouseX, mouseY, width, height, width, fontRenderer);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int mouseButton) throws IOException
	{
		super.mouseClicked(x, y, mouseButton);
		boolean s = !selectedFunction.isEmpty();

		for (Widget widget : widgets)
		{
			if (widget.mouseOver(x, y))
			{
				widget.click();
				break;
			}
		}

		if (s && !selectedFunction.isEmpty())
		{
			selectedFunction = TransistorFunctions.EMPTY;
		}
	}

	@Override
	protected void keyTyped(char c, int key)
	{
		if (key == Keyboard.KEY_ESCAPE || mc.gameSettings.keyBindInventory.isActiveAndMatches(key))
		{
			mc.displayGuiScreen(null);

			if (mc.currentScreen == null)
			{
				mc.setIngameFocus();
			}
		}
		else if (key >= Keyboard.KEY_1 && key <= Keyboard.KEY_4)
		{
			data.setSelected(key - Keyboard.KEY_1);
			TransistorNetHandler.NET.sendToServer(new MessageSelectFunction(key - Keyboard.KEY_1, hand));
		}
	}
}