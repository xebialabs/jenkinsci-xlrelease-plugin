/**
 * Copyright (c) 2014, XebiaLabs B.V., All rights reserved.
 * <p/>
 * <p/>
 * The XL Release plugin for Jenkins is licensed under the terms of the GPLv2
 * <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>, like most XebiaLabs Libraries.
 * There are special exceptions to the terms and conditions of the GPLv2 as it is applied to
 * this software, see the FLOSS License Exception
 * <https://github.com/jenkinsci/xlrelease-plugin/blob/master/LICENSE>.
 * <p/>
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation; version 2
 * of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth
 * Floor, Boston, MA 02110-1301  USA
 */

package com.xebialabs.xlrelease.ci.server;

import java.util.List;

import com.xebialabs.xlrelease.ci.NameValuePair;
import com.xebialabs.xlrelease.ci.util.Folder;
import com.xebialabs.xlrelease.ci.util.Release;
import com.xebialabs.xlrelease.ci.util.TemplateVariable;

public class XLReleaseServerConnectorFacade implements XLReleaseServerConnector {

    private XLReleaseServerConnector defaultConnector;

    XLReleaseServerConnectorFacade(String serverUrl, String proxyUrl, String username, String password) {
        this.defaultConnector = new XLReleaseConnectorImpl(serverUrl, proxyUrl, username, password);
    }

    private XLReleaseServerConnector getConnectorForXlrVersion() {
        return defaultConnector;
    }

    @Override
    public void testConnection() {
        getConnectorForXlrVersion().testConnection();
    }

    @Override
    public String getVersion() {
        return defaultConnector.getVersion();
    }

    @Override
    public List<Release> searchTemplates(final String filter) {
        return getConnectorForXlrVersion().searchTemplates(filter);
    }

    @Override
    public List<Release> getAllTemplates() {
        return getConnectorForXlrVersion().getAllTemplates();
    }

    @Override
    public List<TemplateVariable> getVariables(String templateId) {
        return getConnectorForXlrVersion().getVariables(templateId);
    }

    @Override
    public Release createRelease(final String resolvedTemplate, final String resolvedVersion, final List<NameValuePair> variables) {
        return getConnectorForXlrVersion().createRelease(resolvedTemplate, resolvedVersion, variables);
    }

    @Override
    public void startRelease(final String releaseId) {
        getConnectorForXlrVersion().startRelease(releaseId);
    }

    @Override
    public String getServerURL() {
        return getConnectorForXlrVersion().getServerURL();
    }

    @Override
    public Folder getFolderByPath(String path) {
        return getConnectorForXlrVersion().getFolderByPath(path);
    }

    @Override
    public List<Release> getTemplates(String folderId) {
        return getConnectorForXlrVersion().getTemplates(folderId);
    }

    @Override
    public List<Folder> getFolders(String folderId) {
        return getConnectorForXlrVersion().getFolders(folderId);
    }

    @Override
    public String getFolderId(String queryString) {
        return getConnectorForXlrVersion().getFolderId(queryString);
    }
}
