/**
 * Copyright (C) 2010-2017 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.common;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.structr.api.Predicate;
import org.structr.api.graph.Node;
import org.structr.api.graph.PropertyContainer;
import org.structr.api.graph.Relationship;
import org.structr.cmis.CMISInfo;
import org.structr.common.error.ErrorBuffer;
import org.structr.common.error.FrameworkException;
import org.structr.core.GraphObject;
import org.structr.core.entity.AbstractRelationship;
import org.structr.core.entity.ManyEndpoint;
import org.structr.core.entity.ManyStartpoint;
import org.structr.core.entity.OneEndpoint;
import org.structr.core.entity.OneStartpoint;
import org.structr.core.entity.Principal;
import org.structr.core.entity.Relation;
import org.structr.core.entity.Security;
import org.structr.core.entity.Source;
import org.structr.core.entity.Target;
import org.structr.core.graph.ModificationQueue;
import org.structr.core.graph.NodeInterface;
import org.structr.core.graph.RelationshipInterface;
import org.structr.core.property.PropertyKey;
import org.structr.core.property.PropertyMap;
import org.structr.schema.action.ActionContext;

/**
 * A container that is used internally to combine a related node (determined
 * by IdDeserializationStrategy) and additional properties that are meant to
 * be set on the newly created relationship.
 */
public class EntityAndPropertiesContainer implements NodeInterface {

	private Map<String, Object> properties = null;
	private GraphObject entity             = null;

	public EntityAndPropertiesContainer(final GraphObject entity, final Map<String, Object> properties) {
		this.properties = properties;
		this.entity     = entity;
	}

	public GraphObject getEntity() {
		return entity;
	}

	public Map<String, Object> getProperties() throws FrameworkException {
		return properties;
	}

	// ----- implement hashCode contract to make this class comparable to GraphObject -----
	@Override
	public int hashCode() {
		return entity.hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		return other.hashCode() == hashCode();
	}

	// dummy implementation of NodeInterface
	@Override
	public void init(SecurityContext securityContext, Node dbNode, Class type, boolean isCreation) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void onNodeCreation() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void onNodeInstantiation(boolean isCreation) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void onNodeDeletion() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Node getNode() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isDeleted() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <R extends AbstractRelationship> Iterable<R> getRelationships() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <R extends AbstractRelationship> Iterable<R> getRelationshipsAsSuperUser() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <R extends AbstractRelationship> Iterable<R> getIncomingRelationships() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <R extends AbstractRelationship> Iterable<R> getOutgoingRelationships() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <A extends NodeInterface, B extends NodeInterface, S extends Source, T extends Target, R extends Relation<A, B, S, T>> Iterable<R> getRelationships(Class<R> type) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <A extends NodeInterface, B extends NodeInterface, T extends Target, R extends Relation<A, B, OneStartpoint<A>, T>> R getIncomingRelationship(Class<R> type) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <A extends NodeInterface, B extends NodeInterface, T extends Target, R extends Relation<A, B, ManyStartpoint<A>, T>> Iterable<R> getIncomingRelationships(Class<R> type) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <A extends NodeInterface, B extends NodeInterface, S extends Source, R extends Relation<A, B, S, OneEndpoint<B>>> R getOutgoingRelationship(Class<R> type) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <A extends NodeInterface, B extends NodeInterface, S extends Source, R extends Relation<A, B, S, ManyEndpoint<B>>> Iterable<R> getOutgoingRelationships(Class<R> type) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void setRawPathSegment(Relationship pathSegment) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Relationship getRawPathSegment() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public long getId() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public String getUuid() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public String getType() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void setSecurityContext(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public SecurityContext getSecurityContext() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public PropertyContainer getPropertyContainer() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Iterable<PropertyKey> getPropertyKeys(String propertyView) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <T> Object setProperty(PropertyKey<T> key, T value) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <T> T getProperty(PropertyKey<T> propertyKey) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <T> T getProperty(PropertyKey<T> propertyKey, Predicate<GraphObject> filter) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public <T> Comparable getComparableProperty(PropertyKey<T> key) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void removeProperty(PropertyKey key) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public PropertyKey getDefaultSortKey() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public String getDefaultSortOrder() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void unlockSystemPropertiesOnce() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void unlockReadOnlyPropertiesOnce() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isValid(ErrorBuffer errorBuffer) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean onCreation(SecurityContext securityContext, ErrorBuffer errorBuffer) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean onModification(SecurityContext securityContext, ErrorBuffer errorBuffer, ModificationQueue modificationQueue) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean onDeletion(SecurityContext securityContext, ErrorBuffer errorBuffer, PropertyMap properties) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void afterCreation(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void afterModification(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void afterDeletion(SecurityContext securityContext, PropertyMap properties) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void ownerModified(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void securityModified(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void locationModified(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void propagatedModification(SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void addToIndex() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void updateInIndex() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void removeFromIndex() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void indexPassiveProperties() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public String getPropertyWithVariableReplacement(ActionContext renderContext, PropertyKey<String> key) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Object evaluate(ActionContext actionContext, String key, String defaultValue) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Object invokeMethod(String methodName, Map<String, Object> parameters, boolean throwExceptionForUnknownMethods) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public List<GraphObject> getSyncData() throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isNode() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isRelationship() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public NodeInterface getSyncNode() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public RelationshipInterface getSyncRelationship() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public CMISInfo getCMISInfo() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public int compareTo(Object o) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Principal getOwnerNode() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isGranted(Permission permission, SecurityContext securityContext) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void grant(Permission permission, Principal principal) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public void revoke(Permission permission, Principal principal) throws FrameworkException {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Security getSecurityRelationship(Principal principal) {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isVisibleToPublicUsers() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isVisibleToAuthenticatedUsers() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isNotHidden() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public boolean isHidden() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Date getVisibilityStartDate() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Date getVisibilityEndDate() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Date getCreatedDate() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Date getLastModifiedDate() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}

	@Override
	public Class getEntityType() {
		throw new UnsupportedOperationException("Not supported by this container.");
	}
}
