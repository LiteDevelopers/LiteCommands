
<div align="center"><img src="https://github.com/Rollczi/LiteCommands/assets/49173834/c3f218a0-268a-419d-899d-703ab0501ff0" alt="hacktoberfest" width="60%"/></div>
<div align="center"><h3>LiteCommands & Hacktoberfest 2023</h3></div>
<div align="center">Register on <a href="https://hacktoberfest.com/">hacktoberfest.com</a> and start supporting open source!</div>
<div align="center">Add/Improve/Fix features and open <a href="https://github.com/Rollczi/LiteCommands/pulls">Pull request</a>! If you don't have an idea, see  <a href="https://github.com/Rollczi/LiteCommands/issues">issues</a>!</div>
<br>
<div align="center"><img src="https://savemc.pl/files/litecommandsbanner.png" alt="banner" width="50%"/></div>

# ☄️ LiteCommands [![dependency](https://repo.panda-lang.org/api/badge/latest/releases/dev/rollczi/litecommands/core?color=53a2f9&name=LiteCommands)](https://repo.panda-lang.org/#/releases/dev/rollczi/litecommands) [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/paypalme/NDejlich) [![Discord](https://img.shields.io/discord/896933084983877662?color=8f79f4&label=Lite%20Discord)](https://img.shields.io/discord/896933084983877662?color=8f79f4&label=Lite%20Discord)

#### Command framework for Velocity, Bukkit, Paper, BungeeCord, Minestom, JDA and your other implementations.

Helpful links:

- [Support Discord](https://discord.gg/6cUhkj6uZJ)
- [GitHub issues](https://github.com/Rollczi/LiteCommands/issues)
- [Example](https://github.com/Rollczi/LiteCommands/tree/master/examples/bukkit)
- [Documentation](https://docs.rollczi.dev/)

### Panda Repository (Maven or Gradle)  ❤️

```xml

<repository>
    <id>panda-repository</id>
    <url>https://repo.panda-lang.org/releases</url>
</repository>
```

```groovy
maven { url "https://repo.panda-lang.org/releases" }
```

### Dependencies (Maven or Gradle)

Framework Core

```xml

<dependency>
    <groupId>dev.rollczi</groupId>
    <artifactId>litecommands-core</artifactId>
    <version>3.0.0-BETA-pre22</version>
</dependency>
```

```groovy
implementation 'dev.rollczi:litecommands-core:3.0.0-BETA-pre22'
```

### First Simple Command

`/hello-world <name> <amount>`  
`/hello-world message <text...>`  

```java

@Command(name = "hello-world")
@Permission("dev.rollczi.helloworld")
public class HelloWorldCommand {

    @Execute
    public void command(@Context CommandSender sender, @Arg String name, @Arg int amount) {
        for (int i = 0; i < amount; i++) {
            sender.sendMessage("Hello " + name);
        }
    }
    
    @Execute(name = "message")
    public void subcommand(@Context CommandSender sender, @Join String text) {
        sender.sendMessage(text);
    }

}
```

Register your first command in plugin main class: (in this case for Velocity)

```java
this.liteCommands=LiteVelocityFactory.builder(proxy)
    .command(HelloWorldCommand.class)
    .register();
```

### Velocity Extension Dependencies (Maven or Gradle)

Add this to your dependencies if you want to use ready-made implementation for velocity.

```xml

<dependency>
    <groupId>dev.rollczi</groupId>
    <artifactId>litecommands-velocity</artifactId>
    <version>3.0.0-BETA-pre22</version>
</dependency>
```

```groovy
implementation 'dev.rollczi:litecommands-velocity:3.0.0-BETA-pre22'
```

#### Add -parameters to your compiler to use all features of LiteCommands

```groovy
tasks.withType(JavaCompile) {
    options.compilerArgs << "-parameters"
}
```

```kotlin
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}
```

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.8.1</version>
  <configuration>
    <compilerArgs>
      <arg>-parameters</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

#### All extensions:

- [Velocity](https://github.com/Rollczi/LiteCommands/tree/master/litecommands-velocity)
- [Bukkit](https://github.com/Rollczi/LiteCommands/tree/master/litecommands-bukkit)
- [Bukkit Adventure extension](https://github.com/Rollczi/LiteCommands/tree/master/litecommands-bukkit-adventure)
- [BungeeCord](https://github.com/Rollczi/LiteCommands/tree/master/litecommands-bungee)
- [Minestom](https://github.com/Rollczi/LiteCommands/tree/master/litecommands-minestom)

#### Other examples:

- [Bukkit Example](https://github.com/Rollczi/LiteCommands/tree/master/examples/bukkit)

#### See (Important dependencies used)

- [panda-lang/expressible](https://github.com/panda-lang/expressible)
- [panda-lang/panda (panda-utilities)](https://github.com/panda-lang/panda) (v1.0.0 - v1.9.2) (in v2.0.0 and above a
  built-in DI modeled on it is used)

#### Plugins that use LiteCommands:

- [EternalCore](https://github.com/EternalCodeTeam/EternalCore)
- [EternalCombat](https://github.com/EternalCodeTeam/EternalCombat)
- [EternalCheck](https://github.com/EternalCodeTeam/EternalCheck)
- [ChatFormatter](https://github.com/EternalCodeTeam/ChatFormatter)
