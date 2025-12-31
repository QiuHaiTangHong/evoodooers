package com.helliongames.evoodooers;

import com.helliongames.evoodooers.registration.*;

public class CommonClass {
    public static void init() {
        EvoodooersBlocks.loadClass();
        EvoodooersItems.loadClass();
        EvoodooersBlockEntities.loadClass();
        EvoodooersRecipes.loadClass();
        EvoodooersTabs.loadClass();
        EvoodooersSoundEvents.loadClass();
    }
}