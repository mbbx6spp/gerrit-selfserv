package com.lookout.gerrit.selfserv;

import com.google.gerrit.sshd.PluginCommandModule;

import com.lookout.gerrit.selfserv.ssh.AddUserCommand;
import com.lookout.gerrit.selfserv.ssh.CreateProjectCommand;
import com.lookout.gerrit.selfserv.ssh.ImportProjectCommand;

class SshModule extends PluginCommandModule {
  @Override
  protected void configureCommands() {
    command("add-user").to(AddUserCommand.class);
    command("create-project").to(CreateProjectCommand.class);
    command("import-project").to(ImportProjectCommand.class);
  }
}
