package org.mayocat.shop.grails

import org.apache.commons.lang.builder.HashCodeBuilder

class SecurityUserRole implements Serializable {

	SecurityUser securityUser
	SecurityRole securityRole

	boolean equals(other) {
		if (!(other instanceof SecurityUserRole)) {
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

	static SecurityUserRole get(long securityUserId, long securityRoleId) {
		find 'from SecurityUserRole where securityUser.id=:securityUserId and securityRole.id=:securityRoleId',
			[securityUserId: securityUserId, securityRoleId: securityRoleId]
	}

	static SecurityUserRole create(SecurityUser securityUser, SecurityRole securityRole, boolean flush = false) {
		new SecurityUserRole(securityUser: securityUser, securityRole: securityRole).save(flush: flush, insert: true)
	}

	static boolean remove(SecurityUser securityUser, SecurityRole securityRole, boolean flush = false) {
		SecurityUserRole instance = SecurityUserRole.findBySecurityUserAndSecurityRole(securityUser, securityRole)
		if (!instance) {
			return false
		}

		instance.delete(flush: flush)
		true
	}

	static void removeAll(SecurityUser securityUser) {
		executeUpdate 'DELETE FROM SecurityUserRole WHERE securityUser=:securityUser', [securityUser: securityUser]
	}

	static void removeAll(SecurityRole securityRole) {
		executeUpdate 'DELETE FROM SecurityUserRole WHERE securityRole=:securityRole', [securityRole: securityRole]
	}

	static mapping = {
		id composite: ['securityRole', 'securityUser']
		version false
	}
}
