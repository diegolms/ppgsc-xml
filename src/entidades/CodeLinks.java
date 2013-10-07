/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

/**
 *
 * @author Diego
 * @author Lucas
 */
public class CodeLinks {
    private String rel;
    private String fonte;
    private String alvo;
    
    
    public CodeLinks(){
        
    }
    
    public CodeLinks(String rel, String fonte, String alvo){
        this.rel = rel;
        this.fonte = fonte;
        this.alvo = alvo;
        
    }
            
    
    public String getRel() {
        return rel;
    }

    public void setRel(String real) {
        this.rel = real;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getAlvo() {
        return alvo;
    }

    public void setAlvo(String alvo) {
        this.alvo = alvo;
    }
    
}
