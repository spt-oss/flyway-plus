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

package org.flywaydb.core.internal.placeholder;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import org.flywaydb.core.internal.util.StringUtils;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SQL replacer for MySQL-H2
 */
public class MysqlH2SqlReplacer implements PlaceholderReplacer {
	
	/**
	 * Group 1
	 */
	protected static final String GROUP_1 = "$1";
	
	/**
	 * Line feed
	 */
	protected static final String LF = "\n";
	
	/**
	 * Index
	 */
	protected static final String INDEX = "INDEX";
	
	@Override
	public Map<String, String> getPlaceholderReplacements() {
		
		return Collections.emptyMap();
	}
	
	@Override
	public String replacePlaceholders(String sql) {
		
		if (!StringUtils.hasText(sql)) {
			
			return sql;
		}
		
		InternalSqlReplacer replacer = new InternalSqlReplacer(sql);
		
		// Remove 'SET ...@...'
		replacer.remove("(?m)^SET(?:\\s+)(?:.*?)@(?:.*?)$");
		
		// Remove 'LOCK' and 'UNLOCK'
		replacer.remove("(?m)^LOCK(?:\\s+)TABLES(?:\\s+)(?:.*?)$");
		replacer.remove("(?m)^UNLOCK(?:\\s+)TABLES(?:.*?)$");
		
		// Replace 'DROP /* CONSTRAINT */ INDEX' to 'DROP CONSTRAINT'
		replacer.replace("DROP(?:\\s*)/\\*(?:\\s*)CONSTRAINT(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", "DROP CONSTRAINT");
		
		// Replace 'RENAME /* CONSTRAINT */ INDEX' to 'RENAME CONSTRAINT'
		replacer.replace("RENAME(?:\\s*)/\\*(?:\\s*)CONSTRAINT(?:\\s*)\\*/(?:\\s*)(INDEX|KEY)", "RENAME CONSTRAINT");
		
		// Replace 'FULLTEXT' in table
		replacer.replace("FULLTEXT(?:\\s+)(?:INDEX|KEY)", INDEX);
		replacer.replace("FULLTEXT", INDEX);
		
		// Remove 'WITH PARSER' in table
		replacer.remove("WITH(?:\\s+)PARSER(?:\\s+)(?:[^;]+)");
		
		// Replace 'TEXT' to 'VARCHAR' for fulltext index
		replacer.replace("(\\s+)TEXT([\\s,]+)", "$1VARCHAR(" + Integer.MAX_VALUE + ")$2");
		
		// Remove 'AFTER' in 'CHANGE/MODIFY' column
		replacer.replace("((?:\\s*)CHANGE(?:\\s+)COLUMN(?:\\s+)(?:.*?))(?:\\s+)AFTER(?:\\s+)(?:[^;]+)", GROUP_1);
		replacer.replace("((?:\\s*)MODIFY(?:\\s+)COLUMN(?:\\s+)(?:.*?))(?:\\s+)AFTER(?:\\s+)(?:[^;]+)", GROUP_1);
		
		// Remove 'CHARACTER SET' in column
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'(?:\\s+)COLLATE(?:\\s+)'(?:.*?)'");
		replacer.remove("(?:\\s*)CHARACTER(?:\\s+)SET(?:\\s+)'(?:.*?)'");
		
		// Remove 'COMMENT' in table
		replacer.remove("(?:\\s*)COMMENT(?:\\s*)=(?:\\s*)(?:[^;]+)");
		
		// Replace back-quote to double-quate
		replacer.replace("`", "\"");
		
		// Remove SQL comment
		replacer.remove("(?m)^--(?:.*?)$");
		
		// Remove newlines
		replacer.replace("\r\n", LF);
		replacer.replace("\r", LF);
		replacer.replace("\n\n\n+", LF + LF);
		replacer.replace("^\n+", LF);
		replacer.replace("\n+$", LF);
		
		return replacer.toString();
	}
	
	/**
	 * Internal SQL replacer
	 */
	@RequiredArgsConstructor
	protected static class InternalSqlReplacer {
		
		/**
		 * SQL
		 */
		@NonNull
		private String sql;
		
		/**
		 * Remove pattern in SQL
		 * 
		 * @param pattern pattern
		 * @return {@link InternalSqlReplacer}
		 */
		public InternalSqlReplacer remove(String pattern) {
			
			return this.replace(pattern, "");
		}
		
		/**
		 * Replace pattern in SQL
		 * 
		 * @param pattern pattern
		 * @param replacement replacement
		 * @return {@link InternalSqlReplacer}
		 */
		public InternalSqlReplacer replace(String pattern, String replacement) {
			
			this.sql = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(this.sql).replaceAll(replacement);
			
			return this;
		}
		
		@Override
		public String toString() {
			
			return this.sql;
		}
	}
}
