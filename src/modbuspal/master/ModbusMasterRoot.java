/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package modbuspal.master;

import javax.swing.tree.DefaultMutableTreeNode;
import modbuspal.main.LanguageManager;

/**
 *
 * @author JMC15
 */
public class ModbusMasterRoot
extends DefaultMutableTreeNode
{
    public ModbusMasterRoot()
    {
        setUserObject(LanguageManager.tr("master.tasks_root"));
    }
    
    
}
