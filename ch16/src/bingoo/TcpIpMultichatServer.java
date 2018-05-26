package bingoo;

import java.net.*;
import java.io.*;
import java.util.*;

public class TcpIpMultichatServer {
	HashMap clients;


	TcpIpMultichatServer() {
		clients = new HashMap();
		Collections.synchronizedMap(clients);
		new Thread().start();
	}

	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(7777);
			System.out.println("������ ���۵Ǿ����ϴ�.");

			while (true) {
				socket = serverSocket.accept();
				System.out.println(socket);
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "���� �����Ͽ����ϴ�.");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // start()

	void sendToAll(String msg) {
		Iterator it = clients.keySet().iterator();

		while (it.hasNext()) {
			try {
				DataOutputStream out = (DataOutputStream) clients.get(it.next());

				out.writeUTF(msg);
			} catch (Exception e) {
				
			}
		} // while
	} // sendToAll

	public static void main(String args[]) {
		new TcpIpMultichatServer().start();
	}

	class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		DataOutputStream out;

		ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		public void run() {
			String name = "";
			try {
				name = in.readUTF();
				clients.put(name, out);

				sendToAll("#|" + name + "���� �����̽��ϴ�.");
				System.out.println("���� ���������� ���� " + clients.size() + "�Դϴ�.");

				sendToAll("100|" + clients.size());

				while (true) {

					try {

						String msg = in.readUTF();
						System.out.println("msg:" + msg);
						String msgs[] = msg.split("\\|");
						String protocol = msgs[0];

						System.out.println(msg + "|��������");
						switch (protocol) {

						case "1":
							//@180526
							//sendToAll("1|���ӽ���");
							sendToAll("1|���ӽ���"+"|"+name);
							break;
						default :
							//@180526
							//sendToAll(msg);
							sendToAll(msg + "|"+name);
							break;

						}
					} catch (Exception e) {}
				}
			} catch (Exception e) {
				// ignore
			} finally {
				sendToAll("#" + name + "���� �����̽��ϴ�.");
				clients.remove(name);
				System.out.println("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "���� ������ �����Ͽ����ϴ�.");
				System.out.println("���� ���������� ���� " + clients.size() + "�Դϴ�.");
				sendToAll("100|" + clients.size());

			} // try
		} // run
	} // ReceiverThread
} // class