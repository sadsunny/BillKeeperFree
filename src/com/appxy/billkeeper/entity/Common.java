package com.appxy.billkeeper.entity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;

import com.appxy.billkeeper.R;
import com.appxy.billkeeper.activity.NewBillActivity;

import android.R.integer;
import android.graphics.Color;
import android.util.Log;

public class Common {
	public static int CURRENCY = 148;
	
	public final static int[] COLORS = new int[] { Color.argb(255, 237, 93, 93),
		Color.argb(255, 248, 133, 76),
		Color.argb(255, 250, 177, 81),
		Color.argb(255, 251, 198, 106),
		Color.argb(255, 250, 221, 86),
		Color.argb(255, 246, 236, 68),
		Color.argb(255, 217, 227,53),
		Color.argb(255, 186, 219, 55),
		Color.argb(255, 158, 205, 56),
		Color.argb(255, 112, 202, 52),
		Color.argb(255, 71, 194, 85),
		Color.argb(255, 47, 183, 108),
		Color.argb(255, 0, 214, 186),
		Color.argb(255, 51, 223, 221),
		Color.argb(255, 59, 211,238),
		Color.argb(255, 49, 187, 245),
		Color.argb(255, 95, 165, 237),
		Color.argb(255, 93, 142, 231),
		Color.argb(255, 125, 135, 241),
		Color.argb(255, 135, 115, 236),
		Color.argb(255, 175, 113, 244),
		Color.argb(255, 216, 99, 237),
		Color.argb(255, 233, 90,218),
		Color.argb(255, 225, 66, 145)
	};// piechart???�???��?��??category
																				// fragment�????allcategory???listview�?�???��??

	public final  static Integer[] CATEGORYICON = { R.drawable.icon_baby,
			R.drawable.icon_car, R.drawable.icon_computer,
			R.drawable.icon_cosmetology, R.drawable.icon_credit_card,
			R.drawable.icon_dining, R.drawable.icon_education,
			R.drawable.icon_ele, R.drawable.icon_gas,
			R.drawable.icon_hospital, R.drawable.icon_insurance,
			R.drawable.icon_internet, R.drawable.icon_loan,
			R.drawable.icon_mobile, R.drawable.icon_other,
			R.drawable.icon_pet, R.drawable.icon_recycle,
			R.drawable.icon_rent, R.drawable.icon_repair,
			R.drawable.icon_shopping, R.drawable.icon_sport,
			R.drawable.icon_tel, R.drawable.icon_travel,
			R.drawable.icon_tv, R.drawable.icon_water};
	
