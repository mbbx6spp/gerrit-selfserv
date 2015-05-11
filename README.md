## Selfserv Gerrit Plugin

This is a Gerrit plugin that will provide self-service project creation
for those in the `selfserv Admin` group.

There are currently three commands that will be part of this plugin:

* [create-project](src/main/resources/Documentation/cmd-create-project.md)
* [add-user](src/main/resources/Documentation/cmd-add-user.md)
* (TODO) [import-project](src/main/resources/Documentation/cmd-import-project.md)

### The Problem Space

We want to use Gerrit to streamline and restrict the Git workflow and review
process for development efforts, but we also want simple, automated way to
provide self-service capabilities for creating new projects without adding
all users to the Administrators group and provide some structure around
how Gerrit projects are created and setup.

There are different ways we can do this:

* Put a limited set of users (e.g. engineering managers, and/or techical
  leads) in the Administrators group who we just want to create Gerrit
  projects. Not ideal because all of these users will have full Gerrit
  administrator permissions, which is not appropriate most of the time.
  There is also ample opportunity to not create projects in a consistent
  fashion (project permissions, submit strategy, change-id requirement, etc.)
* Create a new group of users for these project administrator users and grant
  'Create Project' global capability (a feature in Gerrit itsef) to this group,
  then place desired users in this group and "ship" (read: distribute) client-
  side scripts to these users so they can create projects in specific ways.
  This isn't desireable either if we really do care about consistency and ease
  of use for these users. Not al engineering managers are still comfortable
  with using the Terminal any more and because they aren't actively developing
  their dev environment for these scripts (e.g. bundler/gems/Ruby version,
  virtualenv/pip/Python version, etc.) they may go stale and stop working and
  require more support than necessary.
* Create automation that is hosted inside Gerrit and uses Gerrit supported
  plugin and/or extension APIs to provide the consistency of the second option.
  The one-time setup of the new group and adding the necessary users to that
  group and adding the 'Create Project' capability is still required. The
  disadvantage of this approach is that the API is in Java and we don't have
  a lot of direct Java skills at Lookout generally, especially (based on
  comments from RelEng) inside RelEng. However, installing a Gerrit plugin
  and operating the plugin is far more practical and secure than spinning up
  a new service external to Gerrit where credentials need to be transported
  and stored safely, etc.
* Create automation to do the above that is hosted externally from Gerrit
  itself. This gives us the opportunity to write the automation in whatever
  language we care to support, but has the disadvantages that:

  * we are creating a new set of components and/or service that needs to
    be maintained and operated outside of Gerrit ecosystem itself.
  * we have to securely transport and store credentials necessary to
    run this automation outside of Gerrit itself.
  * we have to the additional overhead of not just unit and functional
    testing (as we would need to do with option #3 of building a Gerrit
    plugin based on open source Gerrit APIs) but also end-to-end service
    testing for the new external service for our automation.

This project is opting to provide an initial implementation for option #3 of
cohosting the Gerrit automation for this problem space inside the Gerrit
runtime itself, giving us numerous benefits assuming we can navigate the
Java Gerrit API which is open source and available.


### Bootstrap

Run the `bootstrap` script at the root directory of the plugin Git repository.

