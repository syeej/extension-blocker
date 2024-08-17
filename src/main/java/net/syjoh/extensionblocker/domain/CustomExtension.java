package net.syjoh.extensionblocker.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_extension")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CustomExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String name;
}