	public final static String[] CURRENCY_SIGN ={"Lek", "Kz", "$", "դր", "Afl.", "$", "AZN", "د.ج", "؋",
		"B$", "৳", "Bds$", "BYR", "$", "BD$ ", "Nu.", "Bs", "KM", "P",
		"R$", "£", "$", "лв.", "FBu", ".د.ب", "$", "$", "$", "CFA", "FCFA",
		"F", "$", "￥", "$", "CF", "F", "₡", "Kn", "$MN", "Kč", "؋", "kr",
		"$", "RD$", "$", "$", "kr", "$", "€", "ج.م", "£", "FJ$", "D",
		"GEL", "GH¢", "£", "Q", "FG", "F$", "G", "L", "$", "Ft", "kr.",
		"Rs.", "Rp", "₪", "﷼", "ع.د", "J$", "￥", "د.ا ", "〒", "KSh", "KGS",
		"د.ك", "₭", "Ls", "L", "L$", "Lt", "ل.ل", "ل.د", "MOP", "MDen",
		"MGA", "MK", "RM", "MRf", "R", "UM", "$", "MDL", "₮", "MTn", "K",
		"د.م.", "N$", "रू.", "ƒ", "NT$", "$", "₦", "C$", "₩", "kr", "ر.ع.",
		"PKR", "PAB", "K", "PYG", "PEN", "₱", "zł", "£", "ر.ق", "lei",
		"руб.", "RF", "£", "Db", "RSD", "SR", "Le", "$", "SI$ ", "$", "₩",
		"R", "SL Re", "SDG", "$", "L", "kr", "SFr.", "ل.س", "ر.س", "TJS",
		"TSh", "฿", "T$", "TT$", "TRY", "د.ت ", "USh", "rpH.", "COU", "$U",
		"$", "so‘m", "د.إ", "Vt", "BsF", "₫", "WS$", "﷼", "ZK" };
	
	
	public final static String[] CURRENCY_NAME ={ "Albanian Lek", "Angolan Kwanza", "Argentine Peso",
		"Armenian Dram", "Aruban Florin", "Australian Dollar",
		"Azerbaijanian Manat", "Algerian Dinar", "Afghan Afghani",
		"Bahamian Dollar", "Bangladeshi Taka", "Barbadian Dollar",
		"Belarusian Ruble", "Belize Dollar", "Bermudan Dollar",
		"Bhutanese Ngultrum", "Bolivian Boliviano", "Bosnia-Herzegovina",
		"Botswanan Pula", "Brazilian Real", "British Pound Sterling",
		"Brunei Dollar", "Bulgarian Lev", "Burundian Franc",
		"Bahraini Dinar", "Canadian Dollar", "Cape Verde Escudo",
		"Cayman Islands Dollars", "CFA Franc BCEAO", "CFA Franc BEAC",
		"CFP Franc", "Chilean Peso", "Chinese Yuan Renminbi",
		"Colombian Peso", "Comorian Franc", "Congolese Franc",
		"Costa Rican colón", "Croatian Kuna", "Cuban Peso",
		"Czech Republic Koruna", "Cambodian Riel", "Danish Krone",
		"Djiboutian Franc", "Dominican Peso", "East Caribbean Dollar",
		"Eritrean Nakfa", "Estonian Kroon", "Ethiopian Birr", "Euro",
		"Egyptian Pound", "Falkland Islands Pound", "Fijian Dollar",
		"Gambia Dalasi", "Georgian Lari", "Ghanaian Cedi",
		"Gibraltar Pound", "Guatemalan Quetzal", "Guinean Franc",
		"Guyanaese Dollar", "Haitian Gourde", "Honduran Lempira",
		"Hong Kong Dollar", "Hungarian Forint", "Icelandic króna",
		"Indian Rupee", "Indonesian Rupiah", "Israeli New Sheqel",
		"lraqi Dinar", "Iranian Rial", "Jamaican Dollar", "Japanese Yen",
		"Jordanian Dinar", "Kazakhstani Tenge", "Kenyan Shilling",
		"Kyrgystani Som", "Kuwaiti Dinar", "Laotian Kip", "Latvian Lats",
		"Lesotho Loti", "Liberian Dollar", "Lithuanian Litas",
		"Lebanese Pound", "Libyan Dinar", "Macanese Pataca",
		"Macedonian Denar", "Madagascar Ariary", "Malawian Kwacha",
		"Malaysian Ringgit", "Maldive Islands Rufiyaa", "Mauritian Rupee",
		"Mauritanian Ouguiya", "Mexican Peso", "Moldovan Leu",
		"Mongolian Tugrik", "Mozambican Metical", " Myanma Kyat",
		"Moroccan Dirham", "Namibian Dollar", "Nepalese Rupee",
		"Netherlands Antillean Guilder", "New Taiwan Dollar",
		"New Zealand Dollar", "Nigerian Naira", "Nicaraguan Cordoba Oro",
		"North Korean Won", "Norwegian Krone", "Omani Rial",
		"Pakistani Rupee", "Panamanian Balboa", "Papua New Guinea Kina",
		"Paraguayan Guarani", "Peruvian Nuevo Sol", "Philippine Peso",
		"Polish Zloty", "Pound", "Qatari Rial", "Romanian Leu",
		"Russian Ruble", "Rwandan Franc", "Saint Helena Pound",
		"São Tomé and Príncipe", "Serbian Dinar", "Seychelles Rupee",
		"Sierra Leonean Leone", "Singapore Dollar",
		"Solomon Islands Dollar", "Somali Shilling", "Sorth Korean Won",
		"South African Rand", "Sri Lanka Rupee", "Sudanese Pound",
		"Surinamese Dollar", "Swazi Lilangeni", "Swedish Krona",
		"Swiss Franc", "Saudi Riyal", "Syrian Pound", "Tajikistani Somoni",
		"Tanzanian Shilling", "Thai Baht", "Tonga Pa???anga",
		"Trinidad and Tobago", "Turkish Lira", "Tunisian Dinar",
		"Ugandan Shilling", "Ukrainian Hryvnia", "Unidad de Valor Real",
		"Uruguay Peso", "US Dollar", "Uzbekistan Som",
		"United Arab Emirates", "Vanuatu Vatu",
		"Venezuelan Bolivar Fuerte", "Vietnamese Dong", "Samoabn Tala",
		"Yemeni Rial", "Zambian Kwacha" };
	
	public static String doublepoint2str(double num){
		BigDecimal bg = new BigDecimal(num);
        float f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(f1);
}
	
	public static class MapComparator implements Comparator<Map<String, Object>> { //�?list?????��??�?�?段�??�????�?
	        @Override
	        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
	            // TODO Auto-generated method stub
	        	long due1 = (Long)o1.get("nbk_billDuedate");
	        	long due2 = (Long)o2.get("nbk_billDuedate");
	            if (due1 > due2) {
	                return 1;
	            }else if(due1 < due2){
	            	 return -1;
				}else {
					return 0;
				}
	        }
	 }
	
}