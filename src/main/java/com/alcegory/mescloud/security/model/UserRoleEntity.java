package com.alcegory.mescloud.security.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity(name = "user_role")
@NoArgsConstructor
@IdClass(UserRoleEntity.UserRoleId.class)
public class UserRoleEntity implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Id
    @Column(name = "role_id")
    private Long roleId;
    @Id
    @Column(name = "section_id")
    private Long sectionId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private SectionRoleEntity sectionRole;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
        private Long sectionId;

        public UserRoleId(Long userId, Long roleId, Long sectionId) {
            this.userId = userId;
            this.roleId = roleId;
            this.sectionId = sectionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserRoleId that = (UserRoleId) o;
            return Objects.equals(userId, that.userId) &&
                    Objects.equals(roleId, that.roleId) &&
                    Objects.equals(sectionId, that.sectionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, roleId, sectionId);
        }
    }
}

