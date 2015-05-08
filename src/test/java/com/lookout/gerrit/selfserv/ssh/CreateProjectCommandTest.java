package com.lookout.gerrit.selfserv.ssh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.rules.ExpectedException;

import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.projects.Projects;
import com.google.gerrit.extensions.restapi.RestApiException;

import com.google.gerrit.reviewdb.client.AccountGroup;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.AccountCache;
import com.google.gerrit.server.account.GroupCache;
import com.google.gerrit.server.account.GroupMembership;
import com.google.gerrit.server.config.PluginConfigFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LoggerFactory.class, AccountGroup.class })
public class CreateProjectCommandTest {

  private GerritApi _gerritApi;
  private CurrentUser _currentUser;
  private GroupCache _groupCache;
  private Logger _logger;
  private PluginConfigFactory _pluginCfg;
  private LoggerFactory _loggerFactory;
  private PrintWriter _stdout;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setup() throws Exception {
    _gerritApi = mock(GerritApi.class);
    final Projects projects = mock(Projects.class);
    when(projects.name(anyString())).thenReturn(null);
    when(_gerritApi.projects()).thenReturn(projects);

    _logger = mock(Logger.class);
    _stdout = mock(PrintWriter.class);
    mockStatic(LoggerFactory.class);
    when(LoggerFactory.getLogger(any(Class.class))).thenReturn(_logger);

    _pluginCfg = mock(PluginConfigFactory.class);

    _currentUser = mock(CurrentUser.class);
    final GroupMembership groups = mock(GroupMembership.class);
    when(_currentUser.getEffectiveGroups()).thenReturn(groups);

    _groupCache = mock(GroupCache.class);
    final AccountGroup grp = mock(AccountGroup.class);
    when(_groupCache.get(any(AccountGroup.NameKey.class))).thenReturn(grp);

  }

  // At the moment we are just testing no exception are thrown and that
  // the #run() method completes each time.
  @Test
  public void testSshCommandWithSelfservAdminUser() throws Exception {
    final CreateProjectCommand cmd = new CreateProjectCommand(
      _gerritApi,
      _pluginCfg,
      _currentUser,
      _groupCache,
      _stdout);
    cmd.run();
  }
}
