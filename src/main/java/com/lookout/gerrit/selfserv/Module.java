package com.lookout.gerrit.selfserv;

import com.google.inject.Inject;
import com.google.inject.AbstractModule;

import com.google.gerrit.server.config.PluginConfig;
import com.google.gerrit.server.config.PluginConfigFactory;

import static com.lookout.gerrit.selfserv.Defaults.*;

public class Module extends AbstractModule {
  @Inject
  private PluginConfigFactory _pluginCfg;

  @Override
  protected void configure() {
    final PluginConfig cfg = _pluginCfg.
      getFromGerritConfig(getPluginName());

    final String adminGrp = cfg.getString(getAdminGroupKey());

    if (adminGrp == null) {
      cfg.setString(
        getAdminGroupKey(),
        getDefaultAdminGroup());
    }
  }
}
