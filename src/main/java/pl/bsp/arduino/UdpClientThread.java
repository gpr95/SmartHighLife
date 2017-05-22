package pl.bsp.arduino;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.Logger;

import pl.bsp.enums.ActivityType;

public class UdpClientThread implements Runnable {
	private static final Logger logger = Logger.getLogger(UdpClientThread.class);

	private DatagramSocket serverSocket;
	private byte[] receiveData = new byte[1024];
	private byte[] sendData = new byte[1024];
	public static int numberOfPeopleInRoom = 0;

	public UdpClientThread(int port) {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			logger.error("Could not make udp socket: " + e.getMessage());
		}
	}

	public void run() {
		boolean errorOccured = false;

		while (!errorOccured) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			try {
				serverSocket.receive(receivePacket);
				String received = new String(receivePacket.getData());
				logger.debug("Received: " + received);
				ActivityType type = null;
				if (received.getBytes()[0] == 48) {
					type = ActivityType.LEAVE;
					decrementCount();
				} else if (received.getBytes()[0] == 49) {
					type = ActivityType.ENTER;
					incrementCount();
				}
				System.out.println(": " + type);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				String valueToSend = null;
				if (numberOfPeopleInRoom > 0)
					valueToSend = "ON";
				else
					valueToSend = "OFF";

				sendData = valueToSend.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			} catch (IOException e1) {
				errorOccured = true;
			}
		}
	}

	public static synchronized void incrementCount() {
		numberOfPeopleInRoom++;
	}

	public static synchronized void decrementCount() {
		numberOfPeopleInRoom++;
	}

}
