package com.latmod.transistor.client;

import com.latmod.transistor.Transistor;
import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorFunction;
import com.latmod.transistor.functions.TransistorFunctions;
import com.latmod.transistor.net.MessageSelectFunction;
import com.latmod.transistor.net.TransistorNetHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiTransistor extends GuiScreen
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(Transistor.MOD_ID, "textures/transistor_gui.png");
	private static final int LOCK_U = 312;
	private static final int LOCK_V = 33;
	private static final int LOCK_W = 10;
	private static final int LOCK_H = 14;

	private enum ButtonShape
	{
		LARGE(34, 34, 0, 0),
		SMALL(18, 18, 35, 33),
		BAR(13, 51, 54, 50);

		public final int w, h, v, iu, iv;

		ButtonShape(int _w, int _h, int _v, int _iv)
		{
			w = _w;
			h = _h;
			v = _v;
			iu = 295;
			iv = _iv;
		}
	}

	private enum BorderType
	{
		NONE(328),
		MOUSE_OVER(363),
		USED(398),
		SELECTED(433);

		public final int u;

		BorderType(int _u)
		{
			u = _u;
		}
	}

	public class Button
	{
		public int x, y;
		public final ButtonShape shape;

		public Button(int _x, int _y, ButtonShape s)
		{
			x = _x;
			y = _y;
			shape = s;
		}

		public void click(boolean left)
		{
		}

		public void addHoverText(List<String> text)
		{
		}

		public boolean isBeingUsed()
		{
			return false;
		}

		public void draw(int mouseX, int mouseY)
		{
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			mc.getTextureManager().bindTexture(TEXTURE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			addModalRectToBuffer(buffer, x + 1, y + 1, 295, shape.iv, shape.w - 2, shape.h - 2);

			BorderType type = BorderType.NONE;

			if (mouseOver(mouseX, mouseY))
			{
				type = BorderType.MOUSE_OVER;
			}
			else if (this == selectedFunction)
			{
				type = BorderType.SELECTED;
			}
			else if (isBeingUsed())
			{
				type = BorderType.USED;
			}

			addModalRectToBuffer(buffer, x, y, type.u, shape.v, shape.w, shape.h);
			tessellator.draw();
		}

		public boolean mouseOver(int mouseX, int mouseY)
		{
			return mouseX >= x && mouseX <= x + shape.w && mouseY >= y && mouseY <= y + shape.h;
		}
	}

	private class Bar extends Button
	{
		public Bar(int _x, int _y)
		{
			super(_x, _y, ButtonShape.BAR);
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

		public int getBarColor(int index, int max)
		{
			return 0;
		}

		@Override
		public void addHoverText(List<String> text)
		{
			text.add(getValue() + " / " + getMaxValue());

			if (isLocked())
			{
				text.add(TextFormatting.GOLD + I18n.format("transistor.locked"));
			}
		}

		@Override
		public void draw(int mouseX, int mouseY)
		{
			super.draw(mouseX, mouseY);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			int max = MathHelper.ceil((getValue() * 15D / (double) getMaxValue()));

			if (max > 0)
			{
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				for (int i = 0; i <= max; i++)
				{
					int col = getBarColor(i, max);

					if (col != 0)
					{
						addModalRectToBuffer(buffer, x + 1, y + 2 + (15 - i) * 3, 307, 51 + col * 3, 11, 2);
					}
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
		}
	}

	private class ButtonFunctionBase extends Button
	{
		public ButtonFunctionBase(int _x, int _y, ButtonShape s)
		{
			super(_x, _y, s);
		}

		public TransistorFunction getFunction()
		{
			return TransistorFunctions.EMPTY;
		}

		@Override
		public void addHoverText(List<String> text)
		{
			text.add(getFunction().getDisplayName());

			int p = requiredUnlockPoints();

			if (p > 0)
			{
				text.add(TextFormatting.GOLD + I18n.format("transistor.locked"));
				text.add(TextFormatting.GRAY + I18n.format("transistor.required_points") + ": " + TextFormatting.GOLD + p);
			}
		}

		@Override
		public void draw(int mouseX, int mouseY)
		{
			super.draw(mouseX, mouseY);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			if (!getFunction().isEmpty())
			{
				mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				addSpriteToBuffer(buffer, x + 1, y + 1, shape.w - 2, shape.h - 2, getFunction().sprite);
				tessellator.draw();
			}

			if (requiredUnlockPoints() > 0)
			{
				mc.getTextureManager().bindTexture(TEXTURE);
				GlStateManager.color(1F, 1F, 1F, 0.75F);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				addModalRectToBuffer(buffer, x + 1, y + 1, shape.iu, shape.iv, shape.w - 2, shape.h - 2);
				tessellator.draw();

				GlStateManager.color(1F, 1F, 1F, 1F);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				addModalRectToBuffer(buffer, x + (shape.w - LOCK_W) / 2, y + (shape.h - LOCK_H) / 2, LOCK_U, LOCK_V, LOCK_W, LOCK_H);
				tessellator.draw();
			}
		}

		public int requiredUnlockPoints()
		{
			return 0;
		}
	}

	private class ButtonFunction extends ButtonFunctionBase
	{
		private final TransistorFunction function;

		public ButtonFunction(int x, int y, int i)
		{
			super(x, y, ButtonShape.LARGE);
			function = TransistorFunctions.get(i);
		}

		@Override
		public TransistorFunction getFunction()
		{
			return function;
		}

		@Override
		public int requiredUnlockPoints()
		{
			return data.isUnlocked(function) ? 0 : function.memory;
		}

		@Override
		public boolean isBeingUsed()
		{
			return data.isFunctionInUse(function);
		}

		@Override
		public void click(boolean left)
		{
			if (left)
			{
				if (selectedFunction == null)
				{
					selectedFunction = this;
				}
				else
				{
					selectedFunction = null;
				}
			}
		}
	}

	private class ButtonAttack extends ButtonFunctionBase
	{
		private final int index;

		public ButtonAttack(int x, int y, int i)
		{
			super(x, y, ButtonShape.LARGE);
			index = i;
		}

		@Override
		public TransistorFunction getFunction()
		{
			return data.getAttack(index);
		}

		@Override
		public int requiredUnlockPoints()
		{
			return 0;
		}

		@Override
		public boolean isBeingUsed()
		{
			return data.getSelected() == index;
		}
	}

	private class ButtonUpgrade extends ButtonFunctionBase
	{
		private final int attack;
		private final int slot;

		public ButtonUpgrade(int x, int y, int a, int s)
		{
			super(x, y, ButtonShape.SMALL);
			attack = a;
			slot = s;
		}

		@Override
		public TransistorFunction getFunction()
		{
			return data.getUpgrade(attack, slot);
		}

		@Override
		public int requiredUnlockPoints()
		{
			return 0;
		}
	}

	private class ButtonPassive extends ButtonFunctionBase
	{
		private final int index;

		public ButtonPassive(int x, int y, int i)
		{
			super(x, y, ButtonShape.SMALL);
			index = i;
		}

		@Override
		public TransistorFunction getFunction()
		{
			return data.getPassive(index);
		}

		@Override
		public int requiredUnlockPoints()
		{
			return 0;
		}
	}

	private final TransistorData data;
	private final EnumHand hand;
	public int sizeX, sizeY, posX, posY;
	private final List<GuiTransistor.Button> buttons;
	public ButtonFunctionBase selectedFunction = null;

	public GuiTransistor(TransistorData d, EnumHand h)
	{
		data = d;
		hand = h;
		sizeX = 294;
		sizeY = 139;
		buttons = new ArrayList<>();
	}

	@Override
	public void initGui()
	{
		posX = (width - sizeX) / 2;
		posY = (height - sizeY) / 2;

		buttons.clear();

		for (int y = 0; y < 2; y++)
		{
			for (int x = 0; x < 8; x++)
			{
				buttons.add(new ButtonFunction(posX + 4 + x * 36, posY + 4 + y * 36, x + y * 8));
			}
		}

		for (int i = 0; i < 4; i++)
		{
			buttons.add(new ButtonAttack(posX + 70 + i * 40, posY + 81, i));
		}

		for (int i = 0; i < 8; i++)
		{
			buttons.add(new ButtonUpgrade(posX + 68 + i * 20, posY + 117, i / 2, i % 2));
		}

		for (int i = 0; i < 2; i++)
		{
			buttons.add(new ButtonPassive(posX + 48, posY + 88 + i * 20, i));
			buttons.add(new ButtonPassive(posX + 228, posY + 88 + i * 20, i + 2));
		}

		buttons.add(new Bar(posX + 6, posY + 82)
		{
			@Override
			public void addHoverText(List<String> text)
			{
				text.add(TextFormatting.DARK_AQUA + I18n.format("transistor.energy"));
				text.add(data.getEnergyStored() + " / " + data.getMaxEnergyStored());
			}

			@Override
			public int getValue()
			{
				return data.getEnergyStored();
			}

			@Override
			public int getMaxValue()
			{
				return data.getMaxEnergyStored();
			}
		});

		buttons.add(new Bar(posX + 21, posY + 82)
		{
			@Override
			public void addHoverText(List<String> text)
			{
				text.add(I18n.format("transistor.memory"));
			}

			@Override
			public int getValue()
			{
				return data.getUsedMemory();
			}

			@Override
			public int getMaxValue()
			{
				return data.getMemory();
			}
		});

		buttons.add(new Bar(posX + 260, posY + 82)
		{
			@Override
			public void addHoverText(List<String> text)
			{
				text.add(TextFormatting.GREEN + I18n.format("transistor.xp"));
				int points = data.getPoints();
				text.add(I18n.format("transistor.available_points") + ": " + (points > 0 ? TextFormatting.GREEN : TextFormatting.RED) + points);
			}

			@Override
			public int getValue()
			{
				return data.getXP();
			}

			@Override
			public int getMaxValue()
			{
				return data.getNextLevelXP();
			}
		});

		buttons.add(new Bar(posX + 275, posY + 82));
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.enableTexture2D();
		GlStateManager.color(1F, 1F, 1F, 1F);

		mc.getTextureManager().bindTexture(TEXTURE);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		addModalRectToBuffer(buffer, posX, posY, 0, 0, sizeX, sizeY);
		tessellator.draw();

		for (GuiTransistor.Button button : buttons)
		{
			button.draw(mouseX, mouseY);
		}

		for (GuiTransistor.Button button : buttons)
		{
			if (button.mouseOver(mouseX, mouseY))
			{
				List<String> text = new ArrayList<>();
				button.addHoverText(text);
				GuiUtils.drawHoveringText(text, mouseX, mouseY, width, height, width, fontRenderer);
			}
		}
	}

	private void addModalRectToBuffer(BufferBuilder buffer, int x, int y, int u, int v, int w, int h)
	{
		addModalRectToBuffer(buffer, x, y, u, v, w, h, 512, 256);
	}

	public static void addModalRectToBuffer(BufferBuilder buffer, int x, int y, int u, int v, int w, int h, int tw, int th)
	{
		float sx = 1F / (float) tw;
		float sy = 1F / (float) th;
		buffer.pos(x, y + h, 0D).tex(u * sx, (v + h) * sy).endVertex();
		buffer.pos(x + w, y + h, 0D).tex((u + w) * sx, (v + h) * sy).endVertex();
		buffer.pos(x + w, y, 0D).tex((u + w) * sx, v * sy).endVertex();
		buffer.pos(x, y, 0D).tex(u * sx, v * sy).endVertex();
	}

	public static void addSpriteToBuffer(BufferBuilder buffer, int x, int y, int w, int h, TextureAtlasSprite sprite)
	{
		buffer.pos(x, y + h, 0D).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
		buffer.pos(x + w, y + h, 0D).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
		buffer.pos(x + w, y, 0D).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
		buffer.pos(x, y, 0D).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
	}

	@Override
	protected void mouseReleased(int x, int y, int mouseButton)
	{
		super.mouseReleased(x, y, mouseButton);

		for (GuiTransistor.Button button : buttons)
		{
			if (button.mouseOver(x, y))
			{
				button.click(mouseButton == 0);
				return;
			}
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