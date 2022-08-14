package playground.misc;

import org.junit.jupiter.api.Test;

class JChange {

    private long coin2 = 0;
    private long bill5 = 0;
    private long bill10 = 0;

    @Test
    public void main() {

        long s = 1065L; // Change this value to perform other tests
        JChange m = JChange.optimalChange(s);

        System.out.println("Coin(s)  2€: " + m.coin2);
        System.out.println("Bill(s)  5€: " + m.bill5);
        System.out.println("Bill(s) 10€: " + m.bill10);

        long result = m.coin2 * 2 + m.bill5 * 5 + m.bill10 * 10;
        System.out.println("\nChange given = " + result);
    }

    // Do not modify this method signature
    static JChange optimalChange(long s) {
        JChange change = new JChange();
        //tant que le reste est superieure à 2
        while (s > 2) {
            // si c'est exactement un multiple de 10
            if (s % 10 == 0) {
                change.bill10 = s / 10;
                return change;
            } else {
                //sinon rend des 10 et occupe toi du reste
                change.bill10 = s / 10;
                s = s % 10;
                // si c'est exactement un multiple de 5
                if (s % 5 == 0) {
                    change.bill5 = s / 5;
                    return change;
                } else {
                    //sinon rend des 5 et occupe toi du reste
                    change.bill5 = s / 5;
                    s = s % 5;
                    // si c'est exactement un multiple de 2
                    if (s % 2 == 0) {
                        change.coin2 = s / 2;
                        return change;
                    } else {
                        //sinon rend des 2 et occupe toi du reste
                        change.coin2 = s / 2;
                        s = s % 2;
                    }
                }
            }
        }
        return change;
    }

    public long getCoin2() {
        return coin2;
    }

    public void setCoin2(long coin2) {
        this.coin2 = coin2;
    }

    public long getBill5() {
        return bill5;
    }

    public void setBill5(long bill5) {
        this.bill5 = bill5;
    }

    public long getBill10() {
        return bill10;
    }

    public void setBill10(long bill10) {
        this.bill10 = bill10;
    }
}
