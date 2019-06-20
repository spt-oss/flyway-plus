/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flywaydb.core;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.placeholder.MysqlH2SqlReplacer;
import org.junit.Test;
import org.springframework.boot.jdbc.DataSourceBuilder;

/**
 * {@link Test}: {@link MysqlH2SqlReplacer}
 */
public class CustomFlywayTests {
	
	/**
	 * {@link CustomFlyway#migrate()}
	 */
	@Test
	public void migrate() {
		
		FluentConfiguration config = Flyway.configure()
		/* @formatter:off */
			.locations("classpath:/flyway/migration")
			.schemas("example")
			.sqlMigrationPrefix("")
			.sqlMigrationSeparator("-")
			.dataSource(DataSourceBuilder.create().url("jdbc:h2:mem:test;MODE=MySQL;DATABASE_TO_UPPER=FALSE").build());
			/* @formatter:on */
		
		Flyway flyway = new CustomFlyway(config, new MysqlH2SqlReplacer());
		flyway.clean();
		flyway.migrate();
	}
}
