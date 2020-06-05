package com.bcadaval.memefinder3020.utils.adapter;

import java.util.Locale;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LocaleStringXmlAdapter extends XmlAdapter<String, Locale> {

	@Override
	public String marshal(Locale loc) {
		return loc==null ? null : loc.getLanguage();
	}
	
	@Override
	public Locale unmarshal(String str) {
		return str==null || str.trim().isEmpty() ? null : new Locale(str);
	}

}
