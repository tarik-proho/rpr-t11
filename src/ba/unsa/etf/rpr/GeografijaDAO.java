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


    public void obrisiDrzavu(String drzava) {
        try {
            stmt = conn.prepareStatement("SELECT g.id FROM grad g, drzava d WHERE g.drzava = d.id AND d.naziv = ?");
            stmt.setString(1, drzava);
            ResultSet result = stmt.executeQuery();
            int brojac = 0;
            while (result.next()) {
                int idGrad = result.getInt(1);
                PreparedStatement podUpit = conn.prepareStatement("DELETE FROM grad WHERE id = ?");
                podUpit.setInt(1, idGrad);
                podUpit.executeUpdate();
                brojac++;
            }
            if (brojac == 0)
                return;
            stmt = conn.prepareStatement("DELETE FROM drzava WHERE naziv = ?");
            stmt.setString(1, drzava);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
            System.out.println("Ne postoji data drzava");
        }
    }

    public ArrayList<Grad> gradovi() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        try {
            stmt = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            ResultSet resultGradovi = stmt.executeQuery();
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = resultGradovi.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = resultGradovi.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));
                gradovi.add(grad);
            }
            stmt = conn.prepareStatement("SELECT * FROM drzava");
            ResultSet resultDrzave = stmt.executeQuery();
            while (resultDrzave.next()) {
                Drzava drzava = new Drzava();
                int idDrzava = resultDrzave.getInt(1);
                drzava.setId(idDrzava);
                String nazivDrzava = resultDrzave.getString(2);
                drzava.setNaziv(nazivDrzava);
                int glavniGradId = resultDrzave.getInt(3);
                int idGrad = resultDrzave.getInt(3);
                for (var grad : gradovi) {
                    if (grad.getDrzava().getId() == drzava.getId()) {
                        grad.setDrzava(drzava);
                    }
                    if (glavniGradId == grad.getId())
                        drzava.setGlavniGrad(grad);
                }
            }
        } catch (SQLException ignored) {
            return null;
        }
        return gradovi;
    }

    public Grad glavniGrad(String drzava) {
        Grad grad = new Grad();
        try {
            stmt = conn.prepareStatement("SELECT g.id, g.naziv, g.broj_stanovnika, d.id, d.naziv FROM grad g, drzava d WHERE d.glavni_grad = g.id AND d.naziv = ?");
            stmt.setString(1, drzava);
            ResultSet result = stmt.executeQuery();
            Drzava drzavaFk = new Drzava();
            grad.setDrzava(drzavaFk);
            drzavaFk.setGlavniGrad(grad);
            int brojac = 0;
            while (result.next()) {
                int idGrad = result.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = result.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = result.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int idDrzava = result.getInt(4);
                drzavaFk.setId(idDrzava);
                String nazivDrzave = result.getString(5);
                drzavaFk.setNaziv(nazivDrzave);
                brojac++;
            }
            if (brojac == 0) {
                System.out.println("Data drzava ne postoji");
                return null;
            }
        } catch (SQLException ignored) {
            return null;
        }
        return grad;
    }

    public Drzava nadjiDrzavu(String drzava) {
        Drzava drzavaResult = new Drzava();
        try {
            stmt = conn.prepareStatement("SELECT d.id, d.naziv, g.id, g.naziv, g.broj_stanovnika FROM drzava d, grad g WHERE d.glavni_grad = g.id AND d.naziv = ?");
            stmt.setString(1, drzava);
            ResultSet result = stmt.executeQuery();
            Grad glavniGrad = new Grad();
            drzavaResult.setGlavniGrad(glavniGrad);
            glavniGrad.setDrzava(drzavaResult);
            while (result.next()) {
                int idDrzava = result.getInt(1);
                drzavaResult.setId(idDrzava);
                String nazivDrzave = result.getString(2);
                drzavaResult.setNaziv(nazivDrzave);
                int idGrad = result.getInt(3);
                glavniGrad.setId(idGrad);
                String nazivGrad = result.getString(4);
                glavniGrad.setNaziv(nazivGrad);
                int brojStanovnika = result.getInt(5);
                glavniGrad.setBrojStanovnika(brojStanovnika);
            }
        } catch (SQLException ignored) {
            System.out.println("Data drzava ne postoji");
            return null;
        }
        return drzavaResult;
    }

    private int dajSljedeciID(String nazivTabele) throws SQLException {
        stmt = conn.prepareStatement("SELECT id FROM " + nazivTabele + " ORDER BY id DESC LIMIT 1");
        var result = stmt.executeQuery();
        int id = 0;
        while (result.next())
            id = result.getInt(1);
        return id + 1;
    }

    private int dajGradIDAkoPostoji(String naziv) throws SQLException {
        stmt = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ? AND broj_stanovnika IS NULL");
        stmt.setString(1, naziv);
        var result = stmt.executeQuery();
        int id = -1;
        while (result.next())
            id = result.getInt(1);
        return id;
    }

    public void dodajGrad(Grad grad) {
        try {
            //Provjera da li je dati grad vec u bazi, pri cemu je broj_stanovnika = NULL
            int idAkoPostoji = dajGradIDAkoPostoji( grad.getNaziv());
            if (idAkoPostoji != -1) {
                grad.setId(idAkoPostoji);
                stmt = conn.prepareStatement("SELECT id FROM drzava WHERE glavni_grad = ?");
                stmt.setInt(1, idAkoPostoji);
                var result = stmt.executeQuery();
                int id = -1;
                while (result.next())
                    id = result.getInt(1);
                Drzava temp = new Drzava();
                temp.setId(id);
                grad.setDrzava(temp);
                izmijeniGrad(grad);
                return;
            }
            //Drzava nalazi u tabeli drzava?
            stmt = conn.prepareStatement("SELECT id FROM drzava WHERE naziv = ?");
            stmt.setString(1, grad.getDrzava().getNaziv());
            ResultSet result = stmt.executeQuery();
            int brojac = 0;
            int idDrzave = 0;
            while (result.next()) {
                idDrzave = result.getInt(1);
                brojac++;
            }

            //Unos grada
            int sljedeciIDGrad = dajSljedeciID("grad");
            stmt = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, ?)");
            stmt.setInt(1, sljedeciIDGrad);
            stmt.setString(2, grad.getNaziv());
            stmt.setInt(3, grad.getBrojStanovnika());
            if (brojac == 0)
                stmt.setNull(4, Types.INTEGER);
            else
                stmt.setInt(4, idDrzave);
            stmt.executeUpdate();

            //Ako nema drzave brojac = 0
            if (brojac == 0) {
                int sljedeciIDDrzava = dajSljedeciID("drzava");
                //Dodaj novu drzavu
                stmt = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
                stmt.setInt(1, sljedeciIDDrzava);
                stmt.setString(2, grad.getDrzava().getNaziv());
                stmt.setInt(3, sljedeciIDGrad);
                stmt.executeUpdate();
            }
        } catch (SQLException ignored) {
            System.out.println("Greska");
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            //Provjera da li se glavni grad nalazi u tabeli gradova
            stmt = conn.prepareStatement("SELECT id FROM grad WHERE naziv = ?");
            stmt.setString(1, drzava.getGlavniGrad().getNaziv());
            ResultSet result = stmt.executeQuery();
            int brojac = 0;
            int idGrada = 0;
            while (result.next()) {
                idGrada = result.getInt(1);
                brojac++;
            }
            int sljedeciIDDrzava = dajSljedeciID("drzava");
            //Unos nove drzave
            stmt = conn.prepareStatement("INSERT INTO drzava VALUES (?, ?, ?)");
            stmt.setInt(1, sljedeciIDDrzava);
            stmt.setString(2, drzava.getNaziv());
            if (brojac == 0)
                stmt.setNull(3, Types.INTEGER);
            else
                stmt.setInt(3, idGrada);
            stmt.executeUpdate();
            // brojac = 0
            if (brojac == 0) {
                // grad
                int sljedeciIDGrad = dajSljedeciID("grad");
                stmt = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, NULL, ?)");
                stmt.setInt(1, sljedeciIDGrad);
                stmt.setString(2, drzava.getGlavniGrad().getNaziv());
                stmt.setInt(3, sljedeciIDDrzava);
                stmt.executeUpdate();

                //Glavni grad u drzavi
                stmt = conn.prepareStatement("UPDATE drzava SET glavni_grad = ? WHERE id = ?");
                stmt.setInt(1, sljedeciIDGrad);
                stmt.setInt(2, sljedeciIDDrzava);
                stmt.executeUpdate();
            }
        } catch (SQLException ignored) {
            System.out.println("Greska");
        }
    }

    public void izmijeniGrad(Grad grad) {
        try{
            stmt=conn.prepareStatement("UPDATE grad SET naziv = ?, broj_stanovnika = ? , drzava= ?, WHERE id = ?");
            stmt.setString(1, grad.getNaziv());
            stmt.setInt(2, grad.getBrojStanovnika());
            stmt.setInt(3, grad.getDrzava().getId());
            stmt.setInt(4, grad.getId());
            System.out.println("Uspjesno izmijenjeno " + stmt.executeUpdate() + " red");

        }catch (SQLException ignored){
            System.out.println("Grad ne postoji!");
        }
    }
}

//stmt.executeUpdate(); ako je u pitanju INSERT, UPDATE ili DELETE i vraća koliko je redova izmijenjeno
//privatna funkcija za konekciju i kreiranje baze
//iz initialize i konstruktora ih pozivati