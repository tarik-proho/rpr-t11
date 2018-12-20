package ba.unsa.etf.rpr;

import java.util.ArrayList;

public class Drzava {
    private Integer id;
    private String naziv;
    private Grad glavniGrad;

    public Drzava() {}

    public Drzava(Integer id, String naziv, Grad glavniGrad){
        this.id=id;
        this.naziv=naziv;
        this.glavniGrad=glavniGrad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNaziv(String naziv) {
        this.naziv=naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setGlavniGrad(Grad grad) {
        this.glavniGrad=grad;
    }

    public Grad getGlavniGrad() {
        return glavniGrad;
    }
}
