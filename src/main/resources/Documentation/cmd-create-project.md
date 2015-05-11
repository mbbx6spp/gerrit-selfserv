@PLUGIN@ create-project
=======================

NAME
----
@PLUGIN@ create-project - Create project in selfserv mode

SYNOPSIS
--------
>    ssh -p <port> <host> @PLUGIN@ create-project NAME -- \
>      --description DESC --parent PARENT --change-id --empty-commit

DESCRIPTION
-----------
Creates new project with associated Git repository

ACCESS
------
Any user who has been granted global 'Create Project' capabilities.

SCRIPTING
---------
This command is intended to be used in scripts.

EXAMPLES
--------

Create a new project with default settings:

>    $ ssh -p 29418 gerrit.host @PLUGIN@ create-project my-project

SEE ALSO
--------

* [@PLUGIN@ import-project command](cmd-import-project.md)


