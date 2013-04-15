/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import ch.elexis.Hub;
import ch.elexis.util.Extensions;
import ch.elexis.util.Log;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;

/**
 * Ein Verrechnet ist ein realisiertes Verrechenbar. Ein Verrechenbar wird durch die Zuordnung zu
 * einer Konsultation zu einem Verrechnet. Der Preis eines Verrechnet ist zunächst Taxpunkwert(TP)
 * mal Scale (Immer in der kleinsten Währungseinheit, also Rappen oder ggf. cent). Der effektive
 * Preis kann aber geändert werden (Rabatt etc.) Nebst VK_Scale, welche in der Schweiz dem
 * taxpunktwert entspricht, können noch externe und interne zusätzlich Skalierungen angewendet
 * werden. PrimaryScalefactor wird beispielsweise für %-Reduktionen oder Zusschläge gemäss Tarmed
 * verwendet, SecondaryScalefactor kann ein Rabatt oder ein Privatzuschschlag sein.
 * 
 * @author gerry
 * 
 */
public class Verrechnet extends PersistentObject {
	
	public static final String DETAIL = "Detail";
	private static final String SCALE2 = "Scale2";
	private static final String SCALE = "Scale";
	public static final String LEISTG_CODE = "Leistg_code";
	public static final String LEISTG_TXT = "Leistg_txt";
	public static final String KONSULTATION = "Konsultation";
	public static final String PRICE_SELLING = "VK_Preis";
	public static final String SCALE_SELLING = "VK_Scale";
	public static final String SCALE_TP_SELLING = "VK_TP";
	public static final String COST_BUYING = "EK_Kosten";
	public static final String COUNT = "Zahl";
	public static final String CLASS = "Klasse";
	public static final String USERID = "userID";
	public static final String TABLENAME = "LEISTUNGEN";
	
	public static final String VATSCALE = "vat_scale";
	
	// keep a list of all ch.elexis.VerrechnetAdjuster extensions
	private static ArrayList<IVerrechnetAdjuster> adjusters = new ArrayList<IVerrechnetAdjuster>();
	
	static {
		addMapping(TABLENAME, "Konsultation=Behandlung", LEISTG_TXT, LEISTG_CODE, CLASS, COUNT,
			COST_BUYING, SCALE_TP_SELLING, SCALE_SELLING, PRICE_SELLING, SCALE, SCALE2,
			"ExtInfo=Detail", USERID);
		
		List<IConfigurationElement> adjustersConfigurations =
			Extensions.getExtensions(IVerrechnetAdjuster.EXTENSIONPOINTID);
		for (IConfigurationElement elem : adjustersConfigurations) {
			Object o;
			try {
				o = elem.createExecutableExtension("class");
				if (o instanceof IVerrechnetAdjuster) {
					adjusters.add((IVerrechnetAdjuster) o);
				}
			} catch (CoreException e) {
				// just log the failed instantiation
				ExHandler.handle(e);
			}
		}
	}
	
	public Verrechnet(final IVerrechenbar iv, final Konsultation kons, final int zahl){
		create(null);
		TimeTool dat = new TimeTool(kons.getDatum());
		Fall fall = kons.getFall();
		int tp = iv.getTP(dat, fall);
		double factor = iv.getFactor(dat, fall);
		long preis = Math.round(tp * factor);
		set(new String[] {
			KONSULTATION, LEISTG_TXT, LEISTG_CODE, CLASS, COUNT, COST_BUYING, SCALE_TP_SELLING,
			SCALE_SELLING, PRICE_SELLING, SCALE, SCALE2, USERID
		}, new String[] {
			kons.getId(), iv.getText(), iv.getId(), iv.getClass().getName(),
			Integer.toString(zahl), iv.getKosten(dat).getCentsAsString(), Integer.toString(tp),
			Double.toString(factor), Long.toString(preis), "100", "100", Hub.actUser.getId()
		});
		if (iv instanceof Artikel) {
			((Artikel) iv).einzelAbgabe(1);
		}
		// call the adjusters
		for (IVerrechnetAdjuster adjuster : adjusters) {
			adjuster.adjust(this);
		}
	}
	
	public String getText(){
		return checkNull(get(LEISTG_TXT));
	}
	
