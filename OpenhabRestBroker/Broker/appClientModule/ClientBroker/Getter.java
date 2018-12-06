package ClientBroker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.net.ServerSocket;
import java.net.Socket;

import ClientBroker.hashmap;

public class Getter implements Runnable {
	private Socket client;
	ServerSocket serverSocket = null;

	private hashmap hmap;

	public Getter(Socket client) throws InterruptedException {
		this.client = client;
		this.hmap = new hashmap();
	}

	@Override
	public void run() {

		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));) {
			System.out.println("Thread started with name:" + Thread.currentThread().getName());

			// Invio stato degli items
			writer.write(hmap.getState().toString());
			writer.newLine();
			writer.flush();

			while (true) {
				writer.write(hmap.update().toString());
				writer.newLine();
				writer.flush();
				Thread.sleep(10 * 1000);
			}

		} catch (IOException e) {
			System.out.println("I/O exception: " + e);
		} catch (Exception ex) {
			System.out.println("Excerrion in Thread Run. Exception : " + ex);
		}
	}

}
