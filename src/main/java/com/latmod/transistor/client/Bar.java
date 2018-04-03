package com.latmod.transistor.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * @author LatvianModder
 */
public class Bar extends Widget
{
	public static final int CYAN = 0;
	public static final int GREEN = 1;
	public static final int YELLOW = 2;
	public static final int ORANGE = 3;
	public static final int RED = 4;

	public Bar(GuiTransistor g, int _x, int _y)
	{
		super(g, _x, _y, ButtonShape.BAR);
	}

	public int getValue()
	{
		return 0;
	}

	public int getMaxValue()
	{
		return 100;
	}

	public boolean isLocked()
	{
		return false;
	}

	public int getBars(int actualBars)
	{
		return actualBars;
	}

	public int getBarColor(int index, int bars, int actualBars)
	{
		return CYAN;
	}

	@Override
	public void addHoverText(List<String> text)
	{
		if (isLocked())
		{
			text.add(TextFormatting.GOLD + I18n.format("transistor.locked"));
		}
	}

	@Override
	public void draw(int mouseX, int mouseY)
	{
		gui.mc.getTextureManager().bindTexture(TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addModalRectToBuffer(buffer, x + 1, y + 1, shape.iu, shape.iv, shape.w - 2, shape.h - 2);
		tessellator.draw();

		int actualBars = MathHelper.ceil((getValue() * 15D / (double) getMaxValue()));
		int bars = getBars(actualBars);

		if (bars > 0)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			int b = MathHelper.clamp(bars, 0, 15);

			for (int i = 0; i <= b; i++)
			{
				addModalRectToBuffer(buffer, x + 1, y + 2 + (15 - i) * 3, 307, 51 + getBarColor(i, bars, actualBars) * 3, 11, 2);
			}

			tessellator.draw();
		}

		if (isLocked())
		{
			GlStateManager.color(1F, 1F, 1F, 0.75F);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addModalRectToBuffer(buffer, x + 1, y + 1, shape.iu, shape.iv, shape.w - 2, shape.h - 2);
			tessellator.draw();
			GlStateManager.color(1F, 1F, 1F, 1F);

			addModalRectToBuffer(buffer, x + (shape.w - LOCK_W) / 2, y + (shape.h - LOCK_H) / 2, LOCK_U, LOCK_V, LOCK_W, LOCK_H);
		}

		BorderType type = BorderType.NONE;

		if (hasError())
		{
			type = BorderType.ERROR;
		}
		else if (isBeingUsed())
		{
			type = BorderType.USED;
		}

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addModalRectToBuffer(buffer, x, y, type.u, shape.v, shape.w, shape.h);
		tessellator.draw();
	}
}