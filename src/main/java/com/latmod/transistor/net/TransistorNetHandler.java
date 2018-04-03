package com.latmod.transistor.net;

import com.latmod.transistor.Transistor;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
public class TransistorNetHandler
{
	public static final SimpleNetworkWrapper NET = new SimpleNetworkWrapper(Transistor.MOD_ID);

	public static void init()
	{
		NET.registerMessage(new MessageSelectFunction.Handler(), MessageSelectFunction.class, 0, Side.SERVER);
		NET.registerMessage(new MessageInstallFunction.Handler(), MessageInstallFunction.class, 1, Side.SERVER);
		NET.registerMessage(new MessageUninstallFunction.Handler(), MessageUninstallFunction.class, 2, Side.SERVER);
		NET.registerMessage(new MessageUnlockSlot.Handler(), MessageUnlockSlot.class, 3, Side.SERVER);
		NET.registerMessage(new MessageInstallMemory.Handler(), MessageInstallMemory.class, 4, Side.SERVER);
	}
}