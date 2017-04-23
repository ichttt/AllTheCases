package ichttt.mods.allTheCases;

import ichttt.logicsimModLoader.init.LogicSimModLoader;
import logicsim.SWITCH;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tobias Hotz
 * (c) Tobias Hotz, 2017
 * Licensed under GPL v3
 */
public class SwitchStore {
    private static Map<SWITCH, Boolean> stateMap = new HashMap<>();

    static void collect() {
        stateMap.clear();
        List<SWITCH> switchList = Util.getSwitchesFromGateList(LogicSimModLoader.getApp().lsframe.lspanel.gates);
        for (SWITCH s : switchList) {
            stateMap.put(s, s.getOutput(0));
        }
    }

    static void reset() {
        for (Map.Entry<SWITCH, Boolean> entry : stateMap.entrySet()) {
            entry.getKey().setOutput(entry.getValue());
        }
    }
}
