package ichttt.mods.allTheCases;

import com.google.common.eventbus.Subscribe;
import ichttt.logicsimModLoader.VersionBase;
import ichttt.logicsimModLoader.api.Mod;
import ichttt.logicsimModLoader.config.Config;
import ichttt.logicsimModLoader.config.ConfigCategory;
import ichttt.logicsimModLoader.config.entry.BooleanConfigEntry;
import ichttt.logicsimModLoader.event.GateEvent;
import ichttt.logicsimModLoader.event.loading.LSMLInitEvent;
import ichttt.logicsimModLoader.event.loading.LSMLPreInitEvent;
import ichttt.logicsimModLoader.event.loading.LSMLRegistrationEvent;
import ichttt.logicsimModLoader.exceptions.MissingDependencyException;
import ichttt.logicsimModLoader.gui.MenuBarHandler;
import ichttt.logicsimModLoader.init.LogicSimModLoader;
import ichttt.logicsimModLoader.internal.LSMLInternalMod;
import ichttt.logicsimModLoader.internal.LSMLLog;
import ichttt.logicsimModLoader.internal.ModContainer;
import ichttt.logicsimModLoader.loader.Loader;
import ichttt.logicsimModLoader.update.UpdateContext;
import ichttt.logicsimModLoader.util.I18nHelper;
import ichttt.logicsimModLoader.util.LSMLUtil;
import logicsim.Gate;
import logicsim.LED;
import logicsim.SWITCH;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tobias Hotz
 * (c) Tobias Hotz, 2017
 * Licensed under GPL v3
 */
@Mod(modid = "AllTheCases", modName = "AllTheCases", version = "1.0.2", author = "Tobias Hotz")
public class ModInstance implements ActionListener {
    private static final VersionBase REQUIRED_LSML_VERSION = new VersionBase(0, 2, 2);
    private static I18nHelper i18n;
    static boolean hasGateWithNamesMod = false;
    static BooleanConfigEntry autoMode, invertListWhenManuel;
    private static ModContainer container;
    private static boolean isCollecting = false;
    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) {
        Loader.getInstance().addMod(ModInstance.class);
        LogicSimModLoader.startFromDev();
    }

    public static String translate(String toTranslate) {
        return i18n.translate(toTranslate);
    }

    @Subscribe
    public void register(LSMLRegistrationEvent event) {
        if (!LogicSimModLoader.LSML_VERSION.isMinimum(REQUIRED_LSML_VERSION))
            throw new MissingDependencyException(LSMLUtil.getModAnnotationForClass(ModInstance.class), LSMLInternalMod.MODID, REQUIRED_LSML_VERSION);
        container = Loader.getInstance().getModContainerForModID("AllTheCases");
        try {
            event.checkForUpdate(new UpdateContext(container, new URL("https://raw.githubusercontent.com/ichttt/AllTheCases/master/UpdateInformation.txt")).
                    withWebsite(new URL("https://github.com/ichttt/AllTheCases")).
                    enableAutoUpdate(new URL("https://github.com/ichttt/AllTheCases/blob/master/AllTheCases.jar?raw=true"),
                            new URL("https://raw.githubusercontent.com/ichttt/AllTheCases/master/AllTheCases.modinfo")));
        } catch (Exception e) {
            LSMLLog.warning("[AllTheCases] Failed to register UpdateChecker!");
        }
    }

    @Subscribe
    public void onPreInit(LSMLPreInitEvent event) {
        i18n = new I18nHelper("AllTheCases");
        logger = event.getCustomLogger(container.mod.modid());
        Config config = new Config(container);
        ConfigCategory GENERAL = new ConfigCategory("GENERAL");
        List<String> comment = new ArrayList<>();
        comment.add("Toggles autoMode.");
        comment.add("If false, you will have to manuel select the order");
        autoMode = new BooleanConfigEntry("autoMode", true, comment);
        invertListWhenManuel = new BooleanConfigEntry("invertList", false, "Invert the list when in manuel mode.");
        GENERAL.addEntry(autoMode);
        GENERAL.addEntry(invertListWhenManuel);
        config.addCategory(GENERAL);
        config.load();
        config.save();
        logger.fine("Config created successfully");
    }

    @Subscribe
    public void onInit(LSMLInitEvent event) {
        hasGateWithNamesMod = Loader.getInstance().hasMod("GateNameLink");
        if (hasGateWithNamesMod) {
            logger.info("GateNameLink compat loaded!");
        }

        JMenuItem item = new JMenuItem(translate("calcPossiblePaths"));
        item.addActionListener(this);
        MenuBarHandler.mods.add(item);

        if (!autoMode.value) {
            CustomOrder.activate();
        }
    }

    @Subscribe
    public void onGateClicked(GateEvent.GateSelectionEvent event) {
        if (!CustomOrder.isActive() || !isCollecting)
            return;
        Gate gate = event.gate;
        if (gate instanceof LED)
            CustomOrder.addToLEDList((LED) gate);
        else if (gate instanceof SWITCH)
            CustomOrder.addToSwitchList((SWITCH) gate);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (isCollecting || !CustomOrder.isActive()) {
                isCollecting = false;
                AllTheCases.run();
                CustomOrder.clear();
            } else {
                isCollecting = true;
                LSMLUtil.showMessageDialogOnWindowIfAvailable(translate("CustomOrder"));
            }
        } catch (Throwable t) {
            logger.warning("Failed to execute!\n" + t.getLocalizedMessage());
            LSMLUtil.showMessageDialogOnWindowIfAvailable("Failed to execute!\n" + t.getLocalizedMessage());
            throw t;
        }
    }
}
