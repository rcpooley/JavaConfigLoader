# JavaConfigLoader
Easily load a config files.

Currently only supports loading json config files.

See [TestConfigLoader.java](https://github.com/rcpooley/JavaConfigLoader/blob/master/src/test/java/com/rcpooley/configloader/TestConfigLoader.java) for full usage
## Example Usage
```java
public class Main {
    public static void main(String[] args) throws ConfigException {
        Config config = ConfigLoader.loadJSON(
            Main.class.getResourceAsStream("/config.json"),
            Config.class
        );
        System.out.println("Mysql username: " + config.mysql.username);
    }
}

public class Config {
    MysqlConfig mysql;
}

public class MysqlConfig {
    String host;
    String username;
    String password;
    String database;
}
```
config.json:
```
{
    "mysql": {
        "host": "localhost:3306",
        "username": "root",
        "password": "",
        "database": "testdb"
    }
}
```