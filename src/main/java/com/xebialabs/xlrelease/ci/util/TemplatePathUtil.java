package com.xebialabs.xlrelease.ci.util;

import static com.xebialabs.xlrelease.ci.server.AbstractXLReleaseConnector.*;

/**
 * Created by manish on 08/12/16.
 */
public final class TemplatePathUtil {

    private TemplatePathUtil() {
    }

    public static final String escapeSlashSeq(String string) {
        return string.replaceAll(SLASH_CHARACTER, SLASH_ESCAPE_SEQ);
    }

    public static final String unEscapeSlashSeq(String string) {
        return string.replaceAll(SLASH_MARKER, SLASH_CHARACTER);
    }

    public static final String markSlashEscapeSeq(String string) {
        return string.replaceAll(SLASH_ESCAPE_SEQ, SLASH_MARKER);
    }

    public static final String getFolderPath(String queryString) {
        String folderPath = "";
        if (queryString.split(SLASH_CHARACTER).length > 1) {
            folderPath = queryString.substring(0, queryString.lastIndexOf(SLASH_CHARACTER)) + SLASH_CHARACTER;
        }
        if (queryString.charAt(queryString.length() - 1) == '/')
            folderPath = queryString;

        return folderPath;
    }

    public static final String getSearchString(String queryString) {
        String searchString = "";
        if (queryString.charAt(queryString.length() - 1) != '/')
            searchString = queryString.split(SLASH_CHARACTER)[queryString.split(SLASH_CHARACTER).length - 1];
        return searchString;
    }
}
