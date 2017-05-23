package pl.bsp.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class ArduinoServiceImpl implements ArduinoService {

	@Override
	public String findArduinoInNetwork() {
		String localhostPrefix = null;
		int lastOcetet = 0;
		Enumeration<?> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) interfaces.nextElement();
				Enumeration<InetAddress> iNetAddress = netInterface.getInetAddresses();
				while (iNetAddress.hasMoreElements()) {
					InetAddress i = iNetAddress.nextElement();

					if (i.getHostAddress().contains(".")) {
						String ipString = i.getHostAddress();
						String tmp = ipString.substring(ipString.lastIndexOf('.') + 1, ipString.length());
						try {
							int lastOctetTmp = Integer.parseInt(tmp);
							if (lastOctetTmp > 1) {
								localhostPrefix = ipString.substring(0, ipString.lastIndexOf('.') + 1);
								lastOcetet = lastOctetTmp;
							}
						} catch (NumberFormatException e) {

						}

					}

				}
			}
		} catch (SocketException e) {
		}

		DatagramSocket serverSocket = null;

		byte[] receiveData = new byte[1024];
		byte[] sendData;
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			serverSocket = new DatagramSocket(8888);
			serverSocket.setSoTimeout(50);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String valueToSend = "X";
		sendData = valueToSend.getBytes();

		String received = null;
		InetAddress arduinoIp = null;
		for (int i = lastOcetet + 1; i < 254; i++) {
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						InetAddress.getByName(localhostPrefix + i), 8888);
				serverSocket.send(sendPacket);
				serverSocket.receive(receivePacket);
				received = new String(receivePacket.getData());
				System.out.println(received);
				arduinoIp = InetAddress.getByName(localhostPrefix + i);
			} catch (UnknownHostException e) {
			} catch (IOException e) {
			}
		}

		if (received == null)
			for (int i = lastOcetet - 1; i > 1; i--) {
				try {
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
							InetAddress.getByName(localhostPrefix + i), 8888);
					serverSocket.send(sendPacket);
					serverSocket.receive(receivePacket);
					received = new String(receivePacket.getData());
					System.out.println(received);
					arduinoIp = InetAddress.getByName(localhostPrefix + i);
				} catch (UnknownHostException e) {
				} catch (IOException e) {
				}
			}

		if (arduinoIp != null)
			return arduinoIp.getHostAddress();
		return null;
	}

}
