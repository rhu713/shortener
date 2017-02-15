package shortener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebMain {
	public static void main(String[] args) throws Exception {
		SimpleFileDatabase db = new SimpleFileDatabase("testdb");
		Shortener s = new Shortener(db);
		
		int port = 8888;
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
		server.createContext("/", new RootHandler(s));
		server.setExecutor(null);
		server.start();
		System.out.println("Starting web server at " + port);
	}
}

class RootHandler implements HttpHandler {
	public RootHandler(Shortener s) {
		shortener_ = s;
	}
	
	private String StreamToString(InputStream in) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		return builder.toString();
	}
	
	private String ParseURL(String input) {
		final String needle = "url=";
		int index = input.indexOf(needle);
		
		if (index >= 0) {
			try {
				String answer = URLDecoder.decode(input.substring(index + needle.length()), "UTF-8");
				return answer;
			} catch (Exception e) {
				return "";
			}
		}

		return "";
	}
	
	private String GetIndexString() {
		String page = "<h2>URL Shortener</h2> <form method=\"post\">URL: <input type=\"text\" name=\"url\">"
				+ "<input type=\"submit\" value=\"Shorten\"></form>";
		return page;
	}
	
	private boolean URLHasScheme(String s) {
		try {
			URI uri = new URI(s);
			return !uri.getScheme().isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	public void handle(HttpExchange ex) throws IOException {
        String url = ParseURL(StreamToString(ex.getRequestBody()));
		String suffix = ex.getRequestURI().getPath().substring(1);

		ex.getResponseHeaders().set("Content-Type", "text/html");

		String response = "";

		if (!suffix.isEmpty()) {
			String decrypted = shortener_.Decrypt(suffix);
			if (decrypted == null) {
				response = "Invalid shortened URL!";
			} else {
				if (!URLHasScheme(decrypted)) {
					decrypted = "http://" + decrypted;
				}

				response = "<META HTTP-EQUIV=REFRESH CONTENT=\"0; URL=" + decrypted + "\">";
			}
		} else if (!url.isEmpty()) {
			String encrypted = shortener_.Encrypt(url);
			response = ex.getRequestHeaders().getFirst("Host") + "/" + encrypted;
		} else {
			response = GetIndexString();
		}
		
		ex.sendResponseHeaders(200, response.length());
		
		OutputStream os = ex.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
	
	private Shortener shortener_;
} // End of RootHandler.
