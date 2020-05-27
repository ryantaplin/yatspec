# YatSpec <sup>[![CircleCI](https://circleci.com/gh/nickmcdowall/yatspec.svg?style=svg)](https://circleci.com/gh/nickmcdowall/yatspec)</sup>

## Status
 [![Build Status](https://travis-ci.com/nickmcdowall/yatspec.svg?branch=master)](https://travis-ci.com/nickmcdowall/yatspec)
 
## Releases
 
 Bintray/jcenter: [![](https://api.bintray.com/packages/nickmcdowall/nkm/yatspec/images/download.svg)](https://bintray.com/nickmcdowall/nkm/yatspec/_latestVersion)
 
 or alternatively Jitpack: [![](https://jitpack.io/v/nickmcdowall/yatspec.svg)](https://jitpack.io/#nickmcdowall/yatspec)

## Overview
This project builds upon the excellent [Yatspec](https://github.com/bodar/yatspec) project.

It focuses on the **Sequence Diagram** capabilities that Yatspec and PlantUML provides - to simplify the experience of creating sequence diagrams and capturing payloads.

It also aims modernises the source code to make it easier to use with _Java 11_, _Junit 5_ and _Gradle_. 

### Minimal Requirements ###
* Java 11+
* Junit 5
---

### Quick Start ###

Add YatSpec to your project e.g. Gradle:

````
dependencies {
    testImplementation 'com.github.nickmcdowall:yatspec:release-2020.0.1'
    ...
}
````

`jcenter()` contains the yatspec artifacts (alternatively you can add `https://jitpack.io` as a maven repository):
````
repositories {
    jcenter()
    mavenCentral()
}
````

Create a new Test:

```java
@ExtendWith(SequenceDiagramExtension.class)
public class SequenceDiagramExampleTest {

    // Use this instance to log interactions by calling the log() method
    private TestState interactions = new TestState();

    @Test
    public void messageFromUpstreamToDownstream() {
        // (method names here turn into the specification in report)
    }

}
```

See [yatspec-example](https://github.com/nickmcdowall/yatspec-example) for an example project that produces the below sequence diagram from 
a SpringBootTest class:

![example sequence diagram gif](https://github.com/nickmcdowall/yatspec-example/blob/master/sequence_diagram_example.gif)