@PLUGIN@ Configuration
======================

To configure @PLUGIN@ you can specify the group name which will be used
to allow selfservice creation and importing of new Gerrit projects.

The configuration must be done in the `gerrit.config` of the Gerrit server.

```
[selfserv]
  adminGroup = "selfserv Admins"
```

