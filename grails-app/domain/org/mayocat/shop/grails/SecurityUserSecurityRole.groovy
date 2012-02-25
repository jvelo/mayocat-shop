package org.mayocat.shop.grails

import org.apache.commons.lang.builder.HashCodeBuilder

class SecurityUserSecurityRole implements Serializable {

	SecurityUser securityUser
	SecurityRole securityRole

	boolean equals(other) {
		if (!(other instanceof SecurityUserSecurityRole)) {
			return false
		}

		other.securityUser?.id == securityUser?.id &&
			other.securityRole?.id == securityRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (securityUser) builder.append(securityUser.id)
		if (securityRole) builder.append(securityRole.id)
		builder.toHashCode()
	}

	static SecurityUserSecurityRole get(long securityUserId, long securityRoleId) {
		find 'from SecurityUserSecurityRole where securityUser.id=:securityUserId and securityRole.id=:securityRoleId',
			[securityUserId: securityUserId, securityRoleId: securityRoleId]
	}

	static SecurityUserSecurityRole create(SecurityUser securityUser, SecurityRole securityRole, boolean flush = false) {
		new SecurityUserSecurityRole(securityUser: securityUser, securityRole: securityRole).save(flush: flush, insert: true)
	}

	static boolean remove(SecurityUser securityUser, SecurityRole securityRole, boolean flush = false) {
		SecurityUserSecurityRole instance = SecurityUserSecurityRole.findBySecurityUserAndSecurityRole(securityUser, securityRole)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(SecurityUser securityUser) {
		executeUpdate 'DELETE FROM SecurityUserSecurityRole WHERE securityUser=:securityUser', [securityUser: securityUser]
	}

	static void removeAll(SecurityRole securityRole) {
		executeUpdate 'DELETE FROM SecurityUserSecurityRole WHERE securityRole=:securityRole', [securityRole: securityRole]
	}

	static mapping = {
		id composite: ['securityRole', 'securityUser']
		version false
	}
}
