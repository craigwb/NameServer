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
	int pred, succ;
	int port, ID;
	int IDs[] = new int[1024];
	int i = 0;
	int count = 0;
	Socket servers[] = new Socket[1024];
	Socket bootstrap;
	BufferedReader config;
	String inPair, userIn;
	Scanner split, keyIn;
	volatile boolean quit = false;
	volatile boolean exit;
	public static void main(String args[]) {
		NameServer ns = new NameServer(args);
	}

	NameServer(String args[]) {
		try { // initialize config
			config = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
			ID = Integer.parseInt(config.readLine());
			port = Integer.parseInt(config.readLine());
			if (ID > 0) { // not bootstrap
				System.out.println("Name Server");
				keyIn = new Scanner(System.in);
				split = new Scanner(config.readLine());
				while (!quit) {
					userIn = keyIn.nextLine();
					if (userIn.equalsIgnoreCase("enter")) {
						bootstrap = new Socket(split.next(), split.nextInt());
						PrintWriter out = new PrintWriter(bootstrap.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(bootstrap.getInputStream()));
						out.println(ID);
						out.flush();
						System.out.println(ID);
						int nodes = Integer.parseInt(in.readLine());
						System.out.println(nodes);
						pairs = new Node[nodes];
						for (int i = 0; i<nodes;i++) {
							pairs[i]= new Node(Integer.parseInt(in.readLine()),in.readLine());
							System.out.println(pairs[i].key + " " + pairs[i].value);
						}
						System.out.println("Name Server listening on: " + port);
					} 
					else if (userIn.equalsIgnoreCase("exit"))
						System.out.println();
					else if (userIn.equalsIgnoreCase("quit"))
						quit = true;
					else
						System.out.println("Input not understood.");
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
			ServerSocket server;
			Socket socket;
			try {
				server = new ServerSocket(port);
				while (!quit) {
					try {
						socket = server.accept();
						PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						System.out.println("trying");
						int ID = Integer.parseInt(in.readLine());
						System.out.println(ID);
						IDs[i] = ID;
						servers[ID] = socket;
						i++;
						count++;
						succ = 1024;
						pred = 0;
						for (int x = 0; x<i;x++) {
							if ((IDs[x]!=-1) && (IDs[x]-ID)<(succ-ID) && IDs[x]-ID>0) succ = IDs[x];
							if ((IDs[x]!=-1) && (ID-IDs[x])<(ID-pred) && ID-IDs[x]>0) pred = IDs[x];
						}
						
						out.println(ID-pred);
						out.flush();
						for (int x = 0; x<ID-pred;x++) {
							out.println(pred+x);//id
							out.flush();
							out.println(lookup(pred+x));//value
							out.flush();
						}
					} catch (Exception e) {

					}

				}
				server.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
		}
	}

	public void quit() {
		System.out.println("Quitting safely, this may take a few seconds.");
		quit = true;
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

	public String lookup(int key) {
		int i;
		boolean found = false;
		for (i = 0; i < pairs.length; i++) {
			if (pairs[i] != null && pairs[i].key == key) {
				System.out.println(key + " " + pairs[i].value);
				found = true;
				break;
			}
		}
		if (!found) {
			System.out.println(key+" Key not held");
			return "Key not held";
		}
		else {
			return pairs[i].value;
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

	public class Node {
		int key;
		String value;

		public Node(int key, String value) {
			this.key = key;
			this.value = value;
		}
	}
	

}
