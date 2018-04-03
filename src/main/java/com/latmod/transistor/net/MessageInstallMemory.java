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
public class MessageInstallMemory implements IMessage
{
	public static class Handler implements IMessageHandler<MessageInstallMemory, IMessage>
	{
		@Override
		public IMessage onMessage(MessageInstallMemory message, MessageContext ctx)
		{
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
				ItemStack stack = ctx.getServerHandler().player.getHeldItem(message.hand);

				if (stack.getItem() == TransistorItems.TRANSISTOR)
				{
					TransistorData.get(stack).installMemory();
				}
			});

			return null;
		}
	}

	private EnumHand hand;

	public MessageInstallMemory()
	{
	}

	public MessageInstallMemory(EnumHand h)
	{
		hand = h;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(hand.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		hand = EnumHand.values()[buf.readUnsignedByte()];
	}
}