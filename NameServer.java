import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class NameServer extends Thread {
	Pair pairs[];
	int port;
	private volatile boolean exit = false;

	public static void main(String args[]) {
		NameServer ns;
		Socket sock;
		BufferedReader config;
		String inPair;
		String userIn;
		Scanner split;
		Scanner keyIn;
		int ID;
		int port;
		boolean quit = false;

		try {
			config = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			ID = Integer.parseInt(config.readLine());
			port = Integer.parseInt(config.readLine());
			ns = new NameServer(port);
			if (ID > 0) {
				System.out.println("Name Server listening on: " + port);
				split = new Scanner(config.readLine());
				sock = new Socket(split.next(),split.nextInt());
				keyIn = new Scanner(System.in);
				while (!quit) {
					split = new Scanner(keyIn.nextLine());
					userIn = split.next();
					if (userIn.equalsIgnoreCase("exit"))
						quit = true;
					else if (userIn.equalsIgnoreCase("enter"))
						;
				}
				ns.quit();
			} else if (ID == 0) {
				System.out.println("Bootstrap Name Server listening on: " + port);
				ns.pairs = new Pair[1024];
				while ((inPair = config.readLine()) != null) {
					split = new Scanner(inPair);
					ns.insert(split.nextInt(), split.next());
				}
				keyIn = new Scanner(System.in);
				while (!quit) {
					split = new Scanner(keyIn.nextLine());
					userIn = split.next();
					if (userIn.equalsIgnoreCase("quit"))
						quit = true;
					else if (userIn.equalsIgnoreCase("lookup"))
						ns.lookup(split.nextInt());
					else if (userIn.equalsIgnoreCase("insert"))
						ns.insert(split.nextInt(), split.next());
					else if (userIn.equalsIgnoreCase("delete"))
						ns.delete(split.nextInt());
				}
				ns.quit();

			}
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Config file has error.");
			System.exit(1);
		}

	}

	NameServer(int port) { // bootstrap config
		this.port = port;
		start();
	}

	public void run() {
		ServerSocket server;
		Socket socket;
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(2000);
			while (!exit) {
				try {
					socket = server.accept();
					if (!socket.isClosed())
						socket.close();
				} catch (SocketTimeoutException e) {

				}

			}
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quit() {
		System.out.println("Quitting safely, this may take a few seconds.");
		exit = true;
	}

	public void insert(int key, String value) {
		int i;
		for (i = 0; i < pairs.length; i++) {
			if (pairs[i] == null) {
				pairs[i] = new Pair(key, value);
				break;
			}
		}
		// System.out.println(pairs[i].key + pairs[i].value);
	}

	public void lookup(int key) {
		int i;
		for (i = 0; i < pairs.length; i++) {
			if (pairs[i] != null && pairs[i].key == key) {
				System.out.println(key + ": " + pairs[i].value);
				break;
			}
		}
	}

	public void delete(int key) {
		int i;
		for (i = 0; i < pairs.length; i++) {
			if (pairs[i] != null && pairs[i].key == key) {
				pairs[i] = null;
				break;
			}
		}
	}

	public class Pair {
		int key;
		String value;

		public Pair(int key, String value) {
			this.key = key;
			this.value = value;
		}
	}

}
