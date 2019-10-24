package ichttt.mods.allTheCases;

import ichttt.logicsimModLoader.loader.Loader;
import logicsim.Gate;
import logicsim.GateList;
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
public class Util {
    public static List<SWITCH> getSwitchesFromGateList(GateList list) {
        List<SWITCH> switches = new ArrayList<>();
        for (Object g : list.gates) {
            if (g instanceof SWITCH)
                switches.add((SWITCH)g);
        }
        return switches;
    }

    public static List<LED> getLEDSFromGateList(GateList list) {
        List<LED> leds = new ArrayList<>();
        for (Object g : list.gates) {
            if (g instanceof LED)
                leds.add((LED)g);
        }
        return leds;
    }

    public static void addLabelToGate(TextLabel label, Gate gate, GateList activeGateList) {
        boolean add = true;
        if (ModInstance.hasGateWithNamesMod) {
            try {
                Class<?> compatClass = Class.forName("ichttt.mods.gateNameLink.GateNameLink", false, Loader.getInstance().getModClassLoader());
                compatClass.getDeclaredMethod("addTextField", Gate.class, TextLabel.class).invoke(null, gate, label);
                add = false;
            } catch (ReflectiveOperationException e) {
                ModInstance.getLogger().warning("Failed GateNameLink compat\n" + e.getLocalizedMessage());
                add = true;
            }
        }
        if (add) {
            label.x = gate.x + 50;
            label.y = gate.y;
            activeGateList.addGate(label);
        }
    }
}
