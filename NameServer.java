import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class NameServer extends Thread {
	Node pairs[];
	NameServer pred, succ;
	int port;
	Socket bootstrap;
	BufferedReader config;
	String inPair;
	String userIn;
	Scanner split;
	Scanner keyIn;
	int ID;
	// int port;
	boolean quit = false;
	private volatile boolean exit = false;

	public static void main(String args[]) {
		NameServer ns = new NameServer(args);
	}

	NameServer(String args[]) {
		try {
			// initialize config
			config = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			ID = Integer.parseInt(config.readLine());
			port = Integer.parseInt(config.readLine());
			if (ID > 0) { // not bootstrap
				System.out.println("Name Server");
				split = new Scanner(config.readLine());
				bootstrap = new Socket(split.next(), split.nextInt());
				keyIn = new Scanner(System.in);
				while (!quit) {
					userIn = "";
					if (keyIn.hasNext()) {
						split = new Scanner(keyIn.nextLine());
						if (split.hasNext()) {
							userIn = split.next();
							if (userIn.equalsIgnoreCase("enter")) {
								start();
								System.out.println("Name Server listening on: " + port);
							} else if (userIn.equalsIgnoreCase("exit"))
								quit = true;
							else
								System.out.println("Input not understood.");
						}
					}

				}
			} else if (ID == 0) { // bootstrap
				start();
				System.out.println("Bootstrap Name Server listening on: " + port);
				pairs = new Node[1024];
				while ((inPair = config.readLine()) != null) {
					split = new Scanner(inPair);
					insert(split.nextInt(), split.next());
				}
				keyIn = new Scanner(System.in);
				while (!quit) {
					userIn = "";
					if (keyIn.hasNext()) {
						split = new Scanner(keyIn.nextLine());
						if (split.hasNext()) {
							userIn = split.next();
							if (userIn.equalsIgnoreCase("quit"))
								quit = true;
							else if (split.hasNext()) {
								if (userIn.equalsIgnoreCase("lookup"))
									lookup(split.nextInt());
								else if (userIn.equalsIgnoreCase("insert"))
									insert(split.nextInt(), split.next());
								else if (userIn.equalsIgnoreCase("delete"))
									delete(split.nextInt());
							} else
								System.out.println("Input not understood.");
						}
					}

				}
			}
			quit();
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Config file has error.");
			System.exit(1);
		}
	}

	public void run() {
		if (ID == 0) {
			Socket servers[] = new Socket[1024];
			int IDs[] = new int[1024];
			int i = 0;
			int count=0;
			ServerSocket server;
			Socket socket;
			try {
				server = new ServerSocket(port);
				server.setSoTimeout(2000);
				while (!exit) {
					try {
						socket = server.accept();
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						int ID = Integer.parseInt(in.readLine());
						IDs[i] = ID;
						servers[ID] = socket;
						
					} catch (SocketTimeoutException e) {
						
					}

				}
				server.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			ServerSocket server;
			Socket socket;
			try {
				server = new ServerSocket(port);
				server.setSoTimeout(2000);
				while (!exit) {
					try {
						socket = server.accept();
					} catch (SocketTimeoutException e) {
						
					}

				}
				server.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
				pairs[i] = new Node(key, value);
				break;
			}
		}
		// System.out.println(pairs[i].key + pairs[i].value);
	}

	public void lookup(int key) {
		int i;
		boolean found = false;
		for (i = 0; i < pairs.length; i++) {
			if (pairs[i] != null && pairs[i].key == key) {
				System.out.println(key + ": " + pairs[i].value);
				found = true;
				break;
			}
		}
		if (!found)
			System.out.println("Key not held");
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

	public class Node {
		int key;
		String value;

		public Node(int key, String value) {
			this.key = key;
			this.value = value;
		}
	}

}
