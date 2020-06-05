package com.bcadaval.memefinder3020.modelo.beans.xml;

import java.util.Locale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.bcadaval.memefinder3020.utils.adapter.LocaleStringXmlAdapter;

@XmlRootElement(name = "ajustes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Ajustes {

	@XmlElement(name = "locale")
	@XmlJavaTypeAdapter(value = LocaleStringXmlAdapter.class)
	private Locale locale;

	public Ajustes(Locale locale) {
		super();
		this.locale = locale;
	}
	
	public Ajustes() {
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}
