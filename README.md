# Flyway Plus

[![circleci](https://img.shields.io/badge/circleci-flyway--plus-brightgreen.svg)](https://circleci.com/gh/spt-oss/flyway-plus)
[![maven central](https://img.shields.io/badge/maven_central-flyway--plus-blue.svg)](https://mvnrepository.com/artifact/com.github.spt-oss/flyway-plus)
[![javadoc](https://img.shields.io/badge/javadoc-flyway--plus-blue.svg)](https://www.javadoc.io/doc/com.github.spt-oss/flyway-plus)

* Custom implementation for [Flyway](https://github.com/flyway/flyway)
* ⚠️**This project is unofficial and experimental.**

## Products

* `CustomFlyway extends Flyway`
	* Additional method: *setPlaceholderReplacer(PlaceholderReplacer)*
* `MysqlH2SqlReplacer implements PlaceholderReplacer`
	* Replace MySQL-SQL to H2-SQL on migration

## Usage

1. Add a dependency in your project.

	```xml
	<dependency>
	    <groupId>com.github.spt-oss</groupId>
	    <artifactId>flyway-core</artifactId>
	    <version>5.1.4.1</version>
	</dependency>
	```

1. Create a subclass of `PlaceholderReplacer`. You can also use `MysqlH2SqlReplacer`.

	```java
	package my.project;
	
	import org.flywaydb.core.internal.util.placeholder.PlaceholderReplacer;
	
	public class MyPlaceholderReplacer implements PlaceholderReplacer {
	    ......
	}
	```

1. Setup `CustomFlyway` instance.

	```java
	import java.sql.DataSource;
	import org.flywaydb.core.CustomFlyway;
	import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
	import my.project.MyPlaceholderReplacer;
	
	DataSource dataSource = DataSourceBuilder.create().url("jdbc:mysql://host:3306/db").build();
	
	CustomFlyway flyway = new CustomFlyway();
	flyway.setLocations("classpath:/data");
	flyway.setSchemas("foo", "bar");
	flyway.setDataSource(dataSource);
	flyway.setPlaceholderReplacer(new MyPlaceholderReplacer());
	flyway.migrate();
	```

## License

* This software is released under the Apache License 2.0.
