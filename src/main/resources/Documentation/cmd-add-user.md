@PLUGIN@ add-user
=======================

NAME
----
@PLUGIN@ add-user - Adds a user to the selfserv admin group and grants user
                    'Create Project' capability in All-Projects.

SYNOPSIS
--------
>    ssh -p <port> <host> @PLUGIN@ add-user NAME

DESCRIPTION
-----------
Adds user to the selfserv admin group and grants user 'Create Project'
capability in All-Projects.

ACCESS
------
Any user in the 'Administrators' group and who has full admin capabilities.

SCRIPTING
---------
This command is intended to be used in scripts.

EXAMPLES
--------

Create a new project with default settings:

>    $ ssh -p 29418 gerrit.host @PLUGIN@ add-user username

SEE ALSO
--------

N/A

