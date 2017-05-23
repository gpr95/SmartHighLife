package pl.bsp.arduino;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import org.apache.log4j.Logger;

import pl.bsp.enums.ActivityType;

public class NumberOfPeopleInRoomCounterThread implements Runnable {
	private static final Logger logger = Logger.getLogger(NumberOfPeopleInRoomCounterThread.class);

	private DatagramSocket serverSocket;
	
	private List<String> managedPirDevices;
	
	public static int numberOfPeopleInRoom = 0;

	public NumberOfPeopleInRoomCounterThread(int port) {
		try {
			serverSocket = new DatagramSocket(port);
		} catch (SocketException e) {
			logger.error("Could not make udp socket: " + e.getMessage());
		}
	}

	public void run() {
		boolean errorOccured = false;

		while (!errorOccured) {
			byte[] receiveData = new byte[1024];
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
				logger.debug(": " + type);
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				String valueToSend = null;
				if (numberOfPeopleInRoom > 0)
					valueToSend = "ON";
				else
					valueToSend = "OFF";
				
				byte[] sendData = new byte[1024];
				sendData = valueToSend.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			} catch (IOException e) {
				logger.error("IO Error " + e.getMessage());
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
