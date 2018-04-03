package com.latmod.transistor.net;

import com.latmod.transistor.TransistorData;
import com.latmod.transistor.TransistorItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * @author LatvianModder
 */
public class MessageSelectFunction implements IMessage
{
	public static class Handler implements IMessageHandler<MessageSelectFunction, IMessage>
	{
		@Override
		public IMessage onMessage(MessageSelectFunction message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = ctx.getServerHandler().player.getHeldItem(message.hand);

				if (stack.getItem() == TransistorItems.TRANSISTOR)
				{
					TransistorData.get(stack).setSelected(message.selected);
				}
			});

			return null;
		}
	}

	private int selected;
	private EnumHand hand;

	public MessageSelectFunction()
	{
	}

	public MessageSelectFunction(int s, EnumHand h)
	{
		selected = s;
		hand = h;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(selected);
		buf.writeByte(hand.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		selected = buf.readUnsignedByte();
		hand = EnumHand.values()[buf.readUnsignedByte()];
	}
}