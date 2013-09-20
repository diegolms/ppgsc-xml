/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author Diego
 */
public class FuncoesUtils {
    
    public static void mensagem(Component frame, String titulo, String mensagem, int tipo){
	
       JOptionPane.showMessageDialog(frame, mensagem, titulo, tipo);        
    }

    public static void mostrarMensagem(Component frame, String titulo, String mensagem){

        mensagem(frame, titulo, mensagem, JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void mostrarMensagemErro(Component frame, String titulo, String mensagem){
	mensagem(frame, titulo, mensagem, JOptionPane.ERROR_MESSAGE);
    }
}