	public void setText(String text){
		set(LEISTG_TXT, text);
	}
	
	/**
	 * Taxpunktwert auslesen
	 * 
	 * @return
	 */
	public double getTPW(){
		return checkZeroDouble(get(SCALE_SELLING));
	}
	
	/**
	 * set the primary scale factor (usually system specific or "internal" to the code system NOTE:
	 * This ist NOT identical to the multiplier or "Taxpunkt". The final price will be calculated as
	 * VK_PREIS * VK_SCALE * primaryScale * secondaryScale
	 * 
	 * @param scale
	 *            the new scale value as x.x
	 */
	public void setPrimaryScaleFactor(double scale){
		int sca = (int) Math.round(scale * 100);
		setInt(SCALE, sca);
	}
	
	/**
	 * get the prinary scale factor
	 * 
	 * @see setPrimaryScaleFactor
	 * @return the primary svcale factor as double
	 */
	public double getPrimaryScaleFactor(){
		int sca = checkZero(get(SCALE));
		if (sca == 0) {
			return 1.0;
		}
		return ((double) sca) / 100.0;
	}
	
	/**
	 * Set the secondary scale factor
	 * 
	 * @see setPromaryScaleFactor
	 * @param scale
	 *            the factor
	 */
	public void setSecondaryScaleFactor(double scale){
		int sca = (int) Math.round(scale * 100);
		setInt(SCALE2, sca);
	}
	
	/**
	 * Get the secondary scale factor
	 * 
	 * @see setPrimaryScaleFactor
	 * @return the factor
	 */
	public double getSecondaryScaleFactor(){
		int sca = checkZero(get(SCALE2));
		if (sca == 0) {
			return 1.0;
		}
		return ((double) sca) / 100.0;
	}
	
	/**
	 * Taxpunktpreis setzen
	 * 
	 * @param tp
	 */
	public void setTP(double tp){
		set(SCALE_TP_SELLING, Long.toString(Math.round(tp)));
	}
	
	/**
	 * Den effektiven Preis setzen (braucht nicht TP*Scale zu sein
	 * 
	 * @deprecated use setTP and setFactor
	 */
	@Deprecated
	public void setPreis(final Money m){
		set(PRICE_SELLING, m.getCentsAsString());
	}
	
	/**
	 * Einkaufskosten
	 */
	public Money getKosten(){
		// System.out.println(getText());
		return new Money(checkZero(get(COST_BUYING)));
	}
	
	/**
	 * Den effektiv verrechneten Preis holen (braucht nicht TP*Scale zu sein
	 * 
	 * @deprecated
	 */
	@Deprecated
	public Money getEffPreis(){
		return new Money(checkZero(get(PRICE_SELLING)));
		/*
		 * double amount=checkZero(get("VK_Preis"))*checkZero(get("Scale"))/100.0; return new
		 * Money((int)Math.round(amount));
		 */
	}
	
	/**
	 * Den Preis nach Anwendung sämtlicher SKalierungsfaktoren zurückgeben
	 * 
	 * @return
	 */
	public Money getNettoPreis(){
		
		Money brutto = getBruttoPreis();
		brutto.multiply(getPrimaryScaleFactor());
		brutto.multiply(getSecondaryScaleFactor());
		
		// call the adjusters
		for (IVerrechnetAdjuster adjuster : adjusters) {
			adjuster.adjustGetNettoPreis(this, brutto);
		}
		
		return brutto;
	}
	
	/**
	 * Den Preis nach Anwendung des Taxpunktwerts (aber ohne sonstige Skalierungen) holen
	 */
	public Money getBruttoPreis(){
		int tp = checkZero(get(SCALE_TP_SELLING));
		Konsultation k = getKons();
		Fall fall = k.getFall();
		TimeTool date = new TimeTool(k.getDatum());
		IVerrechenbar v = getVerrechenbar();
		double tpw = 1.0;
		if (v != null) { // Unknown tax system
			tpw = v.getFactor(date, fall);
		}
		return new Money((int) Math.round(tpw * tp));
	}
	
	/** Den Standardpreis holen (Ist immer TP*Scale, auf ganze Rappen gerundet) */
	
