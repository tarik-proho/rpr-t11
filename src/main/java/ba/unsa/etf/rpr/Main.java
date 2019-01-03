
package ba.unsa.etf.rpr;

import java.util.Scanner;

public class Main {
    private static GeografijaDAO dataBase = GeografijaDAO.getInstance();

    public static void main(String[] args) {

        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();

    }

    private static void glavniGrad() {
        Scanner ulaz = new Scanner(System.in);
        String drzava = ulaz.nextLine();
        var grad = dataBase.glavniGrad(drzava);
        System.out.println("Glavni grad države " + grad.getDrzava().getNaziv() + " je " + grad.getNaziv());
    }

    public static String ispisiGradove() {
        var gradovi = dataBase.gradovi();
        String result = "";
        for (var grad : gradovi)
            result += grad.toString() + "\n";
        return result;
    }
}