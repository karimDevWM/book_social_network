/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.karim.book.feedback;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeedBackRequest(
    
    @Positive(message = "200")
    @Min(value=0, message = "201")
    @Max(value=5, message="202")
    Double note,

    @NotNull(message="203")
    @NotEmpty(message="203")
    @NotBlank(message="203")
    String comment,

    @NotNull(message="204")
    Integer bookId
) {

}
