package ichttt.mods.allTheCases;

import ichttt.logicsimModLoader.init.LogicSimModLoader;
import ichttt.logicsimModLoader.util.LSMLUtil;
import logicsim.LED;
import logicsim.SWITCH;
import logicsim.TextLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Hotz
 * (c) Tobias Hotz, 2017
 * Licensed under GPL v3
 */
public class CustomOrder {
    private static final List<SWITCH> switchList = new ArrayList<>();
    private static final List<LED> ledList = new ArrayList<>();
    private static boolean isActive = false;
    private static int switchCount = 0;
    private static int ledCount = 0;

    public static void addToSwitchList(SWITCH gate) {
        if (!switchList.contains(gate)) {
            switchList.add(gate);
            Util.addLabelToGate("Switch " + switchCount, gate, LogicSimModLoader.getApp().lsframe.lspanel.gates);
            switchCount++;
        } else
            LSMLUtil.showMessageDialogOnWindowIfAvailable(ModInstance.translate("GateIsOnList"));
    }

    public static void addToLEDList(LED led) {
        if (!ledList.contains(led)) {
            ledList.add(led);
            Util.addLabelToGate("LED " + ledCount, led, LogicSimModLoader.getApp().lsframe.lspanel.gates);
            ledCount++;
        } else
            LSMLUtil.showMessageDialogOnWindowIfAvailable(ModInstance.translate("GateIsOnList"));
    }

    public static List<SWITCH> getSwitchList() {
        return switchList;
    }

    public static List<LED> getLedList() {
        return ledList;
    }

    public static void activate() {
        isActive = true;
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void clear() {
        ledList.clear();
        switchList.clear();
        switchCount = 0;
        ledCount = 0;
    }
}
