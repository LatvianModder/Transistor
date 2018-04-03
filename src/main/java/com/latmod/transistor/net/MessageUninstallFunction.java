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
public class MessageUninstallFunction implements IMessage
{
	public static class Handler implements IMessageHandler<MessageUninstallFunction, IMessage>
	{
		@Override
		public IMessage onMessage(MessageUninstallFunction message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = ctx.getServerHandler().player.getHeldItem(message.hand);

				if (stack.getItem() == TransistorItems.TRANSISTOR)
				{
					TransistorData.get(stack).uninstallFunction(message.index);
				}
			});

			return null;
		}
	}

	private int index;
	private EnumHand hand;

	public MessageUninstallFunction()
	{
	}

	public MessageUninstallFunction(int i, EnumHand h)
	{
		index = i;
		hand = h;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(index);
		buf.writeByte(hand.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		index = buf.readByte();
		hand = EnumHand.values()[buf.readUnsignedByte()];
	}
}