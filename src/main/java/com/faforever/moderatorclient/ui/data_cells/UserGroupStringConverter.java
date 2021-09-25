package com.faforever.moderatorclient.ui.data_cells;

import com.faforever.moderatorclient.ui.domain.UserGroupFX;
import javafx.util.StringConverter;

public class UserGroupStringConverter extends StringConverter<UserGroupFX> {
    @Override
    public String toString(UserGroupFX userGroupFX) {
        if (userGroupFX == null) {
            return "";
        }
        return userGroupFX.getTechnicalName();
    }

    @Override
    public UserGroupFX fromString(String s) {
        throw new UnsupportedOperationException();
    }
}
