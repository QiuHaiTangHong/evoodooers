package com.helliongames.evoodooers.container;

import com.helliongames.evoodooers.Constants;

public enum TagEnum {
    HAIR_OWNER_UUID("hair_owner_uuid"),
    LAST_SLEPT_UUID("last_slept_uuid"),
    CONNECTED_PLAYER_UUID("connected_player_uuid"),
    UNBOUND_PLAYER_UUID("unbound_player_uuid");
    final String enumValue;

    TagEnum(String enumValue) {
        this.enumValue = enumValue;
    }

    public String get() {
        return Constants.MOD_ID + ":" + this.enumValue;
    }
}
