# spellor
Spell correction library

## What you need 
- Java 8
- Maven

## How to use this library
- Create a new `Spellor` object:
```java
Spellor spellor = new Spellor();
```
- You can feed Spellor with the contents of a custom file. Just input the path and it will take all the words inside the file and add to the dictionary:
```java
spellor.trainOnData("D:/data/input/words.txt");
```
- Just get the appropriate correction of the word you need using the `correction` method:
```java
spellor.correction("spelaing"); // returns spelling
```

## How to include this library in your projects
- Go to the project folder and run `mvn install`
- Use in your project like this:
```xml
<dependency>
    <groupId>com.manparvesh.spellor</groupId>
    <artifactId>spellor</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```