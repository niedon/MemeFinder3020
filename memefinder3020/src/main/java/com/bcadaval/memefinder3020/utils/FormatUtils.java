package com.bcadaval.memefinder3020.utils;

import java.text.DecimalFormat;

public class FormatUtils {
	
	private static String[] MEDIDAS = {"byte", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};;

	public static String formatoPeso(long bytes) {
		
		double resultado = bytes;
		
		for(int i=0; i<MEDIDAS.length;i++) {
			
			if(resultado/1024<1) {
				return String.format("%s %s", new DecimalFormat("#.##").format(resultado), MEDIDAS[i]);
			}else {
				resultado /= 1024;
			}
			
		}
		return "??? bytes";
	}
	
}
