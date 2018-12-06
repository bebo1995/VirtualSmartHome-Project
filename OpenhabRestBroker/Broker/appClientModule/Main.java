
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ClientBroker.Getter;
import ClientBroker.Poster;

public class Main {
	public static void main(String[] args) throws IOException {

		int portNumber = 8005;
		int portNumber2 = 80;
		ExecutorService executor = null;
		try (ServerSocket serverSocket = new ServerSocket(portNumber);
				ServerSocket serverSocket2 = new ServerSocket(portNumber2);) {
			executor = Executors.newFixedThreadPool(10);
			System.out.println("Waiting for clients");
			while (true) {
				Socket clientSocket = serverSocket.accept();
				Socket clientSocket2 = serverSocket2.accept();
				Runnable getter = new Getter(clientSocket);
				Runnable poster = new Poster(clientSocket2);
				executor.execute(getter);
				executor.execute(poster);
			}
		} catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}
}
