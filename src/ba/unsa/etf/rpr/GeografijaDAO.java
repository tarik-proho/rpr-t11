package ba.unsa.etf.rpr;
import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;
    private PreparedStatement stmt;


    private GeografijaDAO() {
        try {
            String url="jdbc:sqlite:baza.db";
            conn=DriverManager.getConnection(url);
            //Class.forName("com.mysql.jdbc.Driver");

            stmt=conn.prepareStatement("DELETE FROM drzava");
            stmt.executeUpdate();

            stmt=conn.prepareStatement("DELETE FROM grad");
            stmt.executeUpdate();
            ArrayList<Drzava> drzavaList = new ArrayList<>();
            ArrayList<Grad> gradList = new ArrayList<>();
            defaultDataFill(drzavaList, gradList);
            stmt = conn.prepareStatement("INSERT INTO drzava (?, ?, NULL");
            for (var drzava : drzavaList) {
                try {
                    stmt.setInt(1, drzava.getId());
                    stmt.setString(2, drzava.getNaziv());
                    stmt.executeUpdate();
                } catch (SQLException ignored) {
                }
            }

            stmt = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            for (var grad : gradList) {
                try {
                    stmt.setInt(1, grad.getId());
                    stmt.setString(2, grad.getNaziv());
                    stmt.setInt(3, grad.getBrojStanovnika());
                    stmt.setInt(4, grad.getDrzava().getId());
                    stmt.executeUpdate();
                } catch (SQLException ignored) {
                }
            }

            stmt = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
            for (var drzava : drzavaList) {
                try {
                    stmt.setInt(1, drzava.getGlavniGrad().getId());
                    stmt.setInt(2, drzava.getId());
                    stmt.executeUpdate();
                } catch (SQLException ignored) {
                }
            }


        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void defaultDataFill(ArrayList<Drzava> drzavaList, ArrayList<Grad> gradList) {
        Drzava francuska = new Drzava(1, "Francuska", null);
        Drzava austrija = new Drzava(3, "Austrija", null);
        Drzava engleska = new Drzava(2, "Velika Britanija", null);

        Grad pariz = new Grad(1, "Pariz", 2206488 , null);
        Grad london = new Grad(2, "London",  	8825000 , null);
        Grad manchester = new Grad(3, "Manchester",  	545500, null);
        Grad bech = new Grad(4, "Beč", 1899055, null);
        Grad graz = new Grad(5, "Graz",  	280200, null);

        francuska.setGlavniGrad(pariz);
        austrija.setGlavniGrad(bech);
        engleska.setGlavniGrad(london);

        drzavaList.add(francuska);
        drzavaList.add(austrija);
        drzavaList.add(engleska);

        pariz.setDrzava(francuska);

        gradList.add(pariz);

        london.setDrzava(engleska);
        manchester.setDrzava(engleska);

        gradList.add(london);
        gradList.add(manchester);

        bech.setDrzava(austrija);
        graz.setDrzava(austrija);

        gradList.add(bech);
        gradList.add(graz);


    }

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    public static GeografijaDAO getInstance() {
        if (instance == null) initialize();
        return instance;
    }

   public static void removeInstance() { instance = null; }


    public ArrayList<Grad> gradovi() {
        //todo vraca spisak gradova sortiranih po broju stanovnika u opadajucem redoslijedu
        return null;
    }

    public Grad glavniGrad(String drzava) {
        //todo vraca null ako drzava ne postoji
        return null;
    }

    public void obrisiDrzavu(String drzava) {
        //todo brise drzavu i sve gradove u njoj
    }

    public Drzava nadjiDrzavu(String drzava) {
        //todo vraca null ako drzava ne postoji
        return null;
    }

    public void dodajGrad(Grad grad) {
        //todo dodaje grad
    }

    public void dodajDrzavu(Drzava drzava) {
        // TODO: dodaje drzavu
    }

    public void izmijeniGrad(Grad grad) {
        //todo azurira slog u bazi za dati grad
    }
}

//stmt.executeUpdate(); ako je u pitanju INSERT, UPDATE ili DELETE i vraća koliko je redova izmijenjeno
//privatna funkcija za konekciju i kreiranje baze
//iz initialize i konstruktora ih pozivati