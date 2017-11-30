public class Main {

    Serial serial;

    public void testComm() {
        serial = new Serial();
        serial.startConnection();
    }

    public static void main(String[] args) {
        new Main().testComm();
    }
}