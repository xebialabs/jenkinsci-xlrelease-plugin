package com.xebialabs.xlrelease.ci.workflow;

import com.google.inject.Inject;
import com.xebialabs.xlrelease.ci.NameValuePair;
import com.xebialabs.xlrelease.ci.XLReleaseNotifier;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Util;
import hudson.model.AutoCompletionCandidates;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.util.List;
import java.util.Map;

import static com.xebialabs.xlrelease.ci.XLReleaseNotifier.XLReleaseDescriptor;

public class XLReleaseStep extends AbstractStepImpl {

    public String serverCredentials = null;
    public String template = null;
    public String version = null;
    public String releaseTitle = null;
    public List<NameValuePair> variables = null;
    public boolean startRelease = false;

    @DataBoundConstructor
    public XLReleaseStep(String serverCredentials, String template, String version, List<NameValuePair> variables, boolean startRelease, String releaseTitle) {
        this.serverCredentials = serverCredentials;
        this.template = template;
        this.version = version;
        this.variables = variables;
        this.startRelease = startRelease;
        this.releaseTitle = releaseTitle;
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

    @DataBoundSetter
    public void setReleaseTitle(String releaseTitle) {
        this.releaseTitle = releaseTitle;
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
            return "xlrCreateRelease";
        }

        @Override
        public String getDisplayName() {
            return "Create and invoke a XLR release";
        }

        public AutoCompletionCandidates doAutoCompleteTemplate(@QueryParameter final String value) {
            return getXLReleaseDescriptor().doAutoCompleteTemplate(value);
        }

        public FormValidation doValidateTemplate(@QueryParameter String serverCredentials, @QueryParameter final String template) {
            return getXLReleaseDescriptor().doValidateTemplate(serverCredentials, template);
        }

        public ListBoxModel doFillServerCredentialsItems() {
            return getXLReleaseDescriptor().doFillCredentialItems();
        }


        public Map<String, String> getVariablesOf(final String credential, final String template) {
            return getXLReleaseDescriptor().getVariablesOf(credential, template);
        }

        public FormValidation doCheckServerCredentials(@QueryParameter String serverCredentials) {
            return getXLReleaseDescriptor().doCheckCredential(serverCredentials);
        }

        public int getNumberOfVariables(@QueryParameter String serverCredentials, @QueryParameter String template) {
            return getXLReleaseDescriptor().getNumberOfVariables(serverCredentials, template);
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
            XLReleaseNotifier releaseNotifier = new XLReleaseNotifier(step.serverCredentials, step.template, (step.releaseTitle != null) ? step.releaseTitle : step.version, step.variables, step.startRelease);
            releaseNotifier.executeRelease(envVars, listener);
            return null;
        }
    }
}
