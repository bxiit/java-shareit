package ru.practicum.shareit.item.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentRequest {
    @NotEmpty(message = "errors.400.comments.bad_content")
    private String text;
}
