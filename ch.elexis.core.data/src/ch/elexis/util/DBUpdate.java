/*******************************************************************************
 * Copyright (c) 2005-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    MEDEVIT - several contributions
 *******************************************************************************/

package ch.elexis.util;

import java.util.ArrayList;
import java.util.List;

import ch.elexis.Hub;
import ch.rgw.tools.VersionInfo;

/**
 * Änderungen der Datenbank im Rahmen eines update durchführen.
 * 
 * @author Gerry
 * 
 */
public class DBUpdate {
	
	static final String[] versions = {
		"1.3.0", "1.3.1", "1.3.2", "1.3.3", "1.3.4", "1.3.5", "1.3.6", "1.3.7", "1.3.8", "1.3.9",
		"1.3.10", "1.3.11", "1.3.12", "1.3.13", "1.4.0", "1.4.1", "1.4.2", "1.4.3", "1.4.4",
		"1.4.5", "1.4.6", "1.5.0", "1.6.0", "1.6.1", "1.6.2", "1.6.3", "1.6.4", "1.7.0", "1.7.1",
		"1.7.2", "1.8.0", "1.8.1", "1.8.2", "1.8.3", "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8",
		"1.8.9", "1.8.10", "1.8.11", "1.8.12", "1.8.13", "1.8.14", "1.8.15", "1.8.16"
	};
	static final String[] cmds =
		{
			"CREATE TABLE EIGENLEISTUNGEN(" + "ID			VARCHAR(25) primary key,"
				+ "Code		VARCHAR(20)," + "Bezeichnung VARCHAR(80)," + "EK_PREIS	CHAR(6),"
				+ "VK_PREIS	CHAR(6)," + "ZEIT		CHAR(4)	);",
			"ALTER TABLE PATIENT_ARTIKEL_JOINT DROP COLUMN PATIENTID;"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT add ID VARCHAR(25);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT add PATIENTID VARCHAR(25);"
				+ "CREATE INDEX PAJ1 ON PATIENT_ARTIKEL_JOINT (PATIENTID);",
			
			"CREATE TABLE HEAP2(" + "ID			VARCHAR(50) primary key," + "Contents   BLOB);",
			
			"ALTER TABLE FAELLE ADD EXTINFO BLOB;"
				+ "ALTER TABLE LEISTUNGEN ADD SCALE CHAR(4) DEFAULT '100';"
				+ "ALTER TABLE LEISTUNGEN ADD DETAIL BLOB;",
			
			"ALTER TABLE LEISTUNGEN ADD VK_TP CHAR(6);"
				+ "ALTER TABLE LEISTUNGEN ADD VK_SCALE CHAR(6);"
				+ "ALTER TABLE KONTAKT ADD TITEL VARCHAR(20);",
			
			"ALTER TABLE FAELLE ADD Status VARCHAR(80);"
				+ "ALTER TABLE REZEPTE ADD RpZusatz VARCHAR(80);"
				+ "ALTER TABLE AUF ADD AUFZusatz VARCHAR(80)",
			
			"ALTER TABLE REZEPTE ADD BriefID VARCHAR(25);"
				+ "ALTER TABLE AUF ADD BriefID VARCHAR(25);",
			
			"ALTER TABLE ARTIKEL ADD Name_intern VARCHAR(80);",
			
			"ALTER TABLE PATIENT_ARTIKEL_JOINT ADD REZEPTID VARCHAR(25);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD DATEFROM CHAR(8);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD DATEUNTIL CHAR(8);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD ANZAHL CHAR(3);"
				+ "CREATE INDEX PAJ2 ON PATIENT_ARTIKEL_JOINT(REZEPTID);",
			
			"ALTER TABLE REMINDERS ADD RESPONSIBLE VARCHAR(25);"
				+ "CREATE INDEX rem3 ON REMINDERS (RESPONSIBLE);"
				+ "ALTER TABLE TARMED ADD NICKNAME VARCHAR(25);",
			
			"ALTER TABLE RECHNUNGEN ADD STATUSDATUM CHAR(8);" + "CREATE TABLE USERCONFIG("
				+ "UserID		VARCHAR(25) primary key," + "Param		VARCHAR(80)," + "Value		BLOB);"
				+ "CREATE INDEX UCFG ON USERCONFIG(Param);",
			
			"ALTER TABLE USERCONFIG DROP Value;" + "ALTER TABLE USERCONFIG ADD VALUE TEXT;",
			
			"DROP TABLE USERCONFIG;" + "CREATE TABLE USERCONFIG(" + "UserID		VARCHAR(25),"
				+ "Param		VARCHAR(80)," + "Value		TEXT);"
				+ "CREATE INDEX UCFG ON USERCONFIG(Param);"
				+ "CREATE INDEX UCFG2 ON USERCONFIG(UserID)",
			
			"S1",
			
			"ALTER TABLE BRIEFE DROP format;" + "ALTER TABLE BRIEFE ADD MimeType VARCHAR(80);"
				+ "ALTER TABLE BRIEFE ADD Path TEXT;",
			
			"ALTER TABLE KONTO ADD RechnungsID VARCHAR(25);"
				+ "ALTER TABLE KONTO ADD ZahlungsID  VARCHAR(25);"
				+ "ALTER TABLE TARMED ADD GueltigVon CHAR(8);"
				+ "ALTER TABLE TARMED ADD GueltigBis CHAR(8);",
			
			"ALTER TABLE LABORWERTE ADD Flags VARCHAR(10);",
			
			"CREATE TABLE LABGROUPS( ID VARCHAR(25) primary key, name VARCHAR(30));"
				+ "CREATE TABLE LABGROUP_ITEM_JOINT(GroupID VARCHAR(25),"
				+ "ItemID VARCHAR(25), Comment TEXT );",
			
			// 1.4.4
			"ALTER TABLE REMINDERS ADD OriginID VARCHAR(25);"
				+ "CREATE TABLE REMINDERS_RESPONSIBLE_LINK(" + "ID				VARCHAR(25) primary key,"
				+ "ReminderID		VARCHAR(25)," + "ResponsibleID	VARCHAR(25)" + ");"
				+ "CREATE INDEX rrl1 on REMINDERS_RESPONSIBLE_LINK (ReminderID);"
				+ "CREATE INDEX rrl2 on REMINDERS_RESPONSIBLE_LINK (ResponsibleID);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD ExtInfo BLOB;",
			
			// 1.4.5
			"ALTER TABLE ARTIKEL ADD Klasse VARCHAR(80);",
			
			// 1.4.6
			"ALTER TABLE LABORITEMS MODIFY titel VARCHAR(80);"
				+ "ALTER TABLE LABORITEMS MODIFY kuerzel VARCHAR(80);",
			
			// 1.5.0
			"ALTER TABLE HEAP MODIFY ID VARCHAR(80);",
			
			// 1.6.0
			"ALTER TABLE HEAP ADD datum CHAR(8);"
				+ "ALTER TABLE KONTAKT ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE KONTAKT_ADRESS_JOINT ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE FAELLE ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE BEHANDLUNGEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LABORWERTE ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE ARTIKEL ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE KONTO ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LEISTUNGEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LEISTUNGSBLOCK ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE DIAGNOSEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE BEHDL_DG_JOINT ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE BRIEFE ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE RECHNUNGEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE ZAHLUNGEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE REMINDERS ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE REMINDERS_RESPONSIBLE_LINK ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE BBS ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LABORITEMS ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LABGROUPS ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE REZEPTE ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE HEAP ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE AUF ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE EIGENLEISTUNGEN ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE HEAP2 ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE HEAP2 MODIFY ID VARCHAR(80);"
				+ "ALTER TABLE HEAP2 ADD datum CHAR(8);"
				+ "ALTER TABLE TARMED ADD deleted CHAR(1) default '0';"
				+ "ALTER TABLE LABORWERTE ADD Origin VARCHAR(30);"
				+ "INSERT INTO TARMED (ID,Nickname) VALUES ('Version','1.0.1');"
				+ "CREATE TABLE LOGS(ID			VARCHAR(25) primary key," + "OID		VARCHAR(80),"
				+ "datum		CHAR(8)," + "typ		VARCHAR(20)," + "userID		VARCHAR(25),"
				+ "station	VARCHAR(40)," + "ExtInfo		BLOB);",
			
			// 1.6.1
			"CREATE TABLE XID(" + "ID			VARCHAR(25) primary key," + "deleted	CHAR(1) default '0',"
				+ "type		VARCHAR(80)," + "object		VARCHAR(25)," + "domain		VARCHAR(255),"
				+ "domain_id	VARCHAR(255)," + "quality	CHAR(1) default '0'" + ");"
				+ "CREATE INDEX XIDIDX1 on XID(domain);"
				+ "CREATE INDEX XIDIDX2 on XID(domain_id);"
				+ "CREATE INDEX XIDIDX3 on XID(object);",
			
			// 1.6.2
			"ALTER TABLE AUF ADD DatumAUZ CHAR(8);" + "ALTER TABLE ARTIKEL ADD LastUpdate CHAR(8);",
			
			// 1.6.3.
			"ALTER TABLE ARTIKEL ADD EAN VARCHAR(15);",
			
			// 1.6.4
			"ALTER TABLE HEAP ADD lastupdate CHAR(14);"
				+ "ALTER TABLE HEAP2 ADD lastupdate CHAR(14)",
			
			// 1.7.0
			"CREATE TABLE ETIKETTEN(" + "ID          VARCHAR(25) primary key,"
				+ "Image       VARCHAR(25)," + "deleted     CHAR(1) default '0',"
				+ "importance	 integer," + "Name        VARCHAR(40)," + "foreground  CHAR(6),"
				+ "background  CHAR(6)" + ");" + "CREATE INDEX ETIKETTE1 on ETIKETTEN(Name);" +
				
				"CREATE TABLE ETIKETTEN_OBJECT_LINK(" + "	obj			VARCHAR(25),"
				+ "	etikette	VARCHAR(25)" + ");"
				+ "CREATE INDEX ETIKETTE2 on ETIKETTEN_OBJECT_LINK(obj);"
				+ "CREATE INDEX ETIKETTE3 on ETIKETTEN_OBJECT_LINK(etikette);" +
				
				"CREATE TABLE DBIMAGE (" + "ID				VARCHAR(25) primary key,"
				+ "deleted		CHAR(1) default '0'," + "Datum			CHAR(8)," + "Title 			VARCHAR(80),"
				+ "Bild			BLOB" + ");" + "CREATE INDEX DBIMAGE1 on DBIMAGE(Title);",
			
			// 1.7.1
			"ALTER TABLE LABORITEMS MODIFY Einheit VARCHAR(20);"
				+ "ALTER TABLE ETIKETTEN MODIFY importance VARCHAR(7);",
			
			// 1.7.2
			"ALTER TABLE LEISTUNGEN ADD SCALE2 CHAR(4);"
				+ "ALTER TABLE ETIKETTEN ADD classes VARCHAR(255);"
				+ "ALTER TABLE LABORWERTE ADD zeit CHAR(6);",
			
			// 1.8.0
			"DROP TABLE PATIENT_GARANT_JOINT;" + "DROP TABLE PLZ;"
				+ "ALTER TABLE KONTAKT ADD lastupdate BIGINT;"
				+ "ALTER TABLE KONTAKT_ADRESS_JOINT ADD lastupdate BIGINT;"
				+ "ALTER TABLE FAELLE ADD lastupdate BIGINT;"
				+ "ALTER TABLE BEHANDLUNGEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE ARTIKEL DROP lastupdate;"
				+ "ALTER TABLE ARTIKEL ADD lastupdate BIGINT;"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT ADD lastupdate BIGINT;"
				+ "ALTER TABLE KONTO ADD lastupdate BIGINT;"
				+ "ALTER TABLE LEISTUNGEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE LEISTUNGSBLOCK ADD lastupdate BIGINT;"
				+ "ALTER TABLE EK_PREISE ADD lastupdate BIGINT;"
				+ "ALTER TABLE VK_PREISE ADD lastupdate BIGINT;"
				+ "ALTER TABLE DIAGNOSEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE BEHDL_DG_JOINT ADD lastupdate BIGINT;"
				+ "ALTER TABLE CONFIG ADD lastupdate BIGINT;"
				+ "ALTER TABLE BRIEFE ADD lastupdate BIGINT;"
				+ "ALTER TABLE RECHNUNGEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE ZAHLUNGEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE REMINDERS ADD lastupdate BIGINT;"
				+ "ALTER TABLE REMINDERS_RESPONSIBLE_LINK ADD lastupdate BIGINT;"
				+ "ALTER TABLE BBS ADD lastupdate BIGINT;"
				+ "ALTER TABLE LABORITEMS ADD lastupdate BIGINT;"
				+ "ALTER TABLE LABORWERTE ADD lastupdate BIGINT;"
				+ "ALTER TABLE LABGROUPS ADD lastupdate BIGINT;"
				+ "ALTER TABLE LABGROUP_ITEM_JOINT ADD lastupdate BIGINT;"
				+ "ALTER TABLE REZEPTE ADD lastupdate BIGINT;"
				+ "ALTER TABLE HEAP DROP lastupdate;" + "ALTER TABLE HEAP ADD lastupdate BIGINT;"
				+ "ALTER TABLE HEAP2 DROP lastupdate;" + "ALTER TABLE HEAP2 ADD lastupdate BIGINT;"
				+ "ALTER TABLE AUF ADD lastupdate BIGINT;"
				+ "ALTER TABLE EIGENLEISTUNGEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE LOGS ADD lastupdate BIGINT;"
				+ "ALTER TABLE USERCONFIG ADD lastupdate BIGINT;"
				+ "ALTER TABLE XID ADD lastupdate BIGINT;"
				+ "ALTER TABLE ETIKETTEN ADD lastupdate BIGINT;"
				+ "ALTER TABLE ETIKETTEN_OBJECT_LINK ADD lastupdate BIGINT;"
				+ "ALTER TABLE DBIMAGE ADD lastupdate BIGINT;" + "CREATE TABLE ARTIKEL_DETAILS("
				+ "ARTICLE_ID      VARCHAR(25)," + "notes           TEXT," + "image           BLOB"
				+ ");",
			
			// 1.8.1
			"ALTER TABLE AUF MODIFY Grund VARCHAR(50);"
				+ "ALTER TABLE LABORITEMS ADD billingcode VARCHAR(20);",
			
			// 1.8.2
			"ALTER TABLE PATIENT_ARTIKEL_JOINT ADD Artikel VARCHAR(80);",
			
			// 1.8.3
			"ALTER TABLE LOGS ADD deleted CHAR(1) default '0';",
			
			// 1.8.4
			"ALTER TABLE KONTAKT MODIFY EMail VARCHAR(80);",
			
			// 1.8.5
			"ALTER TABLE ARTIKEL ADD ValidFrom CHAR(8);"
				+ "ALTER TABLE ARTIKEL ADD ValidTo   CHAR(8);" + "CREATE TABLE OUTPUT_LOG("
				+ "ID				VARCHAR(25) primary key," + "lastupdate		BIGINT,"
				+ "deleted        CHAR(1) default '0'," + "ObjectID		VARCHAR(25),"
				+ "ObjectType		VARCHAR(80)," + "Datum			CHAR(8)," + "Outputter		VARCHAR(80),"
				+ "ExtInfo		BLOB);" + "create INDEX bal_i1 ON OUTPUT_LOG (ObjectID);"
				+ "ALTER TABLE DBIMAGE ADD Prefix VARCHAR(80);",
			
			// 1.8.6
			"CREATE TABLE ETIKETTEN_OBJCLASS_LINK(" + "objclass VARCHAR(80),"
				+ "sticker VARCHAR(25));"
				+ "CREATE INDEX eol1 on ETIKETTEN_OBJCLASS_LINK(objclass);",
			
			// 1.8.7
			"ALTER TABLE LOGS MODIFY station VARCHAR(40);",
			
			// 1.8.8
			"ALTER TABLE KONTAKT_ADRESS_JOINT MODIFY Bezug VARCHAR(80);",
			
			// 1.8.9
			"ALTER TABLE LABORITEMS ADD EXPORT VARCHAR(100);",
			
			// 1.8.10
			// Gerry Weirich in einem Mail vom 26.06.2011
			// In früheren Elexis-Versionen wurden Formeln direkt im Feld abgelegt,
			// aktuell sind es Scripts (Also Objekte vom typ ch.elexis.data.Script)
			// und das Feld muss nur noch den Namen des Scripts halten.
			"DELETE FROM LABORITEMS where length(RefFrauOrTx) > 256;"
				+ "ALTER TABLE LABORITEMS MODIFY RefFrauOrTx VARCHAR(256);"
				+ "ALTER TABLE LABORITEMS MODIFY RefMann     VARCHAR(256);",
			
			// 1.8.11
			// M. Descher (9.9.2011)
			// Anmerkung: Der JPA Standard reserviert für jeden String-Typ ein
			// Element vom Typ VARCHAR(255), da heutige DB Systeme keine großen
			// Einschränkungen dadurch tragen. Es werden daher ab 9.9.2011 per
			// Beschluss Release Meeting 14 sämtliche (String) Felder, falls größer
			// benötigt, standardmässig auf 255 gesetzt.
			"ALTER TABLE KONTAKT MODIFY Bezeichnung1 VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Bezeichnung2 VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Bezeichnung3 VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Strasse VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Ort VARCHAR(255);"
				// (please try Llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogoch :-)
				+ "ALTER TABLE KONTAKT MODIFY Email VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Website VARCHAR(255);"
				+ "ALTER TABLE KONTAKT MODIFY Titel VARCHAR(255);"
				+ "ALTER TABLE KONTAKT ADD TitelSuffix VARCHAR(255);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT MODIFY Dosis VARCHAR(255);"
				+ "ALTER TABLE PATIENT_ARTIKEL_JOINT MODIFY Bemerkung VARCHAR(255);",
			
			// 1.8.12
			// M. Descher (23.3.2012)
			// Due to Ticket #712 - Insufficient length of multiplicator
			"ALTER TABLE VK_PREISE MODIFY MULTIPLIKATOR VARCHAR(8);"
				+ "ALTER TABLE EK_PREISE MODIFY MULTIPLIKATOR VARCHAR(8);",
			
			// 1.8.13
			// M. Descher (30.3.2012)
			// Due to Ticket #838 - Leistungen, "teurer" als 9999.99 Fr.
			"ALTER TABLE LEISTUNGEN MODIFY VK_TP VARCHAR(8);"
				+ "ALTER TABLE LEISTUNGEN MODIFY VK_SCALE VARCHAR(8);",
			
			// 1.8.14
			// M. Descher (16.4.2012)
			// Due to Ticket #917 - Spalte OID in Tabelle Logs zu kurz
			"ALTER TABLE LOGS MODIFY OID VARCHAR(255);"
				+ "ALTER TABLE LOGS MODIFY station VARCHAR(255);",
			// 1.8.15
			// N. Giger 18.07.2012
			// Anpassung für Import ODDB-YAML/CSV. Brauche ATC_code Feld
			"ALTER TABLE ARTIKEL ADD ATC_code VARCHAR(255);",
			// 1.8.16
			// T. Huster 29.08.2012
			// Add userID for needed statistics
			"ALTER TABLE LEISTUNGEN ADD userID VARCHAR(25);"
		};
	
