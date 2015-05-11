include_defs('//bucklets/gerrit_plugin.bucklet')

gerrit_plugin(
  name = 'selfserv',
  srcs = glob(['src/main/java/**/*.java']),
  resources = glob(['src/main/resources/**/*']),
  manifest_entries = [
    'Gerrit-PluginName: selfserv',
    'Gerrit-ApiType: plugin',
    'Gerrit-ApiVersion: 2.11',
    'Gerrit-Module: com.lookout.gerrit.selfserv.Module',
    'Gerrit-SshModule: com.lookout.gerrit.selfserv.SshModule',
    'Gerrit-HttpModule: com.lookout.gerrit.selfserv.HttpModule',
  ],
)

# this is required for bucklets/tools/eclipse/project.py to work
java_library(
  name = 'classpath',
  deps = [':selfserv__plugin'],
)

