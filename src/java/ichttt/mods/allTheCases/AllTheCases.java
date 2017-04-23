package ichttt.mods.allTheCases;

import com.google.common.collect.Lists;
import ichttt.logicsimModLoader.init.LogicSimModLoader;
import logicsim.GateList;
import logicsim.LED;
import logicsim.SWITCH;
import logicsim.TextLabel;

import javax.swing.*;
import java.io.File;
import java.util.List;

/**
 * @author Tobias Hotz
 * (c) Tobias Hotz, 2017
 * Licensed under GPL v3
 */
public class AllTheCases {

    private static File chooseDir() {
        JFileChooser chooser;
        chooser = new JFileChooser(new File("").getAbsolutePath());
        File Dir = null;
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(ModInstance.translate("savePath"));
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            Dir = chooser.getSelectedFile();
        }
        return Dir;
    }

    public static void run() {
        ModInstance.getLogger().info("Computing cases");
        GateList list = LogicSimModLoader.getApp().lsframe.lspanel.gates;
        List<SWITCH> switches;
        List<LED> ledList;
        if (CustomOrder.isActive()) {
            switches = CustomOrder.getSwitchList();
            ledList = CustomOrder.getLedList();
            if (ModInstance.invertListWhenManuel.value) {
                switches = Lists.reverse(switches);
                ledList = Lists.reverse(ledList);
            }
        }
        else {
            switches = Util.getSwitchesFromGateList(list);
            ledList = Util.getLEDSFromGateList(list);
        }

        ExcelManager excelManager = new ExcelManager(ModInstance.translate("Solution"), switches.size(), ledList.size());
        boolean genText = false;
        if (!CustomOrder.isActive())
            genText = JOptionPane.showConfirmDialog(LogicSimModLoader.getApp().frame, ModInstance.translate("addGates"), ModInstance.translate("Question"), JOptionPane.YES_NO_OPTION) == 0;

        ModInstance.getLogger().finer("Freezing switch state");
        SwitchStore.collect();
        ModInstance.getLogger().finer("Running algorithm");
        for (int i = 0; i<Math.pow(2, switches.size()); i++) { // Main Algorithm
            StringBuilder s = new StringBuilder(Integer.toBinaryString(i));
            while (s.length() < switches.size())
                s.insert(0, "0"); //Fill up the String
            char[] c = s.toString().toCharArray();
            String[] inputs = new String[switches.size()];
            String[] outputs = new String[ledList.size()];
            for (int count = 0; count<switches.size();count++) {
                SWITCH currentSwitch = switches.get(count);
                if (c[count]=='1')
                    currentSwitch.setOutput(true);
                else if (c[count]=='0')
                    currentSwitch.setOutput(false);
                else
                    throw new RuntimeException("Internal error: invalid char " + c[count]); //Shouldn't happen
                inputs[count] = c[count] + "";
            }
            list.simulate(); //Simulate
            for (int ledPos = 0; ledPos < ledList.size(); ledPos++) {
                boolean result = ledList.get(ledPos).getInput(0)!=null &&ledList.get(ledPos).getInputState(0);
                outputs[ledPos] = result ? "1":"0";
            }
            excelManager.writeLine(inputs, outputs); //Write lines
        }
        ModInstance.getLogger().fine("Successfully ran algorithm and wrote lines, restoring switch state");
        SwitchStore.reset();

        if (genText) {
            for (int i = 0; i < switches.size(); i++) { //Label switches
                TextLabel label = new TextLabel();
                label.text = "Switch " + i;
                Util.addLabelToGate(label, switches.get(i), list);
            }

            for (int i = 0; i < ledList.size(); i++) { //Label LEDs
                TextLabel label = new TextLabel();
                label.text = "LED " + i;
                Util.addLabelToGate(label, ledList.get(i), list);
            }
        }
        JFrame mainFrame = LogicSimModLoader.getApp().frame;
        mainFrame.repaint();

        File dir = chooseDir();
        if (dir == null)
            return;
        if (excelManager.saveSheet(dir.toString(), ModInstance.translate("Result")))
            JOptionPane.showMessageDialog(mainFrame, ModInstance.translate("ExportSuccess"));
        else
            JOptionPane.showMessageDialog(mainFrame, ModInstance.translate("ExportFailed"));
    }
}