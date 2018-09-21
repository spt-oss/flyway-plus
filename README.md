# Flyway Plus

[![circleci](https://img.shields.io/badge/circleci-flyway--plus-brightgreen.svg)](https://circleci.com/gh/spt-oss/flyway-plus)
[![maven central](https://img.shields.io/badge/maven_central-flyway--plus-blue.svg)](https://mvnrepository.com/artifact/com.github.spt-oss/flyway-plus)
[![javadoc](https://img.shields.io/badge/javadoc-flyway--plus-blue.svg)](https://www.javadoc.io/doc/com.github.spt-oss/flyway-plus)

* Custom implementation for [Flyway](https://github.com/flyway/flyway)
* ⚠️**This project is unofficial and experimental.**

## Products

* `CustomFlyway`
	* Additional method: *setPlaceholderReplacer(PlaceholderReplacer)*
* `MysqlH2SqlReplacer implements PlaceholderReplacer`
	* Replace MySQL-SQL to H2-SQL on migration

## Usage

1. Add a dependency in your project.

	```xml
	<dependency>
	    <groupId>com.github.spt-oss</groupId>
	    <artifactId>flyway-plus</artifactId>
	    <version>5.1.4.0</version>
	</dependency>
	```

1. Create a subclass of `PlaceholderReplacer` or use 
[MysqlH2SqlReplacer](./src/main/java/org/flywaydb/core/internal/util/placeholder/MysqlH2SqlReplacer.java).

	```java
	package my.project;
	
	import org.flywaydb.core.internal.util.placeholder.PlaceholderReplacer;
	
	public class CustomPlaceholderReplacer implements PlaceholderReplacer {
	    ......
	}
	```

1. Setup `CustomFlyway` instance.

	```java
	import java.sql.DataSource;
	import org.flywaydb.core.CustomFlyway;
	import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
	import my.project.CustomPlaceholderReplacer;
	
	DataSource dataSource = DataSourceBuilder.create().url("jdbc:mysql://host:3306/db").build();
	
	CustomFlyway flyway = new CustomFlyway();
	flyway.setLocations("classpath:/data");
	flyway.setSchemas("foo", "bar");
	flyway.setDataSource(dataSource);
	flyway.setPlaceholderReplacer(new CustomPlaceholderReplacer());
	flyway.migrate();
	```

1. Or create Spring bean if your project is based on Spring Boot.

	```yml
	spring.flyway:
	    enabled: true
	    locations:
	        - classpath:/data
	    schemas:
	        - foo
	        - bar
	```
	```java
	import org.flywaydb.core.CustomFlyway;
	import org.springframework.context.annotation.Bean;
	import org.springframework.boot.autoconfigure.flyway.CustomFlywayMigrationStrategy;
	import my.project.CustomPlaceholderReplacer;
	
	@Bean
	public CustomFlywayMigrationStrategy migrationStrategy() {
	    return flyway -> flyway.setPlaceholderReplacer(new CustomPlaceholderReplacer());
	}
	```

## License

* This software is released under the Apache License 2.0.
