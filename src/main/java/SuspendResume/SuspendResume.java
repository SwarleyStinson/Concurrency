package SuspendResume;

//Приостановка и возобновление потока исполнения
class NewThread implements Runnable {
    String name;
    Thread t;
    boolean suspendFlag;

    public NewThread(String name) {
        this.name = name;
        t = new Thread(this, name);
        System.out.println("Новый поток: " + t);
        suspendFlag = false;
        t.start(); //запустить поток исполнения
    }

    //точка входа в поток исполнения
    public void run() {
        for (int i = 15; i > 0; i--) {
            try {
                System.out.println(name + ": " + i);
                Thread.sleep(200);
                synchronized (this) {
                    while (suspendFlag) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name + " завершен.");
    }

    synchronized void mysuspend() {
        suspendFlag = true;
    }

    synchronized void myresume() {
        suspendFlag = false;
        notify();
    }
}

public class SuspendResume {
    public static void main(String[] args) {
        NewThread th1 = new NewThread("Один");
        NewThread th2 = new NewThread("Два");
        try {
            Thread.sleep(1000);
            th1.mysuspend();
            System.out.println("Приостановка потока Один");
            Thread.sleep(1000);
            System.out.println("Возобновление потока Один");
            th1.myresume();

            th2.mysuspend();
            System.out.println("Приостановка потока Два");
            Thread.sleep(1000);
            System.out.println("Возобновление потока Два");
            th2.myresume();

        } catch (InterruptedException e) {
            System.out.println("Главный поток прерван");
        }

        //ожидать завершения потоков исполнения
        try {
            System.out.println("Ожидание завершения потоков");
            th1.t.join();
            th2.t.join();
        } catch (InterruptedException e) {
            System.out.println("Главный поток прерван");
        }
        System.out.println("Главный поток завершен");
    }
}