```shell
$ ./bootstrap
Setting up local Gerrit site dev env in /tmp/gerrit-site
~/src/oss/gerrit-selfserv ~/src/oss/gerrit-selfserv
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building selfserv 0.1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ selfserv ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] Copying 5 resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ selfserv ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ selfserv ---
[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!
[INFO] skip non existing resourceDirectory /home/spotter/src/oss/gerrit-selfserv/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ selfserv ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ selfserv ---
[INFO] Surefire report directory: /home/spotter/src/oss/gerrit-selfserv/target/surefire-reports

-------------------------------------------------------
T E S T S
-------------------------------------------------------
Running com.lookout.gerrit.selfserv.ssh.CreateProjectCommandTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.877 sec

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ selfserv ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 2.651 s
[INFO] Finished at: 2015-05-11T07:39:02-05:00
[INFO] Final Memory: 13M/303M
[INFO] ------------------------------------------------------------------------
~/src/oss/gerrit-selfserv
/tmp/gerrit-site ~/src/oss/gerrit-selfserv
Starting Gerrit Code Review: Already Running!!
~/src/oss/gerrit-selfserv
Using /home/spotter/.ssh/gerrit2.id_rsa for dev env

~~~~~~~~~~~~
INSTRUCTIONS
~~~~~~~~~~~~
Add an entry to your /home/spotter/.ssh/config file for your development gerrit
Maybe something like this:

Host gerrit.dev
  Hostname localhost
  Port 29418
  User spotter
  IdentityFile /home/spotter/.ssh/gerrit2.id_rsa

Now:
1. register a new user account spotter at: http://localhost:8080/#/register
2. Upload your SSH public key at /home/spotter/.ssh/gerrit2.id_rsa.pub
3. Run: ssh -p 29418 localhost -- gerrit plugin ls
```

Once you have followed the insutructions below to setup a local Gerrit server
development environment so you can do test out the Gerrit plugin functionality,
you should be able to observe the following in the Gerrit SSH commands:

```shell
$ ssh gerrit.dev -- gerrit plugin ls
Name                           Version    Status   File
-------------------------------------------------------------------------------
selfserv                       0.1.0-SNAPSHOT ENABLED  selfserv-0.1.0-SNAPSHOT.jar

$ ssh gerrit.dev -- selfserv
Available commands of selfserv are:

   add-user
   create-project
   import-project

See 'selfserv COMMAND --help' for more information.

$ ssh gerrit.dev -- selfserv create-project
fatal: Argument "NAME" is required

$ ssh gerrit.dev -- selfserv create-project --help
selfserv create-project NAME [--] [--change-id [TRUE | FALSE | INHERIT]] [--description DESC] [--empty-commit] [--help (-h)] [--parent PARENT]

  --                                   : end of options
  --change-id [TRUE | FALSE | INHERIT] : Require Change-Id in commits
  --description DESC                   : Description of the project to create
  --empty-commit                       : Creates initial empty commit
  --help (-h)                          : display this help text
  --parent PARENT                      : Name of parent repository

$ ssh gerrit.dev -- selfserv create-project myawesomeserver
Expected admin group (selfserv+Admins) does not exist.
```

Now we haven't created the admin group needed for the Gerrit plugin, so the
plugin nicely warns us abotu this via an errorneous SSH command (exit code 1
and useful error message written to stderr). So we can fix this in a few ways,
my favorite being the builtin Gerrit SSH command `gerrit create-groups GROUPNAME`:

```
$ ssh gerrit.dev -- gerrit create-group \"selfserv Admins\"
```

Now we can proceed using the `selfserv` Gerrit plugin like so:

```
$ ssh gerrit.dev -- selfserv add-user $USER
Added $USER to adminGroup

$ ssh gerrit.dev -- selfserv create-project myawesomeserver
Created project: myawesomeserver

$ ssh gerrit.dev -- selfserv create-project myawesomeserver
Project myawesomeserver already exists.
```

Note when there is an error the SSH command on the client side returns with a
non-zero exit with appropriate error message. So this can be used for
automation given that basic contract. Otherwise successful messaging will be
printed to stdout.

The last SSH command above returns with an exitcode of 1 and prints the error
message to stderr as expected for a UNIX tool.


### Deployment

To configure this plugin read the
[attached config documentation](src/main/resources/Documentation/config.md)

Gerrit provides a mechanism to live deploy plugins by copying the final JAR
Gerrit plugin artifact into the `${GERRIT_SITE}/plugins` directory.

### Status

CI: [![Build Status](https://travis-ci.org/mbbx6spp/gerrit-selfserv.svg)](https://travis-ci.org/mbbx6spp/gerrit-selfserv)

The CI build will do the following:

```
mvn verify
```

This goal will:

* compile the Java sources
* run the test suite
* run `findBugs` on the main source code (code under `src/main/java`) to find
  sources of common bug patterns in Java. I set this in the POM with a low
  threshold to catch more problems earlier.


