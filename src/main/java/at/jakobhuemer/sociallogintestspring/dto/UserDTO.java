package at.jakobhuemer.sociallogintestspring.dto;

import at.jakobhuemer.sociallogintestspring.models.User;

public record UserDTO(
        Long id,
        Long twitchId,
        String login,
        User.AuthorityLevel authorityLevel
) {
}
