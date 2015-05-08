package com.lookout.gerrit.selfserv.ssh;

import com.google.inject.Inject;
import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.Export;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.projects.ProjectApi;
import com.google.gerrit.extensions.api.projects.ProjectInput;
import com.google.gerrit.extensions.client.InheritableBoolean;
import com.google.gerrit.extensions.client.SubmitType;
import com.google.gerrit.extensions.common.ProjectInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gerrit.extensions.restapi.ResourceConflictException;

import com.google.gerrit.reviewdb.client.AccountGroup;

import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.GroupMembership;
import com.google.gerrit.server.account.GroupCache;
import com.google.gerrit.server.account.GroupControl;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.git.strategy.FastForwardOnly;

import com.google.gerrit.sshd.SshCommand;
import com.google.gerrit.sshd.CommandMetaData;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import java.util.List;
import java.util.ArrayList;

import java.io.PrintWriter;

import static com.lookout.gerrit.selfserv.Defaults.*;

/**
  * Creates project in selfserv plugin using optinionated scheme:
  * * Set submit strategy to fast-forward only
  */
@Export("create-project")
@RequiresCapability(GlobalCapability.CREATE_PROJECT)
@CommandMetaData( name = "create-project",
                  description = "Create a new project and associated Git repository")
public class CreateProjectCommand extends SshCommand {
  private final GerritApi _gerritApi;
  private final CurrentUser _currentUser;
  private final GroupCache _groupCache;
  private final PluginConfigFactory _pluginCfg;

  @Argument(index = 0,
            metaVar = "NAME",
            required = true)
  private String _name;

  @Option(name = "--description",
          usage = "Description of the project to create",
          metaVar = "DESC")
  private String _description;

  @Option(name = "--parent",
          usage = "Name of parent repository",
          metaVar = "PARENT")
  private String _parent;

  @Option(name = "--empty-commit",
          usage = "Creates initial empty commit")
  private boolean _emptyCommit = false;

  @Option(name = "--change-id",
          usage = "Require Change-Id in commits")
  private InheritableBoolean _requireChangeId = InheritableBoolean.TRUE;

  @Inject
  public CreateProjectCommand(
    final GerritApi gerritApi,
    final PluginConfigFactory pluginCfg,
    final CurrentUser currentUser,
    final GroupCache groupCache) {

    this._gerritApi = gerritApi;
    this._pluginCfg = pluginCfg;
    this._currentUser = currentUser;
    this._groupCache = groupCache;
  }

  /**
    * Used for testing purposes to inject (manually) mocks.
    */
  public CreateProjectCommand(
    final GerritApi gerritApi,
    final PluginConfigFactory pluginCfg,
    final CurrentUser currentUser,
    final GroupCache groupCache,
    final PrintWriter printWriter) {

    this(gerritApi, pluginCfg, currentUser, groupCache);

    if (printWriter != null) {
      this.stdout = printWriter;
    }
  }

  @Override
  public void run() throws UnloggedFailure, Failure, Exception {
    if (_name == null) {
      stdout.println(usage());
    } else {
      // TODO: Extract out into "action" class a la Strategy/Command design
      // pattern since we are targetting Java 7 for the moment which doesn't
      // permit natural higher order functions.
      final GroupMembership groups = getGroups();
      final AccountGroup.NameKey nameKey = getAdminGroupNameKey(_pluginCfg);
      final AccountGroup ag = _groupCache.get(nameKey);

      if (ag == null) {
        // TODO: Decide if I should throw an UnloggedFailure here instead of Failure?
        throw new Failure(1, "Expected admin group (" + nameKey.toString() + ") does not exist.");
      }

      if (groups != null && groups.contains(ag.getGroupUUID())) {
        final ProjectInput input = new ProjectInput();
        input.name = _name;
        input.description = _description;
        input.submitType = SubmitType.FAST_FORWARD_ONLY;
        input.createEmptyCommit = _emptyCommit;
        input.requireChangeId = _requireChangeId;

        ProjectApi projectApi = _gerritApi.projects().name(_name);
        try {
          projectApi.create(input);
          stdout.println("Created project: " + _name);
        } catch (ResourceConflictException rce) {
          throw new Failure(1, "Project " + _name + " already exists.", rce);
        }
      }
    }
  }

  private GroupMembership getGroups() {
    return _currentUser.getEffectiveGroups();
  }

}
