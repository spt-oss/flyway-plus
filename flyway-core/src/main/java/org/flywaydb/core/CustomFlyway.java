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
package org.flywaydb.core;

import java.lang.reflect.Field;
import java.util.Collection;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.flywaydb.core.internal.callback.CallbackExecutor;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.database.base.Schema;
import org.flywaydb.core.internal.placeholder.PlaceholderReplacer;
import org.flywaydb.core.internal.resolver.CompositeMigrationResolver;
import org.flywaydb.core.internal.resolver.sql.SqlMigrationResolver;
import org.flywaydb.core.internal.schemahistory.SchemaHistory;
import org.flywaydb.core.internal.sqlscript.SqlStatementBuilderFactory;
import org.flywaydb.core.internal.sqlscript.SqlStatementBuilderFactoryWrapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Custom {@link Flyway}
 */
public class CustomFlyway extends Flyway {
	
	/**
	 * {@link PlaceholderReplacer}
	 */
	private final PlaceholderReplacer placeholderReplacer;
	
	/**
	 * Constructor
	 * 
	 * @param configuration {@link Configuration}
	 * @param placeholderReplacer {@link PlaceholderReplacer}
	 */
	public CustomFlyway(Configuration configuration, PlaceholderReplacer placeholderReplacer) {
		
		super(configuration);
		
		this.placeholderReplacer = placeholderReplacer;
	}
	
	@Override
	protected <T> T execute(Command<T> command, boolean scannerRequired) {
		
		return super.execute(new CustomCommand<>(command, this.placeholderReplacer), scannerRequired);
	}
	
	/**
	 * Custom {@link org.flywaydb.core.Flyway.Command}
	 * 
	 * @param <T> result type
	 */
	@RequiredArgsConstructor
	protected static class CustomCommand<T> implements Command<T> {
		
		/**
		 * Delegate
		 */
		@NonNull
		private final Command<T> delegate;
		
		/**
		 * {@link PlaceholderReplacer}
		 */
		private final PlaceholderReplacer placeholderReplacer;
		
		@Override
		public T execute(
		/* @formatter:off */
			MigrationResolver migrationResolver,
			SchemaHistory schemaHistory,
			@SuppressWarnings("rawtypes") Database database,
			@SuppressWarnings("rawtypes") Schema[] schemas,
			CallbackExecutor callbackExecutor) {
			/* @formatter:on */
			
			if (this.placeholderReplacer != null) {
				
				this.replacePlaceholderReplacer(migrationResolver, this.placeholderReplacer);
			}
			
			return this.delegate.execute(migrationResolver, schemaHistory, database, schemas, callbackExecutor);
		}
		
		/**
		 * Replace {@link PlaceholderReplacer}
		 * 
		 * @param resolver {@link MigrationResolver}
		 * @param placeholderReplacer {@link PlaceholderReplacer}
		 */
		protected void replacePlaceholderReplacer(MigrationResolver resolver, PlaceholderReplacer placeholderReplacer) {
			
			// Extract child resolvers
			if (resolver instanceof CompositeMigrationResolver) {
				
				try {
					
					Field field = CompositeMigrationResolver.class.getDeclaredField("migrationResolvers");
					field.setAccessible(true);
					
					@SuppressWarnings("unchecked")
					Collection<MigrationResolver> childResolvers = (Collection<MigrationResolver>) field.get(resolver);
					
					// Recursive call
					for (MigrationResolver childResolver : childResolvers) {
						
						this.replacePlaceholderReplacer(childResolver, placeholderReplacer);
					}
				}
				catch (ReflectiveOperationException e) {
					
					throw new IllegalStateException("Failed to get value of field 'migrationResolvers'", e);
				}
			}
			
			// Replace
			else if (resolver instanceof SqlMigrationResolver) {
				
				try {
					
					Field field = SqlMigrationResolver.class.getDeclaredField("sqlStatementBuilderFactory");
					field.setAccessible(true);
					
					field.set(resolver, new SqlStatementBuilderFactoryWrapper(
						(SqlStatementBuilderFactory) field.get(resolver), placeholderReplacer));
				}
				catch (ReflectiveOperationException e) {
					
					throw new IllegalStateException("Failed to set value of field 'database'", e);
				}
			}
		}
	}
}
