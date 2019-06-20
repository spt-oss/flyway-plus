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
package org.flywaydb.core.internal.sqlscript;

import org.flywaydb.core.internal.placeholder.PlaceholderReplacer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link SqlStatementBuilderFactory} wrapper
 */
@RequiredArgsConstructor
public class SqlStatementBuilderFactoryWrapper implements SqlStatementBuilderFactory {
	
	/**
	 * Delegate
	 */
	@NonNull
	private final SqlStatementBuilderFactory delegate;
	
	/**
	 * {@link PlaceholderReplacer}
	 */
	private final PlaceholderReplacer placeholderReplacer;
	
	@Override
	public PlaceholderReplacer getPlaceholderReplacer() {
		
		if (this.placeholderReplacer != null) {
			
			return this.placeholderReplacer;
		}
		
		return this.delegate.getPlaceholderReplacer();
	}
	
	@Override
	public SqlStatementBuilder createSqlStatementBuilder() {
		
		return this.delegate.createSqlStatementBuilder();
	}
}
