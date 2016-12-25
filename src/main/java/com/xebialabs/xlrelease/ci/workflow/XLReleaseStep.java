package com.xebialabs.xlrelease.ci.workflow;

import com.google.inject.Inject;
import com.sun.istack.NotNull;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.xebialabs.xlrelease.ci.NameValuePair;
import com.xebialabs.xlrelease.ci.XLReleaseNotifier;
import com.xebialabs.xlrelease.ci.util.Release;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.xebialabs.xlrelease.ci.XLReleaseNotifier.XLReleaseDescriptor;
import static hudson.util.FormValidation.error;
import static hudson.util.FormValidation.warning;

public class XLReleaseStep extends AbstractStepImpl {

    public String serverCredentials = null;
    public String template = null;
    public String version = null;
    public List<NameValuePair> variables = null;
    public boolean startRelease = false;

    @DataBoundConstructor
    public XLReleaseStep(String serverCredentials, String template, String version, List<NameValuePair> variables, boolean startRelease) {
        this.serverCredentials = serverCredentials;
        this.template = template;
        this.version = version;
        this.variables = variables;
        this.startRelease = startRelease;
    }

    @DataBoundSetter
    public void setServerCredentials(String serverCredentials) {
        this.serverCredentials = serverCredentials;
    }

    @DataBoundSetter
    public void setTemplate(String template) {
        this.template = Util.fixEmptyAndTrim(template);
    }

    @DataBoundSetter
    public void setVersion(String version) {
        this.version = Util.fixEmptyAndTrim(version);
    }

    @DataBoundSetter
    public void setVariables(List<NameValuePair> variables) {
        this.variables = variables;
    }

    @DataBoundSetter
    public void setStartRelease(boolean startRelease) {
        this.startRelease = startRelease;
    }

    @Override
    public XLReleaseStepDescriptor getDescriptor() {
        return (XLReleaseStepDescriptor) super.getDescriptor();
    }

    @Extension
    public static final class XLReleaseStepDescriptor extends AbstractStepDescriptorImpl {

        private final XLReleaseDescriptor descriptor;

        public XLReleaseStepDescriptor() {
            super(XLReleaseExecution.class);
            descriptor = new XLReleaseDescriptor();
        }

        @Override
        public String getFunctionName() {
            return "xlRelease";
        }

        @Override
        public String getDisplayName() {
            return "Invoke a XLR Release";
        }

        public AutoCompletionCandidates doAutoCompleteTemplate(@QueryParameter final String value) {
            return getXLReleaseDescriptor().doAutoCompleteTemplate(value);
        }

        public FormValidation doValidateTemplate(@QueryParameter String credential, @QueryParameter final String template) {
           return getXLReleaseDescriptor().doValidateTemplate(credential,template);
        }
        public ListBoxModel doFillCredentialItems() {
            return getXLReleaseDescriptor().doFillCredentialItems();
        }


        public Map<String, String> getVariablesOf(final String credential, final String template) {
            return getXLReleaseDescriptor().getVariablesOf(credential, template);
        }

        public FormValidation doCheckCredential(@QueryParameter String credential) {
            return getXLReleaseDescriptor().doCheckCredential(credential);
        }

        public int getNumberOfVariables(@QueryParameter String credential, @QueryParameter String template) {
            return getXLReleaseDescriptor().getNumberOfVariables(credential, template);
        }

        private XLReleaseDescriptor getXLReleaseDescriptor() {
            descriptor.load();
            return descriptor;
        }

    }

    public static final class XLReleaseExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        @Inject
        private transient XLReleaseStep step;

        @StepContextParameter
        private transient EnvVars envVars;

        @StepContextParameter
        private transient TaskListener listener;

        @Override
        protected Void run() throws Exception {
            XLReleaseNotifier releaseNotifier = new XLReleaseNotifier(step.serverCredentials, step.template, step.version, step.variables, step.startRelease);
            releaseNotifier.executeRelease(envVars,listener);
            return null;
        }
    }
}
