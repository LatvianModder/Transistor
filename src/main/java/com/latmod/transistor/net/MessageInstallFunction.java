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
public class MessageInstallFunction implements IMessage
{
	public static class Handler implements IMessageHandler<MessageInstallFunction, IMessage>
	{
		@Override
		public IMessage onMessage(MessageInstallFunction message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = ctx.getServerHandler().player.getHeldItem(message.hand);

				if (stack.getItem() == TransistorItems.TRANSISTOR)
				{
					TransistorData.get(stack).installFunction(message.index, message.function);
				}
			});

			return null;
		}
	}

	private int index, function;
	private EnumHand hand;

	public MessageInstallFunction()
	{
	}

	public MessageInstallFunction(int i, int f, EnumHand h)
	{
		index = i;
		function = f;
		hand = h;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(index);
		buf.writeByte(function);
		buf.writeByte(hand.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		index = buf.readByte();
		function = buf.readByte();
		hand = EnumHand.values()[buf.readUnsignedByte()];
	}
}