import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//
//Синхронизация блоков кода вместо целых методов. Также
// демонстрирует защиту класса, небезопасного
// по отношению к потокам, другим - защищенным - классом.
class Pair {//потоково-небезопасный класс
    private int x, y;

    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Pair() {this(0,0);}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void incrementX() {
        x++;
    }

    public void incrementY() {
        y++;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
    public class PairValuesNotEqualException extends RuntimeException{
        public PairValuesNotEqualException() {
            super("Pair values not equal: " + Pair.this);
        }
    }
    //произвольный инвариант - переменные должны быть равны:
    public void checkState() {
        if (x != y)
            throw new PairValuesNotEqualException();
    }
}

//защита Pair  в потоково-безопасном классе:
abstract class PairManager {
    AtomicInteger checkCounter = new AtomicInteger(0);
    protected Pair p = new Pair();
    private List<Pair> storage = Collections.synchronizedList(new ArrayList<Pair>());

    public synchronized Pair getPair() {
        //создание копии для защиты оригинала
        return new Pair(p.getX(), p.getY());
    }

    //Предполагается, что эта операция
    // занимает много времени
    protected void store(Pair p) {
        storage.add(p);
        try {
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException ignore) {
        }
    }

    public abstract void increment();
}

    //Синхронизация целого метода:
    class PairManager1 extends PairManager{
        public synchronized void increment() {
            p.incrementX();
            p.incrementY();

            store(getPair());
        }
    }

//использование критической секции:
class PairManager2 extends PairManager{
    public void increment() {
        Pair temp;
        synchronized (this){
            p.incrementX();
            p.incrementY();
            temp = getPair();
        }
        store(temp);
    }
}

class PairManipulator implements Runnable {
    private PairManager pm;

    public PairManipulator(PairManager pm) {
        this.pm = pm;
    }

    @Override
    public String toString() {
        return "Pair; " + pm.getPair() + " checkCounter = " + pm.checkCounter.get();
    }

    public void run() {
        while(true){
            pm.increment();
        }
    }
}

class PairChecker implements Runnable {
    private PairManager pm;
    public PairChecker(PairManager pm){
        this.pm = pm;
    }

    public void run() {
        while(true){
            pm.checkCounter.incrementAndGet();
            pm.getPair().checkState();
        }
    }
}

public class CriticalSection {
}
