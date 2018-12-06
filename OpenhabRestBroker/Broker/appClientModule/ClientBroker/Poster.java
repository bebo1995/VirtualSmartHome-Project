package ClientBroker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class Poster implements Runnable {
	private Socket client;
	ServerSocket serverSocket = null;

	public Poster(Socket client) throws InterruptedException {
		this.client = client;
	}

	@Override
	public void run() {

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));) {
			System.out.println("Thread started with name:" + Thread.currentThread().getName());

			String userInput = "";
			while ((userInput = in.readLine()) != null) {
				String[] input = StringCropper(userInput).split(" ");

				for (String x : input) {
					System.out.println(x.toString());
				}

				writer.write(Post(input[0], input[1].toUpperCase()) + "");
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			System.out.println("I/O exception: " + e);
		} catch (Exception ex) {
			System.out.println("Excerrion in Thread Run. Exception : " + ex);
		}
	}

	private int Post(String Item, String NewState) {
		int response = 0;

		try {
			URL url = new URL("http://localhost:8080/rest/items/" + Item);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "text/plain");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestMethod("POST");

			OutputStream wr = conn.getOutputStream();
			wr.write(NewState.getBytes("UTF-8"));
			wr.close();

			response = conn.getResponseCode();
			System.out.println(conn.getResponseCode());

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	private String StringCropper(String oldString) {
		String newString = "";
		for (char s : oldString.toCharArray()) {
			if ((s > 47 && s < 58) || (s > 64 && s < 91) || (s > 96 && s < 123) || s == 32 || s == 46 || s==95) {
				// 48-57 -> Numeri 0-9
				// 65-90 -> lettere A-Z
				// 97-122 -> lettere a-z
				// 32 ->spazio
				// 46 ->punto
				newString += s;
			}
		}
		return newString.trim();
	}

}
