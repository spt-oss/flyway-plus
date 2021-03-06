/*
 * Copyright 2018-2019 the original author or authors.
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

package org.flywaydb.core.internal.placeholder;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * {@link Test}: {@link MysqlH2SqlReplacer}
 */
public class MysqlH2SqlReplacerTest {
	
	/**
	 * Data path
	 */
	private static final String DATA_PATH = "flyway/replacer";
	
	/**
	 * {@link MysqlH2SqlReplacer#replacePlaceholders(String)}
	 */
	@Test
	public void replacePlaceholders() {
		
		// TODO update tests
		String sql = new MysqlH2SqlReplacer()
			.replacePlaceholders(this.readClassPathFile(DATA_PATH + "/before.sql", "\r\n"));
		
		assertThat(sql).isEqualTo(this.readClassPathFile(DATA_PATH + "/after.sql", "\n"));
	}
	
	/**
	 * Read class path file
	 * 
	 * @param path path
	 * @param joiner joiner
	 * @return string in file
	 */
	private String readClassPathFile(String path, String joiner) {
		
		try {
			
			return Files.lines(Paths.get(new ClassPathResource(path).getURI()), StandardCharsets.UTF_8)
				.collect(Collectors.joining(joiner));
		}
		catch (IOException e) {
			
			throw new IllegalStateException(e);
		}
	}
}
