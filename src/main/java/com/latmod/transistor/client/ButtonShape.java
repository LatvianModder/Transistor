package com.latmod.transistor.client;

/**
 * @author LatvianModder
 */
public enum ButtonShape
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