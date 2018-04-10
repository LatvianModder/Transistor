package com.latmod.transistor.client;

import com.latmod.transistor.Transistor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author LatvianModder
 */
public class Widget
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Transistor.MOD_ID, "textures/transistor_gui.png");
	public static final int LOCK_U = 312;
	public static final int LOCK_V = 33;
	public static final int LOCK_W = 10;
	public static final int LOCK_H = 14;

	public final GuiTransistor gui;
	public int x, y;
	public final ButtonShape shape;

	public Widget(GuiTransistor g, int _x, int _y, ButtonShape s)
	{
		gui = g;
		x = _x;
		y = _y;
		shape = s;
	}

	public void click()
	{
	}

	public void addHoverText(List<String> text)
	{
	}

	public boolean isSelected()
	{
		return false;
	}

	public boolean isBeingUsed()
	{
		return false;
	}

	public boolean hasError()
	{
		return false;
	}

	public void draw(int mouseX, int mouseY)
	{
	}

	public boolean mouseOver(int mouseX, int mouseY)
	{
		return mouseX >= x && mouseX <= x + shape.w && mouseY >= y && mouseY <= y + shape.h;
	}

	public static void addModalRectToBuffer(BufferBuilder buffer, int x, int y, int u, int v, int w, int h)
	{
		addModalRectToBuffer(buffer, x, y, u, v, w, h, 512, 256);
	}

	public static void addModalRectToBuffer(BufferBuilder buffer, int x, int y, int u, int v, int w, int h, int tw, int th)
	{
		float sx = 1F / (float) tw;
		float sy = 1F / (float) th;
		buffer.pos(x, y + h, 0).tex(u * sx, (v + h) * sy).endVertex();
		buffer.pos(x + w, y + h, 0).tex((u + w) * sx, (v + h) * sy).endVertex();
		buffer.pos(x + w, y, 0).tex((u + w) * sx, v * sy).endVertex();
		buffer.pos(x, y, 0).tex(u * sx, v * sy).endVertex();
	}

	public static void addFullRectToBuffer(BufferBuilder buffer, int x, int y, int w, int h)
	{
		buffer.pos(x, y + h, 0).tex(0, 1).endVertex();
		buffer.pos(x + w, y + h, 0).tex(1, 1).endVertex();
		buffer.pos(x + w, y, 0).tex(1, 0).endVertex();
		buffer.pos(x, y, 0).tex(0, 0).endVertex();
	}
}