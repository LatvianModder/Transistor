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
public class MessageUnlockSlot implements IMessage
{
	public static class Handler implements IMessageHandler<MessageUnlockSlot, IMessage>
	{
		@Override
		public IMessage onMessage(MessageUnlockSlot message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = ctx.getServerHandler().player.getHeldItem(message.hand);

				if (stack.getItem() == TransistorItems.TRANSISTOR)
				{
					TransistorData.get(stack).unlockSlot(message.index, true);
				}
			});

			return null;
		}
	}

	private int index;
	private EnumHand hand;

	public MessageUnlockSlot()
	{
	}

	public MessageUnlockSlot(int i, EnumHand h)
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