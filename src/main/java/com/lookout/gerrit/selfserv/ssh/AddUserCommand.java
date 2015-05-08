package com.lookout.gerrit.selfserv.ssh;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.common.collect.Lists;
import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.Export;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.restapi.IdString;
import com.google.gerrit.extensions.restapi.TopLevelResource;
import com.google.gerrit.extensions.restapi.UnprocessableEntityException;

import com.google.gerrit.reviewdb.client.AccountGroup;

import com.google.gerrit.server.account.GroupCache;
import com.google.gerrit.server.group.AddMembers;
import com.google.gerrit.server.group.GroupsCollection;
import com.google.gerrit.server.group.GroupResource;

import com.google.gerrit.server.config.PluginConfigFactory;

import com.google.gerrit.sshd.SshCommand;
import com.google.gerrit.sshd.CommandMetaData;

import org.kohsuke.args4j.Argument;

import java.util.List;
import java.util.ArrayList;

import java.io.PrintWriter;

import static com.lookout.gerrit.selfserv.Defaults.*;

/**
  * Adds a user to the +adminGroup+ for the selfserv plugin.
  */
@Export("add-user")
@RequiresCapability(GlobalCapability.ADMINISTRATE_SERVER)
@CommandMetaData( name = "add-user",
                  description = "Add user to the selfserv.adminGroup and grant appropriate capabilities")
public class AddUserCommand extends SshCommand {
  private final Provider<AddMembers> _addMembers;
  private final GroupsCollection _groupsCollection;
  private final GroupCache _groupCache;
  private final PluginConfigFactory _pluginCfg;

  @Argument(index = 0,
            metaVar = "USERNAME",
            required = true)
  private String _username;

  @Inject
  public AddUserCommand(
    final Provider<AddMembers> addMembers,
    final GroupsCollection groupsCollection,
    final GroupCache groupCache,
    final PluginConfigFactory pluginCfg) {

    this._addMembers = addMembers;
    this._groupsCollection = groupsCollection;
    this._groupCache = groupCache;
    this._pluginCfg = pluginCfg;
  }

  /**
    * Used for testing purposes to inject (manually) mocks.
    */
  public AddUserCommand(
    final Provider<AddMembers> addMembers,
    final GroupsCollection groupsCollection,
    final GroupCache groupCache,
    final PluginConfigFactory pluginCfg,
    final PrintWriter printWriter) {

    this(addMembers, groupsCollection, groupCache, pluginCfg);

    if (printWriter != null) {
      this.stdout = printWriter;
    }
  }

  @Override
  public void run() throws UnloggedFailure, Failure, Exception {
    if (_username == null) {
      stdout.println(usage());
    } else {
      final AccountGroup.NameKey nameKey = getAdminGroupNameKey(_pluginCfg);
      final AccountGroup ag = _groupCache.get(nameKey);

      if (ag == null) {
        // TODO: Decide if I should throw an UnloggedFailure here instead of Failure?
        throw new Failure(1, "Expected admin group (" + nameKey.toString() + ") does not exist.");
      }

      final GroupResource resource = _groupsCollection.parse(
        TopLevelResource.INSTANCE,
        IdString.fromUrl(ag.getGroupUUID().get()));
      final List<String> members = Lists.newLinkedList();
      members.add(_username);
      final AddMembers.Input input = AddMembers.Input.fromMembers(members);
      try {
        _addMembers.get().apply(resource, input);
        stdout.println("Added " + _username + " to adminGroup");
      } catch (UnprocessableEntityException uee) { /* FYI this is gross, but the API is a clusterfuck, sorry! */
        throw new Failure(1, "Username (" + _username + ") couldn't be added to adminGroup", uee);
      }
    }
  }
}
