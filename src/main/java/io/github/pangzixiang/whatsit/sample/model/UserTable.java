package io.github.pangzixiang.whatsit.sample.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTable {
    private String userId;
    private String userName;
    private String key;
    private LocalDateTime createdTimestamp;
}
