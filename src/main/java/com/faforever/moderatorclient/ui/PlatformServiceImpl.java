package com.faforever.moderatorclient.ui;

import javafx.application.HostServices;

public class PlatformServiceImpl implements PlatformService {
    private final HostServices hostService;

    public PlatformServiceImpl(HostServices hostServices) {
        this.hostService = hostServices;
    }

    @Override
    public void showDocument(String url) {
        hostService.showDocument(url);
    }
}
