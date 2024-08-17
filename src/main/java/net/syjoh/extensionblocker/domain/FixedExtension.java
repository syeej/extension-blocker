package net.syjoh.extensionblocker.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "fixed_extension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FixedExtension {
    @Id
    @Enumerated(EnumType.STRING)
    private FixedExtensionType name;

    @Column(nullable = false)
    private boolean blocked;

    public static FixedExtension of(FixedExtensionType name, boolean blocked) {
        return new FixedExtension(name, blocked);
    }

    public FixedExtension updateBlockedStatus(boolean newStatus) {
        return new FixedExtension(this.name, newStatus);
    }

}
