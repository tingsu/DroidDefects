/*
 *  Copyright 2011 James Stapleton
 * 
 *  This file is part of PasswordMaker Pro For Android.
 *
 *  PasswordMaker Pro For Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  PasswordMaker Pro For Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PasswordMaker Pro For Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.passwordmaker.android;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.passwordmaker.android.LeetConverter.LeetLevel;
import org.passwordmaker.android.PwmProfile.UrlComponents;

public class PasswordMaker {
	private static Pattern urlRegex = Pattern.compile("([^:\\/\\/]*:\\/\\/)?([^:\\/]*)([^#]*)");
	PwmProfile profile = new PwmProfile();

	public PasswordMaker() {}

	public PasswordMaker(PwmProfile profile) {
		this.profile = profile;
	}

	public void setProfile(PwmProfile selProfile) {
		profile = selProfile;
	}

	public PwmProfile getProfile() {
		return profile;
	}

	public boolean matchesPasswordHash(String masterPassword) {
		if ( ! profile.hasPasswordHash() ) {
			return true;
		}
		String testPassHash = generatePassword(profile.getPasswordSalt(), masterPassword);
		return testPassHash.equals(profile.getCurrentPasswordHash());
	}
    /** Parsed from: change #<a href="https://github.com/bitboxer/chrome-passwordmaker/blob/4000f7ff7da1aa709b4a079dde5728f0e96cb887/javascript/settings.js#L12">4000f7ff7da1aa709b4a079dde5728f0e96cb887</a> from <a href="https://github.com/bitboxer/chrome-passwordmaker">chrome-passwordmaker</a>
     */
    private static Set<String> TLD = new HashSet<String>(Arrays.asList(
            "aland.fi", "wa.edu.au", "nsw.edu.au", "vic.edu.au", "csiro.au", "conf.au", "info.au", "oz.au",
            "telememo.au", "sa.edu.au", "nt.edu.au", "tas.edu.au", "act.edu.au", "wa.gov.au", "nsw.gov.au",
            "vic.gov.au", "qld.gov.au", "sa.gov.au", "tas.gov.au", "nt.gov.au", "act.gov.au", "archie.au", "edu.au",
            "gov.au", "id.au", "org.au", "asn.au", "net.au", "com.au", "qld.edu.au", "com.bb", "net.bb", "org.bb",
            "gov.bb", "agr.br", "am.br", "art.br", "edu.br", "com.br", "coop.br", "esp.br", "far.br", "fm.br",
            "g12.br", "gov.br", "imb.br", "ind.br", "inf.br", "mil.br", "net.br", "org.br", "psi.br", "rec.br",
            "srv.br", "tmp.br", "tur.br", "tv.br", "etc.br", "adm.br", "adv.br", "arq.br", "ato.br", "bio.br",
            "bmd.br", "cim.br", "cng.br", "cnt.br", "ecn.br", "eng.br", "eti.br", "fnd.br", "fot.br", "fst.br",
            "ggf.br", "jor.br", "lel.br", "mat.br", "med.br", "mus.br", "not.br", "ntr.br", "odo.br", "ppg.br",
            "pro.br", "psc.br", "qsl.br", "slg.br", "trd.br", "vet.br", "zlg.br", "nom.br", "ab.ca", "bc.ca", "mb.ca",
            "nb.ca", "nf.ca", "nl.ca", "ns.ca", "nt.ca", "nu.ca", "on.ca", "pe.ca", "qc.ca", "sk.ca", "yk.ca", "com.cd",
            "net.cd", "org.cd", "ac.cn", "com.cn", "edu.cn", "gov.cn", "net.cn", "org.cn", "ah.cn", "bj.cn", "cq.cn",
            "fj.cn", "gd.cn", "gs.cn", "gz.cn", "gx.cn", "ha.cn", "hb.cn", "he.cn", "hi.cn", "hl.cn", "hn.cn", "jl.cn",
            "js.cn", "jx.cn", "ln.cn", "nm.cn", "nx.cn", "qh.cn", "sc.cn", "sd.cn", "sh.cn", "sn.cn", "sx.cn", "tj.cn",
            "xj.cn", "xz.cn", "yn.cn", "zj.cn", "co.ck", "org.ck", "edu.ck", "gov.ck", "net.ck", "ac.cr", "co.cr",
            "ed.cr", "fi.cr", "go.cr", "or.cr", "sa.cr", "eu.int", "ac.in", "co.in", "edu.in", "firm.in", "gen.in",
            "gov.in", "ind.in", "mil.in", "net.in", "org.in", "res.in", "ac.id", "co.id", "or.id", "net.id", "web.id",
            "sch.id", "go.id", "mil.id", "war.net.id", "ac.nz", "co.nz", "cri.nz", "gen.nz", "geek.nz", "govt.nz",
            "iwi.nz", "maori.nz", "mil.nz", "net.nz", "org.nz", "school.nz", "aid.pl", "agro.pl", "atm.pl", "auto.pl",
            "biz.pl", "com.pl", "edu.pl", "gmina.pl", "gsm.pl", "info.pl", "mail.pl", "miasta.pl", "media.pl", "nil.pl",
            "net.pl", "nieruchomosci.pl", "nom.pl", "pc.pl", "powiat.pl", "priv.pl", "realestate.pl", "rel.pl",
            "sex.pl", "shop.pl", "sklep.pl", "sos.pl", "szkola.pl", "targi.pl", "tm.pl", "tourism.pl", "travel.pl",
            "turystyka.pl", "com.pt", "edu.pt", "gov.pt", "int.pt", "net.pt", "nome.pt", "org.pt", "publ.pt", "com.tw",
            "club.tw", "ebiz.tw", "game.tw", "gov.tw", "idv.tw", "net.tw", "org.tw", "av.tr", "bbs.tr", "bel.tr",
            "biz.tr", "com.tr", "dr.tr", "edu.tr", "gen.tr", "gov.tr", "info.tr", "k12.tr", "mil.tr", "name.tr",
            "net.tr", "org.tr", "pol.tr", "tel.tr", "web.tr", "ac.za", "city.za", "co.za", "edu.za", "gov.za", "law.za",
            "mil.za", "nom.za", "org.za", "school.za", "alt.za", "net.za", "ngo.za", "tm.za", "web.za", "bourse.za",
            "agric.za", "cybernet.za", "grondar.za", "iaccess.za", "inca.za", "nis.za", "olivetti.za", "pix.za",
            "db.za", "imt.za", "landesign.za", "co.kr", "pe.kr", "or.kr", "go.kr", "ac.kr", "mil.kr", "ne.kr",
            "chiyoda.tokyo.jp", "tcvb.or.jp", "ac.jp", "ad.jp", "co.jp", "ed.jp", "go.jp", "gr.jp", "lg.jp", "ne.jp",
            "or.jp", "com.mx", "net.mx", "org.mx", "edu.mx", "gob.mx", "ac.uk", "co.uk", "gov.uk", "ltd.uk", "me.uk",
            "mod.uk", "net.uk", "nic.uk", "nhs.uk", "org.uk", "plc.uk", "police.uk", "sch.uk", "ak.us", "al.us",
            "ar.us", "az.us", "ca.us", "co.us", "ct.us", "dc.us", "de.us", "dni.us", "fed.us", "fl.us", "ga.us",
            "hi.us", "ia.us", "id.us", "il.us", "in.us", "isa.us", "kids.us", "ks.us", "ky.us", "la.us", "ma.us",
            "md.us", "me.us", "mi.us", "mn.us", "mo.us", "ms.us", "mt.us", "nc.us", "nd.us", "ne.us", "nh.us", "nj.us",
            "nm.us", "nsn.us", "nv.us", "ny.us", "oh.us", "ok.us", "or.us", "pa.us", "ri.us", "sc.us", "sd.us", "tn.us",
            "tx.us", "ut.us", "vt.us", "va.us", "wa.us", "wi.us", "wv.us", "wy.us", "com.ua", "edu.ua", "gov.ua",
            "net.ua", "org.ua", "cherkassy.ua", "chernigov.ua", "chernovtsy.ua", "ck.ua", "cn.ua", "crimea.ua", "cv.ua",
            "dn.ua", "dnepropetrovsk.ua", "donetsk.ua", "dp.ua", "if.ua", "ivano-frankivsk.ua", "kh.ua", "kharkov.ua",
            "kherson.ua", "kiev.ua", "kirovograd.ua", "km.ua", "kr.ua", "ks.ua", "lg.ua", "lugansk.ua", "lutsk.ua",
            "lviv.ua", "mk.ua", "nikolaev.ua", "od.ua", "odessa.ua", "pl.ua", "poltava.ua", "rovno.ua", "rv.ua",
            "sebastopol.ua", "sumy.ua", "te.ua", "ternopil.ua", "vinnica.ua", "vn.ua", "zaporizhzhe.ua", "zp.ua",
            "uz.ua", "uzhgorod.ua", "zhitomir.ua", "zt.ua", "ac.il", "co.il", "org.il", "net.il", "k12.il", "gov.il",
            "muni.il", "idf.il", "co.im", "org.im", "com.sg"
    ));

    public static class Domain {
        String tld;
        String domain;
        String subdomains;
        String fullDomain;

        public Domain(String domainString) {
            fullDomain = domainString;
            // empty domain
            if ( fullDomain.length() == 0 ) {
                tld = "";
                domain = "";
                subdomains = "";
                return;
            }
            int dot = 0;
            while (dot != -1) {
                if ( TLD.contains(domainString.substring(dot+1)) ) {
                    tld = domainString.substring(dot+1);
                    int startOfDomain = domainString.lastIndexOf(".", dot-1);
                    domain = domainString.substring(startOfDomain+1);
                    if ( startOfDomain > 0 ) {
                        subdomains = domainString.substring(0, startOfDomain);
                    } else {
                        subdomains = "";
                    }
                    break;
                }
                dot = domainString.indexOf(".", dot+1);
            }
            if ( dot == -1 ) {
                // do tld found
                dot = domainString.lastIndexOf(".");
                tld = domainString.substring(dot+1);
                int startOfDomain = domainString.lastIndexOf(".", dot-1);
                domain = domainString.substring(startOfDomain+1);
                if ( startOfDomain > 0 ) {
                    subdomains = domainString.substring(0, startOfDomain);
                } else {
                    subdomains = "";
                }
            }
        }

        public String getTld() {
            return tld;
        }

        public String getDomain() {
            return domain;
        }

        public String getSubdomains() {
            return subdomains;
        }

        public boolean hasDomain() {
            return domain.length() > 0;
        }

        public boolean hasSubDomains() {
            return subdomains.length() > 0;
        }

        public String fullDomain() {
            return fullDomain;
        }
    }

	public String getModifiedInputText(String inputText) {
		EnumSet<UrlComponents> uriComponents = profile.getUrlComponents();
		if (uriComponents.isEmpty())
			return inputText;
		// var temp = location.href.match("([^://]*://)([^/]*)(.*)");
		Matcher matcher = urlRegex.matcher(inputText);
		if (!matcher.matches())
			return inputText;
		String protocol = matcher.group(1);
		String domainText = matcher.group(2);
		String portPath = matcher.group(3);
		if ( protocol == null ) protocol = "";
		if ( domainText == null ) domainText = "";
		if ( portPath == null ) portPath = "";

		StringBuilder retVal = new StringBuilder(inputText.length());
		if (uriComponents.contains(UrlComponents.Protocol)
				&& protocol.length() > 0) {
			retVal.append(protocol);
		}
        final Domain domain = new Domain(domainText);
        final boolean hasSubDomain = uriComponents.contains(UrlComponents.Subdomain) && domain.hasSubDomains();
        if ( hasSubDomain ) {
            retVal.append(domain.getSubdomains());
        }
        if ( uriComponents.contains(UrlComponents.Domain) && domain.hasDomain() ) {
            if ( hasSubDomain ) retVal.append(".");
            retVal.append(domain.getDomain());
        }
        if (uriComponents.contains(UrlComponents.PortPathAnchorQuery)
				&& portPath.length() > 0) {
			retVal.append(portPath);
		}
		return retVal.toString();

	}

	public String generatePassword(String inputText, String masterPassword) {
		final PwmHashAlgorithm hasher = profile.getHashAlgo();
		StringBuilder password = new StringBuilder();
		final String charSet = profile.getCharacters();
		final int maxLen = profile.getLengthOfPassword();
		int count = 0;
		final LeetLevel leetLevel = profile.getLeetLevel();
		String data = getData(getModifiedInputText(inputText));

		if ( profile.getUseLeet().useBefore() ) {
			masterPassword = LeetConverter.convert(leetLevel, masterPassword);
			data = LeetConverter.convert(leetLevel, data);
		}

		while ( password.length() < maxLen ) {
			final String buildPass;
			if ( count == 0 ) {
				buildPass = hasher.hash(masterPassword, data, charSet);
			} else {
				buildPass = hasher.hash(masterPassword + "\n" + Integer.toString(count), data, charSet);
			}
			password.append(buildPass);
			count++;
		}
		String strPass = password.toString();
		if ( profile.getUseLeet().useAfter() ) {
			strPass = LeetConverter.convert(leetLevel, strPass);
		}

		if ( profile.getPrefix().length() > 0 ) {
			strPass = profile.getPrefix() + strPass;
		}
		if ( profile.getSuffix().length() > 0 ) {
			int loc = maxLen - profile.getSuffix().length();
			if ( loc < strPass.length() ) {
				strPass = strPass.substring(0, loc) + profile.getSuffix();
			} else {
				strPass = strPass + profile.getSuffix();
			}
		}
		if ( strPass.length() > maxLen ) {
			strPass = strPass.substring(0, maxLen);
		}
		return strPass;
	}

	public String generateVerificationCode(String masterPassword) {
		final PwmHashAlgorithm hasher = PwmHashAlgorithm.get(HashAlgo.SHA_256);
		StringBuilder password = new StringBuilder();
		final String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final int maxLen = 3;
		int count = 0;

		while ( password.length() < maxLen ) {
			final String buildPass;
			if ( count == 0 ) {
				buildPass = hasher.hash(masterPassword, "", charSet);
			} else {
				buildPass = hasher.hash(masterPassword + "\n" + Integer.toString(count), "", charSet);
			}
			password.append(buildPass);
			count++;
		}
		String strPass = password.toString();

		if ( strPass.length() > maxLen ) {
			strPass = strPass.substring(0, maxLen);
		}
		return strPass;
	}

	private String getData(String inputText) {
		String username = profile.getUsername();
		String modifier = profile.getModifier();
		StringBuilder buffer = new StringBuilder(inputText.length()
				+ username.length() + modifier.length() );
		buffer.append(inputText).append(username).append(modifier);
		return buffer.toString();
	}
}
