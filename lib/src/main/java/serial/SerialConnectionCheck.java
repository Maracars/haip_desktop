package serial;

public class SerialConnectionCheck implements Runnable{
	
	Serial serial;
	
	public SerialConnectionCheck(Serial serial) {
		this.serial = serial;
	}

	@Override
	public void run() {
		while(true) {
			System.out.println(serial.isConnected());
		}
		
	}

}
