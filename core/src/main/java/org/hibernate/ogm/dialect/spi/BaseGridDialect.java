/*
 * Hibernate OGM, Domain model persistence for NoSQL datastores
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.ogm.dialect.spi;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.dialect.lock.LockingStrategy;
import org.hibernate.ogm.dialect.impl.ExceptionThrowingLockingStrategy;
import org.hibernate.ogm.model.key.spi.EntityKeyMetadata;
import org.hibernate.ogm.model.spi.Tuple;
import org.hibernate.ogm.type.spi.GridType;
import org.hibernate.persister.entity.Lockable;
import org.hibernate.type.Type;

/**
 * Recommended base class for {@link GridDialect} implementations.
 * 
 * @author Gunnar Morling
 */
public abstract class BaseGridDialect implements GridDialect {

	/**
	 * Returns a no-op locking strategy for all lock modes which will raise an exception upon lock retrieval. Dialects may override this method to provide support for those lock modes they can handle.
	 */
	@Override
	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
		return new ExceptionThrowingLockingStrategy(this, lockMode);
	}

	@Override
	public GridType overrideType(Type type) {
		return null;
	}

	@Override
	public boolean supportsSequences() {
		return false;
	}

	@Override
	public DuplicateInsertPreventionStrategy getDuplicateInsertPreventionStrategy(EntityKeyMetadata entityKeyMetadata) {
		return DuplicateInsertPreventionStrategy.LOOK_UP;
	}

	@Override
	public void forEachTuple(ModelConsumer consumer, EntityKeyMetadata... entityKeyMetadatas) {
		for (EntityKeyMetadata entityKeyMetadata : entityKeyMetadatas) {
			List<Tuple> tuples = getTuples(entityKeyMetadata);
			for (Tuple tuple : tuples) {
				consumer.consume(tuple);
			}
		}
	}

	// Should be abstract, but high impact on all subclasses
	protected List<Tuple> getTuples(EntityKeyMetadata entityKeyMetadata) {
		return null;
	}
}
