package at.jakobhuemer.sociallogintestspring.dto;

import at.jakobhuemer.sociallogintestspring.models.user.AccessScope;
import at.jakobhuemer.sociallogintestspring.models.user.AuthorityLevel;

import java.util.List;

public record UserDTO(
        Long id,
        Long twitchId,
        String login,
        AuthorityLevel authorityLevel,
        List<AccessScope> accessScopes
) {
}
