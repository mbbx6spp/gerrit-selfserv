package com.lookout.gerrit.selfserv;

import com.google.gerrit.reviewdb.client.AccountGroup;

import com.google.gerrit.server.config.PluginConfigFactory;

public class Defaults {
  public static final String getPluginName() {
    return "selfserv";
  }

  public static final String getDefaultAdminGroup() {
    return getPluginName() + " Admins";
  }

  public static final String getAdminGroupKey() {
    return "adminGroup";
  }

  public static AccountGroup.NameKey getAdminGroupNameKey(PluginConfigFactory cfg) {
    String groupName = cfg.getFromGerritConfig(getPluginName()).
      getString(getAdminGroupKey(), getDefaultAdminGroup());
    return new AccountGroup.NameKey(groupName);
  }
}
