package com.faforever.moderatorclient.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Getter
public enum UserSearchProperty {
    NAME("Name", "login"),
    ID("Id", "id"),
    EMAIL("Email", "email"),
    STEAM_ID("Steam Id", "accountLinks.serviceId"),
    GOG_ID("Gog Id", "accountLinks.serviceId"),
    IP("Ip Address", "recentIpAddress"),
    PREVIOUS_NAME("Previous Name", "names.name"),
    UID("UID Hash", "uniqueIdAssignments.uniqueId.hash"),
    DEVICE_ID("Device Id", "uniqueIdAssignments.uniqueId.deviceId"),
    CPU_NAME("CPU Name", "uniqueIdAssignments.uniqueId.name"),
    UUID("UUID", "uniqueIdAssignments.uniqueId.uuid"),
    SERIAL_NUMBER("Serial Number", "uniqueIdAssignments.uniqueId.serialNumber"),
    PROCESSOR_ID("Processor Id", "uniqueIdAssignments.uniqueId.processorId"),
    BIOS_VERSION("Bios Version", "uniqueIdAssignments.uniqueId.SMBIOSBIOSVersion"),
    VOLUME_SERIAL_NUMBER("Volume Serial Number", "uniqueIdAssignments.uniqueId.volumeSerialNumber"),
    MEMORY_SERIAL_NUMBER("Memory Serial Number", "uniqueIdAssignments.uniqueId.memorySerialNumber"),
    MANUFACTURER("Manufacturer", "uniqueIdAssignments.uniqueId.manufacturer");

    final String caption;
    final String apiKey;

    private static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String IPV6_PATTERN =
            "([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}";

    public record WithValue(UserSearchProperty property, String value) {
    }

    public static WithValue autoDetect(String searchPattern) {
        if (isUUID(searchPattern)) {
            return new WithValue(UUID, searchPattern);
        }
        if (isValidIp(searchPattern)) {
            return new WithValue(IP, searchPattern);
        }
        if (isEmail(searchPattern)) {
            return new WithValue(EMAIL, searchPattern);
        }
        if (isHash(searchPattern)) {
            return new WithValue(UID, searchPattern);
        }
        if (isUserId(searchPattern)) {
            return new WithValue(ID, searchPattern);
        }
        if (isLoginNameAndId(searchPattern)) {
            // parse copy & paste of names with id like TheUsername [id 12345]
            int startIndex = searchPattern.indexOf("[id ") + 4;
            int endIndex = searchPattern.indexOf("]", startIndex);
            return new WithValue(ID, searchPattern.substring(startIndex, endIndex));
        }
        if (isLoginName(searchPattern)) {
            return new WithValue(NAME, searchPattern);
        }
        if (isSteamId(searchPattern)) {
            return new WithValue(STEAM_ID, searchPattern);
        }

        return null;
    }

    private static boolean isUserId(String searchPattern) {
        // restrict user id length, because steam ids are also numeric, but longer
        Pattern pattern = Pattern.compile("^\\d{1,9}$");
        Matcher matcher = pattern.matcher(searchPattern);
        return matcher.matches();
    }

    private static boolean isEmail(String searchPattern) {
        Pattern pattern = Pattern.compile("^.*@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
        Matcher matcher = pattern.matcher(searchPattern);
        return matcher.matches();
    }

    private static boolean isHash(String searchPattern) {
        return searchPattern.matches("^[a-fA-F0-9]+$") && searchPattern.length() == 32;
    }

    private static boolean isLoginName(String searchPattern) {
        return searchPattern.indexOf('@') == -1 && (!Character.isDigit(searchPattern.charAt(0)));
    }

    private static boolean isLoginNameAndId(String searchPattern) {
        return searchPattern.contains("[id ") && searchPattern.contains("]");
    }

    private static boolean isUUID(String searchPattern) {
        return searchPattern.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    private static boolean isValidIp(String searchPattern) {
        return searchPattern.matches(IPV4_PATTERN) || searchPattern.matches(IPV6_PATTERN);
    }

    private static boolean isSteamId(String searchPattern) {
        return searchPattern.matches("^\\d{17}$");
    }
}