	public Money getStandardPreis(){
		IVerrechenbar v = getVerrechenbar();
		Konsultation k = getKons();
		Fall fall = k.getFall();
		TimeTool date = new TimeTool(k.getDatum());
		double factor = 1.0;
		int tp = 0;
		if (v != null) {
			factor = v.getFactor(date, fall);
			tp = v.getTP(date, fall);
		} else {
			tp = checkZero(get(SCALE_TP_SELLING));
		}
		return new Money((int) Math.round(factor * tp));
	}
	
	/** Bequemlichkeits-Shortcut für Standardbetrag setzen */
	public void setStandardPreis(){
		IVerrechenbar v = getVerrechenbar();
		Konsultation k = getKons();
		Fall fall = k.getFall();
		TimeTool date = new TimeTool(k.getDatum());
		double factor = v.getFactor(date, fall);
		int tp = v.getTP(date, fall);
		long preis = Math.round(tp * factor);
		set(new String[] {
			SCALE_SELLING, SCALE_TP_SELLING, PRICE_SELLING
		}, Double.toString(factor), Integer.toString(tp), Long.toString(preis));
	}
	
	public Konsultation getKons(){
		return Konsultation.load(get(KONSULTATION));
	}
	
	/** Wie oft wurde die Leistung bei derselben Kons. verrechnet? */
	public int getZahl(){
		return checkZero(get(COUNT));
	}
	
	public void setZahl(final int z){
		set(COUNT, Integer.toString(z));
	}
	
	public String getCode(){
		IVerrechenbar verrechenbar = getVerrechenbar();
		if (verrechenbar == null) {
			return "?";
		} else {
			return verrechenbar.getCode();
		}
	}
	
	/**
	 * Set arbitrary additional informations to a service
	 * 
	 * @param key
	 *            name of the information
	 * @param value
	 *            value of the information
	 */
	@SuppressWarnings("unchecked")
	public void setDetail(final String key, final String value){
		Map ext = getMap(DETAIL);
		if (value == null) {
			ext.remove(key);
		} else {
			ext.put(key, value);
		}
		setMap(DETAIL, ext);
		
	}
	
	/**
	 * retrieve additional information
	 * 
	 * @param key
	 *            name of the requested information
	 * @return value if the information or null if no information with that name was found
	 */
	@SuppressWarnings("unchecked")
	public String getDetail(final String key){
		Map ext = getMap(DETAIL);
		return (String) ext.get(key);
	}
	
	/**
	 * Change the count for this service or article. If it ist an Artikel, the store will be updated
	 * accordingly
	 * 
	 * @param neuAnzahl
	 *            new count this service is to be billed.
	 */
	public void changeAnzahl(int neuAnzahl){
		int vorher = getZahl();
		setZahl(neuAnzahl);
		IVerrechenbar vv = getVerrechenbar();
		if (vv instanceof Artikel) {
			Artikel art = (Artikel) vv;
			art.einzelRuecknahme(vorher);
			art.einzelAbgabe(neuAnzahl);
		}
	}
	
	/** Frage, ob dieses Verrechnet aus dem IVerrechenbar tmpl entstanden ist */
	public boolean isInstance(final IVerrechenbar tmpl){
		String[] res = new String[2];
		get(new String[] {
			CLASS, LEISTG_CODE
		}, res);
		if (tmpl.getClass().getName().equals(res[0])) {
			if (tmpl.getId().equals(res[1])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * return the IVerrechenbar this Verrechnet is based on
	 */
	public IVerrechenbar getVerrechenbar(){
		String[] res = new String[2];
		get(new String[] {
			CLASS, LEISTG_CODE
		}, res);
		try {
			return (IVerrechenbar) Hub.poFactory.createFromString(res[0] + "::" + res[1]);
		} catch (Exception ex) {
			log.log("Fehlerhafter Leistungscode " + getLabel(), Log.ERRORS);
		}
		return null;
	}
	
	@Override
	public String getLabel(){
		return checkNull(get(LEISTG_TXT));
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Verrechnet load(final String id){
		return new Verrechnet(id);
	}
	
	protected Verrechnet(){}
	
	protected Verrechnet(final String id){
		super(id);
	}
}
