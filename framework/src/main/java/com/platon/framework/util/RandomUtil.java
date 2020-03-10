package com.platon.framework.util;

import java.util.Random;

public class RandomUtil {
	
	static class InstanceHolder {
		final static Random	instance	= new Random();
	}
	
	public static int randInt(int n) {
		int sign = 1;
		if (n == 0)
			return 0;
		
		if (n < 0) {
			sign = -1;
			n = -n;
		}
		return InstanceHolder.instance.nextInt(n) * sign;
	}
	
	public static float randFloat() {
		return InstanceHolder.instance.nextFloat();
	}
	
	public static int randInt(int m, int n) {
		
		int base = n;
		int span = m - n;
		if (n > m) {
			base = m;
			span = n - m;
		}
		
		return base + randInt(span);
	}
}