	static Log log = Log.get("DBUpdate");
	
	static VersionInfo vi;
	
	/**
	 * Diese Methode erledigt Datenbankänderungen, die im Rahmen eines Updates nötig sind Versions
	 * enthält eine Versionsliste, cmds ein Kommando für jede dieser Versionen. Ein Kommando ist
	 * entweder
	 * <ul>
	 * <li>direkt ein SQL-Befehl,
	 * <li>eine ; getrennte Liste von SQL-Befehlen</li>
	 */
	public static void doUpdate(){
		String dbv = Hub.globalCfg.get("dbversion", null);
		if (dbv == null) {
			log.log("Kann keine Version lesen", Log.ERRORS);
			SWTHelper.alert("Fataler Fehler bei Datenbank-Update",
				"Kann keine Versionsinformation lesen. Abbruch");
			System.exit(0);
		} else {
			vi = new VersionInfo(dbv);
		}
		
		List<String> sqlStrings = new ArrayList<String>();
		for (int i = 0; i < versions.length; i++) {
			if (vi.isOlder(versions[i])) {
				String[] cmd = cmds[i].split(";");
				for (int cmdIdx = 0; cmdIdx < cmd.length; cmdIdx++)
					sqlStrings.add(cmd[cmdIdx]);
			}
		}
		// create log message
		log.log("Start DBUpdate from Version " + dbv + " to Version "
			+ versions[versions.length - 1], Log.INFOS);
		
		SqlWithUiRunner runner =
			new SqlWithUiRunner(sqlStrings.toArray(new String[0]), Hub.PLUGIN_ID);
		// update version if all updates are successful
		if (runner.runSql()) {
			Hub.globalCfg.set("dbversion", Hub.DBVersion);
			Hub.globalCfg.set("ElexisVersion", Hub.Version);
			Hub.globalCfg.flush();
			// create log message
			log.log("DBUpdate from Version " + dbv + " to Version " + versions[versions.length - 1]
				+ " successful.", Log.INFOS);
		} else {
			log.log("DBUpdate from Version " + dbv + " to Version " + versions[versions.length - 1]
				+ " failed.", Log.ERRORS);
		}
	}
}
