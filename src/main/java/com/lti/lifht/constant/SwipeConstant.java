package com.lti.lifht.constant;

import java.util.HashMap;
import java.util.Map;

public class SwipeConstant {

	// In or Out - space intentional
	public static final String ENTRY = " Entry";
	public static final String EXIT = " Exit";

	// Doors / Gates
	public static final String DOOR_MD = "Apple Main Door Door";
	public static final String DOOR_VC = "Apple VC Room-1 Door";
	public static final String DOOR_BP = "Apple BP Room -1 Door";
	public static final String DOOR_SR = "Apple Server Room Door";
	public static final String DOOR_TR = "Apple Training Room Door";
	public static final String DOOR_T1 = "Apple Turnstile - 1";
	public static final String DOOR_TW = "Apple Turnstile WG";
	public static final String DOOR_T2 = "Apple Turnstile - 2";
	public static final String DOOR_TS = "Apple Turnstile";

	// Event Codes
	public static final int DENIED1 = 199;
	public static final int TIMEOUT = 200;
	public static final int GRANTED1 = 201;
	public static final int GRANTED2 = 203;
	public static final int UNKNOWN = 204;
	public static final int DENIED2 = 205;
	public static final int EXPIRED = 207;
	public static final int DENIED3 = 213;
	public static final int ANTIPASS = 215;
	public static final int TRACED = 234;

	// Event-validity map
	public static final Map<Integer, Boolean> VALIDATE_ENTRY = new HashMap<>();
	static {
		VALIDATE_ENTRY.put(DENIED1, false);
		VALIDATE_ENTRY.put(TIMEOUT, true);
		VALIDATE_ENTRY.put(GRANTED1, true);
		VALIDATE_ENTRY.put(GRANTED2, true);
		VALIDATE_ENTRY.put(UNKNOWN, false);
		VALIDATE_ENTRY.put(DENIED2, false);
		VALIDATE_ENTRY.put(EXPIRED, false);
		VALIDATE_ENTRY.put(DENIED3, false);
		VALIDATE_ENTRY.put(ANTIPASS, false);
		VALIDATE_ENTRY.put(TRACED, false);
	};

}
