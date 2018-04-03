package com.latmod.transistor.client;

/**
 * @author LatvianModder
 */
public enum BorderType
{
	NONE,
	MOUSE_OVER,
	USED,
	SELECTED,
	ERROR;

	public final int u;

	BorderType()
	{
		u = 328 + ordinal() * 35;
	}
}