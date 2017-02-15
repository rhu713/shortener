package shortener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Shortener {
	
	public Shortener (SimpleFileDatabase db) {
		db_ = db;
	}
	
	// Returns the shortened version of <url>.
	public String Encrypt(String url) {
		int record_number = db_.AddRecord(url);		
		return IntToString(BigInteger.valueOf(record_number));
	}

	// Returns the actual url behind the shortened <url>.
	public String Decrypt(String url) {
		BigInteger record_number = StringToInt(url);
		return db_.GetRecord(record_number.intValue());
	}
	
	
	private String IntToString(BigInteger input) {		
		if (input.compareTo(BigInteger.ZERO) < 0) return null;
		
		List<Character> chars = new ArrayList<>();
		BigInteger base = BigInteger.valueOf(ALLOWED_CHARACTERS.length());

		while (input.compareTo(base) >= 0) {
			chars.add(ALLOWED_CHARACTERS.charAt(input.mod(base).intValue()));
			input = input.divide(base);
		}
		chars.add(ALLOWED_CHARACTERS.charAt(input.intValue()));
		
		StringBuilder builder = new StringBuilder();
		for (int i = chars.size() - 1; i >=0; --i) {
			builder.append(chars.get(i));
		}
		
		return builder.toString();
	}
	
	public BigInteger StringToInt(String url) {
		// Some quick and simple O(n^2) string to corresponding integer algorithm.		
		BigInteger answer = BigInteger.ZERO;
		BigInteger base = BigInteger.valueOf(ALLOWED_CHARACTERS.length());

		for (int i = 0; i < url.length(); ++i) {
			int index = ALLOWED_CHARACTERS.indexOf(url.charAt(url.length() - i - 1));
			if (index < 0) return null;
			
			answer = answer.add(BigInteger.valueOf(index).multiply(base.pow(i)));
		}
		
		return answer;
	}
	
	private SimpleFileDatabase db_;

	static final String ALLOWED_CHARACTERS = 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-._~!$&'()*+,;=:@";

}
