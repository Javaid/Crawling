package com.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Scotland {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedWriter writer;
		FileWriter fWriter = new FileWriter("accountants.csv", true);
		writer = new BufferedWriter(fWriter);
		// for (int i = 1; i < 2; i++) {

		// getPageLinks(
		// "https://www.carehome.co.uk/care_search_results.cfm/searchcountry/Scotland/searchchtype/carehomeonly/orderid/-1/startpage/"+i,
		// writer);

		// getPageLinks("http://www.scotlanddirectory.info/category/accountants.html",writer);

		// }

		String[] links = { "accountants.html", "advertising.html", "airports.html", "alternativehealth.html",
				"architects.html", "artdealers.html", "artsgroups.html", "attractions.html", "auctioneers.html",
				"av.html", "bakers.html", "banks.html", "beautysalons.html", "bedandbreakfasts.html", "bicycles.html",
				"bistros.html", "bookshops.html", "bowlingclubs.html", "breweries.html", "broadcasters.html",
				"builders.html", "buildersmerchants.html", "butchers.html", "coffeeshops.html", "carparts.html",
				"vehiclerental.html", "caravanparks.html", "carehomes.html", "carpetcleaners.html", "carpets.html",
				"casinos.html", "caterers.html", "charity.html", "chineserestaurants.html", "chiropodists.html",
				"churches.html", "cinemas.html", "clothing.html", "coachhire.html", "consulates.html",
				"cosmeticdentists.html", "cosmeticsurgery.html", "counsellors.html", "couriers.html", "courts.html",
				"dating.html", "delicatessens.html", "dentists.html", "designprint.html", "doctors.html",
				"cleaners.html", "drivingschools.html", "drycleaners.html", "electrical.html", "electricians.html",
				"emergency.html", "environmentalconsultants.html", "escorts.html", "estateagents.html", "events.html",
				"farmshops.html", "farmers.html", "fencers.html", "festivals.html", "financialadvisers.html",
				"financial.html", "firesafety.html", "fishmongers.html", "florists.html", "footballclubs.html",
				"frenchrestaurants.html", "garagedoors.html", "gardencentres.html", "gardeners.html", "genealogy.html",
				"generalgoods.html", "giftshops.html", "glaziers.html", "golfclubs.html", "golfshops.html",
				"greengrocers.html", "grocers.html", "guesthouses.html", "hairdressers.html", "halls.html",
				"residences.html", "cosmetics.html", "healthclubs.html", "healthfood.html", "hifi.html",
				"hospitals.html", "hostels.html", "hotelgroups.html", "hotels.html", "hr.html", "it.html",
				"icecreams.html", "imageconsultants.html", "independentschools.html", "indianrestaurants.html",
				"insolvency.html", "interiors.html", "internetcafes.html", "investments.html",
				"italianrestaurants.html", "jewellers.html", "joiners.html", "karting.html", "kennels.html",
				"kitchen.html", "language.html", "propertymanagers.html", "libraries.html", "lifecoaches.html",
				"lingerie.html", "locksmiths.html", "makeupartists.html", "management.html", "marketresearch.html",
				"mexicanrestaurants.html", "mobilebars.html", "mobilephones.html", "motorcycles.html", "museums.html",
				"musicshops.html", "musicians.html", "nailtechnicians.html", "newsagents.html", "newspapers.html",
				"nurseryschools.html", "offlicences.html", "oil.html", "opticians.html", "painters.html",
				"personaltrainers.html", "petrolstations.html", "petshops.html", "pharmacies.html",
				"photographers.html", "photography.html", "physiotherapists.html", "pizza.html",
				"planningconsultants.html", "plasterers.html", "plumbers.html", "politicalparties.html",
				"politicians.html", "postoffices.html", "primaryschools.html", "printers.html", "prisons.html",
				"publicrelations.html", "pubs.html", "railways.html", "recordshops.html", "recruitment.html",
				"refrigeration.html", "removals.html", "restofworldrestaurants.html", "roofers.html", "rugbyclubs.html",
				"sandwiches.html", "saunas.html", "restaurants.html", "fishrestaurants.html", "schools.html",
				"selfcatering.html", "servicedapartments.html", "servicedoffices.html", "sexualhealth.html",
				"shoeshops.html", "shoppingcentres.html", "snooker.html", "solicitors.html", "spanishrestaurants.html",
				"sportsclubs.html", "sportsshops.html", "sportsinjury.html", "stationery.html",
				"americanrestaurants.html", "supermarkets.html", "surveyors.html", "sweetshops.html", "tailors.html",
				"takeaway.html", "tanning.html", "tattoo.html", "taxis.html", "thairestaurants.html", "theatres.html",
				"tilesuppliers.html", "tilers.html", "toolhire.html", "touroperators.html", "toyshops.html",
				"training.html", "travelagents.html", "tyres.html", "colleges.html", "departments.html",
				"universitylibraries.html", "societies.html", "unisports.html", "veggierestaurants.html",
				"garages.html", "carvalets.html", "venues.html", "vets.html", "videorental.html", "voluntary.html",
				"waste.html", "webdesign.html", "welders.html", "distilleries.html", "windowcleaners.html",
				"writers.html", "youthgroups.html" };

		for (String link : links) {
			getPageLinks("http://www.scotlanddirectory.info/category/" + link, writer);
		}

		// getCategories("http://www.scotlanddirectory.info/");
		writer.close();
	}

	public static void getCategories(String URL) throws IOException {
		Document document = Jsoup.connect(URL).get();
		Elements categories = document.select("#main p a");

		for (Element a : categories) {
			String aHref = a.attr("href");
			int intIndex = aHref.indexOf("category");
			if (intIndex != -1) {
				String[] cat = aHref.split("/category/");
				System.out.print(cat[1] + "\",\"");
			}
		}
	}
	public static void getPageLinks(String URL, BufferedWriter fWriter) {
		try {
			Document document = Jsoup.connect(URL).get();
			Elements addressOnPage = document.select("div[class=content]");
			Elements pAddress = addressOnPage.select("p");
			for (Element p : pAddress) {
				String pText = p.html();
				int intIndex = pText.indexOf("<br>");
				if (intIndex == -1) {

				} else {
					System.out.println("===================================");
					String[] addr = pText.split("<br>");
					System.out.println(addr[1].replace(",", ",|"));
					fWriter.write(addr[1].replace(",", ",|") + "\n");
				}
			}
		} catch (IOException e) {
			System.err.println("For '" + URL + "': " + e.getMessage());
		}
	}

	/*
	 * public static void address(Element addressOnPage){ int counter = 0; for
	 * (Element addr : addressOnPage) { System.out.println(counter++); //if
	 * (addr.text().length() > 0) { String businessaddress =
	 * addr.select("p[class=grey]").text(); String[] address =
	 * businessaddress.split(",");
	 * 
	 * System.out.println(address[0] +"|" + address[1]);
	 * 
	 * //String state = addr.select("span[id^=listing-state-]").text(); //String
	 * city = addr.select("span[id^=listing-city-]").text(); //String zip =
	 * addr.select("span[id^=listing-zip-]").text();
	 * 
	 * //System.out.println(businessaddress + " " + city + " " + state + " " +
	 * zip); //fWriter.write(businessaddress + "|" + city + "|" + state + "|" +
	 * zip + "\n"); // } } }
	 */

}
