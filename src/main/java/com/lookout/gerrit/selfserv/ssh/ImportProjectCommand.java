package com.lookout.gerrit.selfserv.ssh;

import com.google.inject.Inject;
import com.google.gerrit.common.data.GlobalCapability;
import com.google.gerrit.extensions.annotations.Export;
import com.google.gerrit.extensions.annotations.RequiresCapability;
import com.google.gerrit.extensions.api.GerritApi;

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
@Export("import-project")
@RequiresCapability(GlobalCapability.ADMINISTRATE_SERVER)
@CommandMetaData( name = "import-project",
                  description = "Imports projects from existing Git remote")
public class ImportProjectCommand extends SshCommand {
  private final GerritApi _gerritApi;
  private final PluginConfigFactory _pluginCfg;

  @Argument(index = 0,
            metaVar = "NAME",
            required = true)
  private String _name;

  @Inject
  public ImportProjectCommand(
    final GerritApi gerritApi,
    final PluginConfigFactory pluginCfg) {

    this._gerritApi = gerritApi;
    this._pluginCfg = pluginCfg;
  }

  /**
    * Used for testing purposes to inject (manually) mocks.
    */
  public ImportProjectCommand(
    final GerritApi gerritApi,
    final PluginConfigFactory pluginCfg,
    final PrintWriter printWriter) {

    this(gerritApi, pluginCfg);

    if (printWriter != null) {
      this.stdout = printWriter;
    }
  }

  @Override
  public void run() throws UnloggedFailure, Failure, Exception {
    if (_name == null) {
      stdout.println(usage());
    } else {
      throw new Failure(1, "Not implemented.");
    }
  }
}
