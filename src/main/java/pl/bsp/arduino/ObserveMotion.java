package pl.bsp.arduino;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import pl.bsp.model.Resource;
import pl.bsp.model.User;
import pl.bsp.services.ArduinoService;
import pl.bsp.services.ArduinoServiceImpl;
import pl.bsp.services.ResourceServiceImpl;
import pl.bsp.services.UserServiceImpl;

public class ObserveMotion implements Runnable {
	ArduinoService ardServ = new ArduinoServiceImpl();
	private String arduinoIp;
	private String userName;

	List<pl.bsp.model.Resource> resources;

	@Autowired
	ResourceServiceImpl resServ;
	@Autowired
	UserServiceImpl userService;

	public ObserveMotion(String arduinoIp, String userName) {
		this.arduinoIp = arduinoIp;
		this.userName = userName;
	}

	public void addResource(Resource resource) {
		resources.add(resource);
	}

	public String getUserName() {
		return userName;
	}


	@Override
	public void run() {
		String received = null;
		byte[] receiveData = new byte[1024];
		String valueToSend = "T";
		byte[] sendData = valueToSend.getBytes();
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		try {
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(arduinoIp),
					8888);
			serverSocket.send(sendPacket);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean errorOccured = false;
		while (!errorOccured) {
			try {
				serverSocket.receive(receivePacket);
				received = new String(receivePacket.getData());
				System.out.println(received);
				if (ArduinoReader.iNeedToCheckDatabase) {
					User resourcesOwner = userService.findByUsername(userName);
					resources = resourcesOwner.getResources();

				}
				if (received.charAt(2) == 0) {
					for (Resource r : resources) {
						if (r.getAction().equals("GET/POST")) {
							ardServ.turnOffTheLight(arduinoIp, r.getSerialId());
						}
					}
				}
				if (received.charAt(2) == 1) {
					for (Resource r : resources) {
						if (r.getAction().equals("GET/POST")) {
							ardServ.turnOnTheLight(arduinoIp, r.getSerialId());
						}
					}
				}
			} catch (SocketTimeoutException e) {
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
