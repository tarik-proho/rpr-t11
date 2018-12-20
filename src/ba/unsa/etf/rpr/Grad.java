package ba.unsa.etf.rpr;

public class Grad {
    private Integer id;
    private String naziv;
    private Integer brojStanovnika;
    private Drzava drzava;

    public Grad() {}
    public Grad(Integer id, String naziv, Integer brojStanovnika, Drzava drzava){
        this.id=id;
        this.naziv=naziv;
        this.brojStanovnika=brojStanovnika;
        this.drzava=drzava;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv=naziv;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava=drzava;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika=brojStanovnika;
    }



}
