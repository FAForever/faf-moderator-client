package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.moderatorclient.ui.domain.GroupPermissionFX;
import javafx.util.StringConverter;

public class GroupPermissionStringConverter extends StringConverter<GroupPermissionFX> {
    public String toString(GroupPermissionFX groupPermissionFX) {
        if (groupPermissionFX == null) {
            return "";
        }
        return groupPermissionFX.getTechnicalName();
    }

    @Override
    public GroupPermissionFX fromString(String s) {
        throw new UnsupportedOperationException();
    }
}
