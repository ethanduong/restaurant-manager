/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commonfunction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import org.eclipse.persistence.jpa.jpql.parser.DateTime;

/**
 *
 * @author Jame Moriarty
 */
public class ValidateForm {

    public static boolean vltEmpty(String[] List_String_Text_Field, String[] String_label_tex) {

        for (int i = 0; i < List_String_Text_Field.length; i++) {
                System.out.println(String_label_tex[i] + " " + List_String_Text_Field[i]);
                if (List_String_Text_Field[i].isEmpty()) {
                    JOptionPane.showMessageDialog(null, String_label_tex[i] + " không được trống !");
                    return false;              
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Date d = new Date();
        SimpleDateFormat fmd = new SimpleDateFormat("hh:mm dd-MM-yyyy");
        System.out.println(fmd.format(d));
    }

}
