package com.otp.login.web.auth.token;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class GenerateOTPUtil {
	
	public static String generateToken(String email, int pin) {

		int emailLength = email.length();
		int[] code = new int[6];
		TimeZone timeZone1 = TimeZone.getTimeZone("America/Los_Angeles");
		Calendar calendarAMS = new GregorianCalendar(timeZone1);

		DecimalFormat mFormat = new DecimalFormat("00");

		int genrated_secret_code = (pin + calendarAMS.get(Calendar.MINUTE)) % 29;  
		String currentHour = mFormat.format(calendarAMS.get(Calendar.HOUR_OF_DAY)+genrated_secret_code);
		int minute = 0;
		if(calendarAMS.get(Calendar.SECOND) > 49){
			minute = calendarAMS.get(Calendar.MINUTE) +1;
		}
		
		String currentMinute = mFormat.format(minute + (emailLength % 27));
		//String currentMinute = mFormat.format(calendarAMS.get(Calendar.MINUTE) + (emailLength % 27));
		// String currentDay =
		// mFormat.format(calendarAMS.get(Calendar.DAY_OF_MONTH));
		// int currentMonth = Integer.parseInt(String.format("%02d:%02d",
		// calendarAMS.get(Calendar.MONTH)));
		int[] hh = convertIntToArray(currentHour);
		int hourPlusMinut = calendarAMS.get(Calendar.HOUR_OF_DAY) + calendarAMS.get(Calendar.MINUTE);
		int[] ss = convertIntToArray(String.valueOf(hourPlusMinut));
		int[] mm = convertIntToArray(currentMinute);

		if (emailLength % 2 == 0) {
			code[0] = hh[0];
			code[2] = hh[1];
			code[3] = mm[0];
			code[5] = mm[1];
			code[1] = ss[0];
			code[4] = ss[1];
		} else if (genrated_secret_code%2 == 0) {
			code[0] = mm[0];
			code[2] = ss[0];
			code[3] = hh[1];
			code[5] = mm[1];
			code[1] = hh[0];
			code[4] = ss[1];
		}else
		{
			code[0] = mm[1];
			code[2] = ss[0];
			code[3] = hh[1];
			code[5] = mm[0];
			code[1] = hh[0];
			code[4] = ss[1];
			
		}

		
		return printArray(code);

	}

	private static int[] convertIntToArray(String valueToConvert) {
		int[] intArray = new int[2];
		int strLength = valueToConvert.length();
		for (int i = 0; i < strLength; i++) {
			if (!Character.isDigit(valueToConvert.charAt(i))) {
				System.out.println("Contains an invalid digit");
				break;
			}
			intArray[i] = Integer.parseInt(String.valueOf(valueToConvert.charAt(i)));
		}
		// System.out.println(Arrays.toString(intArray));
		return intArray;
	}

	private static String printArray(int[] anArray) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < anArray.length; i++) {
			sb.append(anArray[i]);
		}
		return sb.toString();
	}
	

}
