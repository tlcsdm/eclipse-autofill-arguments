# Auto Filling Java Call Arguments

This project is a Eclipse plugin that automates the writing of these builders. With just two clicks you can generate all the builder code you need.

## About
The Builder Pattern was first introduced by Joshua Bloch at JavaOne 2007. It's a pattern for class creation and helps getting rid of ugly constructors, constructor telescoping and increases the general readability of your code.

## Use
In the eclipse java editor window, right click and select Source -> Generate Builder Pattern Code or Ctrl + Alt + O.

![screenshot](https://raw.github.com/tlcsdm/eclipse-autofill-arguments/master/plugins/com.tlcsdm.eclipse.autofiller/images/usage-context-menu-option.jpg)

Then select which fields you want to expose in the builder.

![screenshot](https://raw.github.com/tlcsdm/eclipse-autofill-arguments/master/plugins/com.tlcsdm.eclipse.autofiller/images/usage-selection-window.jpg)

## Build

This project uses [Tycho](https://github.com/eclipse-tycho/tycho) with [Maven](https://maven.apache.org/) to build. It requires Maven 3.9.0 or higher version.

Dev build:

```
mvn clean verify
```

Release build:

```
mvn clean org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=2.0.0 verify
```

## Install

1. Add `https://raw.githubusercontent.com/tlcsdm/eclipse-autofill-arguments/master/update_site/` as the upgrade location in Eclipse.
2. Download from [Jenkins](https://jenkins.tlcsdm.com/job/eclipse-plugin/job/eclipse-autofill-arguments)
