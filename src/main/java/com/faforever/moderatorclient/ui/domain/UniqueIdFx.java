package com.faforever.moderatorclient.ui.domain;

import com.faforever.commons.api.dto.BanLevel;
import com.faforever.commons.api.dto.BanStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.time.OffsetDateTime;

public class UniqueIdFx extends AbstractEntityFX {
    private final StringProperty hash;
    private final StringProperty uuid;
    private final StringProperty memorySerialNumber;
    private final StringProperty deviceId;
    private final StringProperty manufacturer;
    private final StringProperty name;
    private final StringProperty processorId;
    private final StringProperty SMBIOSBIOSVersion;
    private final StringProperty serialNumber;
    private final StringProperty volumeSerialNumber;

    public UniqueIdFx() {
        hash = new SimpleStringProperty();
        uuid = new SimpleStringProperty();
        memorySerialNumber = new SimpleStringProperty();
        deviceId = new SimpleStringProperty();
        manufacturer = new SimpleStringProperty();
        name = new SimpleStringProperty();
        processorId = new SimpleStringProperty();
        SMBIOSBIOSVersion = new SimpleStringProperty();
        serialNumber = new SimpleStringProperty();
        volumeSerialNumber = new SimpleStringProperty();
    }


    public String getHash() {
        return hash.get();
    }

    public void setHash(String hash) {
        this.hash.set(hash);
    }

    public StringProperty hashProperty() {
        return hash;
    }

    public String getUuid() {
        return uuid.get();
    }

    public void setUuid(String uuid) {
        this.uuid.set(uuid);
    }

    public StringProperty uuidProperty() {
        return uuid;
    }

    public String getMemorySerialNumber() {
        return memorySerialNumber.get();
    }

    public void setMemorySerialNumber(String memorySerialNumber) {
        this.memorySerialNumber.set(memorySerialNumber);
    }

    public StringProperty memorySerialNumberProperty() {
        return memorySerialNumber;
    }

    public String getDeviceId() {
        return deviceId.get();
    }

    public void setDeviceId(String deviceId) {
        this.deviceId.set(deviceId);
    }

    public StringProperty deviceIdProperty() {
        return deviceId;
    }

    public String getManufacturer() {
        return manufacturer.get();
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer.set(manufacturer);
    }

    public StringProperty manufacturerProperty() {
        return manufacturer;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getProcessorId() {
        return processorId.get();
    }

    public StringProperty processorIdProperty() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId.set(processorId);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSMBIOSBIOSVersion() {
        return SMBIOSBIOSVersion.get();
    }

    public StringProperty SMBIOSBIOSVersionProperty() {
        return SMBIOSBIOSVersion;
    }

    public void setSMBIOSBIOSVersion(String SMBIOSBIOSVersion) {
        this.SMBIOSBIOSVersion.set(SMBIOSBIOSVersion);
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public StringProperty serialNumberProperty() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber.set(serialNumber);
    }

    public String getVolumeSerialNumber() {
        return volumeSerialNumber.get();
    }

    public StringProperty volumeSerialNumberProperty() {
        return volumeSerialNumber;
    }

    public void setVolumeSerialNumber(String volumeSerialNumber) {
        this.volumeSerialNumber.set(volumeSerialNumber);
    }
}
