package ichttt.mods.allTheCases;

import ichttt.mods.gateNameLink.GateNameLink;
import logicsim.*;

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
        if (ModInstance.hasGateWithNamesMod) {
            GateNameLink.addTextField(gate, label);
        } else {
            label.x = gate.x + 50;
            label.y = gate.y;
            activeGateList.addGate(label);
        }
    }
}
