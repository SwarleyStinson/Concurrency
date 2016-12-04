//

public class SerialNubmerGenerator {
    private static volatile int serialNumber = 0;
    public static int nextSerialNumber() {
        return serialNumber++;  //небезопасно для потоков, потому что операция инкремента не атомарна
    }
}
