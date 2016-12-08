package com.xebialabs.xlrelease.ci.server;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.GenericType;
import com.xebialabs.xlrelease.ci.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import com.xebialabs.xlrelease.ci.NameValuePair;

public class XLReleaseConnectorImpl extends AbstractXLReleaseConnector {
    public XLReleaseConnectorImpl(final String serverUrl, final String proxyUrl, final String username, final String password) {
        super(serverUrl, proxyUrl, username, password);
    }

    @Override
    public ClientResponse getVariablesResponse(final String templateId) {
        WebResource service = buildWebResource();
        return service
                .path("api/v1/templates/Applications")
                .path(templateId)
                .path("variables")
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);
    }

    @Override
    public ClientResponse createReleaseResponse(final String resolvedTemplate, final String resolvedVersion, final List<NameValuePair> variables) {
        WebResource service = buildWebResource();
        String queryString = TemplatePathUtil.markSlashEscapeSeq(resolvedTemplate);
        final String folderId = getFolderId(queryString);
        final String templateName = TemplatePathUtil.unEscapeSlashSeq(queryString.substring(queryString.lastIndexOf(SLASH_CHARACTER) + 1));
        List<Release> templates = getTemplates(folderId);

        CollectionUtils.filter(templates, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
            return ((Release) o).getTitle().equals(templateName);
            }
        });

        String templateInternalId;

        if (templates.size() == 0) {
            throw new RuntimeException ("No template found with given path " + resolvedTemplate + templateName);
        } else if (templates.size() == 1) {
            Release template = templates.get(0);
            templateInternalId = template.getInternalId();
        } else {
            throw new RuntimeException("Multiple templates found with same title.");
        }
        CreateReleasePublicForm createReleasePublicForm = new CreateReleasePublicForm(resolvedVersion, convertToVariablesMap(variables));
        return service
                .path("api/v1/templates/Applications")
                .path(templateInternalId)
                .path("create")
                .type(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, createReleasePublicForm);
    }

    @Override
    public ClientResponse startReleaseResponse(final String releaseId) {
        WebResource service = buildWebResource();
        return service
                .path("api/v1/releases/Applications")
                .path(releaseId)
                .path("start")
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);
    }

    @Override
    public List<TemplateVariable> filterVariables(final List<TemplateVariable> variables) {
        CollectionUtils.filter(variables, new Predicate() {
            public boolean evaluate(Object o) {
                if (o instanceof TemplateVariable) {
                    List<String> acceptedTypes = new ArrayList<String>();
                    acceptedTypes.add("xlrelease.StringVariable");
                    acceptedTypes.add("xlrelease.XLDeployPackageVariable");
                    acceptedTypes.add("xlrelease.XLDeployEnvironmentVariable");
                    return acceptedTypes.contains(((TemplateVariable) o).getType());
                }
                return false;
            }
        });
        CollectionUtils.transform(variables, new Transformer() {
            @Override
            public Object transform(final Object o) {
                if (o instanceof TemplateVariable) {
                    String key = ((TemplateVariable) o).getKey();
                    if (key != null) {
                        ((TemplateVariable) o).setKey(String.format("${%s}", key));
                    }
                }
                return o;
            }
        });
        return variables;
    }

    @Override
    public Folder getFolderByPath(String path) {
        WebResource service = buildWebResource();
        return service
                .path("api/v1/folders/find")
                .queryParam("byPath",path)
                .accept(MediaType.APPLICATION_JSON)
                .get(Folder.class);
    }

    @Override
    public List<Release> getTemplates(String folderId) {
        WebResource service = buildWebResource();
        GenericType<List<Release>> genericType = new GenericType<List<Release>>() {
        };
        return service
                .path("api/v1/folders")
                .path(folderId)
                .path("templates")
                .accept(MediaType.APPLICATION_JSON)
                .get(genericType);
    }

    @Override
    public List<Folder> getFolders(String folderId) {
        WebResource service = buildWebResource();
        GenericType<List<Folder>> genericType = new GenericType<List<Folder>>() {
        };
        return service
                .path("api/v1/folders")
                .path(folderId)
                .path("list")
                .accept(MediaType.APPLICATION_JSON)
                .get(genericType);
    }
}
