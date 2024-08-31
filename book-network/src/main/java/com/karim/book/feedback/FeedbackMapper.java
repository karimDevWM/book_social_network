/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.karim.book.feedback;

import java.util.Objects;

import com.karim.book.book.Book;
import org.springframework.stereotype.Service;

@Service
public class FeedbackMapper {

    public Feedback toFeedBack(FeedBackRequest request) {
        return Feedback.builder()
            .note(request.note())
            .comment(request.comment())
            .book(Book.builder()
                .id(request.bookId())
                .archived(false) // not required and has no impact :: just to satisfy lombok
                .shareable(false) // not required and has no impact :: just to satisfy lombok
                .build()
            )
            .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer id) {
        return FeedbackResponse.builder()
            .note(feedback.getNote())
            .comment(feedback.getComment())
            .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
            .build();
    }
}
