package tt.chat.vc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tt.chat.vc.security.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldMatch(firstFieldName = "password", secondFieldName = "matchingPassword", message = "The passwords must match")

public class UserDto {
    @JsonIgnore
    private Long id;

    @NotBlank
    @Size(min = 4, max = 8)
    private String username;
    @NotBlank
    @Size(min = 4, max = 8)
    private String password;
    @NotNull
    @Size(min = 4, max = 8)
    private String matchingPassword;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 5, max = 20)
    private String phone;
}
