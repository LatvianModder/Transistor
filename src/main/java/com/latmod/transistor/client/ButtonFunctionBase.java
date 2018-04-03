package com.latmod.transistor.client;

import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.net.MessageInstallFunction;
import com.latmod.transistor.net.MessageUninstallFunction;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

/**
 * @author LatvianModder
 */
public abstract class ButtonFunctionBase extends Widget
{
	public ButtonFunctionBase(GuiTransistor g, int _x, int _y, ButtonShape s)
	{
		super(g, _x, _y, s);
	}

	public TransistorFunction getFunction()
	{
		return gui.data.getFunction(getIndex());
	}

	public abstract int getIndex();

	@Override
	public void click(boolean left)
	{
		if (getFunction().isEmpty())
		{
			if (left && gui.selectedFunction != null)
			{
				int index = getIndex();
				int func = gui.selectedFunction.function.index;

				if (gui.data.installFunction(index, func))
				{
					TransistorNetHandler.NET.sendToServer(new MessageInstallFunction(index, func, gui.hand));
					gui.selectedFunction = null;
				}
			}
		}
		else if (!left)
		{
			gui.mc.displayGuiScreen(new GuiYesNo((result, id) -> {
				gui.mc.displayGuiScreen(gui);

				if (result)
				{
					if (gui.data.uninstallFunction(getIndex()))
					{
						TransistorNetHandler.NET.sendToServer(new MessageUninstallFunction(getIndex(), gui.hand));
					}
				}
			}, I18n.format("transistor.uninstall_function_q"), "", 0));
		}
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		gui.mc.getTextureManager().bindTexture(TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addModalRectToBuffer(buffer, x + 1, y + 1, shape.iu, shape.iv, shape.w - 2, shape.h - 2);
		tessellator.draw();

		if (!getFunction().isEmpty())
		{
			gui.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addSpriteToBuffer(buffer, x + 1, y + 1, shape.w - 2, shape.h - 2, getFunction().sprite);
			tessellator.draw();
		}

		if (isLocked())
		{
			gui.mc.getTextureManager().bindTexture(TEXTURE);
			GlStateManager.color(1F, 1F, 1F, 0.75F);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addModalRectToBuffer(buffer, x + 1, y + 1, shape.iu, shape.iv, shape.w - 2, shape.h - 2);
			tessellator.draw();

			GlStateManager.color(1F, 1F, 1F, 1F);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addModalRectToBuffer(buffer, x + (shape.w - LOCK_W) / 2, y + (shape.h - LOCK_H) / 2, LOCK_U, LOCK_V, LOCK_W, LOCK_H);
			tessellator.draw();
		}

		BorderType type = BorderType.NONE;

		if (isSelected())
		{
			type = BorderType.SELECTED;
		}
		else if (mouseOver(mouseX, mouseY) && !getFunction().isEmpty())
		{
			type = BorderType.MOUSE_OVER;
		}
		else if (hasError())
		{
			type = BorderType.ERROR;
		}
		else if (isBeingUsed())
		{
			type = BorderType.USED;
		}

		gui.mc.getTextureManager().bindTexture(TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addModalRectToBuffer(buffer, x, y, type.u, shape.v, shape.w, shape.h);
		tessellator.draw();

		super.draw(mouseX, mouseY);
	}

	@Override
	public boolean isSelected()
	{
		return gui.selectedFunction != null && !gui.selectedFunction.hasError() && !isLocked() && getFunction().isEmpty();
	}

	public boolean isLocked()
	{
		return false;
	}
}