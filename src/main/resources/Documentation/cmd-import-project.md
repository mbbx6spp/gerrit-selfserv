@PLUGIN@ import-project
=======================

NAME
----
@PLUGIN@ import-project - import project in selfserv mode

SYNOPSIS
--------
>    ssh -p <port> <host> @PLUGIN@ import-project NAME -- \
>      --description DESC --parent PARENT --source SOURCEURL \
>      --change-id --empty-commit

DESCRIPTION
-----------
Imports new project with associated Git repository from an existing Git remote.

ACCESS
------
Any user who has been granted global 'Create Project' capabilities.

SCRIPTING
---------
This command is intended to be used in scripts.

EXAMPLES
--------

import a new project with default settings:

>    $ ssh -p 29418 gerrit.host @PLUGIN@ import-project my-project \
>      --source https://github.com/foo/bar.git

SEE ALSO
--------

* [@PLUGIN@ create-project command](cmd-create-project.md)


