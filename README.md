# semver

Parse, compare and bump semantic versions.

## Usage

```java
Version v = Version.parse("1.4.2-beta.1");
v.bump(Version.Part.MINOR);               // 1.5.0
Version.parse("1.0.0").compareTo(v) > 0;  // true
```

## Test

```bash
mvn test
```

## License

MIT
