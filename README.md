# <u>**HBM's Nuclear Tech Mod Community Edition**</u>
An attempt at a "definitive" port of NTM to 1.12. Came from necessity as other developers have failed to update and maintain other forks. Heavy WIP as there is much content to port

> **IMPORTANT: If you have Universal Tweaks installed, set `B:"Disable Fancy Missing Model"` to `false` to fix model rotation**  
> This can be found at `config/Universal Tweaks - Tweaks.cfg`

## FAQ

### Is it survival ready?

**As of me writing this, no**. Mod has been badly maintained on 1.12, missing the majority of the content. We still have
about a year or 2 worth of content to port and bugtest. The current builds are testing only. We invite you to our
GitHub, where we publish nightly releases.

### How different is it from extended?

**Extended worlds are fully incompatible!** We have rewritten 50-75% of the entire mod, porting every single feature we can.
The amount of changes is difficult to track at this point. I invite you to check our GitHub issues, as we use them to
track missing/added content.

### Why not improve the extended edition?

Alcater has not updated his version in close to a year, his version as many performance bottlenecks and weird approaches
to implementation of some features. Not to mention his refusal to work with us, hence we decided to fork and work
separately.

### If it's in development, why publish it on CurseForge?

**We seek bug reports.** It is more than obvious to us that without presence on websites such as curse, modrinth, our reach
is severely diminished. We want to make players aware that there is a proper port in the works, and therefore help us
either via bug reports and directly, via pull requests.We always seek new contributors.

### Will this version have modifications for specific mod pack use?
**No!** While the port was started as part of the warfactory project, It is maintained as a standalone mod. Any changes are
in order to ensure compatibility, stability, or ease development for mod pack developers, however no direct changes for
specific mod packs will be implemented.

### Will you port it to 1.1x/1.2x?

**We don't plan to do so, no.** We need to stay committed to one version at a time. Fragmentation, and the insane amount of
separate teams that worked on this mod, is what killed the mod's chance to be ported. This is why we want to centralize
our efforts on one version at a time.

#### Discord:
[Warfactory Offical](https://discord.gg/eKFrH7P5ZR)

## Development guide.
For development Java17/21 is used.

We have [Jabel](https://github.com/bsideup/jabel) to target Java8 bytecode seamlessly (make sure you don't use APIs introduced in Java9+)


### General quickstart
1. Clone this repository.
2. Prepare JDK (preferably 17+).
3. Run task `setupDecompWorkspace` (this will setup workspace, including MC sources deobfuscation)
4. Ensure everything is OK. Run task `runClient` (should open minecraft client with mod loaded)


- Always use `gradlew` (Linux/MACOS) or `gradlew.bat` (Win) and not `gradle` for tasks. So each dev will have consistent environment.
### Development quirks for Apple M-chip machines.

Since there are no natives for ARM arch, therefore you will have to use x86_64 JDK (the easiest way to get the right one is IntelliJ SDK manager)

You can use one of the following methods:
- GRADLE_OPTS env variable `export GRADLE_OPTS="-Dorg.gradle.java.home=/path/to/your/desired/jdk"`
- additional property in gradle.properties (~/.gradle or pwd) `org.gradle.java.home=/path/to/your/desired/jdk`
- direct usage with -D param in terminal `./gradlew -Dorg.gradle.java.home=/path/to/your/desired/jdk wantedTask`

#### Troubleshooting:

1. If you see that even when using x86_64 JDK in logs gradle treats you as ARM machine. Do following:
    1. Clear workspace `git fetch; git clean -fdx; git reset --hard HEAD` (IMPORTANT: will sync local to git, and remove all progress)
    2. Clear gradle cache `rm -rf ~/.gradle` (IMPORTANT: will erase WHOLE gradle cache)
    3. Clear downloaded JVMs `rm -rf /path/to/used/jvm`
       (path to used jvm can be found in /run/logs/latest.log like this `Java is OpenJDK 64-Bit Server VM, version 1.8.0_442, running on Mac OS X:x86_64:15.3.2, installed at /this/is/the/path`)
    4. Repeat quickstart.
